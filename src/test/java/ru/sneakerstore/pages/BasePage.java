package ru.sneakerstore.pages;
import com.codeborne.selenide.WebDriverRunner;

import static com.codeborne.selenide.Selenide.*;

public abstract class BasePage {

    protected void openPage(String url) {
        open(url);
    }

    protected void waitForLoad(Long sleepMS) {
        sleep(sleepMS); // временно
    }


}