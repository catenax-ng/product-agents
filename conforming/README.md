# Catena-X Knowledge Agents (Hey Catena!) Conforming Agent

The Conforming Agent is a component that may be used in place of Matchmaking and Binding Agents to prove the conformity of other components of the KA architecture.
It implements (depending on its configuration) and validates the KA-MATCH and KA-BIND SPARQL profiles behind the Knowledge Agents standard API.
It may also be used to provoque error behaviour in the KA dataflow in order to check robustness and conformity in the case of problems.
It may also be used in unit test environments as a mock server.

## Architecture

The FOSS Conforming Agent is built using [Jersey]().

### Security

The conforming agent will be configurable to support the following http/s based security mechanisms:
- Basic Authentication
- Oauth2

## Deployment & Usage

### Containerizing (Conforming Agent)

To build the docker image of the Agent, please invoke this command

```console
docker build -t ghcr.io/catenax-ng/product-knowledge/dataspace/conforming-agent:latest -f src/main/docker/Dockerfile .
```

To run the docker image, you could invoke this command

```console
docker run -p 8080:8080 \
  ghcr.io/catenax-ng/product-knowledge/dataspace/conforming-agent:latest
````

Afterwards, you should be able to access the [local SparQL endpoint](http://localhost:8080/) via
the browser or by directly invoking a query

```console
curl --location --request POST 'http://localhost:8080/sparql' \
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

### Deployment

An example how to deploy the image can be found 
* in the [Docker Compose](../../../infrastructure/docker-compose.yml) recipe as well as 
* in the [Helm Chart](../../../infrastructure/templates/core-provider.yaml) template.

### Interact with the Conforming Agent

Once started (either locally on port 8080 or in a forwarded port from github codespaces via [docker compose](../../../infrastructure/README.md) you will be able 
to access a SPARQL Console:

- http://localhost:8080/
- https://drcgjung-catenax-ng-product-knowledge-wjgvvr6fgx97-8080.githubpreview.dev/

At the same time you may directly interface the SPARQL over HTTP endpoint

```console
curl --location --request POST 'http://localhost:8080/sparql' \
--header 'Content-Type: application/sparql-query' \
--header 'Accept: application/json' \
--data-raw 'PREFIX : <http://example.org/voc#>
SELECT DISTINCT ?teacher {
  ?teacher a :Teacher .
}
'
```

## Notice

* see copyright notice in the top folder
* see license file in the top folder
* see authors file in the top folder





