# Tractus-X Conforming Agent (KA-CONF)

KA-CONF is a module of the [Tractus-X Knowledge Agents Reference Implementations](../README.md).

* see copyright notice in the top folder
* see license file in the top folder
* see authors file in the top folder

## About this Module

The Conforming Agent is a component that may be used in place of Matchmaking and Binding Agents to prove the conformity of other components of the KA architecture.
It implements (depending on its configuration) and validates the KA-MATCH and KA-BIND SPARQL profiles behind the Knowledge Agents standard API.
It may also be used to provoque error behaviour in the KA dataflow in order to check robustness and conformity in the case of problems.
It may also be used in unit test environments as a mock server.

## Architecture

The FOSS Conforming Agent is built using [Jersey](https://github.com/eclipse-ee4j/jersey) (for the REST endpoints) and [RDF4J](https://rdf4j.org/) (for SPARQL parsing). It exposes three endpoints for the three different SPARQL profiles which are to be certified/tested in the KA architecture:
* The '/match' endpoint for the KA-MATCH profile
* The '/bind' endpoint for the KA-BIND profile
* The '/transfer' endpoint for the KA-TRANSFER profile

The Conforming Agent does not perform any logic but just returns prerendered results depending on the input parameters.

### Security

The conforming agent will be configurable to support the following http/s based security mechanisms:
- Basic Authentication
- Oauth2

## Deployment

### Compile, Test & Package

```console
mvn package
```

This will generate 
- a [plugin jar](target/original-conforming-agent-1.9.5-SNAPSHOT.jar) containing all necessary components to be dropped into a Jakarta-Compatible Web Server.
- a [standalone jar](target/conforming-agent-1.9.5-SNAPSHOT.jar) including the Jakarta-Reference Implementation (Glassfish).

### Run Locally

The [standalone jar](target/conforming-agent-1.9.5-SNAPSHOT.jar) may be started as follows

```console
java -cp target/conforming-agent-1.9.5-SNAPSHOT.jar org.eclipse.tractusx.agents.conforming.Bootstrap"
```

### Containerizing

You could either call

```console
mvn install -Pwith-docker-image
```

or invoke the following docker command after a successful package run

```console
docker build -t ghcr.io/catenax-ng/product-agents/conforming-agent:1.9.5-SNAPSHOT -f src/main/docker/Dockerfile .
```

This will create a docker image based on a minimal java environment for running the Glassfish-based standalone jar.

To run the docker image, you could invoke this command

```console
docker run -p 8080:8080 \
  ghcr.io/catenax-ng/product-agents/conforming-agent:1.9.5-SNAPSHOT
````

Afterwards, you should be able to access the [local SparQL endpoint](http://localhost:8080/) via
the browser or by directly invoking a query

```console
curl --location --request POST 'http://localhost:8080/bind' \
--header 'Content-Type: application/sparql-query' \
--header 'Accept: application/json' \
--data-raw 'PREFIX : <http://example.org/voc#>

SELECT ?x
WHERE {
   ?x a :Professor .
}'
```

You may manipulate any of the following environment variables to configure the image behaviour.
Note that there is no builtin security (ssl/auth) for the exposed endpoints.
This must be provided by hiding them in an appropriate service network layer.

| ENVIRONMENT VARIABLE        | Required  | Example                                                                | Description                          | List |
|---	                        |---	      |---	                                                                   |---                                   | ---  |
| JAVA_TOOL_OPTIONS           |           | -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8090   | JMV (Debugging option)               | X    | 

### Helm Chart

A helm chart for deploying the remoting agent can be found under [this folder](../charts/remoting-agent).

It can be added to your umbrella chart.yaml by the following snippet

```console
dependencies:
  - name: conforming-agent
    repository: https://catenax-ng.github.io/product-knowledge/infrastructure
    version: 1.9.5-SNAPSHOT
    alias: my-conforming-agent
```

and then installed using

```console
helm dependency update
```

In your values.yml, you configure your specific instance of the conforming agent like this

```console
##############################################################################################
# Conforming Agent
##############################################################################################

my-conforming-agent: 
  securityContext: *securityContext
  nameOverride: my-conforming-agent
  fullnameOverride: my-conforming-agent
  ingresses:
    - enabled: true
      # -- The hostname to be used to precisely map incoming traffic onto the underlying network service
      hostname: "my-conforming-agent.public-ip"
      annotations:
        nginx.ingress.kubernetes.io/rewrite-target: /$1
        nginx.ingress.kubernetes.io/use-regex: "true"
      # -- Agent endpoints exposed by this ingress resource
      endpoints:
        - default
      tls:
        enabled: true
        secretName: my-conforming-tls        
```






