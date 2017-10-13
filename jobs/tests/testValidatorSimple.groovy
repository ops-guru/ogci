//import io.opsguru.ci.OGLogging
//import io.opsguru.ci.OGLoggingLevel
import io.opsguru.ci.Utilities
import io.opsguru.ci.Validator

stage ('Test:Validator') {

    def c_Validator = new Validator(this)

    def utils = new Utilities(this)
    def c_property = "logger"
    result = c_Validator.validateObjProperty(utils, c_property)
	echo "Validator.validateObjProperty_result: ${result}"
}
