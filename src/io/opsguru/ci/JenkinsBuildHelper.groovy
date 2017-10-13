package io.opsguru.ci

import com.cloudbees.groovy.cps.*
import io.opsguru.ci.OGLogging
import io.opsguru.ci.OGLoggingLevel
import io.opsguru.ci.Utilities

class JenkinsBuildHelper implements Serializable {
    private def script = null
    private OGLogging  logger = null
    public Utilities utilities = null
    public String verbosity = null

    public JenkinsBuildHelper(def script, def logger=null) {
        this.script = script
        this.logger = logger
        this.verbosity = script.env.logLevel
        if (!logger) {
            this.logger = new OGLogging(script, null, OGLoggingLevel.info)
        }
        this.utilities = new Utilities(script, this.logger)
//        this.dry_run = this.getDryRun()
    }

    def updateBuildColumn(String var_name, String var_value, def labelsData = null) {
        if (labelsData == null) {
            labelsData = [
                (var_name): ["black", "lightgreen", "0px", "white"],
            ]
        }
        def actionColorsArr = labelsData[var_name]
// Need 'Groovy Postbuild Plugin' to access script method 'manager'
        this.script.manager.addShortText(
                var_value,
                actionColorsArr[0],
                actionColorsArr[1],
                actionColorsArr[2],
                actionColorsArr[3]
        )

    }
}