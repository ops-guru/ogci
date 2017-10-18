def repoName = 'ogci'
def teamName = 'ops-guru'
def gitServer = 'github.com'
def useRepository = "https://${gitServer}/${teamName}/${repoName}"
def gitSshUrl = "git@${gitServer}:${teamName}/${repoName}.git"
def gitCredentialsId = 'creds_ssh_ghapi'

def job_parameters = []
job_parameters << string(
    name        : 'sha1',
    description : 'git Commit Id (tag/branch)',
    defaultValue: 'master',
)

def job_properties = []

job_properties << [
    $class       : 'GithubProjectProperty',
    displayName  : repoName,
    projectUrlStr: useRepository
]

job_properties << parameters(job_parameters)

// GhprbTrigger ghprbTrigger = new GhprbTrigger(
//     '', //String adminlist
//     '', // String whitelist
//     'ops-guru', // String orgslist
//     'H/2 * * * *', // String cron
//     '', // String triggerPhrase
//     false, // Boolean onlyTriggerPhrase
//     true, // Boolean useGitHubHooks
//     false, // Boolean permitAll,
//     false, // Boolean autoCloseFailedPullRequests
//     false, // Boolean displayBuildErrorsOnDownstreamBuilds
//     '', // String commentFilePath
//     [], // List<GhprbBranch> whiteListTargetBranches
//     true, // Boolean allowMembersOfWhitelistedOrgsAsAdmin
//     'Passed', // String msgSuccess
//     'Failed', // String msgFailure
//     'ogci', // String commitStatusContext
//     'GitHub', // gitCredentialsId, // String gitHubAuthId
//     '', // String buildDescTemplate
//     [], // List<GhprbExtension> extensions
// )

job_properties << pipelineTriggers([])
properties(job_properties)

node() {
    def sha1 = params.sha1
    echo "Testing ogci io.opsguru.ci.at ${params.sha1}"
    if (env.sha1) {
        sha1 = env.sha1
    }
    library([
            identifier: "${repoName}@${sha1}",
            retriever: modernSCM([
                    $class: 'GitSCMSource',
                    credentialsId: 'creds_ssh_ghapi',
                    id: '10e1bf54-4464-4e38-82ae-5af3a65eed8a',
                    remote: gitSshUrl,
                    traits: [
                            [
                                    $class: 'jenkins.plugins.git.traits.BranchDiscoveryTrait'
                            ]
                    ]
            ])
    ])
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
    def testFileNamesOutput = sh([
       script: "find jobs/tests -name test*Simple.groovy",
       returnStdout: true
    ])

    def testFileNamesArr = testFileNamesOutput.tokenize('\n')

    echo "Array of files: ${testFileNamesArr}"
	
	for (def testFileName in testFileNamesArr) {
		load testFileName
	}
}
