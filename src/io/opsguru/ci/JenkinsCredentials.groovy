package io.opsguru.ci

import com.cloudbees.groovy.cps.*
import io.opsguru.ci.OGLogging
import io.opsguru.ci.OGLoggingLevel
import io.opsguru.ci.Utilities

class JenkinsCredentials implements Serializable {

    private def script = null
    private OGLogging  logger = null
    public Utilities utilities = null
    public String verbosity = null



    JenkinsCredentials(def script, def logger=null) {
        this.script = script
        this.logger = logger
        this.verbosity = script.env.logLevel
        if (!logger) {
            def level = (script.env.logLevel!=null)?script.env.logLevel:OGLoggingLevel.info
            this.logger = new OGLogging(script, null, level)
        }
        this.utilities = new Utilities(script, this.logger)
        this.dry_run = this.getDryRun()
    }


}
