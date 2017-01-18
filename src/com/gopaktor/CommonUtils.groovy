package com.gopaktor

def sendSlackNotification(build, channel, teamDomain, credentialsId) {
    def changeLog = getChangeLog()
    def color = getColor(build)
    def slackMessage = getMessage(build, changeLog)
    withCredentials([[$class: 'StringBinding', credentialsId: credentialsId,
                      variable: 'SLACK_TOKEN']]) {
        slackSend channel: "${channel}", color: "${color}", message: "${slackMessage}", teamDomain: "${teamDomain}", token: "${env.SLACK_TOKEN}"
    }
}

def getMessage(build, changeLog, failReason) {
    def message = "Project - ${env.JOB_NAME}, Status - ${build.result}\nCommits :\n${changeLog}"
    if (build.result == 'FAILURE') {
        message = message + "\nReason - ${failReason}"
    }
    return message
}

def getMessage(build, changeLog) {
    return "Project - ${env.JOB_NAME}, Status - ${build.result}\nCommits :\n${changeLog}"
}

def getColor(build) {
    if (build.result == 'SUCCESS') {
        return "#4e8c23"
    }
    return "#8b0f04"
}

@NonCPS
def getChangeLog() {
    MAX_MSG_LEN = 100
    def changeString = ""

    def changeLogSets = currentBuild.rawBuild.changeSets
    for (int i = 0; i < changeLogSets.size(); i++) {
        def entries = changeLogSets[i].items
        for (int j = 0; j < entries.length; j++) {
            def entry = entries[j]
            truncated_msg = entry.msg.take(MAX_MSG_LEN)
            changeString += " - ${truncated_msg} [${entry.author}]\n"
        }
    }

    if (!changeString) {
        changeString = " - No new changes\n"
    }
    return changeString
}