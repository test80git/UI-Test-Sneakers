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
        MAIL_PASSWORD = credentials('MAIL_PASSWORD')
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
                        sh '/opt/gradle/bin/gradle clean test -Drun.id=1'
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
                                sh "/opt/gradle/bin/gradle test ${failedTests} -Drun.id=2"
                            }
                        }
                    }
                }
            }
        }

//         stage('Send Email') {
//             steps {
//                 withCredentials([string(credentialsId: 'MAIL_PASSWORD', variable: 'MAIL_PASSWORD')]) {
//                     sh '''
//                         chmod +x send_email.sh
//                         ./send_email.sh "Test Subject" "Test Body"
//                     '''
//                 }
//             }
//         }

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
                def subject = "SUCCESS: ${env.JOB_NAME} - Build #${env.BUILD_NUMBER}"
                def body = "All tests passed successfully!"
                def allureUrl = "${env.BUILD_URL}allure/"

                // Email уведомление
                sh """
                                 chmod +x send_email.sh
                                 ./send_email.sh "${subject}" "${body}" "${allureUrl}"
                             """

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
                def subject = "❌ FAILED: ${env.JOB_NAME} - Build #${env.BUILD_NUMBER}"
                def body = "Build failed! Check logs for details."
                def allureUrl = "${env.BUILD_URL}allure/"

              sh '''
                                chmod +x send_email.sh
                                ./send_email.sh "${subject}" "${body}" "${allureUrl}"
                            '''

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
                def subject = "⚠️ UNSTABLE: ${env.JOB_NAME} - Build #${env.BUILD_NUMBER}"
                def body = "Some tests failed after ${params.MAX_RETRIES} attempts. Check Allure report."
                def allureUrl = "${env.BUILD_URL}allure/"

                sh '''
                    chmod +x send_email.sh
                    ./send_email.sh "${subject}" "${body}" "${allureUrl}"
                '''

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