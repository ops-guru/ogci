import io.opsguru.ci.OGLogging
import io.opsguru.ci.OGLoggingLevel

stage ('Test:OGLogging') {

	def OGlogger = new OGLogging(this, null, OGLoggingLevel.info)
	LogMsg = "OGLogging_Test_Message level:info"
	OGlogger.info(LogMsg)
}