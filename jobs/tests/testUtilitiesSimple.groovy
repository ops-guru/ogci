import io.opsguru.ci.OGLogging
import io.opsguru.ci.OGLoggingLevel
import io.opsguru.ci.Utilities
stage ('Test:Utilities') {

    def utils = new Utilities(this)
    def fName = "testvars.tfvars"
    def testMap = [:]
    testMap << [key: "value"]
    utils.map2File(fName, testMap)
    // TODO: actually validate content
}
