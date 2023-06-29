//
// Application to provide REST APIs as SPARQL services
// See copyright notice in the top folder
// See authors file in the top folder
// See license file in the top folder
//
package org.eclipse.tractusx.agents.remoting;

import org.eclipse.rdf4j.sail.config.SailConfigException;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * class to represent an authentication description
 * from the config graph of the remoting SAIL repo.
 */
public class AuthenticationConfig {
 
    /** auth key (defaults to Authorization) */
    protected String authKey = "Authorization";

    /** actual auth code */
    protected String authCode = null;
   
    @Override
    public String toString() {
        return super.toString()+"/authentication";
    }   

    /**
     * Validates the authentication config
     * @throws SailConfigException
     */
    public void validate(String context) throws SailConfigException {
        if (authCode==null || authCode.length() == 0) {
            throw new SailConfigException(String.format("Authentication code in %s is not provided",context));
        }
    }
}
