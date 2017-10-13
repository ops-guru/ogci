//import io.opsguru.ci.OGLogging
//import io.opsguru.ci.OGLoggingLevel
//import io.opsguru.ci.Utilities
import io.opsguru.ci.JenkinsCredsHelper


stage ('Test:JenkinsCredsHelper') {

	def JCHelper = new JenkinsCredsHelper(this)
//	def credentials_test = [:]
//	credentials_test << [key: "value"]
//	result = JCHelper.genCredsMapById(credentials_test)
//	result = JCHelper.genCredsMapByDescription(credentials_test)
//	echo "result: ${result}"
}