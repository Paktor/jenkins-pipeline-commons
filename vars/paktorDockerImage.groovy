#!/usr/bin/env groovy

/**
 * Build and publish docker image. Image name comes from jenkins job
 *
 */
def call(repository = 'dr.gopaktor.com/paktor') {
    paktorCheckout scm

    def imageName = (env.JOB_NAME =~ /docker-images\//).replaceFirst('')

  	ws(pwd() + "/docker-${imageName}") {
		def img

    	stage ('Build image') {
            img = docker.build "${repository}/${imageName}:${env.BUILD_NUMBER}"
    	}

    	stage ('Publish image') {
  	    	img.push()
  	    	img.push('latest')
  	    }
    }
}
