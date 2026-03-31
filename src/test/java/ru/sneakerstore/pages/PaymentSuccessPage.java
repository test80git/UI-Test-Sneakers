package ru.sneakerstore.pages;

import com.codeborne.selenide.SelenideElement;
import lombok.extern.slf4j.Slf4j;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@Slf4j
public class PaymentSuccessPage extends BasePage {
    private SelenideElement successMessage = $(".success");
    private SelenideElement orderInfo = $(".order-info");
    private SelenideElement timer = $("#counter");

    public PaymentSuccessPage verifyHeading() {
        successMessage.shouldHave(text("Оплата прошла успешно!"));
        log.info("Проверка текста \"Оплата прошла успешно\"");
        return this;
    }

    public PaymentSuccessPage verifyOrderIdContains(String expectedOrderId) {
        orderInfo.shouldHave(text(expectedOrderId));
        log.info("Проверка Заказа");
        return this;
    }

    public PaymentSuccessPage verifyTimerExists() {
        timer.shouldBe(visible);
        return this;
    }
}