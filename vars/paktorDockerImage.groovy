#!/usr/bin/env groovy

/**
 * Build and publish docker image. Image name comes from jenkins job
 *
 */
def call() {
    paktorCheckout scm

    def imageName = (env.JOB_NAME =~ /docker-images\//).replaceFirst('')

  	ws(pwd() + "/docker-${imageName}") {
		def img

        def imgRepoName = "dr.gopaktor.com/paktor/${imageName}"

    	stage ('Build image') {
        	img = docker.build "${imgRepoName}:${env.BUILD_NUMBER}"
    	}

    	stage ('Publish image') {
  	    	img.push()
  	    	img.push('latest')
  	    }

        stage ('Clean image') {
            sh "docker rmi ${imgRepoName}"
        }
  	}
}
