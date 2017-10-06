@Library([
    'ogci'
]) _
import io.opsguru.ci.Utilities

node() {
    echo "Testing io.opsguru.ci.Utilities"
    stage ('Test') {
        def utils = new Utilities(this)
        def fName = "testvars.tfvars"
        def testMap = [:]
        testMap << [key: "value"]
        utils.map2File(fName, testMap)
        // TODO: actually validate content
    }
}
