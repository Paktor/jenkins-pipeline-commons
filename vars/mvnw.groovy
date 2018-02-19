#!/usr/bin/env groovy

/**
 * Execute maven wrapper
 *
 * @param commands
 */
def call(commands, collectTests = false) {
    try {
        withCredentials([
                usernamePassword(credentialsId: 'artifactory-teamcity', usernameVariable: 'MAVEN_USER', passwordVariable: 'MAVEN_PASSWORD')
        ]) {
            sh "./mvnw -B -U $commands"
        }
    } finally {
        if (collectTests) {
            junit testResults: "**/target/surefire-reports/junitreports/TEST-*.xml", allowEmptyResults: true
            junit testResults: "**/target/failsafe-reports/junitreports/TEST-*.xml", allowEmptyResults: true
        }
    }
}

