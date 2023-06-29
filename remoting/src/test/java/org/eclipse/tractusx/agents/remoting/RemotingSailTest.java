//
// Test for provisioning of REST APIs as SPARQL services
// See copyright notice in the top folder
// See authors file in the top folder
// See license file in the top folder
//
package org.eclipse.tractusx.agents.remoting;

import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.util.*;

import org.eclipse.rdf4j.query.*;

import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.common.iteration.Iterations;

import org.eclipse.rdf4j.rio.*;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * The main test for a remoting-enabled SparQL endpoint
 */
public class RemotingSailTest {

    public static String REPO_NAMESPACE="http://www.openrdf.org/config/repository#";

    /**
     * tests parsing a config
     */
    @Test    
    public void testConfig() throws Exception {
        Model graph = Rio.parse(RemotingSailTest.class.getResourceAsStream("/config.ttl"), REPO_NAMESPACE,
				RDFFormat.TURTLE);
        RemotingSailConfig rsc=new RemotingSailConfig(RemotingSailFactory.SAIL_TYPE);
        rsc.parse(graph,Models.subjectBNode(graph.filter(null,rsc.vf.createIRI("http://www.openrdf.org/config/sail#","sailType"),rsc.vf.createLiteral("org.eclipse.tractusx.agents:Remoting"))).get());
        rsc.validate();
        assertEquals(5,rsc.invocations.size(),"correct number of invocation configs");
        InvocationConfig health = rsc.invocations.get("https://w3id.org/catenax/ontology/health#HealthIndication");
        assertEquals(100,health.batch,"Correct batch size");
        assertEquals("https://w3id.org/catenax/ontology/health#requestComponentId",health.result.correlationInput,"Correct correlation input");
        InvocationConfig rul = rsc.invocations.get("https://w3id.org/catenax/ontology/rul#RemainingUsefulLife");
        assertNotNull(rul.callbackProperty,"Correct asynchronous mode");
        ArgumentConfig notificationTemplate=rul.arguments.get("https://w3id.org/catenax/ontology/rul#notification");
        assertNotNull(notificationTemplate,"Found the notification template argument");
        assertEquals(-1,notificationTemplate.priority,"Notification template has default value");
        assertNotNull(notificationTemplate.defaultValue,"Notification template has default value");
        ReturnValueConfig responseResult=rul.result.outputs.get("https://w3id.org/catenax/ontology/rul#content");
        assertNotNull(responseResult,"Notification content found");
    }
    
    /**
     * tests basic invocation features
     */
    @Test    
    public void testInvocation() throws Exception {
        
        RemotingSailConfig rsc=new RemotingSailConfig(RemotingSailFactory.SAIL_TYPE);
        InvocationConfig ic=new InvocationConfig();
        rsc.invocations.put("https://w3id.org/catenax/ontology/prognosis#Prognosis",ic);
        ic.targetUri="class:org.eclipse.tractusx.agents.remoting.TestFunction#test";
        ArgumentConfig ac=new ArgumentConfig();
        ac.argumentName = "arg0";
        ic.arguments.put("https://w3id.org/catenax/ontology/prognosis#input-1",ac);
        ac=new ArgumentConfig();
        ac.argumentName = "arg1";
        ic.arguments.put("https://w3id.org/catenax/ontology/prognosis#input-2",ac);
        ResultConfig rc=new ResultConfig();
        ic.result=rc;
        ic.resultName="https://w3id.org/catenax/ontology/prognosis#Result";

        ReturnValueConfig rvc=new ReturnValueConfig();

        rc.outputs.put("https://w3id.org/catenax/ontology/prognosis#output",rvc);
        rsc.validate();

        Repository rep = new SailRepository(new RemotingSail(rsc));

        try (RepositoryConnection conn = rep.getConnection()) {
            TupleQuery query=(TupleQuery) conn.prepareQuery(QueryLanguage.SPARQL,
            "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> "+
                "PREFIX prognosis: <https://w3id.org/catenax/ontology/prognosis#> "+
                "SELECT ?invocation ?output "+
                "WHERE { "+
                "?invocation a prognosis:Prognosis; "+
                "            prognosis:input-1 \"1\"^^xsd:string; "+
                "            prognosis:input-2 \"2\"^^xsd:string; "+
                "            prognosis:output ?output. "+
                //"            prognosis:hasResult [ prognosis:output ?output ]. "+
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
            assertTrue(firstBindingSet.getValue("invocation").stringValue().startsWith("https://w3id.org/catenax/ontology/prognosis#"),"Invocation binding has the right prefix");
            assertEquals(2,firstBindingSet.size(),"Correct number of variables in binding 0");
            assertTrue(firstBindingSet.getBindingNames().contains("output"),"Found output binding");
            assertEquals("3",firstBindingSet.getValue("output").stringValue());
        }
    }
    
    /**
     * tests basic invocation features
     */
    @Test    
    public void testRemoting() throws Exception {
        
        RemotingSailConfig rsc=new RemotingSailConfig(RemotingSailFactory.SAIL_TYPE);
        InvocationConfig ic=new InvocationConfig();
        rsc.invocations.put("https://w3id.org/catenax/ontology/prognosis#Prognosis",ic);
        ic.targetUri="https://api.agify.io";
        ArgumentConfig ac=new ArgumentConfig();
        ac.argumentName = "name";
        ic.arguments.put("https://w3id.org/catenax/ontology/prognosis#name",ac);
        ReturnValueConfig rvc=new ReturnValueConfig();
        rvc.path="age";
        rvc.dataType="http://www.w3.org/2001/XMLSchema#int";
        ResultConfig rc=new ResultConfig();
        ic.result=rc;
        ic.resultName="https://w3id.org/catenax/ontology/prognosis#Result";
        rc.outputs.put("https://w3id.org/catenax/ontology/prognosis#prediction",rvc);
        rvc=new ReturnValueConfig();
        rvc.path="count";
        rvc.dataType="http://www.w3.org/2001/XMLSchema#int";
        rc.outputs.put("https://w3id.org/catenax/ontology/prognosis#count",rvc);
        rsc.validate();

        Repository rep = new SailRepository(new RemotingSail(rsc));

        try (RepositoryConnection conn = rep.getConnection()) {
            TupleQuery query=(TupleQuery) conn.prepareQuery(QueryLanguage.SPARQL,
            "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> "+
                "PREFIX prognosis: <https://w3id.org/catenax/ontology/prognosis#> "+
                "SELECT ?invocation ?prediction ?count "+
                "WHERE { "+
                "?invocation a prognosis:Prognosis; "+
                "            prognosis:name \"Schorsch\"^^xsd:string; "+
                "            prognosis:prediction ?prediction; "+
                "            prognosis:count ?count. "+
                "}");
            final TupleQueryResult result = query.evaluate();
		    final String[] names = result.getBindingNames().toArray(new String[0]);
		    assertEquals(3,names.length,"Correct number of binding names");
		    assertEquals("invocation",names[0],"Correct binding name 1");
		    assertEquals("prediction",names[1],"Correct binding name 2");
		    assertEquals("count",names[2],"Correct binding name 3");
            java.util.List<BindingSet> bindings = Iterations.asList(result);
		    assertEquals(1,bindings.size(),"Correct number of bindings");
            BindingSet firstBindingSet=bindings.get(0);
            assertTrue(firstBindingSet.getBindingNames().contains("invocation"),"Found invocation binding");
            assertTrue(firstBindingSet.getValue("invocation").stringValue().startsWith("https://w3id.org/catenax/ontology/prognosis#"),"Invocation binding has the right prefix");
            assertEquals(3,firstBindingSet.size(),"Correct number of variables in binding 0");
            assertTrue(firstBindingSet.getBindingNames().contains("prediction"),"Found prediction binding");
            assertTrue(61<=((Literal) firstBindingSet.getValue("prediction")).intValue(),"Correct prediction value");
            assertTrue(firstBindingSet.getBindingNames().contains("count"),"Found count binding");
            assertTrue(((Literal) firstBindingSet.getValue("count")).intValue()>50,"Correct cound value");
        }
    }

    /**
     * tests basic invocation features
     */
    @Test
    public void testRemotingBatch() throws Exception {

        RemotingSailConfig rsc=new RemotingSailConfig(RemotingSailFactory.SAIL_TYPE);
        InvocationConfig ic=new InvocationConfig();
        rsc.invocations.put("https://w3id.org/catenax/ontology/prognosis#Prognosis",ic);
        ic.targetUri="https://api.agify.io";
        ArgumentConfig ac=new ArgumentConfig();
        ac.argumentName = "name";
        ic.arguments.put("https://w3id.org/catenax/ontology/prognosis#name",ac);
        ReturnValueConfig rvc=new ReturnValueConfig();
        rvc.path="age";
        rvc.dataType="http://www.w3.org/2001/XMLSchema#int";
        ResultConfig rc=new ResultConfig();
        ic.result=rc;
        ic.resultName="https://w3id.org/catenax/ontology/prognosis#Result";
        rc.outputs.put("https://w3id.org/catenax/ontology/prognosis#prediction",rvc);
        rvc=new ReturnValueConfig();
        rvc.path="count";
        rvc.dataType="http://www.w3.org/2001/XMLSchema#int";
        rc.outputs.put("https://w3id.org/catenax/ontology/prognosis#count",rvc);
        rsc.validate();

        Repository rep = new SailRepository(new RemotingSail(rsc));

        try (RepositoryConnection conn = rep.getConnection()) {
            TupleQuery query=(TupleQuery) conn.prepareQuery(QueryLanguage.SPARQL,
                         "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> "+
                            "PREFIX prognosis: <https://w3id.org/catenax/ontology/prognosis#> "+
                            "SELECT ?invocation ?input ?prediction ?count "+
                            "WHERE { "+
                            " VALUES(?input) { (\"Schorsch\"^^xsd:string) (\"Christoph\"^^xsd:string)}"+
                            " ?invocation a prognosis:Prognosis; "+
                            "             prognosis:name ?input; "+
                            "             prognosis:prediction ?prediction; "+
                            "             prognosis:count ?count. "+
                            "}");
            final TupleQueryResult result = query.evaluate();
            final String[] names = result.getBindingNames().toArray(new String[0]);
            assertEquals(4,names.length,"Correct number of binding names");
            assertEquals("invocation",names[0],"Correct binding name 1");
            assertEquals("input",names[1],"Correct binding name 2");
            assertEquals("prediction",names[2],"Correct binding name 3");
            assertEquals("count",names[3],"Correct binding name 4");
            java.util.List<BindingSet> bindings = Iterations.asList(result);
            assertEquals(2,bindings.size(),"Correct number of bindings");
            BindingSet firstBindingSet=bindings.get(0);
            assertEquals(4,firstBindingSet.size(),"Correct number of variables in binding 0");
            assertTrue(firstBindingSet.getBindingNames().contains("invocation"),"Found invocation binding");
            assertTrue(firstBindingSet.getValue("invocation").stringValue().startsWith("https://w3id.org/catenax/ontology/prognosis#"),"Invocation binding has the right prefix");
            assertTrue(firstBindingSet.getBindingNames().contains("input"),"Found input binding");
            assertTrue(firstBindingSet.getValue("input").stringValue().equals("Schorsch"));
            assertTrue(firstBindingSet.getBindingNames().contains("prediction"),"Found prediction binding");
            assertTrue(61<=((Literal) firstBindingSet.getValue("prediction")).intValue(),"Correct prediction value");
            assertTrue(firstBindingSet.getBindingNames().contains("count"),"Found count binding");
            assertTrue(((Literal) firstBindingSet.getValue("count")).intValue()>50,"Correct cound value");
            BindingSet secondBindingSet=bindings.get(1);
            assertEquals(4,secondBindingSet.size(),"Correct number of variables in binding 0");
            assertTrue(secondBindingSet.getBindingNames().contains("invocation"),"Found invocation binding");
            assertTrue(secondBindingSet.getValue("invocation").stringValue().startsWith("https://w3id.org/catenax/ontology/prognosis#"),"Invocation binding has the right prefix");
            assertTrue(secondBindingSet.getBindingNames().contains("input"),"Found input binding");
            assertTrue(secondBindingSet.getValue("input").stringValue().equals("Christoph"));
            assertTrue(secondBindingSet.getBindingNames().contains("prediction"),"Found prediction binding");
            assertTrue(41<=((Literal) secondBindingSet.getValue("prediction")).intValue(),"Correct prediction value");
            assertTrue(secondBindingSet.getBindingNames().contains("count"),"Found count binding");
            assertTrue(((Literal) secondBindingSet.getValue("count")).intValue()>15000,"Correct cound value");
        }
    }



}