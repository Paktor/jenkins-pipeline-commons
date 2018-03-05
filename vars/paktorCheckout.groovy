#!/usr/bin/env groovy

/**
 * Checkout with all lfs actions and submodules
 *
 * @param scm
 */
def call(scm) {
    stage('Checkout') {
        step([$class: 'WsCleanup'])

        checkout([
                $class: 'GitSCM',
                branches: scm.branches,
                extensions: scm.extensions + [[$class: 'GitLFSPull'], [$class: 'SubmoduleOption', parentCredentials: true, recursiveSubmodules: true]],
                userRemoteConfigs: scm.userRemoteConfigs
        ])
    }
}
