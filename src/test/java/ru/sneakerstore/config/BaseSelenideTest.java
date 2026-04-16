package ru.sneakerstore.config;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.Allure;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sneakerstore.callback.SaverFailed;

import java.io.ByteArrayInputStream;

@ExtendWith(SaverFailed.class)
abstract public class BaseSelenideTest {

    private static final Logger log = LoggerFactory.getLogger(BaseSelenideTest.class);

    @BeforeAll
    static void setup() {

        // Настройки для CI/CD
//        Configuration.headless = true;
//        Configuration.browser = "chrome";
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--remote-allow-origins=*");
        options.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
        Configuration.browserCapabilities = options;

        SelenideLogger.addListener("AllureSelenide",
                new AllureSelenide()
                        .screenshots(true)
                        .savePageSource(true)
                        .includeSelenideSteps(true)
        );
    }

    private void setUp() {
        // WebDriverManager.chromedriver().setup(); // Закомментировано для CI
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
        Configuration.fastSetValue = false; // Использовать реальный ввод (не JS)
    }

    @BeforeEach
    public void init(TestInfo testInfo) {
        log.info("Running test: {}", testInfo.getDisplayName());
        setUp();
    }

    @AfterEach
    public void tearDown() {
        // Делаем скриншот вручную
        if (WebDriverRunner.hasWebDriverStarted()) {
            try {
                byte[] screenshot = ((TakesScreenshot) WebDriverRunner.getWebDriver())
                        .getScreenshotAs(OutputType.BYTES);
                Allure.addAttachment("Screenshot after test", new ByteArrayInputStream(screenshot));
                System.out.println("Screenshot saved"); // Проверка в консоли
            } catch (Exception e) {
                System.err.println("Screenshot failed: " + e.getMessage());
            }
        }
        Selenide.closeWebDriver();
    }

    protected void pause(long milliseconds) {
        Selenide.sleep(milliseconds);
    }

}
