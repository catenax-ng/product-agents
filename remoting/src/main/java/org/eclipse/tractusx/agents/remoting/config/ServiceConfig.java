//
// Application to provide REST APIs as SPARQL services
// See copyright notice in the top folder
// See authors file in the top folder
// See license file in the top folder
//
package org.eclipse.tractusx.agents.remoting.config;

import org.eclipse.rdf4j.sail.config.SailConfigException;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * class to represent a single invocation description
 * from the config graph of the remoting SAIL repo.
 */
public class ServiceConfig {
    /** regexp to check invocation */
    public static Pattern classPattern = Pattern.compile(
            "(?<classType>class):(?<class>[a-zA-Z0-9\\.]+)#(?<method>[a-zA-Z0-9]+)|(?<restType>https?)://(?<url>[a-zA-Z0-9\\.:/%#\\-]+)");

    /** url of the target service */
    protected String targetUri = null;

    /** method for the invocation, maybe POST or POST-MF, GET or INVOKE */
    protected String method = null;

    /** a matcher for the targetUri containing regex groups/bindings */
    protected Matcher matcher = null;

    /** the maximal batch size */
    protected long batch = 1;

    /** whether it is an asynchronous call */
    protected String callbackProperty;

    /** prefix to attach to all properties */
    protected String inputProperty;

    /** whether and where to put a unique invocation id */
    protected String invocationIdProperty;

    /**
     * an optional authentication
     */
    protected AuthenticationConfig authentication;

    /**
     * map of arguments
     */
    protected Map<String, ArgumentConfig> arguments = new java.util.HashMap<String, ArgumentConfig>();

    /** the result */
    String resultName=null;
    ResultConfig result=null;

    /**
     * sets
     * @param targetUri reference
     */
    public void setTargetUri(String targetUri) {
        this.targetUri = targetUri;
    }

    /**
     * sets
     * @param resultName reference
     */
    public void setResultName(String resultName) {
        this.resultName = resultName;
    }

    /**
     * sets
     * @param result config
     */
    public void setResult(ResultConfig result) {
        this.result = result;
    }

    /**
     * @return invocation method (POST-JSON, POST-JSON-MF, GET, ...)
     */
    public String getMethod() {
        return method;
    }

    /**
     * @return the matcher that parsed the target uri and provides parsing components
     */
    public Matcher getMatcher() {
        return matcher;
    }

    /**
     * @return maximal batch size
     */
    public long getBatch() {
        return batch;
    }

    /**
     * @return callback property to set callback address to
     */
    public String getCallbackProperty() {
        return callbackProperty;
    }

    /**
     * @return property which contains the actual request data
     */
    public String getInputProperty() {
        return inputProperty;
    }

    /**
     * @return property which should host the invocation id
     */
    public String getInvocationIdProperty() {
        return invocationIdProperty;
    }

    /**
     * @return authentication info
     */
    public AuthenticationConfig getAuthentication() {
        return authentication;
    }

    /**
     * @return map of iri to argument configurations
     */
    public Map<String, ArgumentConfig> getArguments() {
        return arguments;
    }

    /**
     * @return result reference
     */
    public String getResultName() {
        return resultName;
    }

    /**
     * @return result config
     */
    public ResultConfig getResult() {
        return result;
    }

    @Override
    public String toString() {
        return super.toString()+"/service";
    }   

    /**
     * Validates the invocation config
     * @throws SailConfigException if validation is unsuccessful
     */
    public void validate(String context) throws SailConfigException {
        if (targetUri == null || targetUri.length() == 0) {
            throw new SailConfigException(String.format("Service URL in invocation %s is not provided",context));
        }
        matcher = classPattern.matcher(targetUri);
        if (!matcher.matches()) {
            throw new SailConfigException(String.format("Service URL %s has no supported format.", targetUri));
        }
        if (method == null) {
            if (matcher.group("classType") != null) {
                method = "INVOKE";
            } else if (matcher.group("restType") != null) {
                method = "GET";
            } else {
                throw new SailConfigException(String.format("Cannot deduce service method from URL %s.", targetUri));
            }
        } else {
            switch (method) {
                case "INVOKE":
                    if (matcher.group("classType") == null) {
                        throw new SailConfigException(String.format(
                                "INVOKE Method can onl be used with class-type service URLs but was %s.", targetUri));
                    }
                    break;
                case "POST-JSON":
                case "POST-JSON-MF":
                case "GET":
                    if (matcher.group("restType") == null) {
                        throw new SailConfigException(String.format(
                                "%s Method can only be used with REST service URLs but was %s.", method, targetUri));
                    }

            }
        }
        if(callbackProperty!=null) {
            if(result.callbackProperty==null) {
                throw new SailConfigException("There should be a result callbackProperty configured when the invocation callbackProperty is set.");
            }
        }
        for (Map.Entry<String, ArgumentConfig> arg : arguments.entrySet()) {
            arg.getValue().validate(arg.getKey());
        }
        if(resultName==null || result==null) {
            throw new SailConfigException(String.format(
                "There was no result configured for the invocation."));

        } else {
            result.validate(resultName);
        }
        if(authentication!=null) {
            authentication.validate(context);
        }
    }
}
