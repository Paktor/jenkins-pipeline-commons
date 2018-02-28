#!/usr/bin/env groovy

/**
 * Cleanup workspace
 *
 * @param commands
 */
def call() {
    stage('Cleanup') {
        step([$class: 'WsCleanup'])
    }
}
