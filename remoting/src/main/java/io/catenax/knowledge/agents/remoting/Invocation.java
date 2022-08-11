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
    public IRI service;
    public Map<IRI,Value> inputs=new HashMap<IRI,Value>();
    public Map<IRI,Var> outputs=new HashMap<IRI,Var>();
}