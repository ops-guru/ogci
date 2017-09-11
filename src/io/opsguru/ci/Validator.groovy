#!groovy
// vim: filetype=groovy
package io.opsguru.ci

import com.cloudbees.groovy.cps.*
import groovy.lang.MissingPropertyException
import io.opsguru.ci.OGLogging
import io.opsguru.ci.OGLoggingLevel

class Validator implements Serializable {
    private def script = null
    private def logger = null

    public Validator(def script, def logger=null) {
        this.script = script
        this.logger = logger
        if (!logger) {
            def level = (script.env.VERBOSITY ==~ /^-vv+/)?OGLoggingLevel.debug:OGLoggingLevel.info
            this.logger = new OGLogging(script, null, level)
        }
    }

    public boolean validateObjProperty(def obj, def property) {
        def obj_class = obj.getClass()
        logger.debug(["in validateObjProperty(", obj.toString(), " '", property + "')"].join(""))
        if ((obj instanceof Map) && obj.containsKey(property)) {
            logger.debug([property, "is a key in", obj_class.toString()].join(" "))
            return true
        }
        if (obj.metaClass.hasProperty(property)) {
            logger.debug(["the object ", obj_class.toString(), "metaClass has property '", property, "'"].join(""))
            return true
        }
        if (obj.hasProperty(property)) {
            logger.debug(["the object ", obj_class.toString(), " has property '", property, "'"].join(""))
            return true
        }
        // TODO: check what's wrong with this:
        logger.debug("Before accessing the obj directly")
        try {
            def property_direct = obj."${property}" ?: null
            if (property_direct){
                logger.debug(["the object ", obj_class.toString(), "property '", property, "' is defined"].join(""))
                return true
            }
            logger.debug("After accessing the obj directly")
        } catch(MissingPropertyException exc1) {
            logger.debug("Caught MissingPropertyException")
        }

        return false
    }

    public boolean validateParamNonEmpty(def param_name) {
        logger.debug(["in validateParamNonEmpty(", param_name, ")"].join(""))
        def currentParams = null
        if (validateObjProperty(this.script, 'binding')) {
            currentParams = this.script.binding.variables
            logger.debug("using this.script.binding.variables")
            if (validateObjProperty(currentParams, param_name)) {
                return true
            }
        }
        if (this.script.params) {
            currentParams = this.script.params
            logger.debug("using this.script.params")
            if (validateObjProperty(currentParams, param_name)) {
                return true
            }
        }
        if (validateObjProperty(this.script, 'env')) {
            currentParams = this.script.env
            logger.debug("using this.script.env")
            return validateObjProperty(currentParams, param_name)
        }
        logger.debug("failed finding the property ${param_name}")
        return false
    }

    public void validateNonEmpty(def param_names_list) {
        logger.debug(["in validateNonEmpty(", param_names_list.toString(), ")"].join(""))
        for (def param_name in param_names_list) {
            if (!validateParamNonEmpty(param_name)){
                logger.error(["Failed to validate '", param_name, "'"].join(""))
                script.error "Failed to validate '${param_name}'"
            }
        }
    }
}
