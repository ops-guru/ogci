@Library([
    'ogci@bugs/1'
]) _
import io.opsguru.ci.Utilities

def useRepository = 'https://github.com/ops-guru/ogci'
def gitSshUrl = 'git@github.com:ops-guru/ogci.git'
def gitCredentialsId = 'creds_ssh_ghapi'

def job_parameters = []
job_parameters << string(
    name: 'sha1',
    description: 'git Commit Id (tag/branch)',
    defaultValue: 'master',
)

def job_properties = []

job_properties << [
    $class: 'GithubProjectProperty',
    displayName: '', 
    projectUrlStr: "${useRepository}/"
]


job_properties << parameters(job_parameters)

job_properties << pipelineTriggers([
        githubPush(),
        pollSCM('H/1 * * * *')
])

properties(job_properties)


node() {
    echo "Testing io.opsguru.ci.Utilities at ${params.GIT_COMMIT}"
    def sha1 = params.sha1
    if (env.sha1) {
        sha1 = env.sha1
    }
    stage ('Checkout') {
        checkout([
            $class: 'GitSCM',
            branches: [
                [name: "${sha1}"]
            ],
            doGenerateSubmoduleConfigurations: false,
            extensions: [],
            submoduleCfg: [],
            userRemoteConfigs: [
                [
                    credentialsId: gitCredentialsId,
                    url: gitSshUrl,
                    name: 'origin',
                    refspec: "+refs/heads/*:refs/remotes/origin/* +refs/pull/*:refs/remotes/origin/pr/*"
                ]
            ]
        ])

    }
    stage ('Test') {
        def utils = new Utilities(this)
        def fName = "testvars.tfvars"
        def testMap = [:]
        testMap << [key: "value"]
        utils.map2File(fName, testMap)
        // TODO: actually validate content
    }
}
