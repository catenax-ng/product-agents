//
// Test for provisioning of REST APIs as SPARQL services
// See copyright notice in the top folder
// See authors file in the top folder
// See license file in the top folder
//
package io.catenax.knowledge.agents.remoting;

import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.util.*;
import org.eclipse.rdf4j.model.vocabulary.*;

import org.eclipse.rdf4j.query.*;

import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.repository.config.RepositoryConfigSchema;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.eclipse.rdf4j.common.iteration.Iterations;

import org.eclipse.rdf4j.rio.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * The main test for a remoting-enabled SparQL endpoint
 */
public class RemotingSailTest {

    /**
     * tests parsing a config
     */
    @Test    
    public void testConfig() throws Exception {
        Model graph = Rio.parse(this.getClass().getResourceAsStream("/config.ttl"), RepositoryConfigSchema.NAMESPACE,
				RDFFormat.TURTLE);
        RemotingSailConfig rsc=new RemotingSailConfig(RemotingSailFactory.SAIL_TYPE);
        rsc.parse(graph,Models.subjectBNode(graph.filter(null,rsc.vf.createIRI("http://www.openrdf.org/config/sail#","sailType"),rsc.vf.createLiteral("io.catenax.knowledge:Remoting"))).get());
        rsc.validate();
    }
    
    /**
     * tests basic remoting features
     */
    @Test    
    public void testRemoting() throws Exception {
        
        RemotingSailConfig rsc=new RemotingSailConfig(RemotingSailFactory.SAIL_TYPE);
        InvocationConfig ic=new InvocationConfig();
        rsc.invocations.put("https://github.com/catenax-ng/product-knowledge/ontology/prognosis.ttl#Prognosis",ic);
        ic.targetUri="class:io.catenax.knowledge.agents.remoting.TestFunction#test";
        ic.output="https://github.com/catenax-ng/product-knowledge/ontology/prognosis.ttl#output";
        ArgumentConfig ac=new ArgumentConfig();
        ac.argumentName = "arg0";
        ic.arguments.put("https://github.com/catenax-ng/product-knowledge/ontology/prognosis.ttl#input-1",ac);
        ac=new ArgumentConfig();
        ac.argumentName = "arg1";
        ic.arguments.put("https://github.com/catenax-ng/product-knowledge/ontology/prognosis.ttl#input-2",ac);
        
        rsc.validate();

        Repository rep = new SailRepository(new RemotingSail(rsc));

        try (RepositoryConnection conn = rep.getConnection()) {
            TupleQuery query=(TupleQuery) conn.prepareQuery(QueryLanguage.SPARQL,
            "PREFIX cx: <https://github.com/catenax-ng/product-knowledge/ontology/cx.ttl#> "+
            "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> "+
            "PREFIX prognosis: <https://github.com/catenax-ng/product-knowledge/ontology/prognosis.ttl#> "+
            "SELECT ?invocation ?output "+
            "WHERE { "+
            "?invocation a prognosis:Prognosis; "+
            "            prognosis:input-1 \"1\"^^xsd:string; "+
            "            prognosis:input-2 \"2\"^^xsd:string; "+
            "            prognosis:output ?output. "+
            "}");
            final TupleQueryResult result = query.evaluate();
		    final String[] names = result.getBindingNames().toArray(new String[0]);
		    assertEquals(2,names.length,"Correct number of binding names");
		    assertEquals("invocation",names[0],"Correct binding name 1");
		    assertEquals("output",names[1],"Correct binding name 2");
            java.util.List<BindingSet> bindings = Iterations.asList(result);
		    assertEquals(1,bindings.size(),"Correct number of bindings");
            BindingSet firstBindingSet=bindings.get(0);
            assertTrue(firstBindingSet.getBindingNames().contains("invocation"),"Found invocation binding");
            assertTrue(firstBindingSet.getValue("invocation").stringValue().startsWith("https://github.com/catenax-ng/product-knowledge/ontology/prognosis.ttl#"),"Invocation binding has the right prefix");
            assertEquals(2,firstBindingSet.size(),"Correct number of variables in binding 0");
            assertTrue(firstBindingSet.getBindingNames().contains("output"),"Found output binding");
            assertEquals("3",firstBindingSet.getValue("output").stringValue());
        }
    }
}