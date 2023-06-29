# Tractus-X Remoting Agent

The Remoting Agent is an implementation of a Binding Agent along the Catena-X Knowledge Agents (Hey Catena!) architecture & standard.
It will bind/relay typical REST-based backend services and logic using XML and/or JSON payloads in a secure manner to the dataspace (published as so-called Graph Assets).
It does this by translating incoming SPARQL queries following the KA-BIND profile into proper synchronous and asynchronous API calls.

## Architecture

The Remoting Agent integrates with [Eclipse RDF4J](https://rdf4j.org) by introducing a (virtual) storage and inference layer (SAIL) that is indeed is backed by local Java classes and/or remote REST services.

The [Remoting SAIL](src/main/java/org/eclipse/tractusx/agents/remoting/RemotingSail.java) provides the implementation of a so-called repository (=a SPARQL endpoint that can be used as the target address of an EDC asset and/or as a service context in federated queries).

Each remoting repository is configured in a TTL (Terse Triple Language) [configuration file](src/test/resources/config.ttl) using the `org.eclipse.tractusx.agents:Remoting` Sailtype and lists
the available functions (=a particular class of nodes). 

```ttl
# ... PREFIX declarations

[] rdf:type rep:Repository ;
   rep:repositoryID "prognosis" ;
   rdfs:label "Prognosis Functions" ;
   rep:repositoryImpl [
      rep:repositoryType "openrdf:SailRepository" ;
      sr:sailImpl [
         sail:sailType "org.eclipse.tractusx.agents:Remoting" ;
         cx-fx:callbackAddress <http://localhost:8888/callback>;
         cx-fx:supportsInvocation cx-rt:Test;
         cx-fx:supportsInvocation cx-prognosis:Prognosis;
         cx-fx:supportsInvocation cx-lifetime:LifetimePrognosis;
         cx-fx:supportsInvocation cx-health:HealthIndication;
         cx-fx:supportsInvocation cx-rul:RemainingUsefulLife;
      ]
   ].

# Function declaration

cx-prognosis:Prognosis rdf:type cx-fx:Function;
  dcterms:description "Prognosis is a sample simulation function with input and output bindings."@en ;
  dcterms:title "Prognosis" ;
  cx-fx:targetUri "https://api.agify.io";
  cx-common:authenticationCode "Dummy-Authorization";
  cx-common:authenticationKey "Dummy-Key";
  cx-fx:input cx-prognosis:name;
  cx-fx:output cx-prognosis:prediction;
  cx-fx:result cx-prognosis:hasResult.

cx-prognosis:name rdf:type cx-fx:Argument;
  dcterms:description "Name is an argument to the Prognosis function."@en ;
  dcterms:title "Name";
  cx-fx:argumentName "name".

cx-prognosis:prediction rdf:type cx-fx:ReturnValue;
   dcterms:description "Prediction (Value) is an integer-based output of the Prognosis function."@en ;
   dcterms:title "Prediction" ;
   cx-fx:valuePath "age";
   cx-fx:dataType xsd:int.

cx-prognosis:hasResult rdf:type cx-fx:Result;
  cx-fx:output cx-prognosis:support.

cx-prognosis:support rdf:type cx-fx:ReturnValue;
   dcterms:description "Support (Value) is another integer-based output of the Prognosis function."@en ;
   dcterms:title "Support" ;
   cx-fx:valuePath "count";
   cx-fx:dataType xsd:int.

# Further declarations ...
```

When a concrete query is done, these classes are instantiated as invocation nodes.

```sparql
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> 
PREFIX prognosis: <https://w3id.org/catenax/ontology/prognosis#> 

SELECT ?invocation ?output 
WHERE { 
  ?invocation a prognosis:Prognosis; 
                prognosis:input-1 "1"^^xsd:string; 
                prognosis:input-2 "2"^^xsd:string; 
                prognosis:output ?output. 
                prognosis:hasResult [ prognosis:output ?output ].
}
```

The Remoting SAIL is [configured](src/main/java/org/eclipse/tractusx/agents/remoting/RemotingSailConfig.java) by declaring the functions and their binding (determined by a targetUri property). Currently, there are two bindings available
* Class Binding (targetUri follows the pattern "class:<className/>#<methodName/>") 
* REST Binding (targetUri follows the pattern "https?://<url>")

### Binding JSON structures

### Asynchronous Calls

## Deployment

### Compile, Test & Package

```console
mvn package
```

This will generate 
- a [standalone jar](target/agents.remoting-1.9.4-SNAPSHOT.jar) containing all necessary rdf4j components to build your own repository server.
- a [pluging jar](target/original-agents.remoting-1.9.4-SNAPSHOT.jar) which maybe dropped into an rdf4j server for remoting support.

### Run Locally

The standalone jar](target/agents.remoting-0.5.2-SNAPSHOT.jar) contains an example application that runs a sample repository against a sample source

```console
java -jar target/agents.remoting-1.9.4-SNAPSHOT.jar -Dorg.slf4j.simpleLogger.defaultLogLevel=DEBUG
```

### Containerizing

You could either call

```console
mvn install -Pwith-docker-image
```

or invoke the following docker command after a successful package run

```console
docker build -t ghcr.io/catenax-ng/product-agents/remoting-agent:1.9.4-SNAPSHOT -f src/main/docker/Dockerfile .
```

This will create a docker image including an extended rdf4j-server as well as an interactive rdf4j-workbench.

### Helm Chart


### Run the Container Locally

see [the infrastructure](../../../infrastructure) folder how to configure a repository including a remoting source.

## Notice

* see copyright notice in the top folder
* see license file in the top folder
* see authors file in the top folder





