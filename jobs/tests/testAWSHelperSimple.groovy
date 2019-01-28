//import io.opsguru.ci.utils.OGLogging
//import io.opsguru.ci.utils.OGLoggingLevel
//import io.opsguru.ci.utils.Utilities
import io.opsguru.ci.AWSHelper

stage ('Test:AWSHelper') {

    def c_AWSHelper = new AWSHelper(this)

}
