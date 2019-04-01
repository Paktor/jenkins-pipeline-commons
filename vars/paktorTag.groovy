#!/usr/bin/env groovy

/**
 * Create an push an annotated tag for a paktor build
 *
 * @param tag name to tag build with 
 */
def call(tag) {

  sh "git tag -m 'master build: $tag' -a '$tag'"

  sshagent(['99cf5548-0170-462e-b900-bae5e26e56cd']) {
    sh "git push origin $tag"
  }
}
