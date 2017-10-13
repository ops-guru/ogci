def repoName = 'ogci'
def teamName = 'ops-guru'
def gitServer = 'github.com'
def useRepository = "https://${gitServer}/${teamName}/${repoName}"
// ------------ Change for local Git ------------
//def gitSshUrl = "git@${gitServer}:${teamName}/${repoName}.git"
def gitSshUrl = "D:\\git\\ogci"
// ----------------------------------------------
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
// ------------ Change for local Git ------------
/*					
                    credentialsId: 'creds_ssh_ghapi',
                    id: '10e1bf54-4464-4e38-82ae-5af3a65eed8a',
                    remote: gitSshUrl,
                    traits: [
                            [
                                    $class: 'jenkins.plugins.git.traits.BranchDiscoveryTrait'
                            ]
                    ]
*/
					credentialsId: '',
					id: 'ab97b430-3bb5-4b96-a4e6-5572a6ca275a',
//					remote: 'D:\\git\\ogci'
					remote: gitSshUrl
// ----------------------------------------------
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
// ------------ Change for local Git ------------
/*
                        [
                                credentialsId: gitCredentialsId,
                                url: gitSshUrl,
                                name: 'origin',
                                refspec: "+refs/heads/*:refs/remotes/origin/* +refs/pull/*:refs/remotes/origin/pr/*"
                        ]
*/
						[url: gitSshUrl]
// ----------------------------------------------
                ]
        ])
    }
// ------------- Change for windows -------------
/*
    def groovies = sh([
       script: "find jobs/tests -name test*Simple.groovy",
       returnStdout: true
    ])

    def grooviesArr = groovies.tokenize('\n')
*/
    def groovies = bat([
       script: "dir jobs\\tests\\test*Simple.groovy /B",
       returnStdout: true
    ])
//	echo "Bat result: _${groovies}_"

    def grooviesArr_ = groovies.tokenize('\r\n')	// WinEOL == 0D0A == '\r\n'
//	echo "Bat tokenized: _${grooviesArr_}_"
//	for (def sToken in grooviesArr_) {println "Token_${sToken}_"}

	def j = grooviesArr_.size()
	def grooviesArr = []
	if (j > 0) {	// Remove List[0] = Bat command line
		for (i = 1; i < j; i++) {
			grooviesArr.add("jobs\\tests\\" + grooviesArr_[i])
		}
	}
//			println "[_${grooviesArr_[i]}_]"
// ----------------------------------------------

    echo "Array of files: ${grooviesArr}"
	
	for (def groovyPath in grooviesArr) {
		load groovyPath
	}
}
