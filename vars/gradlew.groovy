#!/usr/bin/env groovy

/**
 * Use gradle wrapper for builds.
 *
 * @param commands
 */
def call(commands, collectTests = false) {
    try {
        withCredentials([
                usernamePassword(credentialsId: 'artifactory-teamcity', usernameVariable: 'MAVEN_USER', passwordVariable: 'MAVEN_PASSWORD')
        ]) {
            sh "./gradlew -PmavenUser=\$MAVEN_USER -PmavenPassword=\$MAVEN_PASSWORD $commands"
        }
    } finally {
        if (collectTests) {
            junit testResults: '**/build/test-results/test/TEST-*.xml', allowEmptyResults: true, keepLongStdio: true
        }
    }
}
