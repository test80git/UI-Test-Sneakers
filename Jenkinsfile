pipeline {
    agent any

    parameters {
        string(name: 'MAX_RETRIES', defaultValue: '3', description: 'Количество попыток перезапуска упавших тестов')
        booleanParam(name: 'SKIP_INITIAL_RUN', defaultValue: false, description: 'Пропустить первый запуск всех тестов')
        booleanParam(name: 'GENERATE_ALLURE', defaultValue: true, description: 'Генерировать Allure отчёт')
    }

    environment {
        TELEGRAM_TOKEN = credentials('TELEGRAM_TOKEN')
        TELEGRAM_CHAT_ID = credentials('TELEGRAM_CHAT_ID')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Run Tests with Retry') {
            steps {
                script {
                    sh 'chmod +x gradlew'

                    def maxRetries = params.MAX_RETRIES.toInteger()
                    def currentRun = 0
                    def allTestsPassed = false
                    def failedTestsList = []

                    if (!params.SKIP_INITIAL_RUN) {
                        currentRun++
                        echo "Run ${currentRun}: Running all tests"
                        sh './gradlew clean test'
                    }

                    while (currentRun < maxRetries) {
                        def failedTestsFile = 'build/failed-tests.txt'
                        if (!fileExists(failedTestsFile)) {
                            allTestsPassed = true
                            break
                        }

                        def failedTests = readFile(failedTestsFile).trim()
                        if (failedTests.isEmpty()) {
                            allTestsPassed = true
                            break
                        }

                        currentRun++
                        echo "Run ${currentRun}: Retrying failed tests: ${failedTests}"
                        failedTestsList.add(failedTests)
                        sh "./gradlew test ${failedTests}"
                    }

                    if (!allTestsPassed && currentRun >= maxRetries) {
                        echo "⚠️ Tests still failing after ${maxRetries} attempts"
                        currentBuild.result = 'UNSTABLE'
                    } else {
                        echo "✅ All tests passed after ${currentRun} run(s)"
                    }
                }
            }
        }
    }

    post {
        always {
            script {
                if (params.GENERATE_ALLURE) {
                    allure includeProperties: false, results: [[path: 'build/allure-results']]
                }
                archiveArtifacts artifacts: 'build/failed-tests.txt', allowEmptyArchive: true
            }
        }

        success {
            script {
                def duration = currentBuild.durationString.replace(' and counting', '')

                // Email уведомление
                emailext (
                        subject: "✅ SUCCESS: ${env.JOB_NAME} - Build #${env.BUILD_NUMBER}",
                        body: """
                        Build completed successfully!
                        
                        Job: ${env.JOB_NAME}
                        Build number: ${env.BUILD_NUMBER}
                        Duration: ${duration}
                        
                        All tests passed!
                        
                        View build: ${env.BUILD_URL}
                        Allure report: ${env.BUILD_URL}allure/
                    """,
                        to: 'smith80java@gmail.com',
                        from: 'jenkins@gmail.com'
                )

                // Telegram уведомление
                def telegramMsg = """
                ✅ *BUILD SUCCESSFUL* ✅
                *Job*: ${env.JOB_NAME}
                *Build*: #${env.BUILD_NUMBER}
                *Duration*: ${duration}
                [View Logs](${env.BUILD_URL})
                """
                sh """
                    curl -s -X POST https://api.telegram.org/bot${TELEGRAM_TOKEN}/sendMessage \
                    -d chat_id=${TELEGRAM_CHAT_ID} \
                    -d text="${telegramMsg}"
                """
            }
        }

        failure {
            script {
                def duration = currentBuild.durationString.replace(' and counting', '')

                // Email уведомление
                emailext (
                        subject: "❌ FAILED: ${env.JOB_NAME} - Build #${env.BUILD_NUMBER}",
                        body: """
                        Build failed!
                        
                        Job: ${env.JOB_NAME}
                        Build number: ${env.BUILD_NUMBER}
                        Duration: ${duration}
                        
                        Check logs for details: ${env.BUILD_URL}
                        Allure report: ${env.BUILD_URL}allure/
                    """,
                        to: 'smith80java@gmail.com',
                        from: 'jenkins@gmail.com'
                )

                // Telegram уведомление
                def telegramMsg = """
                ❌ *BUILD FAILED* ❌
                *Job*: ${env.JOB_NAME}
                *Build*: #${env.BUILD_NUMBER}
                *Duration*: ${duration}
                [View Logs](${env.BUILD_URL})
                """
                sh """
                    curl -s -X POST https://api.telegram.org/bot${TELEGRAM_TOKEN}/sendMessage \
                    -d chat_id=${TELEGRAM_CHAT_ID} \
                    -d text="${telegramMsg}"
                """
            }
        }

        unstable {
            script {
                // Email уведомление для нестабильной сборки
                emailext (
                        subject: "⚠️ UNSTABLE: ${env.JOB_NAME} - Build #${env.BUILD_NUMBER}",
                        body: """
                        Tests are unstable!
                        
                        Job: ${env.JOB_NAME}
                        Build number: ${env.BUILD_NUMBER}
                        
                        Some tests failed after ${params.MAX_RETRIES} attempts.
                        
                        Check Allure report: ${env.BUILD_URL}allure/
                        View build: ${env.BUILD_URL}
                    """,
                        to: 'smith80java@gmail.com',
                        from: 'jenkins@gmail.com'
                )

                // Telegram уведомление
                def telegramMsg = "⚠️ *TESTS UNSTABLE* ⚠️\nJob: ${env.JOB_NAME}\nBuild: #${env.BUILD_NUMBER}\nSome tests failed after ${params.MAX_RETRIES} attempts.\n[View Logs](${env.BUILD_URL})"
                sh """
                    curl -s -X POST https://api.telegram.org/bot${TELEGRAM_TOKEN}/sendMessage \
                    -d chat_id=${TELEGRAM_CHAT_ID} \
                    -d text="${telegramMsg}"
                """
            }
        }
    }
}