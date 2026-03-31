package ru.sneakerstore.pages;

import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class CartPage extends BasePage {

    private static final Logger log = LoggerFactory.getLogger(CartPage.class);

    private SelenideElement checkoutBtn = $("#checkoutBtn");
    private SelenideElement cartItems = $("#cartItems");
    private SelenideElement cartSummary = $("#cartSummary");

    private SelenideElement findTotal = $(By.xpath("//div[@class='summary-row total']/span[2]"));
    private SelenideElement findDiscount = $(By.xpath("//*[@id=\"cartSummary\"]/div[3]/span[2]"));
    private SelenideElement findSummary = $(By.xpath("//*[@id=\"cartSummary\"]/div[2]/span[2]"));
    private SelenideElement findQuantity = $(By.xpath("//*[@id=\"cartSummary\"]/div[1]/span[2]"));


    /**
     * Методы для получения значений из summary
     */
    private String extractPrice(SelenideElement element) {
        // Очищаем цену от пробелов и валюты
        Pattern pattern = Pattern.compile("\\D+");
        return pattern.matcher(element.text()).replaceAll("");
    }

    public String getTotalAmount() {
        return extractPrice(findTotal);
    }

    public String getDiscountAmount() {
        return extractPrice(findDiscount);
    }

    public String getSubtotalAmount() {
        return extractPrice(findSummary);
    }

    public String getQuantity() {
        return extractPrice(findQuantity);
    }

    /**
     * Проверить общую сумму
     *
     * @param expectedTotal
     */
    public void verifyTotalAmount(String expectedTotal) {
        String actualTotal = getTotalAmount();
        log.info("Total amount: {}", actualTotal);
        Assertions.assertEquals(expectedTotal, actualTotal, "Incorrect total amount!");
    }

    /**
     * Проверить скидку
     *
     * @param expectedDiscount
     */
    public void verifyDiscountAmount(String expectedDiscount) {
        String actualDiscount = getDiscountAmount();
        log.info("Discount amount: {}", actualDiscount);
        Assertions.assertEquals(expectedDiscount, actualDiscount, "Incorrect discount amount!");
    }

    /**
     * Находит элемент с суммой без скидки и возвращает очищенное значение.
     *
     * @param expectedSubtotal
     */
    public void verifySubtotalAmount(String expectedSubtotal) {
        String actualSubtotal = getSubtotalAmount();
        log.info("Subtotal amount: {}", actualSubtotal);
        Assertions.assertEquals(expectedSubtotal, actualSubtotal, "Incorrect subtotal amount!");
    }

    /**
     * Проверить количество товаров
     *
     * @param expectedQuantity
     */
    public void verifyQuantity(String expectedQuantity) {
        String actualQuantity = getQuantity();
        log.info("Quantity amount: {}", actualQuantity);
        Assertions.assertEquals(expectedQuantity, actualQuantity, "Incorrect subtotal amount!");
    }



    /**
     * Перейти к оформлению заказа
     */
    public PaymentPage proceedToCheckout() {
        log.info("Proceeding to checkout");
        checkoutBtn.scrollTo().shouldBe(visible).click();
        return new PaymentPage();
    }

}
