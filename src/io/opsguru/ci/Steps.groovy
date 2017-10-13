package io.opsguru.ci

import io.opsguru.ci.OGLogging
import io.opsguru.ci.OGLoggingLevel

class Steps implements Serializable {

    private def script = null
    private def logger = null

    public Steps(def script, def logger=null) {
        this.script = script
        this.logger = logger
        if (!logger) {
            this.logger = new OGLogging(script, null, OGLoggingLevel.info)
        }
    }

	public void svn_checkout(def credentialsId,
                         def realm,
                         def repo_root_url,
                         def repo_folder = null,
                         def local_folder = null) {
		if (!repo_folder) {
			repo_folder = 'trunk'
		}
		if (!local_folder) {
			local_folder = ''
		}
		def remote_repo = [repo_root_url, repo_folder].join('/')
		checkout([
				$class                : 'SubversionSCM',
				poll                  : poll,
				additionalCredentials : [
						[
								credentialsId: credentialsId,
								realm        : realm
						]
				],
				excludedCommitMessages: '',
				excludedRegions       : '',
				excludedRevprop       : '',
				excludedUsers         : '',
				filterChangelog       : false,
				ignoreDirPropChanges  : true,
				includedRegions       : repo_folder,
				locations             : [
						[
								credentialsId        : credentialsId,
								depthOption          : 'infinity',
								ignoreExternalsOption: true,
								local                : local_folder,
								remote               : remote_repo
						]
				],
				workspaceUpdater      : [
						$class: 'UpdateUpdater'
				]
		])
	}

}
