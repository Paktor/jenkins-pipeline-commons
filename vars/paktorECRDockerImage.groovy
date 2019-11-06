#!/usr/bin/env groovy

/**
 * Build and publish ECR docker images.
 * Supports multi-regions but by default publishes to Singapore (ap-southeast-1)
 */
def call(regions = ['ap-southeast-1'], accountId = '373337940780') {
    paktorCheckout scm

    def imageName = (env.JOB_NAME =~ /docker-images\//).replaceFirst('')

    ws(pwd() + "/docker-${imageName}") {
        regions.each{ region ->
            stage("Build and publish image for $region") {

                def img = docker.build "${accountId}.dkr.ecr.${region}.amazonaws.com/${imageName}:${env.BUILD_NUMBER}"

                aws_ecr_login("--no-include-email --region=$region")

                img.push()
                img.push('latest')
            }
        }
    }
}
