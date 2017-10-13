//import io.opsguru.ci.OGLogging
//import io.opsguru.ci.OGLoggingLevel
//import io.opsguru.ci.Utilities
import io.opsguru.ci.JenkinsSlaveHelper

stage ('Test:JenkinsSlaveHelper') {

	def JSHelper = new JenkinsSlaveHelper(this)
//	def credentials_test = [:]
//	credentials_test << [key: "value"]
//	result = JCHelper.genCredsMapById(credentials_test)
//	result = JCHelper.genCredsMapByDescription(credentials_test)
//	echo "result: ${result}"

}
