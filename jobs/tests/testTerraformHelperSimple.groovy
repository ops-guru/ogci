//import io.opsguru.ci.OGLogging
//import io.opsguru.ci.OGLoggingLevel
//import io.opsguru.ci.Utilities
import io.opsguru.ci.TerraformHelper

stage ('Test:TerraformHelper') {

	def TfHelper = new TerraformHelper(this)
    def fName = "testvars.tfvars"
    def testMap = [:]
    testMap << [key: "value"]
	TfHelper.genTerraformVarsFile(testMap, fName)
//	TfHelper.setupTool("1.4")
}