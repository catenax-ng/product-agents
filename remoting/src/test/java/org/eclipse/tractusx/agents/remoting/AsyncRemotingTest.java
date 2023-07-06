//
// Test for provisioning of REST APIs as SPARQL services
// See copyright notice in the top folder
// See authors file in the top folder
// See license file in the top folder
package org.eclipse.tractusx.agents.remoting;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.eclipse.rdf4j.common.iteration.Iterations;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.util.Models;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.tractusx.agents.remoting.config.RemotingSailConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class AsyncRemotingTest {

    @LocalServerPort
    String localServerPort;

    @BeforeEach
    public void setup() {
    }

    /**
     * tests async invocation
     */
    @Test
    public void testAsync() throws Exception {
        final HttpPost httppost = new HttpPost("http://localhost:"+localServerPort+"/callback");
        httppost.setEntity(new StringEntity("{\"header\":{\"referenceNotificationID\":\"puffpaff\"}}"));
        try (final CloseableHttpClient httpclient = HttpClients.createDefault()) {
           HttpResponse response = httpclient.execute(httppost);
           assertEquals(200,response.getStatusLine().getStatusCode());
        }
        Model graph = Rio.parse(RemotingSailTest.class.getResourceAsStream("/config.ttl"), RemotingSailTest.REPO_NAMESPACE,
                RDFFormat.TURTLE);
        RemotingSailConfig rsc=new RemotingSailConfig(RemotingSailFactory.SAIL_TYPE);
        rsc.parse(graph, Models.subjectBNode(graph.filter(null,rsc.getValueFactory().createIRI("http://www.openrdf.org/config/sail#","sailType"),
                rsc.getValueFactory().createLiteral("org.eclipse.tractusx.agents:Remoting"))).get());
        rsc.validate();
        Repository rep = new SailRepository(new RemotingSail(rsc));
        for(int connection=0;connection<2;connection++) {
            try (RepositoryConnection conn = rep.getConnection()) {
                for(int queryCount=0;queryCount<2;queryCount++) {
                    TupleQuery query = (TupleQuery) conn.prepareQuery(QueryLanguage.SPARQL,
                            "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> " +
                                    "PREFIX rul: <https://w3id.org/catenax/ontology/rul#> " +
                                    "SELECT ?invocation ?component ?result " +
                                    "WHERE { " +
                                    " VALUES(?component ?type) { (<urn:one> \"GearOil\"^^xsd:string) (<urn:one> \"GearSet\"^^xsd:string) (<urn:two> \"Clutch\"^^xsd:string)}" +
                                    " ?invocation a rul:RemainingUsefulLife; " +
                                    "             rul:component ?component; " +
                                    "             rul:observationType ?type; " +
                                    "             rul:content ?result. " +
                                    "}");
                    final TupleQueryResult result = query.evaluate();
                    final String[] names = result.getBindingNames().toArray(new String[0]);
                    assertEquals(3, names.length, "Correct number of binding names");
                    assertEquals("invocation", names[0], "Correct binding name 1");
                    assertEquals("component", names[1], "Correct binding name 2");
                    assertEquals("result", names[2], "Correct binding name 3");
                    java.util.List<BindingSet> bindings = Iterations.asList(result);
                    assertEquals(2, bindings.size(), "Correct number of bindings");
                    for (BindingSet firstBindingSet : bindings) {
                        assertEquals(3, firstBindingSet.size(), "Correct number of variables in binding 0");
                        assertTrue(firstBindingSet.getBindingNames().contains("invocation"), "Found invocation binding");
                        assertTrue(firstBindingSet.getValue("invocation").stringValue().startsWith("https://w3id.org/catenax/ontology/rul#"), "Invocation binding has the right prefix");
                        assertTrue(firstBindingSet.getBindingNames().contains("component"), "Found input binding");
                        assertTrue(firstBindingSet.getBindingNames().contains("result"), "Found response binding");
                        assertTrue(firstBindingSet.getValue("result").stringValue().length() > 30, "Correct response value");
                    }
                }
            }
        }
    }

}
