//
// Application to provide REST APIs as SPARQL services
// See copyright notice in the top folder
// See authors file in the top folder
// See license file in the top folder
//
package org.eclipse.tractusx.agents.remoting;

import org.eclipse.rdf4j.sail.config.SailConfigException;

/**
 * represents the config of an argument
 */
public class ArgumentConfig {
     /** name of the argument */
     protected String argumentName;
     protected boolean mandatory=true;
     
     public void validate(String context) throws SailConfigException {
         if (argumentName==null || argumentName.length()==0) {
            throw new SailConfigException(String.format("Only support named arguments %s.",context));
         }    
     }

     @Override
     public String toString() {
         return super.toString()+"/argument("+String.valueOf(mandatory)+")";
     }
 
}
