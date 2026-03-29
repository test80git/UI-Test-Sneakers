package ru.sneakerstore.tests;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.sneakerstore.config.BaseSelenideTest;
import ru.sneakerstore.pages.LoginPage;

import static com.codeborne.selenide.Selenide.open;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DiscountSettingsTest extends BaseSelenideTest {

    private LoginPage loginPage;

    @BeforeEach
    public void setUp() {
        loginPage = new LoginPage();
        open("/login.html");
    }

    @Test
    @Order(1)
    @DisplayName("Включение скидки за количество")
    void enableQuantityDiscount() {
        loginPage.selectUser("Andrey88")
                .enableQuantityDiscount();

        assertThat(loginPage.isQuantityDiscountEnabled()).isTrue();
    }



    @Test
    @Order(2)
    @DisplayName("Включение скидки за сумму")
    void enableSumDiscount() {
        loginPage.selectUser("Andrey88")
                .enableSumDiscount();

        assertThat(loginPage.isSumDiscountEnabled()).isTrue();
    }

    @Test
    @Order(3)
    @DisplayName("Включение промокода с вводом кода")
    void enablePromoWithCode() {
        loginPage.selectUser("Andrey88")
                .enablePromoDiscount()
                .setPromoCode("TESTTHEBEST");

        assertThat(loginPage.isPromoDiscountEnabled()).isTrue();
        assertThat(loginPage.getPromoCode()).isEqualTo("TESTTHEBEST");
    }

    @Test
    @Order(4)
    @DisplayName("Включение всех скидок одновременно")
    void enableAllDiscounts() {
        loginPage.selectUser("Andrey88")
                .selectAllDiscounts()
                .setPromoCode("TESTTHEBEST");

        assertThat(loginPage.isQuantityDiscountEnabled()).isTrue();
        assertThat(loginPage.isSumDiscountEnabled()).isTrue();
        assertThat(loginPage.isPromoDiscountEnabled()).isTrue();
    }

    @Test
    @Order(5)
    @DisplayName("Отключение всех скидок")
    void clearAllDiscounts() {
        loginPage.selectUser("Andrey88")
                .selectAllDiscounts()
                .clearAllDiscounts();

        assertThat(loginPage.isQuantityDiscountEnabled()).isFalse();
        assertThat(loginPage.isSumDiscountEnabled()).isFalse();
        assertThat(loginPage.isPromoDiscountEnabled()).isFalse();
    }

    @ParameterizedTest
    @Order(6)
    @CsvSource({
            "Andrey88, QUANTITY, true",
            "Toma1990, SUM, true",
            "FedyaMel, PROMO, true"
    })
    @DisplayName("Параметризованный тест разных скидок для разных пользователей")
    void testDiscountCombinations(String username, String discountType, boolean expected) {
        loginPage.selectUser(username);

        switch (discountType) {
            case "QUANTITY":
                loginPage.enableQuantityDiscount();
                assertThat(loginPage.isQuantityDiscountEnabled()).isEqualTo(expected);
                break;
            case "SUM":
                loginPage.enableSumDiscount();
                assertThat(loginPage.isSumDiscountEnabled()).isEqualTo(expected);
                break;
            case "PROMO":
                loginPage.enablePromoDiscount();
                assertThat(loginPage.isPromoDiscountEnabled()).isEqualTo(expected);
                break;
        }
    }

}
