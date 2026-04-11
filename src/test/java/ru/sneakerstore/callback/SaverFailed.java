package ru.sneakerstore.callback;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

/**
 * Автоматически собирать информацию о провалившихся тестах и сохранять их в текстовый файл в формате, готовом для повторного запуска через Gradle.
 * <p>
 * AfterAllCallback — выполняется после всех тестов в классе
 * AfterTestExecutionCallback — выполняется после каждого тестового метода
 */

public class SaverFailed implements AfterAllCallback, AfterTestExecutionCallback {

    private static final Set<String> failedTests = new HashSet<>();

    /**
     * Сохраняет список упавших тестов в файл после выполнения всех тестов
     * 1. Определяет путь к файлу: {user.dir}/build/failed-tests.txt
     * 2. Собирает все имена упавших тестов в одну строку через пробел
     * 3. Записывает строку в файл
     *
     * @param context - контекст выполнения тестов (содержит информацию о классе, методе и т.д.)
     * @throws Exception — может возникнуть при записи в файл
     */
    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        String output = System.getProperty("user.dir") + "/build/failed-tests.txt";
        Path path = Paths.get(output);
        String result = String.join(" ", failedTests);
        Files.writeString(path, result);
    }


    /**
     * Отслеживает выполнение каждого теста и сохраняет упавшие
     * <p>
     * 1. Получает имя метода (methodName) и имя класса (className) из контекста
     * 2. Формирует строку идентификатора: --tests {className}.{methodName}*
     * 3. Если тест упал (есть исключение в контексте) — добавляет идентификатор в failedTests
     *
     * @param context — контекст выполнения тестов (содержит информацию о классе, методе и т.д.)
     * @throws Exception — не выбрасывает
     */
    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        String methodName = context.getRequiredTestMethod().getName();
        Class<?> testClass = context.getRequiredTestClass();
        String className = testClass.getName();

        String testToWrite = String.format("--tests %s.%s*", className, methodName);

        context.getExecutionException().ifPresent(x -> failedTests.add(testToWrite));
    }
}
