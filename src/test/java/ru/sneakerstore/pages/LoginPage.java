package ru.sneakerstore.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.Assertions;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;

public class LoginPage extends BasePage {


    private ElementsCollection userCard = $$x("//div[@class='user-card']");
    private SelenideElement usernameInput = $("#username");
    private SelenideElement passwordInput = $("#password");
    private SelenideElement loginButton = $("button[type='submit']");

    // Элементы настроек скидок
    private SelenideElement discountQuantityCheckbox = $("#discountQuantity");
    private SelenideElement discountSumCheckbox = $("#discountSum");
    private SelenideElement discountPromoCheckbox = $("#discountPromo");
    private SelenideElement promoInput = $("#promoCode");
    private SelenideElement promoCodeField = $("#promoCode");

    public void login(String username, String password) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        loginButton.click();
    }

    public LoginPage selectUser(String username) {
        userCard.findBy(text(username)).click();
        waitForLoad(1000L);
        return this;
    }

    public void assertSelectedUser(String username){
        String text = usernameInput.getValue();
        Assertions.assertEquals(text, username);
    }

    // Методы для работы со скидками
    public LoginPage enableQuantityDiscount() {
        if (!discountQuantityCheckbox.isSelected()) {
            discountQuantityCheckbox.click();
        }
        return this;
    }

    public LoginPage disableQuantityDiscount() {
        if (discountQuantityCheckbox.isSelected()) {
            discountQuantityCheckbox.click();
        }
        return this;
    }

    public LoginPage enableSumDiscount() {
        if (!discountSumCheckbox.isSelected()) {
            discountSumCheckbox.click();
        }
        return this;
    }

    public LoginPage disableSumDiscount() {
        if (discountSumCheckbox.isSelected()) {
            discountSumCheckbox.click();
        }
        return this;
    }

    public LoginPage enablePromoDiscount() {
        if (!discountPromoCheckbox.isSelected()) {
            discountPromoCheckbox.click();
        }
        return this;
    }

    public LoginPage setPromoCode(String code) {
        promoInput.setValue(code);
        return this;
    }

    public LoginPage selectAllDiscounts() {
        enableQuantityDiscount();
        enableSumDiscount();
        enablePromoDiscount();
        return this;
    }

    public LoginPage clearAllDiscounts() {
        disableQuantityDiscount();
        disableSumDiscount();
        if (discountPromoCheckbox.isSelected()) {
            discountPromoCheckbox.click();
        }
        return this;
    }

    public boolean isQuantityDiscountEnabled() {
        return discountQuantityCheckbox.isSelected();
    }

    public boolean isSumDiscountEnabled() {
        return discountSumCheckbox.isSelected();
    }

    public boolean isPromoDiscountEnabled() {
        return discountPromoCheckbox.isSelected();
    }

    public String getPromoCode() {
        return promoInput.getValue();
    }


}

