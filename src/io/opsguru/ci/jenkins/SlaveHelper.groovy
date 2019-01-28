package io.opsguru.ci.jenkins


import io.opsguru.ci.utils.OGLogging
import io.opsguru.ci.utils.OGLoggingLevel
import io.opsguru.ci.utils.Utilities
import jenkins.model.*
import hudson.model.*
import hudson.slaves.*
import hudson.plugins.sshslaves.*
//import hudson.slaves.EnvironmentVariablesNodeProperty.Entry;
import hudson.tools.ToolLocationNodeProperty
import hudson.tasks.Maven.MavenInstallation

class SlaveHelper implements Serializable {

    private def script = null
    private OGLogging  logger = null
    public Utilities utilities = null
    public CredsHelper creds = null
    public String verbosity = null


    public SlaveHelper(def script, def logger=null) {
        this.script = script
        this.logger = logger
        this.verbosity = script.env.logLevel
        if (!logger) {
            def level = (script.env.logLevel!=null)?script.env.logLevel:OGLoggingLevel.info
            this.logger = new OGLogging(script, null, level)
        }
        this.utilities = new Utilities(script, this.logger)
        this.creds = new CredsHelper(script, this.logger)
    }

    private void jenkinsSlaveAddTools(DumbSlave slave, HashMap tools=[:]) {
        def nodeProps = slave.getNodeProperties()
        this.logger.debug("Creating maven installation")

        def toolsArr = []
        for (def key in (tools.keySet() as ArrayList)) {
            def toolName = tools[key].name
            def toolPath = tools[key].path
            def toolInst = null // toolInstallation
            if (key == 'maven') {
                toolInst = new MavenInstallation(tools[key].name, tools[key].path)
            }
            if (key == 'jdk') {
                toolInst = new JDK(tools[key].name, tools[key].path)
            }
            def toolDescr = toolInst.getDescriptor() // toolDescriptor
            // toolLocation:
            def toolLoc = new hudson.tools.ToolLocationNodeProperty.ToolLocation(toolDescr, toolName, toolPath)
            toolsArr << toolLoc
        }
        ToolLocationNodeProperty tlnp = new hudson.tools.ToolLocationNodeProperty(toolsArr)
        this.logger.debug("Adding ToolLocation properties")
        nodeProps << tlnp
        slave.save()
        this.logger.debug("Adding the node to jenkins")
    }


    public def addJenkinsAgent(def agentHostName, def credentialsId, def agentName = null, def labelsArr = null) {
        assert agentHostName != null
        assert credentialsId != null
        this.logger.debug("in Runner.addJenkinsAgent(${agentHostName}, ${credentialsId}, ${agentName}, ${labelsArr})")
        this.logger.debug("Calculating username")
        String userName = this.creds.getCredentialUsernameById(credentialsId)
        if (!labelsArr) {
            labelsArr = []
        }
        this.logger.info("Using hostName=${agentHostName}, userName=${userName}")
        this.logger.debug("Creating a node")
        DumbSlave node = new DumbSlave(
                agentHostName,
                "/home/${userName}",
                new SSHLauncher(
                        agentHostName,  // java.lang.String
                        22,             // java.lang.Integer
                        credentialsId,  // java.lang.String
                        "",             // java.lang.String
                        "",             // java.lang.String
                        "",             // java.lang.String
                        "",             // java.lang.String
                        0,              // java.lang.Integer
                        0,              // java.lang.Integer
                        0,              // java.lang.Integer
                        // TODO: see how we can verify yet not be stuck
//                        new hudson.plugins.sshslaves.verifiers.NonVerifyingKeyVerificationStrategy() // java.lang.Class
                        new hudson.plugins.sshslaves.verifiers.NonVerifyingKeyVerificationStrategy() // java.lang.Class,
                )
        )
        // TODO: support tools addition
        // def tools = ()
        // jenkinsSlaveAddTools(node, tools)
        Jenkins.instance.addNode(node)
        // connect to the slave, just in case
        this.logger.debug("Connecting to the node")
        node.computer.connect(true)
        // set labels on new slave
        this.jenkinsSlaveUpdateLabels(agentHostName, labelsArr)
    }

    public void removeJenkinsAgent(def agentHostName) {
        assert agentHostName != null
        this.logger.debug("in Runner.removeJenkinsAgent(${agentHostName})")
        Node targetNode = Jenkins.instance.getNode(agentHostName)
        if (!targetNode) {
            return
        }
        Jenkins.instance.removeNode(targetNode)
        return
    }

    public def getSlaveByName(def name) {
        for (def slave in jenkins.model.Jenkins.instance.slaves) {
            if (name == slave.getNodeName()) {
                return slave
            }
        }
        return null;
    }

    public void jenkinsSlaveUpdateLabels(def agentName, def labelsArr) {
        def slave = this.getSlaveByName(agentName)
        if (slave == null) {
            this.logger.warn("Failed to find slave named ${agentName}")
            return
        }
        def currLabelsString =  slave.getLabelString()
        for (label in labelsArr) {
            currLabelsString += " " + label
        }
        slave.setLabelString(currLabelsString)
    }


}
