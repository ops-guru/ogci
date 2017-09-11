package io.opsguru.ci

import com.cloudbees.groovy.cps.*
import io.opsguru.ci.OGLoggingLevel

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
        if (this.logLevel.ordinal() >= OGLoggingLevel.valueOf('debug').ordinal()) {
            this.script.echo "Current loglevel: ${this.logLevel.toString()}"
        }

    }

    private def prefix(def message, String prefix='debug') {
        def prefix_str = prefix
        if (prefix == 'stage') {
            prefix_str = 'info'
        }
        if (this.logLevel.ordinal() < OGLoggingLevel.valueOf(prefix_str).ordinal()) {
            return
        }
        def chain = []
        if (prefix == 'stage') {
            chain << '#################################################\n'
            chain << "# STAGE: ${message}\n"
            chain << '#################################################\n'
        }
        else {
            if (prefix.size() > 0) {
                chain << prefix.toUpperCase()
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
        prefix(message, "debug")
    }

    public def info(def message) {
        prefix(message, "info")
    }
    public def warn(def message) {
        prefix(message, "warn")
    }

    public def error(def message) {
        prefix(message, "error")
    }

    public def fatal(def message) {
        prefix(message, "fatal")
    }

    public def stage(def message) {
        prefix(message, "stage")
    }
}
