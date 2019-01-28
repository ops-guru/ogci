//import io.opsguru.ci.utils.OGLogging
//import io.opsguru.ci.utils.OGLoggingLevel
import io.opsguru.ci.utils.Utilities

stage ('Test:Utilities') {

    def utils = new Utilities(this)
    def fName = "testvars.tfvars"
    def testMap = [:]
    testMap << [key: "value"]
    utils.map2File(fName, testMap)
    // TODO: actually validate content
}
