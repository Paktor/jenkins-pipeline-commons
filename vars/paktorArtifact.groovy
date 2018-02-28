#!/usr/bin/env groovy

/**
 * Do simple build for gradle project
 *
 * @param scm
 */
def call(dir, commands) {
    try {
        docker.image('dr.gopaktor.com/paktor/build-image:latest').inside {
            paktorCheckout scm

            stage('Build artifact') {
                ws(pwd() + "/${dir}") {
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
