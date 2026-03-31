package ru.sneakerstore.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class NavBarPage{

    protected SelenideElement catalogBtn = $("#catalogLink");
    protected SelenideElement cartBtn = $("#cartLink");
    protected SelenideElement userDisplay = $("#userDisplay");
    protected SelenideElement switchUserBtn = $("#switchUserBtn");
    protected SelenideElement logoutBtn = $("#logoutBtn");


    public CartPage openCart() {
        cartBtn.scrollTo().shouldBe(visible).click();
        return new CartPage();
    }


    public CatalogPage openCatalog() {
        catalogBtn.scrollTo().shouldBe(visible).click();
        return new CatalogPage();
    }

    public LoginPage switchUser() {
        switchUserBtn.scrollTo().shouldBe(visible).click();
        return new LoginPage();
    }

    public LoginPage logout() {
        logoutBtn.scrollTo().shouldBe(visible).click();
        return new LoginPage();
    }

}
