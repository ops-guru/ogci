package io.opsguru.ci


import groovy.json.*
import com.cloudbees.groovy.cps.*
import static java.util.UUID.randomUUID
import io.opsguru.ci.OGLogging
import io.opsguru.ci.OGLoggingLevel
import io.opsguru.ci.Validator

class Utilities implements Serializable {

    private static def AuthRealms = [
        assembla: "Assembla Restricted Area",
        riouxsvn: "RiouxSVN",
    ]
    private def script = null
    private def validator = null
    public def logger = null

    public Utilities(def script, def logger=null) {
        this.script = script
        this.logger = logger
        if (!logger) {
            this.logger = new OGLogging(script, null, OGLoggingLevel.info)
        }
        this.validator  = new Validator(script, this.logger)
    }

    @NonCPS
    public def resolveVar(String varName, def defaultVal='', def objList=[this.script, this.script.env, this.script.params]) {
        this.logger.debug("in Utilities.resolveVar('${varName}', '${defaultVal.toString()}', '${objList.toString()}')")
        logger.debug("in Utilities.resolveVar('${varName}', '${defaultVal.toString()}')")
        for (def item in objList) {
            this.logger.debug("trying to find ${varName} inside ${item.getClass()}")
            if (this.validator.validateObjProperty(item, varName)) {
                this.logger.debug("><> ><> ><> found ${varName} inside ${item.getClass()}")
                def result = item."${varName}"
                this.logger.debug("got result: ${result}")
                if (result != null) {
                    return result
                }
            }
        }
        return defaultVal
    }

    public static split_params_list(String param, String splitter=',') {
        return param.split(splitter)
    }

    @NonCPS
    public def String getCredsIdByDescription(def description) {
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

    def createCredentialsMapByDescription(def credentials_defaults=[:]) {
        def result = [:]
        if (credentials_defaults == [:]) {
            credentials_defaults = Defaults.CREDENTIALS_DESCRIPTIONS
        }

        def keys = credentials_defaults.keySet() as ArrayList
        for (key in keys) {
            def value = getCredsIdByDescription(credentials_defaults[key])
            result[key] = value
        }
        return result
    }


    public paramsListToBoolean(ArrayList param_names_list) {
        for (def param_name in param_names_list) {
            logger.debug("converting '${param_name}' to boolean")
            def value = resolveVar(param_name).toBoolean()
            logger.debug("'${param_name}' value: ${value}")
            this.script.env."${param_name}" = value
        }
    }

    def getAbsPath(def fname){
        if (fname.startsWith(File.separator)) {
            return fname
        }
        def curr_dir = new File(getClass().protectionDomain.codeSource.location.path).parent
        def resultFName = new File(curr_dir, fname)
        return resultFName.getPath()
    }


    def getFileText(def fname) {
        def absPath = getAbsPath(fname)
        def result = new File(absPath).getText('utf-8') as String
        return result
    }


    public HashMap parseInventory(fname) {
        def fileData = getFileText(fname)
        def curr_section = null
        def result = [:]
        def linesep = System.getProperty("line.separator")
        for (def line in fileData.split(linesep)) {
            if ('' == line) {
                continue
            }
            def sectionMatcher = "${line}" =~ /^\[[a-zA-Z][a-zA-Z0-9]*\]$/
            if (sectionMatcher.matches()) {
                curr_section = line.replace('[','').replace(']','')
                continue
            }
            def line_split = line.split(' ', 2)
            def host = line_split[0]
            if (!result.containsKey(curr_section)) {
                result."${curr_section}" = [:]
            }
            if (!result."${curr_section}".containsKey(host)) {
                result."${curr_section}"."${host}" = []
            }
            if (1 == line_split.size()) {
                continue
            }
            def options_raw = line_split[1].trim()
            def options_arr = options_raw.tokenize(' ')
            def options = []
            options_arr.each {
                def pair = it.trim().split('=', 2)
                if (pair.size() > 1) {
                    if ('' == pair[1]) {
                        error "Incorrect inventory file!"
                    }
                    options << [(pair[0]): pair[1]]
                } else {
                    if ('' == pair[0]) {
                        error "Incorrect inventory file!"
                    }
                    options << pair[0]
                }
            }
            result."${curr_section}"."${host}" = options
        }
        return result
    }

    public void createArtifactFile(def fname, def inventory_fname, def groupname) {
        echo "Creating ${fname}"
        def inventoryObject = parseInventory(inventory_fname)
        if (!inventoryObject.containsKey(groupname)) {
            error "Inventory file has no group '${groupname}'"
        }
        def cluster = inventoryObject.cluster
        def instance_ids = []
        def instance_ips = []
        cluster.each {
            instance_ips << it.key
            instance_ids << it.instance_id
        }
        def data_arr = []
        data_arr << "INSTANCE_IDS=${instance_ids.join(',')}"
        data_arr << "INSTANCE_IPS=${instance_ips.join(',')}"
        data_arr << ""
        def data = data_arr.join(System.getProperty("line.separator"))
        this.script.writeFile([
                file: fname,
                text: data,
                encoding: 'utf-8'
        ])
    }

    public String make_git_url(def user, def team, def server, def repo, def proto=null){
        def result = 'git@'
        if (proto && proto.size()) {
            result = proto + '://'
        }
        result += server
        if (!proto) {
            result += ':'
        }
        else {
            result += '/'
        }
        result += team + '/'
        result += repo + '.git'
        return result
    }

    public makeCheckoutMap(
            def server,
            def team,
            def user,
            def repo,
            def branches,
            def cred_id,
            def target_dir=null) {
        def result = [:]
        result['$class'] = 'GitSCM'
        result.branches = []
        for (def branch in branches) {
            result.branches << [name: branch]
        }
        result.userRemoteConfigs = []
        def url =  make_git_url(user, team, server, repo, null)
        result.userRemoteConfigs << [
                url: url,
                credentialsId: cred_id,
                name: 'origin',
                refspec: '+refs/heads/*:refs/remotes/origin/*'
        ]
        result.doGenerateSubmoduleConfigurations = false
        result.submoduleCfg = []
        result.extensions = []
        if (null != target_dir) {
            result.extensions << [
                    $class: 'RelativeTargetDirectory',
                    relativeTargetDir: target_dir
            ]
        }
        return result
    }

    public def mvn_version(def pom_file=null) {
        if (!pom_file) {
            pom_file = pwd() + '/pom.xml'
        }
        def matcher = readFile(pom_file) =~ '<version>(.+)</version>'
        matcher ? matcher[0][1] : null
    }

    public def mvn_app(def pom_file=null) {
        if (!pom_file) {
            pom_file = pwd() + '/pom.xml'
        }
        def matcher = readFile(pom_file) =~ '<finalName>(.+)</finalName>'
        matcher ? matcher[0][1] : null
    }

    public def get_version(def proj_type=null, def conf_file=null) {
        def result = null
        if (!proj_type) {
            proj_type = 'mvn'
        }
        switch (proj_type) {
            case ['mvn']:
                result = mvn_version(conf_file)
                break
            default:
                result = null
                break
        }
        return result
    }

    public def get_app(def proj_type=null, def conf_file=null) {
        def result = null
        if (!proj_type) {
            proj_type = 'mvn'
        }
        switch (proj_type) {
            case ['mvn']:
                result = mvn_app(conf_file)
                break
            default:
                result = null
                break
        }
        return result
    }


    public String generateUUID() {
        def result = randomUUID() as String
        return result.toUpperCase()
    }

    public void exc_print(def exc) {
        logger.fatal("Flow failed with exception: ${exc.toString()}")
        logger.info("Message=${exc.getMessage()}")
        logger.info("Cause=${exc.getCause()}")
        logger.info("StackTrace=${exc.printStackTrace()}")
        this.script.manager.buildFailure()
    }

    public def url2ServerFQDN(def server_url) {
        def server_url_split = server_url.tokenize('://')
        def server_name_split = server_url_split[1].tokenize('/')
        def fqdn = server_name_split[0]
        return fqdn
    }

    public def getAuthRealm(def server_fqdn) {
        def server = server_fqdn.tokenize('.')[1]
        if (server == 'assembla') {
            return "Assembla Restricted Area"
        }
        if (server == 'riouxsvn') {
            return "RiouxSVN"
        }
        return server
    }

    public def map2File(
            def fName, 
            def data=[:],
            def sep=null, 
            def quote_char=null,
            def quote=true) {
        def file_data = ""
        if (!sep) {
            sep = '='
        }
        if (!quote_char) {
            quote_char = '"'
        }

        for (def element in data) {
            def key = element.key.toString()
            def value = element.toString()
            if (quote) {
                value = ['', value, ''].join(quote_char)
            }
            file_data += [key, value].join(sep) + "\n"
        }
        this.script.writeFile([
                text    : file_data,
                encoding: 'UTF-8',
                file    : fName
        ])

    }
}
