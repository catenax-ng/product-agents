package org.eclipse.tractusx.agents.remoting;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.eclipse.rdf4j.common.iteration.Iterations;
import org.eclipse.rdf4j.model.Literal;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.channels.CancelledKeyException;

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
        rsc.parse(graph, Models.subjectBNode(graph.filter(null,rsc.vf.createIRI("http://www.openrdf.org/config/sail#","sailType"),
                rsc.vf.createLiteral("org.eclipse.tractusx.agents:Remoting"))).get());
        rsc.validate();
        Repository rep = new SailRepository(new RemotingSail(rsc));
        try (RepositoryConnection conn = rep.getConnection()) {
            TupleQuery query = (TupleQuery) conn.prepareQuery(QueryLanguage.SPARQL,
                    "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> " +
                            "PREFIX rul: <https://w3id.org/catenax/ontology/rul#> " +
                            "SELECT ?invocation ?input ?result " +
                            "WHERE { " +
                            " VALUES(?input) { (\"Schorsch\"^^xsd:string) (\"Christoph\"^^xsd:string)}" +
                            " ?invocation a rul:RemainingUsefulLife; " +
                            "             rul:content ?result. " +
                            "}");
            final TupleQueryResult result = query.evaluate();
            final String[] names = result.getBindingNames().toArray(new String[0]);
            assertEquals(3, names.length, "Correct number of binding names");
            assertEquals("invocation", names[0], "Correct binding name 1");
            assertEquals("input", names[1], "Correct binding name 2");
            assertEquals("result", names[2], "Correct binding name 3");
            java.util.List<BindingSet> bindings = Iterations.asList(result);
            assertEquals(2, bindings.size(), "Correct number of bindings");
            for(BindingSet firstBindingSet : bindings) {
                assertEquals(3, firstBindingSet.size(), "Correct number of variables in binding 0");
                assertTrue(firstBindingSet.getBindingNames().contains("invocation"), "Found invocation binding");
                assertTrue(firstBindingSet.getValue("invocation").stringValue().startsWith("https://w3id.org/catenax/ontology/rul#"), "Invocation binding has the right prefix");
                assertTrue(firstBindingSet.getBindingNames().contains("input"), "Found input binding");
                assertTrue(firstBindingSet.getBindingNames().contains("result"), "Found response binding");
                assertTrue(((Literal) firstBindingSet.getValue("result")).stringValue().length()>30, "Correct response value");
            }
        }
    }

}
