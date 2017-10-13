import io.opsguru.ci.OGLogging
import io.opsguru.ci.OGLoggingLevel
//import io.opsguru.ci.Utilities
import io.opsguru.ci.JenkinsBuildHelper


stage ('Test:JenkinsBuildHelper') {

	def JBHelper = new JenkinsBuildHelper(this)
	JBHelper.updateBuildColumn("label1", "label_JBH")
}