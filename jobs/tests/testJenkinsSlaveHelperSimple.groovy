//import io.opsguru.ci.utils.OGLogging
//import io.opsguru.ci.utils.OGLoggingLevel
//import io.opsguru.ci.utils.Utilities
import io.opsguru.ci.jenkins.SlaveHelper
import io.opsguru.ci.jenkins.SlaveHelper

stage ('Test:SlaveHelper') {

	def JSHelper = new SlaveHelper(this)
//	def credentials_test = [:]
//	credentials_test << [key: "value"]
//	result = JCHelper.genCredsMapById(credentials_test)
//	result = JCHelper.genCredsMapByDescription(credentials_test)
//	echo "result: ${result}"

}
