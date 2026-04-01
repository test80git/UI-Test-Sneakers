# Шпаргалка по Selenide

## 📍 Основные методы поиска элементов

| Метод | Описание | Пример |
|-------|----------|--------|
| `$()` | Поиск одного элемента по CSS-селектору | `$("#login")` - по id<br>`$(".btn")` - по классу<br>`$("button")` - по тегу |
| `$$()` | Поиск коллекции элементов по CSS-селектору | `$$(".product-card")` - все товары |
| `$x()` | Поиск одного элемента по XPath | `$x("//div[@class='user-card']")` |
| `$$x()` | Поиск коллекции по XPath | `$$x("//div[@class='product-card']")` |

## 🔍 Примеры поиска элементов

```java
// Поиск по ID
SelenideElement loginBtn = $("#loginButton");
SelenideElement username = $("#username");

// Поиск по классу
SelenideElement errorMsg = $(".error-message");
ElementsCollection products = $$(".product-card");

// Поиск по атрибуту
SelenideElement submitBtn = $("button[type='submit']");
SelenideElement disabledBtn = $("button[disabled]");

// Поиск по тексту (XPath)
SelenideElement cartBtn = $x("//button[contains(text(), 'Корзина')]");
ElementsCollection errors = $$x("//div[@class='error-message']");

// Комбинированный поиск
SelenideElement sizeBtn = $(".sizes button.size-btn:not(.out-of-stock)");
```

## 🎯 Основные команды SelenideElement

### Действия
```java
// Клики
element.click();                    // обычный клик
element.doubleClick();              // двойной клик
element.contextClick();             // правый клик
element.scrollTo().click();         // скролл + клик

// Ввод текста
element.setValue("text");           // установить значение
element.val("text");                // то же самое
element.append("text");             // добавить к существующему
element.clear();                    // очистить поле

// Работа с формами
element.selectRadio("value");       // выбрать радио
element.selectOption("value");      // выбрать из выпадающего списка
element.uploadFile(file);           // загрузить файл
```

### Проверки (Conditions)
```java
// Видимость
element.shouldBe(visible);          // должен быть видим
element.shouldBe(hidden);           // должен быть скрыт

// Текст
element.shouldHave(text("Текст"));  // содержит текст
element.shouldHave(exactText("Текст")); // точное совпадение

// Атрибуты
element.shouldHave(attribute("class", "active"));
element.shouldHave(value("123"));   // проверка значения

// Состояния
element.shouldBe(enabled);          // активен
element.shouldBe(disabled);         // отключен
element.shouldBe(selected);         // выбран (checkbox/radio)

// Комбинированные
element.shouldBe(visible, enabled); // несколько условий
```

### Ожидания
```java
// Явные ожидания
element.shouldBe(visible, Duration.ofSeconds(10));
element.should(appear);             // появится

// Неявные
element.shouldNotBe(visible);       // не должен быть видим
element.shouldNotHave(text("Error"));
```

## 📦 Работа с коллекциями (ElementsCollection)

```java
ElementsCollection products = $$(".product-card");

// Поиск в коллекции
products.findBy(text("Adidas"));           // найти по тексту
products.filter(visible);                   // отфильтровать
products.exclude(hidden);                   // исключить

// Работа с индексами
products.first().click();                   // первый
products.last().click();                    // последний
products.get(2).click();                    // третий

// Проверки коллекции
products.shouldHave(size(3));               // должно быть 3 элемента
products.shouldHave(texts("1", "2", "3"));  // тексты элементов
```

## 🌐 Работа с браузером

```java
// Навигация
open("/login.html");                        // открыть URL
open("https://example.com");

// Управление
Selenide.back();                            // назад
Selenide.refresh();                         // обновить
Selenide.clearBrowserCookies();             // очистить cookies
Selenide.clearBrowserLocalStorage();        // очистить localStorage

// Скриншоты
Selenide.screenshot("name");                // сделать скриншот
element.screenshot();                       // скриншот элемента

// Диалоги
String alertText = confirm();               // подтвердить алерт
String alertText = dismiss();               // отменить алерт
String alertText = prompt("text");          // ввести в промпт
```

## 🔄 Полезные комбинации

```java
// Цепочки действий
$("#username").setValue("user")
    .shouldHave(value("user"))
    .pressEnter();

// Поиск внутри элемента
SelenideElement card = $(".product-card");
card.$(".price").shouldHave(text("1000"));
card.$$(".size-btn").filter(visible).first().click();

// Ожидание с условием
$("#cartLink").scrollTo()
    .shouldBe(visible)
    .click();

// Работа с модальными окнами
$("#openModal").click();
$(".modal").shouldBe(visible);
$(".modal .close").click();
$(".modal").shouldBe(hidden);
```

## 📝 Реальные примеры из проекта

```java
// Поиск карточки товара по тексту
SelenideElement product = $$x("//div[contains(@class, 'product-card')][.//*[contains(text(), '" + productName + "')]]").first();

// Поиск внутри найденного элемента
product.find(By.xpath(".//button[contains(text(), 'Выбрать размер')]")).click();

// Работа с коллекцией размеров
$$(".sizes button.size-btn:not(.out-of-stock)")
    .findBy(text(String.valueOf(size)))
    .click();

// Клик с предварительным скроллом
checkoutBtn.scrollTo().shouldBe(visible).click();

// Проверка текста после клика
String alertText = confirm();
assertTrue(alertText.contains("Товар добавлен в корзину!"));
```
---
## Разница между # и . в Selenide/CSS

| Символ | Тип | Поиск по | Пример |
|--------|-----|----------|--------|
| `#` | id | Уникальному идентификатору | `$("#cartCount")` |
| `.` | class | Классу (может быть несколько) | `$(".add-to-cart-btn")` |

## Примеры:

```java
// # - поиск по ID (уникальный)
$("#loginBtn")      // <button id="loginBtn">
$("#username")      // <input id="username">
$("#cartCount")     // <span id="cartCount">

// . - поиск по классу (может быть много элементов)
$(".user-card")     // <div class="user-card">
$(".product-card")  // <div class="product-card new">
$(".add-to-cart-btn") // <button class="add-to-cart-btn">

// Комбинации
$(".product-card .product-name")  // элемент внутри
$("#sortSelect option")            // дочерние элементы
```

## Важно:

- **ID (#)** - уникальный, один на странице
- **Класс (.)** - может быть у многих элементов

---


# Команда запуска локально:
```bash
python -m http.server 8080 --directory src/test/resources
```
Краткая шпаргалка по правильным методам

|Что делаем	|Правильный метод Selenide|
|---|---|
|Выбрать из выпадающего списка	|selectOption("текст")|
|Выбрать по value	|selectOptionByValue("value")|
|Ожидание с таймаутом	|shouldBe(visible, Duration.ofSeconds(5))|
|Клик по кнопке "+"	|$$(".quantity-btn").last().click()|
|Клик по кнопке "-"	|$$(".quantity-btn").first().click()|
|Поиск по тексту в коллекции	|products.findBy(text("Adidas"))|
|Фильтрация	|products.filter(visible)|
|Исключение	|products.exclude(hidden)|
|Проверка размера коллекции|	shouldHave(size(5))|
|Проверка текстов коллекции	|shouldHave(texts("a", "b", "c"))|
|Правый клик|	contextClick()|
|Двойной клик|	doubleClick()|


Для понимания каждого отдельного метода смотрите SelenidePracticeTest
к resources/test-page.html

Скриншот смотреть в `build/reports/tests/test`


