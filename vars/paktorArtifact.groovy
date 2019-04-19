#!/usr/bin/env groovy

/**
 * Do simple build for gradle project
 *
 * @param scm
 */
def call(commands) {
    try {
        insideBuildImage {
            paktorCheckout scm

            stage('Build artifact') {
                ws(pwd() + "/${env.JOB_NAME}") {
                    gradlew commands
                }
            }
        }

        currentBuild.result = 'SUCCESS'
    } catch (Exception e) {
        currentBuild.result = 'FAILURE'
        currentBuild.description = "$e"
    }
}
