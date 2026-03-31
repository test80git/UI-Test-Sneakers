package ru.sneakerstore.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import ru.sneakerstore.config.BaseSelenideTest;
import ru.sneakerstore.pages.CartPage;
import ru.sneakerstore.pages.CatalogPage;
import ru.sneakerstore.pages.LoginPage;
import ru.sneakerstore.pages.NavBarPage;
import ru.sneakerstore.pages.PaymentPage;
import ru.sneakerstore.pages.PaymentSuccessPage;

import static com.codeborne.selenide.Selenide.open;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Сквозные сценарии пользователя")
public class UserJourneyTest extends BaseSelenideTest {

    private LoginPage loginPage;
    private CatalogPage catalogPage;
    private CartPage cartPage;
    private NavBarPage navBarPage;
    private PaymentPage paymentPage;
    private PaymentSuccessPage paymentSuccessPage;

    @BeforeEach
    public void setUp() {
        loginPage = new LoginPage();
        catalogPage = new CatalogPage();
        cartPage = new CartPage();
        navBarPage = new NavBarPage();
        paymentPage = new PaymentPage();
        paymentSuccessPage = new PaymentSuccessPage();
        open("/login.html");
    }

    @Test
    @Order(1)
    @DisplayName("Проверка Недостаточно товара")
    void happyPathCompletePurchase() {
        // 1. Логин с выбором скидки
        loginPage.selectUser("AdaLovelace")
                .enableQuantityDiscount()
                .login("AdaLovelace", "password");

        // 2. Выбор товара
        catalogPage.selectSneaker("New Balance 574")
                .chooseSize(40)
                .addToCartNotEnoughProduct(4)
                .addToCart(3);

        navBarPage.openCart();

        // 3. Переход в корзину
        cartPage.proceedToCheckout();

        // 4. Оплата
        paymentPage.verifyOrderDiscount().
                verifyOrderTotal().
                enterCardDetails("4111111111111111", "12/28", "IVAN PETROV", "123")
                .pay();

    }

    @Test
    @Order(2)
    @DisplayName("Покупка трех товаров со скидкой для количества")
    void happyPathQualityPurchase() {
        // 1. Логин с выбором скидки
        loginPage.selectUser("FedyaMel")
                .enableQuantityDiscount()
//                .enableSumDiscount()
                .login("FedyaMel", "password");

        // 2. Выбор товара
        catalogPage.selectSneaker("Nike Air Zoom Alphafly Next% 3")
                .chooseSize(37)
                .addToCart(3);

        navBarPage.openCart();

        // 3. Переход в корзину
        cartPage.verifyTotalAmount("14790");
        cartPage.verifyDiscountAmount("2610");
        cartPage.verifySubtotalAmount("17400");
        cartPage.proceedToCheckout();

        // 4. Оплата
        paymentPage.verifyOrderDiscount().
                verifyOrderTotal().
                enterCardDetails("4111111111111111", "12/28", "IVAN PETROV", "123")
                .pay();

    }

    @Test
    @Order(3)
    @DisplayName("Покупка двух товаров со скидкой для суммы больше 20000 руб")
    void happyPathSumPurchase() {
        // 1. Логин с выбором скидки
        loginPage.selectUser("FedyaMel")
                .enableSumDiscount()
//                .enableSumDiscount()
                .login("FedyaMel", "password");

        // 2. Выбор товара
        catalogPage.selectSneaker("беговые Adidas Ultraboost 22")
                .chooseSize(42)
                .addToCart(2);

        navBarPage.openCart();

        // 3. Переход в корзину
        cartPage.verifyTotalAmount("26100");
        cartPage.verifyDiscountAmount("2900");
        cartPage.verifySubtotalAmount("29000");
        cartPage.verifyQuantity("2");
        cartPage.proceedToCheckout();

        // 4. Оплата
        paymentPage.verifyOrderDiscount().
                verifyOrderTotal().
                enterCardDetails("4111111111111111", "12/28", "IVAN PETROV", "123")
                .pay();

    }

    @Test
    @Order(4)
    @DisplayName("Покупка двух товаров со скидкой для суммы больше 20000 руб")
    void happyPathSumAndQualityPurchase() {
        // 1. Логин с выбором скидки
        loginPage.selectUser("FedyaMel")
                .enableQuantityDiscount()
                .enableSumDiscount()
                .login("FedyaMel", "password");

        // 2. Выбор товара
        catalogPage.selectSneaker("беговые Adidas Ultraboost 22")
                .chooseSize(43)
                .addToCart(3);

        navBarPage.openCart();

        // 3. Переход в корзину
        cartPage.verifyTotalAmount("32625");
        cartPage.verifyDiscountAmount("10875");
        cartPage.verifySubtotalAmount("43500");
        cartPage.verifyQuantity("3");
        cartPage.proceedToCheckout();

        // 4. Оплата
        paymentPage.verifyOrderDiscount().
                verifyOrderTotal().
                enterCardDetails("4111111111111111", "12/28", "IVAN PETROV", "123")
                .pay();

    }

    @Test
    @Order(5)
    @DisplayName("Проверка оплаты")
    void paymentVerification() {
        // 1. Логин с выбором скидки
        loginPage.selectUser("AdaLovelace")
                .enableQuantityDiscount()
                .login("AdaLovelace", "password");

        // 2. Выбор товара
        catalogPage.selectSneaker("New Balance 574")
                .chooseSize(41)
                .addToCart(1);

        navBarPage.openCart();

        // 3. Переход в корзину
        cartPage.proceedToCheckout();

        // 4. Оплата
        paymentPage.verifyOrderDiscount().
                verifyOrderTotal();

        paymentPage.enterCardDetails("0000000000000000", "12/28", "IVAN PETROV", "123")
                .pay();
        paymentPage.findError("Карта заблокирована");

        paymentPage.enterCardDetails("1234567890123456", "12/28", "IVAN PETROV", "123")
                .pay();
        paymentPage.findError("Недостаточно средств");

        paymentPage.enterCardDetails("4111111111111111", "12/24", "IVAN PETROV", "123")
                .pay();
        paymentPage.findError("Срок действия карты истёк");

        paymentPage.enterCardDetails("4111111111111111", "13/33", "IVAN PETROV", "123")
                .pay();
        paymentPage.findError("Неверный формат. Используйте MM/YY");

        paymentPage.enterCardDetails("41111111", "12/33", "IVAN PETROV", "123")
                .pay();
        paymentPage.findError("Ошибка валидации входных данных");

        paymentPage.enterCardDetails("4111111111111111", "12/33", "IVAN PETROV", "123")
                .pay();

        paymentSuccessPage.verifyHeading()
                .verifyOrderIdContains("Заказ №");
    }

    @Test
    @Order(10)
    @DisplayName("Сценарий: отмена заказа после оформления")
    void cancelOrderAfterCreation() {
        // похожий сценарий
    }


}
