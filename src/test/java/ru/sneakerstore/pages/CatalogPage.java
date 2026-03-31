package ru.sneakerstore.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.$$x;
import static com.codeborne.selenide.Selenide.confirm;


@Slf4j
public class CatalogPage extends BasePage {

    private final NavBarPage navBarPage;

    private ElementsCollection productCard = $$(".product-card");
    private ElementsCollection chooseProduct = $$("#productsGrid > div:nth-child(1) > button");


    // Модальное окно и его элементы
    private SelenideElement modal = $("#productModal");
    private SelenideElement modalSizes = $("#modalSizes");
    private SelenideElement sizeButton = $(".sizes button, .size-btn");

    private SelenideElement quantityMinus = $(By.xpath("//button[@class='quantity-btn'][contains(text(), '-')]"));
    private SelenideElement quantityPlus = $(By.xpath("//button[@class='quantity-btn'][contains(text(), '+')]"));
    private SelenideElement quantityValue = $("#modalQuantity");

    private SelenideElement addToCartBtn = $(By.xpath("//button[.='Добавить в корзину']"));
    private SelenideElement closeModalBtn = $(".close, .modal-close");

    public CatalogPage() {
        this.navBarPage = new NavBarPage();
    }


    public CatalogPage selectSneaker(String productName) {
        log.info("Selecting product: {}", productName);
        // Находим карточку товара по тексту названия
        SelenideElement product =
                $$x("//div[contains(@class, 'product-card')][.//*[contains(text(), '" + productName + "')]]")
                        .first();
        product.shouldBe(visible);

        // Скроллим к карточке
        product.scrollTo();

        // Находим кнопку "Выбрать размер" внутри карточки и кликаем
        product.find(By.xpath(".//button[contains(text(), 'Выбрать размер')]"))
                .click();

        waitForModalToAppear();
        log.info("Selected product : {}", productName);
        return this;
    }

    public CatalogPage chooseSize(int size) {
        log.info("Choosing size: {}", size);
        // Ждем, когда размеры станут доступны
        modalSizes.shouldBe(visible);

        // Находим все кнопки размеров, исключая out-of-stock
        SelenideElement targetSize = $$(".sizes button.size-btn:not(.out-of-stock)")
                .findBy(text(String.valueOf(size)));

        targetSize.shouldBe(visible).click();
        log.info("Size {} selected", size);
        return this;
    }

    public String addToCart(int quantity) {
        log.info("Adding {} items to cart", quantity);
        // Устанавливаем количество (если нужно больше 1)
        String value = quantityValue.getValue();
        assert value != null;
        if (value.contains("1") && quantity > 1) {
            setQuantity(quantity);
        }
        if (Integer.parseInt(value) > quantity) {
            setMinusQuantity(Integer.parseInt(value) - quantity);
        }
        // Нажимаем "Добавить в корзину"
        addToCartBtn.shouldBe(visible).click();
        // Обработка алерта
        return confirm();
    }

    private void setMinusQuantity(int i) {
        for (int index = 0; index < i; index++) {
            quantityMinus.shouldBe(visible).click();
        }
    }

    public String addToCartNotEnoughProduct(int quantity) {
        log.info("Adding {} items to cart", quantity);
        // Устанавливаем количество (если нужно больше 1)
        if (quantity > 1) {
            setQuantity(quantity);
        }
        // Нажимаем "Добавить в корзину"
        addToCartBtn.shouldBe(visible).click();
        // Обработка алерта
        return confirm();

    }

    private void waitForModalToAppear() {
        modal.shouldBe(visible);
        modalSizes.shouldBe(visible);
        log.info("Modal window opened");
    }

    private void setQuantity(int quantity) {
        log.info("Setting quantity to {}, size={}", quantity, quantityValue.getText());
        // Очищаем поле количества
//        quantityValue.shouldBe(visible).clear();
//        quantityValue.setValue(String.valueOf(quantity));

//         Альтернатива: через кнопки +/-
        for (int i = 1; i < quantity; i++) {
            quantityPlus.click();
        }
    }

    private void waitForSuccessMessage() {
        SelenideElement successMsg = $(".success-message, .toast-success");
        if (successMsg.exists()) {
            successMsg.shouldBe(visible);
            log.info("Item added to cart successfully");
        }
        waitForLoad(1000L);
    }
}
