#!/usr/bin/env groovy

/**
 * Create an push an annotated tag for a paktor build
 *
 * @param tag name to tag build with 
 */
def call(tag) {

  sh 'git config --local credential.helper "!p() { echo username=\\$GIT_USERNAME; echo password=\\$GIT_PASSWORD; }; p"'

  sh "git tag -m 'master build: $tag' -a '$tag'"
  withCredentials([
    usernamePassword(credentialsId: 'github', usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_PASSWORD')
  ]) {
    sh "git push origin $tag"
  }
}
