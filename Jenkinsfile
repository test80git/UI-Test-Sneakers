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

        stage('First Run') {
            steps {
                script {
                    sh 'chmod +x gradlew'
                    catchError(buildResult: 'SUCCESS', stageResult: 'UNSTABLE') {
                        sh './gradlew clean test -Drun.id=1'
                    }
                }
            }
        }

        stage('Retry Failed') {
            steps {
                script {
                    def failedTestsFile = 'build/failed-tests.txt'
                    if (fileExists(failedTestsFile)) {
                        def failedTests = readFile(failedTestsFile).trim()
                        if (failedTests) {
                            sh 'chmod +x gradlew'
                            catchError(buildResult: 'SUCCESS', stageResult: 'UNSTABLE') {
                                sh "./gradlew test ${failedTests} -Drun.id=2"
                            }
                        }
                    }
                }
            }
        }

    }

    post {
        always {
            script {
                if (params.GENERATE_ALLURE) {
                 // Объединяем результаты из всех запусков
                                sh '''
                                    mkdir -p build/allure-results-final
                                    cp -r build/allure-results-1/* build/allure-results-final/ 2>/dev/null || true
                                    cp -r build/allure-results-2/* build/allure-results-final/ 2>/dev/null || true
                                '''
                    allure includeProperties: false, results: [[path: 'build/allure-results-final']]
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
                        to: 'smith-m-80@mail.ru',
                        from: 'smith-m-80@mail.ru'
                )

                // Telegram уведомление
                def telegramMsg = """
                ✅ *BUILD SUCCESSFUL* ✅
                *Job*: ${env.JOB_NAME}
                *Build*: #${env.BUILD_NUMBER}
                *Duration*: ${duration}
                [View Logs](${env.BUILD_URL})
                """
                /*
                sh """
                    curl -s -X POST https://api.telegram.org/bot${TELEGRAM_TOKEN}/sendMessage \
                    -d chat_id=${TELEGRAM_CHAT_ID} \
                    -d text="${telegramMsg}"
                """
                */
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
                        to: 'smith-m-80@mail.ru',
                        from: 'smith-m-80@mail.ru'
                )

                // Telegram уведомление
                def telegramMsg = """
                ❌ *BUILD FAILED* ❌
                *Job*: ${env.JOB_NAME}
                *Build*: #${env.BUILD_NUMBER}
                *Duration*: ${duration}
                [View Logs](${env.BUILD_URL})
                """
                /*
                sh """
                    curl -s -X POST https://api.telegram.org/bot${TELEGRAM_TOKEN}/sendMessage \
                    -d chat_id=${TELEGRAM_CHAT_ID} \
                    -d text="${telegramMsg}"
                """
                */
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
                        to: 'smith-m-80@mail.ru',
                        from: 'smith-m-80@mail.ru'
                )

                // Telegram уведомление
                def telegramMsg = "⚠️ *TESTS UNSTABLE* ⚠️\nJob: ${env.JOB_NAME}\nBuild: #${env.BUILD_NUMBER}\nSome tests failed after ${params.MAX_RETRIES} attempts.\n[View Logs](${env.BUILD_URL})"
              /*
                sh """
                    curl -s -X POST https://api.telegram.org/bot${TELEGRAM_TOKEN}/sendMessage \
                    -d chat_id=${TELEGRAM_CHAT_ID} \
                    -d text="${telegramMsg}"
                """
                */
            }
        }
    }
}