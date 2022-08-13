//
// Remoting interface to the Storage and Inference Layer (SAIL)
// See copyright notice in the top folder
// See authors file in the top folder
// See license file in the top folder
//

package io.catenax.knowledge.agents.remoting;

import java.util.Map;
import java.util.HashMap;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.query.algebra.Var;

/**
 * keeps the state of an ongoing invocation
 */
public class Invocation {
    /** the type of service invoked */
    public IRI service;
    /** unique key for the invocation */
    public IRI key;
    /** input bindings */
    public Map<IRI,Value> inputs=new HashMap<IRI,Value>();
    /** output bindings */
    public Map<Var,IRI> outputs=new HashMap<Var,IRI>();
    /** the actual result as a value */
    public Value result;
}