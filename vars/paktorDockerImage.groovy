#!/usr/bin/env groovy

/**
 * Build and publish docker image. Image name comes from jenkins job
 *
 */
def call(repository = 'dr.gopaktor.com/paktor', regions=['ap-southeast-1']) {
    paktorCheckout scm

    def imageName = (env.JOB_NAME =~ /docker-images\//).replaceFirst('')

  	ws(pwd() + "/docker-${imageName}") {
		def img

        def fullImageName = "${repository}/${imageName}:${env.BUILD_NUMBER}"

    	stage ('Build image') {
            img = docker.build "${fullImageName}"
    	}

		regions.each{ region ->
			stage ("Publish image for $region") {
				aws_ecr_login("--no-include-email --region=$region")

				img.push()
				img.push('latest')
			}
		}
    }
}
