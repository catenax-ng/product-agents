# Tractus-X Remoting Agent

This is a folder providing a FOSS implementations of a Function Binding (aka Remoting) Agent.

Binding Agents are needed by any Agent-Enabled dataspace providers to connect the dataspace protocol/representation (here: a combination of the SPARQL query language/operating on RDF triple graphs) to the underlying business data and logic.

The Remoting Agent in particular is able to interact with typical REST backend services based on XML and/or JSON payloads. The SPARQL profile which is used is called KA-BIND-F (the Knowledge Agent Functional Binding Profile).

## Architecture

Tractus-X Remoting Agent is built on [Eclipse RDF4J](https://rdf4j.org) by introducing a (virtual) storage and inference layer (SAIL) that is indeed is backed by local Java classes and/or remote REST services.

![Remoting Agent Architecture](../docs/RemotingRDF4J.drawio.svg)

The [Remoting SAIL](src/main/java/org/eclipse/tractusx/agents/remoting/RemotingSail.java) provides the implementation of a so-called repository (=a SPARQL endpoint that can be used as the target address of an EDC asset and/or as a service context in federated queries).

Each remoting repository is configured in a TTL (Terse Triple Language) [configuration file](src/test/resources/config.ttl) using the `org.eclipse.tractusx.agents:Remoting` Sailtype and lists
the available functions (=a particular class of nodes). Each remoting repository is parsed, represented and possibly exported from the runtime by means of [Remoting SAIL Config](src/main/java/org/eclipse/tractusx/agents/remoting/config/RemotingSailConfig.java), for each function (class), a corresponding [Service Config](src/main/java/org/eclipse/tractusx/agents/remoting/config/ServiceConfig.java) is maintained.

Here is an example repository configuration which uses the public service `https://api.agify.io` to make a prediction of the age of a person given its name.

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
         cx-fx:supportsInvocation prognosis:Prognosis;
      ]
   ].

# Function declaration

prognosis:Prognosis rdf:type cx-fx:Function;
  dcterms:description "Prognosis is a sample simulation function with input and output bindings."@en ;
  dcterms:title "Prognosis" ;
  cx-fx:targetUri "https://api.agify.io";
  cx-common:authenticationCode "Dummy-Authorization";
  cx-common:authenticationKey "Dummy-Key";
  cx-fx:input prognosis:name;
  cx-fx:result prognosis:hasResult.

prognosis:name rdf:type cx-fx:Argument;
  dcterms:description "Name is an argument to the Prognosis function."@en ;
  dcterms:title "Name";
  cx-fx:argumentName "name".

prognosis:hasResult rdf:type cx-fx:Result;
  cx-fx:output prognosis:prediction;
  cx-fx:output prognosis:support.

prognosis:prediction rdf:type cx-fx:ReturnValue;
   dcterms:description "Prediction (Value) is an integer-based output of the Prognosis function."@en ;
   dcterms:title "Prediction" ;
   cx-fx:valuePath "age";
   cx-fx:dataType xsd:int.

prognosis:support rdf:type cx-fx:ReturnValue;
   dcterms:description "Support (Value) is another integer-based output of the Prognosis function."@en ;
   dcterms:title "Support" ;
   cx-fx:valuePath "count";
   cx-fx:dataType xsd:int.

# Further declarations ...
```

When a concrete query is sent to the RDF4J controller, a [Remoting SAIL Connection](src/main/java/org/eclipse/tractusx/agents/remoting/RemotingSailConnection.java)
is setup which evaluates the query with the help of the stateful [Query Executor](src/main/java/org/eclipse/tractusx/agents/remoting/QueryExecutor.java). With the help of the Remoting Sail Configuraiton, The Query Executor instantiates all function nodes as [Invocations](src/main/java/org/eclipse/tractusx/agents/remoting/Invocation.java). During processing, input and output variables are accessed and stored in a BindingSet Collection for which the Query Executor implements the [IBindingHost](src/main/java/org/eclipse/tractusx/agents/remoting/IBindingHost.java).

```sparql
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> 
PREFIX prognosis: <https://w3id.org/catenax/ontology/prognosis#> 

SELECT ?name ?invocation ?prediction ?support 
WHERE { 
  VALUES (?name) { "Schorsch"^^xsd:string }
  ?invocation a prognosis:Prognosis; 
                prognosis:name ?name; 
                prognosis:prediction ?prediction;
                prognosis:support ?support.
}
```

Currently, there are two principle methods of function bindings available
* Class Binding ([Service Config](src/main/java/org/eclipse/tractusx/agents/remoting/config/ServiceConfig.java).targetUri follows the pattern "class:<className/>#<methodName/>") 
* REST Binding ([Service Config](src/main/java/org/eclipse/tractusx/agents/remoting/config/ServiceConfig.java)targetUri follows the pattern "https?://<url>")

For REST Binding, we support the following outgoing request formats/content types (being configured via [Service Config](src/main/java/org/eclipse/tractusx/agents/remoting/config/ServiceConfig.java).method). Note that responses are always interpreted as XML or JSON depending on the response content type.
* GET: Input arguments are mapped to URL query parameters. 
* POST-XML: Input Arguments are mapped into an XML document body with content-type "application/xml" T
* POST-JSON: Input Arguments are mapped into an JSON document body with content-type "application/json"
* POST-JSON-MF: Input Arguments are mapped into JSON-based files send in a multi-part  request with content-type multipart/form-data (and XXX as boundary and "application/json" as Content-Disposition)
* POST-XML-MF: Input Arguments are mapped into XML-based files send in a multi-part request with content-type multipart/form-data (and XXX as boundary and application/json as Content-Disposition)

Invocations can be batched. Normally ([Service Config](src/main/java/org/eclipse/tractusx/agents/remoting/config/ServiceConfig.java).batch is set to 1, no [Argument Config](src/main/java/org/eclipse/tractusx/agents/remoting/config/ArgumentConfig.java).formsBatchGroup is set to true) Remoting Agent will produce an outgoing REST call for each incoming tuple/binding. If [Service Config](src/main/java/org/eclipse/tractusx/agents/remoting/config/ServiceConfig.java).batch is greater than 1 or
there is some [Argument Config](src/main/java/org/eclipse/tractusx/agents/remoting/config/ArgumentConfig.java).formsBatchGroup set to true, several tuples/bindings can be sent in a single invocation (usually in an array or by using flexible argument paths using '{<iriofinput>}' path elements). In that case, we also expect the responses to contain several individual results which are mapped/joined with the original input bindings using the ResultConfig.correlationInput reference.

Invocation can be asynchronous. That means that the called backend will not return a proper response, just a successful notification code. Instead we send the public URL of the builtin [CallbackController](src/main/java/org/eclipse/tractusx/agents/remoting/callback/CallbackController.java) which is configured in the callbackAddress property of the remoting repository (and is transmitted in the callbackAddressProperty of the ServiceConfig). In order to correlate outgoing (batch) requests with asynchronous responses sent to the CallbackController, we rely on setting a unique request identifier specified in ServiceConfig.invocationIdProperty and comparing it with the content of the ResultConfig.callbackProperty
 
## Deployment

### Compile, Test & Package

```console
mvn package
```

This will generate 
- a [standalone jar](target/remoting-agent-1.9.4-SNAPSHOT.jar) containing all necessary rdf4j components to build your own repository server.
- a [pluging jar](target/original-remoting-agent-1.9.4-SNAPSHOT.jar) which maybe dropped into an rdf4j server for remoting support.

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

A helm chart for deploying the remoting agent can be found under [this folder](../charts/remoting-agent).

It can be added to your umbrella chart.yaml by the following snippet

```console
dependencies:
  - name: remoting-agent
    repository: https://catenax-ng.github.io/product-knowledge/infrastructure
    version: 1.9.4-SNAPSHOT
    alias: my-remoting-agent
```

and then installed using

```console
helm dependency update
```

In your values.yml, you configure your specific instance of the remoting agent like this

```console
##############################################################################################
# API Binding Agent
##############################################################################################

my-remoting-agent: 
  securityContext: *securityContext
  nameOverride: my-remoting-agent
  fullnameOverride: my-remoting-agent
  ingresses:
    - enabled: true
      # -- The hostname to be used to precisely map incoming traffic onto the underlying network service
      hostname: "my-remoting-agent.public-ip"
      annotations:
        nginx.ingress.kubernetes.io/rewrite-target: /$1
        nginx.ingress.kubernetes.io/use-regex: "true"
      # -- Agent endpoints exposed by this ingress resource
      endpoints:
        - default
      tls:
        enabled: true
        secretName: my-remoting-tls
  repositories:
    prognosis: |-
      #
      # Rdf4j configuration for prognosis sample
      #
      @prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>.
      @prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>.
      @prefix rep: <http://www.openrdf.org/config/repository#>.
      @prefix sr: <http://www.openrdf.org/config/repository/sail#>.
      @prefix sail: <http://www.openrdf.org/config/sail#>.
      @prefix sp: <http://spinrdf.org/sp#>.
      @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
      @prefix json: <https://json-schema.org/draft/2020-12/schema#> .
      @prefix dcterms: <http://purl.org/dc/terms/> .
      @prefix cx-fx: <https://w3id.org/catenax/ontology/function#>.
      @prefix prognosis: <https://w3id.org/prognosis>.

    [] rdf:type rep:Repository ;
       rep:repositoryID "prognosis" ;
       rdfs:label "Prognosis Functions" ;
       rep:repositoryImpl [
          rep:repositoryType "openrdf:SailRepository" ;
          sr:sailImpl [
             sail:sailType "org.eclipse.tractusx.agents:Remoting" ;
             cx-fx:callbackAddress <https://my-remoting-agent.public-ip/callback>;
             cx-fx:supportsInvocation prognosis:Prognosis;
          ]
       ].
    
    # Function declaration
    
    prognosis:Prognosis rdf:type cx-fx:Function;
      dcterms:description "Prognosis is a sample simulation function with input and output bindings."@en ;
      dcterms:title "Prognosis" ;
      cx-fx:targetUri "https://api.agify.io";
      cx-common:authenticationCode "Dummy-Authorization";
      cx-common:authenticationKey "Dummy-Key";
      cx-fx:input prognosis:name;
      cx-fx:result prognosis:hasResult.
    
    prognosis:name rdf:type cx-fx:Argument;
      dcterms:description "Name is an argument to the Prognosis function."@en ;
      dcterms:title "Name";
      cx-fx:argumentName "name".
    
    prognosis:hasResult rdf:type cx-fx:Result;
      cx-fx:output prognosis:prediction;
      cx-fx:output prognosis:support.
    
    prognosis:prediction rdf:type cx-fx:ReturnValue;
       dcterms:description "Prediction (Value) is an integer-based output of the Prognosis function."@en ;
       dcterms:title "Prediction" ;
       cx-fx:valuePath "age";
       cx-fx:dataType xsd:int.
    
    prognosis:support rdf:type cx-fx:ReturnValue;
       dcterms:description "Support (Value) is another integer-based output of the Prognosis function."@en ;
       dcterms:title "Support" ;
       cx-fx:valuePath "count";
       cx-fx:dataType xsd:int.
        
```

### Run the Container Locally

see [the infrastructure](https://github.com/catenax-ng/product-knowledge/tree/main/infrastructure) folder how to configure a repository including a remoting source.

## Notice

* see copyright notice in the top folder
* see license file in the top folder
* see authors file in the top folder





