//import io.opsguru.ci.OGLogging
//import io.opsguru.ci.OGLoggingLevel
//import io.opsguru.ci.Utilities
import io.opsguru.ci.AWSHelper

stage ('Test:AWSHelper') {

    def c_AWSHelper = new AWSHelper(this)

}
