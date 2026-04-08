package ru.sneakerstore.tests;

import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.sneakerstore.config.BaseSelenideTest;
import ru.sneakerstore.pages.LoginPage;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LoginTest extends BaseSelenideTest {

    private LoginPage loginPage;

    @BeforeEach
    public void setUp() {
        loginPage = new LoginPage();
        open("/login.html");  // ← открываем страницу перед каждым тестом
       pause(200);
    }


    @Test
    @Order(1)
    @DisplayName("Успешный логин с валидными данными")
    void successfulLogin() {
        // Проверяем, что перешли на главную
        assertThat(title()).isEqualTo("Вход в магазин кроссовок");

        loginPage.login("Andrey88", "password");
        $("h2").shouldHave(text("Sneaker Store"));
    }

    @Test
    @Order(10)
    @DisplayName("Ошибка при неверном пароле")
    void loginWithWrongPassword() {
        // Проверяем, что перешли на главную
        assertThat(title()).isEqualTo("Вход в магазин кроссовок");

        loginPage.login("Andrey88", "wrong");

        // Проверяем сообщение об ошибке
        SelenideElement errorMessage = $(".error-message");
        errorMessage.shouldBe(visible);

        assertThat(errorMessage.getText())
                .contains("Неверный логин или пароль");
    }


    @ParameterizedTest
    @ValueSource(strings = {"Andrey88", "Toma1990", "FedyaMel", "Pushkin", "AdaLovelace"})
    @Order(2)
    @DisplayName("Проверка выбора пользователя")
    void testWithUser(String username) {
        loginPage.selectUser(username)
                .assertSelectedUser(username);
    }

}
