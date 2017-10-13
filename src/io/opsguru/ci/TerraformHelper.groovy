package io.opsguru.ci

import io.opsguru.ci.OGLogging
import io.opsguru.ci.OGLoggingLevel
import io.opsguru.ci.Utilities


class TerraformHelper implements Serializable {
    private def script = null
    private def utilities = null
    public def logger = null

    public TerraformHelper(def script, def logger=null) {
        this.script = script
        this.logger = logger
        if (!logger) {
            this.logger = new OGLogging(script, null, OGLoggingLevel.info)
        }
		this.utilities = new Utilities(script)
    }


    public def genTerraformVarsFile(def data = [:], def fname) {
        def sep = ' = '
        def quote_char = '"'
        def quote = true
        this.utilities.map2File(fname, data, sep, quote_char, quote)

    }

    public def setupTool(def version){

        if (this.script.env.DEFAULT_TF_VERSION) {
            version = this.script.env.DEFAULT_TF_VERSION
        }
        def TF_TOOL_NAME = ['TF', version].join('-')
        this.logger.debug("Setting up a terraform tool name: ${TF_TOOL_NAME}")
        def result = this.script.tool([
                name: TF_TOOL_NAME,
                type: 'org.jenkinsci.plugins.terraform.TerraformInstallation'
        ])
        return result
    }
}
