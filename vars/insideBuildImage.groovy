#!/usr/bin/env groovy

/**
 * Do some actions inside build image
 *
 * @param scm
 */
def call(actions) {
    def buildImage = docker.image('dr.gopaktor.com/paktor/build-image:latest')

    buildImage.pull()

    withCredentials([
        usernamePassword(credentialsId: 'artifactory-teamcity', usernameVariable: 'MAVEN_USER', passwordVariable: 'MAVEN_PASSWORD')
    ]) {
        buildImage.inside('--cpu-period=100000 --cpu-quota=200000') {
            sh "docker login -u \$MAVEN_USER -p \$MAVEN_PASSWORD dr.gopaktor.com"

            actions.call()
        }
    }
}
