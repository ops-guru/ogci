package io.opsguru.ci

import com.cloudbees.groovy.cps.*
import io.opsguru.ci.OGLogging
import io.opsguru.ci.OGLoggingLevel
import io.opsguru.ci.Utilities

class JenkinsCredsHelper  implements Serializable {
    private def script = null
    private OGLogging  logger = null
    public Utilities utilities = null
    public String verbosity = null

    public JenkinsCredsHelper(def script, def logger=null) {
        this.script = script
        this.logger = logger
        this.verbosity = script.env.logLevel
        if (!logger) {
            def level = (script.env.logLevel!=null)?script.env.logLevel:OGLoggingLevel.info
            this.logger = new OGLogging(script, null, level)
        }
        this.utilities = new Utilities(script, this.logger)
    }

    @NonCPS
    def getCredentialUsernameById(def credentialId) {
        this.logger.debug("in getCredentialUsernameById(${credentialId})")
        def credentials_store = jenkins.model.Jenkins.instance.getExtensionList(
                'com.cloudbees.plugins.credentials.SystemCredentialsProvider'
        ) as ArrayList
        for (store_item in credentials_store) {
            this.logger.debug("store_item: ${store_item}")
            for (cred_item in store_item.credentials) {
                if (cred_item.id == credentialId) {
                    this.logger.debug("credentials with id: '${credentialId}' matches: '${cred_item}'")
                    return cred_item.username
                }
            }
        }
        return null
    }


    public def genCredsMapById(def credentials_defaults=CREDENTIALS_DEFAULT) {
        def result = [:]

        //echo "credentials_defaults: ${credentials_defaults}"
        def keys = credentials_defaults.keySet() as ArrayList
        for (def key in keys) {
            def value = credentials_defaults.get(key)
            def param = [key.toUpperCase(), 'CREDENTIALS_ID'].join('_')
            if (this.utilities.resolveVar(param)) {
                value = this.utilities.resolveVar(param)
            }
            result[key] = value
        }
        //echo "result: ${result}"
        return result
    }

    @NonCPS
    public def getCredsIdByDescription(def description) {
        def creds = com.cloudbees.plugins.credentials.CredentialsProvider.lookupCredentials(
                com.cloudbees.plugins.credentials.Credentials.class,
                jenkins.model.Jenkins.instance,
                null,
                null
        ) as ArrayList
        for (c in creds) {
            if (description == c.description) {
                return c.id
            }
        }
        return null
    }

    def genCredsMapByDescription(def credentials_defaults=[:]) {
        def result = [:]
        if (credentials_defaults == [:]) {
            credentials_defaults = Defaults.CREDENTIALS_DESCRIPTIONS
        }

        //echo "credentials_defaults: ${credentials_defaults}"
        def keys = credentials_defaults.keySet() as ArrayList
        for (key in keys) {
            def value = getCredsIdByDescription(credentials_defaults[key])
            result[key] = value
        }
        //echo "result: ${result}"
        return result
    }

}
