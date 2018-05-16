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

        def fullImageName = "dr.gopaktor.com/paktor/${imageName}:${env.BUILD_NUMBER}"

    	stage ('Build image') {
            img = docker.build "${fullImageName}"
    	}

    	stage ('Publish image') {
  	    	img.push()
  	    	img.push('latest')
  	    }
    }
}
