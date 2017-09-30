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


    public void svn_checkout(def app_name, def repo_folder, def local_folder, def poll=true, def dataMap=[:]) {

        assert app_name : "cannot have a null app_name"
        assert dataMap[app_name].url != null : "cannot have a null url"
        assert dataMap[app_name].url != "" : "cannot have an empty url"
        def repo_url = dataMap[app_name].url
        def remote_repo = [repo_url, repo_folder].join('/')
        def scm_fqdn = getSCMServerFQDN(repo_url)
        def scm_realm = getAuthRealm(scm_fqdn)
        def credentialsId = OdiRepositoriesMap[app_name].credentialsId

        checkout([
                $class                : 'SubversionSCM',
                poll                  : poll,
                additionalCredentials : [
                        [
                                credentialsId: credentialsId,
                                realm        : "<https://${scm_fqdn}:443> ${scm_realm}"
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
