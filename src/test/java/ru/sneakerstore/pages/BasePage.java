package ru.sneakerstore.pages;
import static com.codeborne.selenide.Selenide.*;

public abstract class BasePage {

    protected void openPage(String url) {
        open(url);
    }

    protected void waitForLoad() {
        sleep(1000); // временно
    }
}