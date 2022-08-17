//
// Remoting interface to the Storage and Inference Layer (SAIL)
// See copyright notice in the top folder
// See authors file in the top folder
// See license file in the top folder
//
package io.catenax.knowledge.agents.remoting;

import org.eclipse.rdf4j.sail.config.SailConfigException;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap;

/**
 * class to represent a single invocation description
 * from the config graph of the remoting SAIL repo.
 */
public class InvocationConfig {
    /** regexp to check invocation */
    public static Pattern classPattern=Pattern.compile("(?<classType>class):(?<class>[a-zA-Z0-9\\.]+)#(?<method>[a-zA-Z0-9]+)|(?<restType>https?)://(?<url>[a-zA-Z0-9\\.:/%#]+)");

    /** url of the target service */
    protected String targetUri = null;
    protected Matcher matcher = null;
    
    /**
     * map of arguments
     */
    protected Map<String,ArgumentConfig> arguments= new java.util.HashMap<String,ArgumentConfig>();
    
    /**
     * map of outputs
     */
    protected Map<String,ReturnValueConfig> outputs=new java.util.HashMap<String,ReturnValueConfig>();

    public void validate() throws SailConfigException {
        if (targetUri==null || targetUri.length()==0) {
            throw new SailConfigException("REST service URL is not provided");
        }    
        matcher=classPattern.matcher(targetUri);
        if(!matcher.matches()) {
            throw new SailConfigException(String.format("REST service URL %s has no supported format.",targetUri));
        }
        for(Map.Entry<String,ArgumentConfig> arg: arguments.entrySet()) {
            arg.getValue().validate();
        }
        for(Map.Entry<String,ReturnValueConfig> arg: outputs.entrySet()) {
            arg.getValue().validate();
        }
    }
}
