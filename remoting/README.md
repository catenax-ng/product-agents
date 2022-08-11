# Catena-X Knowledge Agents (Hey Catena!) Remoting Agent

This is a folder providing the FOSS implementations of a Hey Catena! agent that binds simple remote (REST) services to the dataspace.

It relies on the [Eclipse RDF4J](https://rdf4j.org) for building a storage and inference layer (SAIL) which is backed by a remote REST service.

## Compile, Test & Package

```console
mvn package
```

This will generate 
- a [standalone jar](target/agents.remoting-0.5.2-SNAPSHOT.jar) containing all necessary rdf4j components to build your own repository server.
- a [pluging jar](target/original-agents.remoting-0.5.2-SNAPSHOT.jar) which maybe dropped into an rdf4j server for remoting support.

## Run Locally

The standalone jar](target/agents.remoting-0.5.2-SNAPSHOT.jar) contains an example application that runs a sample repository against a sample source

```console
java -jar target/agents.remoting-0.5.2-SNAPSHOT.jar -Dorg.slf4j.simpleLogger.defaultLogLevel=DEBUG
```

## Containerizing

```console
docker build -t ghcr.io/catenax-ng/product-knowledge/dataspace/remoting-agent:0.5.2 .
```

will create a docker image including an extended rdf4j-server aw well as an interactive rdf4j-workbench.

## Run the Container Locally

see [the infrastructure](../../../infrastructure) folder how to configure a repository including a remoting source.

## Notice

* see copyright notice in the top folder
* see license file in the top folder
* see authors file in the top folder





