#!/usr/bin/env groovy

/**
 * Build and publish docker image. Image name comes from jenkins job
 *
 */
def call() {
    paktorCheckout scm

  	ws (pwd() + "/docker-${env.JOB_NAME}") {
		def img

    	stage ('Build image') {
        	img = docker.build "dr.gopaktor.com/paktor/${env.JOB_NAME}:${env.BUILD_NUMBER}"
    	}

    	stage ('Publish image') {
  	    	img.push()
  	    	img.push('latest')
  	    }
  	}
}
