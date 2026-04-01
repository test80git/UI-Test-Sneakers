package ru.sneakerstore.tests;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sneakerstore.config.BaseSelenideTest;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.CollectionCondition.*;

public class SelenidePracticeTest extends BaseSelenideTest {

    private static final Logger log = LoggerFactory.getLogger(SelenidePracticeTest.class);

    @BeforeEach
    void openPage() {
        open("http://localhost:8080/test-page.html");
    }

    // ==================== 1. ПОИСК ЭЛЕМЕНТОВ ====================

    @Test
    @DisplayName("1. Поиск элементов: $, $$, $x, $$x")
    void elementSearchTest() {
        // $() - поиск по CSS
        SelenideElement loginBtn = $("#loginBtn"); // кнопка Войти
        loginBtn.shouldBe(visible);

        // $x() - поиск по XPath
        SelenideElement usernameInput = $x("//input[@id='username']");
        usernameInput.shouldBe(visible);
        SelenideElement passwordInput = $x("//input[@id='password']");
        usernameInput.shouldBe(visible);

        // $$() - коллекция по CSS
        ElementsCollection userCards = $$(".user-card");
        userCards.shouldHave(size(2));

        // Вход
        usernameInput.setValue("AdaLovelace");
        passwordInput.setValue("password");
        loginBtn.click();



        // $$x() - коллекция по XPath; contains для динамических классов
        ElementsCollection products = $$x("//div[contains(@class, 'product-card')]");
        products.shouldHave(size(5));

        // CSS selector
//        ElementsCollection products = $$(".product-card");
//        products.shouldHave(size(5));
    }

    // ==================== 2. ДЕЙСТВИЯ ====================

    @Test
    @DisplayName("2. Действия: click, setValue, scrollTo, doubleClick, contextClick")
    void actionsTest() {
        // Логин
        $(".user-card").click();
        $("#username").setValue("FedyaMel");
        $("#password").setValue("password");
        $("#loginBtn").scrollTo().click();

        // Двойной клик (на товаре)
        $$(".product-card").first().doubleClick();

        // Правый клик (контекстное меню)
        $("#contextMenuArea").contextClick();
        $("#contextResult").shouldHave(text("Правый клик сработал"));
    }

    // ==================== 3. ПРОВЕРКИ (CONDITIONS) ====================

    @Test
    @DisplayName("3. Проверки: visible, text, attribute, enabled, cssClass")
    void conditionsTest() {
        // visible / hidden
        $("#loginSection").shouldBe(visible);
        $("#dropdownSection").shouldBe(hidden);
        $("#contextMenuArea").shouldBe(hidden);
        $("#catalogSection").shouldBe(hidden);

        // text / exactText
        $(".logo").shouldHave(text("SneakerStore"));

        // attribute
        $("#username").shouldHave(attribute("placeholder", "Логин"));
        $("#password").shouldHave(attribute("placeholder", "Пароль"));

        // enabled / disabled
        $("#loginBtn").shouldBe(enabled);

        // cssClass
        $(".user-card").shouldHave(cssClass("user-card"));

        // value
        $("#username").setValue("test");
        $("#username").shouldHave(value("test"));
    }

    // ==================== 4. РАБОТА С КОЛЛЕКЦИЯМИ ====================

    @Test
    @DisplayName("4. Работа с коллекциями: size, text, filter, find, exclude, first, last, get")
    void collectionTest() {
        // Логин
        $(".user-card").click();
        $("#username").setValue("FedyaMel");
        $("#password").setValue("password");
        $("#loginBtn").click();

        ElementsCollection products = $$(".product-card");

        // size - проверка количества
        products.shouldHave(size(5));

        // findBy - поиск по тексту (находит элемент)
        products.findBy(text("Adidas")).shouldBe(visible);

        // filter - фильтрация коллекции
        products.filter(visible).shouldHave(size(5));

        // exclude - исключение элементов
        ElementsCollection nonDiscount = products.exclude(cssClass("sale"));
        nonDiscount.shouldHave(size(3));

        // first / last
        products.first().shouldBe(visible);
        products.last().shouldBe(visible);

        // get - по индексу
        products.get(2).shouldBe(visible);

        // texts - проверка всех текстов
        $$(".product-name").shouldHave(texts(
                "беговые Adidas Ultraboost 22",
                "Nike Air Zoom Alphafly Next% 3",
                "New Balance 574",
                "Puma RS-X",
                "Reebok Classic"
        ));
    }

    // ==================== 5. РАБОТА С ВЫПАДАЮЩИМ СПИСКОМ ====================

    @Test
    @DisplayName("5. Выпадающий список: selectOption")
    void dropdownTest() {
        // Логин
        $(".user-card").click();
        $("#username").setValue("FedyaMel");
        $("#password").setValue("password");
        $("#loginBtn").click();

        // ✅ Ждем появления выпадающего списка
        $("#dropdownSection").shouldBe(visible);
        $("#sortSelect").shouldBe(visible);

        // Выбор значения из выпадающего списка
         $("#sortSelect").selectOption("Цена: по возрастанию");
        $("#sortSelect").selectOptionByValue("priceAsc");

        // Проверка сортировки (цены по возрастанию)
        ElementsCollection prices = $$(".product-price");
        int firstPrice = Integer.parseInt(prices.first().text().replace(" ₽", ""));
        int secondPrice = Integer.parseInt(prices.get(1).text().replace(" ₽", ""));
        assert firstPrice <= secondPrice;

        // selectOptionByValue
        $("#sortSelect").selectOptionByValue("priceDesc"); // по value
        // selectOption
        $("#sortSelect").selectOption("Название: А-Я"); // по видимому тексту
        // selectOptionContainingText
        $("#sortSelect").selectOptionContainingText("возраст");  // по части текста
//        // selectRadio. Здесь не используется
//        $("#sortSelect").selectRadio("value");  // для radio
    }

    // ==================== 6. ОЖИДАНИЯ ====================

    @Test
    @DisplayName("6. Ожидания: shouldBe, waitUntil")
    void waitTest() {
        // Логин
        $(".user-card").click();
        $("#username").setValue("FedyaMel");
        $("#password").setValue("password");
        $("#loginBtn").click();

        // Явное ожидание с таймаутом
        $("#catalogSection").shouldBe(visible, java.time.Duration.ofSeconds(5));

        // should + условие
        $("#username").shouldHave(value("FedyaMel"));

        $("#cartCount").shouldBe(visible);                        // стандартное ожидание
        $("#cartCount").shouldBe(visible, Duration.ofSeconds(5)); // с таймаутом
        $("#cartCount").should(appear);                           // появится
    }

    // ==================== 7. РАБОТА С МОДАЛЬНЫМ ОКНОМ ====================

    @Test
    @DisplayName("7. Работа с модальным окном")
    void modalTest() {
        // Логин
        $(".user-card").click();
        $("#username").setValue("FedyaMel");
        $("#password").setValue("password");
        $("#loginBtn").click();

        // Открыть модальное окно - Выбрать размер
        $$(".select-size-btn").first().click();

        // Проверка что модалка открылась
        $("#productModal").shouldBe(visible);

        // Выбор размера (первый доступный, исключая out-of-stock)
        $$(".size-btn").filter(not(Condition.cssClass("out-of-stock")))
                .first()
                .click();

        // Увеличение количества. Изначально 1, после нажатия 2
        $$(".quantity-btn").last().click();

        // Добавление в корзину
        $(".add-to-cart-btn").click();

        // Проверка закрытия модалки
        $("#productModal").shouldBe(hidden);

        // Проверка обновления корзины
        $("#cartCount").shouldHave(text("2"));
        log.info("Поставить breakPoint здесь");
    }

    // ==================== 8. ПОЛНЫЙ СЦЕНАРИЙ ====================

    @Test
    @DisplayName("8. Полный сценарий: логин → выбор товара → оплата")
    void fullScenarioTest() {
        // ----- ЛОГИН -----
        // Выбор пользователя
        $(".user-card").click();

        // Ввод данных
        $("#username").setValue("FedyaMel");
        $("#password").setValue("password");

        // Включение скидок
        $("#discountQuantity").click();
        $("#discountSum").click();

        // Клик по кнопке входа
        $("#loginBtn").click();

        // Проверка успешного входа
        $("#catalogSection").shouldBe(visible);
        $("#userDisplay").shouldHave(text("FedyaMel"));

        // ----- ВЫБОР ТОВАРА -----
        // Выбор первого товара
        $$(".product-card").first().find(".select-size-btn").click();

        // Выбор размера (первый доступный)
        $$(".size-btn").filter(not(Condition.cssClass("out-of-stock")))
                .first()
                .click();

        ElementsCollection quantityBtns = $$(".quantity-btn");
        // Увеличение количества до 3
        for (int i = 0; i < 2; i++) {
            quantityBtns.last().click();
        }

        // Добавление в корзину
        $(".add-to-cart-btn").click();

        // ----- КОРЗИНА -----
        // Открыть корзину
        $("#cartLink").click();

        // Проверка корзины
        $("#cartSection").shouldBe(visible);
        $("#cartQuantity").shouldHave(text("3"));

        // ----- ОПЛАТА -----
        // Перейти к оплате
        $("#checkoutBtn").click();

        // Заполнить данные карты
        $("#cardNumber").setValue("4111111111111111");
        $("#expiryDate").setValue("12/28");
        $("#cardHolder").setValue("IVAN PETROV");
        $("#cvv").setValue("123");

        // Оплатить
        $("#payButton").click();

        // ----- ПРОВЕРКА УСПЕХА -----
        $("#successSection").shouldBe(visible);
        $("h2.success").shouldHave(text("Оплата прошла успешно!"));
//        $("#successSection").shouldHave(text("Оплата прошла успешно!"));
    }

    // ==================== 9. ПРОВЕРКА ОШИБОК ====================

    @Test
    @DisplayName("9. Проверка ошибок оплаты")
    void paymentErrorsTest() {
        // Логин и добавление товара
        $(".user-card").click();
        $("#username").setValue("FedyaMel");
        $("#password").setValue("password");
        $("#loginBtn").click();

        $$(".select-size-btn").first().click();
        $$(".size-btn").filter(not(Condition.cssClass("out-of-stock"))).first().click();
        $(".add-to-cart-btn").click();

        $("#cartLink").click();
        $("#checkoutBtn").click();

        // ----- ПРОВЕРКИ ОШИБОК -----

        // Ошибка: карта заблокирована
        $("#cardNumber").setValue("0000000000000000");
        $("#expiryDate").setValue("12/28");
        $("#cardHolder").setValue("IVAN PETROV");
        $("#cvv").setValue("123");
        $("#payButton").click();
        $("#paymentError").shouldHave(text("Карта заблокирована"));

        // Ошибка: недостаточно средств
        $("#cardNumber").setValue("1234567890123456");
        $("#payButton").click();
        $(".error-message.show").shouldHave(text("Недостаточно средств"));

        // Ошибка: срок истек
        $("#cardNumber").setValue("4111111111111111");
        $("#expiryDate").setValue("12/24");
        $("#payButton").click();
        $(".error-message.show").shouldHave(text("Срок действия карты истёк"));
    }

    // ==================== 10. СКРИНШОТ ====================

    @Test
    @DisplayName("10. Создание скриншота")
    void screenshotTest() {
        // Логин
        $(".user-card").click();
        $("#username").setValue("FedyaMel");
        $("#password").setValue("password");
        $("#loginBtn").click();

        // Добавить товар
        $$(".select-size-btn").first().click();
        $$(".size-btn").filter(not(Condition.cssClass("out-of-stock"))).first().click();
        $(".add-to-cart-btn").click();

        // Открыть корзину
        $("#cartLink").click();

        // Скриншот всей страницы
        screenshot("full-page-screenshot");

        // Скриншот конкретного элемента
        $("#cartSummary").screenshot();
    }
}