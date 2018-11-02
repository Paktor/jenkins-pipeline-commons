
def call(spec) {

    def aws_j = {c -> readJSON(text: aws(c) ?: '{}')}

    def params = [:]
    spec.resolveStrategy = Closure.DELEGATE_FIRST
    spec.delegate = params
    spec()

    if (! (params.service && params.vpc && params.subnets && params.composeFile && params.cluster && params.port && params.sg)) {
        throw new IllegalArgumentException("must specify service, vpc, subnets, cluster, container port, security groups and composeFile to create website")
    }

    def subnets = params.subnets.join(' ')
    def region = params.region ?: 'ap-southeast-1'
    def sg = params.sg.join(' ')

    def services = aws_j("ecs list-services --cluster ${params.cluster} --region=${region}")
    boolean found = services.serviceArns?.find{ it.find("/${params.service}\$") } ?: false
    if (!found) {
        println "service ${params.service} not found... creating new one."

        // create a target group for the service to register targets
        def response = aws_j("elbv2 create-target-group --name ${params.service} --protocol HTTP --port 80" +
                " --vpc-id ${params.vpc} --region=${region}")
        def targetsArn = response.TargetGroups[0].TargetGroupArn
        println "created target group for ${params.service}: $targetsArn"

        // create a loadbalancer for this service
        response = aws_j("elbv2 create-load-balancer --name ${params.service} --subnets $subnets --security-groups $sg --region=${region}")
        def loadBalancerArn = response.LoadBalancers[0].LoadBalancerArn
        println "created load balancer for ${params.service}: $loadBalancerArn"

        // create a loadbalancer HTTP listener that forwards to the service targets
        aws("elbv2 create-listener --load-balancer-arn $loadBalancerArn --protocol HTTP --port 80" +
                " --default-actions Type=forward,TargetGroupArn=$targetsArn --region=${region}")
        println "created HTTP listener for ${params.service}: $loadBalancerArn"
        if (params.certificateArn) {
            // create a loadbalancer HTTPS listener that forwards to the service targets
            aws("elbv2 create-listener --load-balancer-arn $loadBalancerArn --protocol HTTPS --port 443" +
                    " --default-actions Type=forward,TargetGroupArn=$targetsArn" +
                    " --certificates CertificateArn=${params.certificateArn} --ssl-policy ELBSecurityPolicy-2016-08 --region=${region}")
            println "created HTTPS listener for ${params.service}: $loadBalancerArn"
        }

        // create the ECS service
        ecs_cli("compose --file=${params.composeFile} --cluster=${params.cluster} --project-name=${params.service}" +
                " service create --target-group-arn=$targetsArn --container-name=${params.service} --container-port=${params.port} --region=${region}")
    } else {
        println "service '${params.service}' is already setup... skipping"
    }
}
