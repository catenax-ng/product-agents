# Tractus-X Knowledge Agents Reference Implementations (KA)

![GitHub contributors](https://img.shields.io/github/contributors/catenax-ng/product-agents)
![GitHub Org's stars](https://img.shields.io/github/stars/catenax-ng)
![GitHub](https://img.shields.io/github/license/catenax-ng/product-agents)
![GitHub all releases](https://img.shields.io/github/downloads/catenax-ng/product-agents/total)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=eclipse-tractusx_knowledge-agents&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=eclipse-tractusx_knowledge-agents)

KA is a product of the [Catena-X Knowledge Agents Kit](https://catenax-ng.github.io/product-knowledge) implementing the binding modules of the CX-0084 standard (Federated Queries in Dataspaces).

* See the [copyright notice](COPYRIGHT.md)
* See the [authors file](AUTHORS.md)
* See the [license file](LICENSE.md)
* See the [code of conduct](CODE_OF_CONDUCT.md)
* See the [contribution guidelines](CONTRIBUTING.md)
* See the [dependencies and their licenses](DEPENDENCIES.md)

## About the Project

This repository provides FOSS implementations for so-called Agents. 

An "Agent" is a (re- as well as pro-active) component which understands, partially elaborates and possibly delegates declarative scripts or queries (the so-called "Skills") over individual data and service assets ("Knowledge Graphs") of the dataspace.

Agents speak [Semantic Web Based](https://www.w3.org/2001/sw/wiki/Main_Page) dataspace protocols (such as [SPARQL](https://www.w3.org/2001/sw/wiki/SPARQL)) which are negotiated and 
transferred by the [Tractus-X Knowledge Agent Extensions for the Eclipse Dataspace Components (KA-EDC)](https://github.com/catenax-ng/product-agents-edc).
Binding Agents translate these protocols to your backend data storage or API.

We provide several of agent implementations in this product. 

- [Matchmaking Agent (KA-MATCH upcoming)](matchmaking) An agent implementation is needed by any Agent-Enabled dataspace participant to reason about the Dataspace/EDC (and hence: other Matchmaking Agents). It is also able to delegate work on sub-graphs/asset to tenant-internal agents (Binding Agents) providing actual data and functions. For Agent-Enabled dataspace consumers, deploying the Matchmaking Agent alongside an Agent-Enabled EDC is sufficient.
- Binding Agents are needed by any Agent-Enabled dataspace provider in order to link the Dataspace/Protocol layer to the actual Data and Functional Resources. Binding (sometimes: Bridging) is a technology which rather translates between protocols rather than translating data (Mapping).
  - [Provisioning Agent (KA-PROV)](provisioning) An agent implementation which binds typical SQL-based backend data sources to SPARQL.
  - [Remoting Agent (KA-RMT)](remoting) An agent implementation which binds typical REST services to SPARQL.
- [Conforming Agent (KA-CONF)](conforming) An agent implementation which may play any role (Matchmaking Agent, Binding Agent, EDC Transfer) in order to test the conformity of all other parts of the architecture/standard.

Included in this repository are ready-made [Helm charts](charts). 
They can be installed from the [Catena-X Knowledge Agents Kit Helm Repository](https://docs.catenax-ng.github.io/product-knowledge/infrastructure).

## Getting Started

### Build

To compile, package and containerize the binary artifacts (includes running the unit tests)

```shell
mvn package -Pwith-docker-image
```

To publish the binary artifacts (environment variables GITHUB_ACTOR and GITHUB_TOKEN must be set)

```shell
mvn -s settings.xml publish
```

### Deployment

* See the individual agent documentations
  * [Provisioning Agent](provisioning/README.md)
  * [Remoting Agent](remoting/README.md)
  * [Conforming Agent](conforming/README.md)



