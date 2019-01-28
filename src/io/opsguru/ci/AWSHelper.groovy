package io.opsguru.ci

import io.opsguru.ci.utils.OGLogging
import io.opsguru.ci.utils.OGLoggingLevel
import io.opsguru.ci.utils.Utilities

//import io.opsguru.ci.Runner


class AWSHelper implements Serializable {
    private def script = null
    private def utilities = null
//    private def runner = null
    public def logger = null

    public AWSHelper(def script, def logger=null) {
        this.script = script
        this.logger = logger
        if (!logger) {
            this.logger = new OGLogging(script, null, OGLoggingLevel.info)
        }
        this.utilities  = new Utilities(script, this.logger)
//        this.runner  = new Runner(script, this.logger)
    }



}
