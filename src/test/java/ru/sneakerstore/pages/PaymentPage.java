package ru.sneakerstore.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.time.Duration;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$x;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.sleep;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;

@Slf4j
public class PaymentPage extends BasePage {

    private SelenideElement payBtn = $("#payButton");
    private SelenideElement returnCart = $("#paymentForm > button:nth-child(6)");

    private ElementsCollection infoOrder = $$x("/html/body/div/div[1]");
    private SelenideElement orderItems = $("#orderItems");
    private SelenideElement orderTotal = $("#orderTotal");
    private SelenideElement orderDiscount = $("#orderDiscount");

    private SelenideElement cardNumber = $("#cardNumber");
    private SelenideElement expiryDate = $("#expiryDate");
    private SelenideElement cardHolder = $("#cardHolder");
    private SelenideElement cvv = $("#cvv");

    private ElementsCollection errorMessage = $$x("//div[@class='error-message']");

    @Step("Поиск ошибки: {message}")
    public PaymentPage findError (String message) {
        SelenideElement selenideElement = errorMessage.find(text(message));
       Assertions.assertThat(selenideElement.getText()).isEqualTo(message);
        return this;
    }

    public PaymentPage setNumberCart(String number) {
        cardNumber.setValue(number);
        return this;
    }

    public PaymentPage setExpiryDate(String number) {
        expiryDate.setValue(number);
        return this;
    }

    public PaymentPage setCardHolder(String number) {
        cardHolder.setValue(number);
        return this;
    }

    public PaymentPage setCVV(String number) {
        cvv.setValue(number);
        return this;
    }

    public void getNumberCart() {
        String value = cardNumber.getValue();
        assert value != null;
        Assertions.assertThat(!value.isEmpty());
    }

    @Step("Оплата заказа")
    public PaymentPage pay() {
        payBtn.scrollTo().shouldBe(visible).click();
        return this;
    }

    @Step("Ввод деталей карты")
    public PaymentPage enterCardDetails(String numberCart,
                                        String expiryDate,
                                        String cardHolder,
                                        String cvv) {
        return setNumberCart(numberCart).
                setExpiryDate(expiryDate).
                setCardHolder(cardHolder).
                setCVV(cvv);
    }

    @Step("Проверка общей суммы заказа")
    public PaymentPage verifyOrderTotal() {
        log.info("Verify order total: {}", orderTotal.getText());
        return this;
    }

    @Step("Проверка скидки в заказе")
    public PaymentPage verifyOrderDiscount() {
        log.info("Verify order discount: {}", orderDiscount.getText());
//        orderDiscount.getValue().isEmpty();
        return this;
    }

    @Step("Вернуться на страницу корзины")
    public void returnToCartPage() {
        log.info("Return to cart page");
        returnCart.click();
    }

}
