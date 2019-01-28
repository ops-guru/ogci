package io.opsguru.ci.utils

class OGLogging implements Serializable {
    private def script = null
    private def name = null
    private OGLoggingLevel logLevel = OGLoggingLevel.info
    public OGLogging(
            def script,
            String name=null,
            // def fpath=null,
            OGLoggingLevel logLevel=OGLoggingLevel.info) {
        this.script = script
        this.name = 'OGeneLoggingDefault'
        if ((name) && (name.size()>0)) {
            this.name = name
        }
        this.logLevel = logLevel
//        if (fpath) {
//            this.fpath = fpath
//        }
        if (this.logLevel.ordinal() >= OGLoggingLevel.debug.ordinal()) {
            this.script.echo "Current loglevel: ${this.logLevel}"
        }

    }

    private def prefix(def message, OGLoggingLevel prefix=OGLoggingLevel.debug) {
        def prefix_str = prefix.toString()
        if (prefix == OGLoggingLevel.stage) {
            prefix_str = OGLoggingLevel.info.toString()
        }
        if (this.logLevel.ordinal() < prefix.ordinal()) {
            return
        }
        def chain = []
        if (prefix == OGLoggingLevel.stage) {
            chain << '#################################################\n'
            chain << "# STAGE: ${message}\n"
            chain << '#################################################\n'
        }
        else {
            if (prefix.toString().size() > 0) {
                chain << prefix.toString().toUpperCase()
                chain << ': '
            }
            if (message.size() == 0) {
                return
            }
            chain << message
        }

        if (chain.size() == 0) {
            return
        }
        this.script.echo chain.join('')
    }

    public def debug(def message) {
        prefix(message, OGLoggingLevel.debug)
    }

    public def info(def message) {
        prefix(message, OGLoggingLevel.info)
    }
    public def warn(def message) {
        prefix(message, OGLoggingLevel.warn)
    }

    public def error(def message) {
        prefix(message, OGLoggingLevel.error)
    }

    public def fatal(def message) {
        prefix(message, OGLoggingLevel.fatal)
    }

    public def stage(def message) {
        prefix(message, OGLoggingLevel.stage)
    }
}
