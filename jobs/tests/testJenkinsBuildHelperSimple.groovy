import io.opsguru.ci.utils.OGLogging
import io.opsguru.ci.utils.OGLoggingLevel
//import io.opsguru.ci.utils.Utilities
import io.opsguru.ci.jenkins.JenkinsBuildHelper


stage ('Test:JenkinsBuildHelper') {

	def JBHelper = new JenkinsBuildHelper(this)
	JBHelper.updateBuildColumn("label1", "label_JBH")
}