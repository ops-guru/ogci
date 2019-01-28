//import io.opsguru.ci.utils.OGLogging
//import io.opsguru.ci.utils.OGLoggingLevel
//import io.opsguru.ci.utils.Utilities
import io.opsguru.ci.terraform.TerraformHelper

stage ('Test:TerraformHelper') {

	def TfHelper = new TerraformHelper(this)
    def fName = "testvars.tfvars"
    def testMap = [:]
    testMap << [key: "value"]
	TfHelper.genTerraformVarsFile(testMap, fName)
//	TfHelper.setupTool("1.4")
}