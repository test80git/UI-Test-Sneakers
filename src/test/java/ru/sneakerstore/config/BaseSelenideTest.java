package ru.sneakerstore.config;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

abstract public class BaseSelenideTest {

    /**
     * Инициализация selenide с настройками
     */
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        // Настройки читаются автоматически из selenide.properties
        // Дополнительные настройки
        Configuration.fastSetValue = false;
    }

    /**
     * Выполнение метода перед каждым запуском тестов
     */
    @BeforeEach
    public void init() {
        setUp();
    }

    /**
     * Выполнение метода после каждого закрытия тестов
     */
    @AfterEach
    public void tearDown() {
        Selenide.closeWebDriver();
    }

    // Метод для паузы между действиями
    protected void pause(long milliseconds) {
        Selenide.sleep(milliseconds);
    }


}
