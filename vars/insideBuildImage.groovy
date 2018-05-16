#!/usr/bin/env groovy

/**
 * Do some actions inside build image
 *
 * @param scm
 */
def call(actions) {
    def buildImage = docker.image('dr.gopaktor.com/paktor/build-image:latest')

    buildImage.pull()

    buildImage.inside('--cpu-period=100000 --cpu-quota=200000') {
        actions.call()
    }
}
