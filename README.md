# Tractus-X Knowledge Agents (Hey Catena!) Reference Implementations

This repository provides FOSS implementations for the 
[Catena-X Standard on Federated Queries in Dataspaces](https://catenax-ng.github.io/product-knowledge/docs/adoption-view/CX-0084-Federated_Queries_In_Data_Spaces_v1.0.0) which builds
on the [Semantic Web body](https://www.w3.org/2001/sw/wiki/Main_Page).

These implementations may be used in conjunction with the 
[Tractus-X Extensions for the Eclipse Dataspace Components](https://github.com/catenax-ng/product-agents-edc).

According to the architecture behind the standard, an "Agent" is a (re- as well as pro-active) component which understands, partially elaborates and possibly delegates declarative scripts or queries (the so-called "Skills") over individual data and service assets ("Knowledge Graphs") of the dataspace. 

"Agents" can be found in different roles of the architecture. Hence, we provide several components in this product. All of them are targeted to implement the [SPARQL](https://www.w3.org/2001/sw/wiki/SPARQL) protocol.

- [Matchmaking Agent (upcoming)](matchmaking) An agent implementation is needed by any Agent-Enabled dataspace participant to reason about the Dataspace/EDC (and hence: other Matchmaking Agents). It is also able to delegate work on sub-graphs/asset to tenant-internal agents (Binding Agents) providing actual data and functions. For Agent-Enabled dataspace consumers, deploying the Matchmaking Agent alongside an Agent-Enabled EDC is sufficient.
- Binding Agents are needed by any Agent-Enabled dataspace provider in order to link the Dataspace/Protocol layer to the actual Data and Functional Resources. Binding (sometimes: Bridging) is a technology which rather translates between protocols rather than translating data (Mapping).
  - [Provisioning Agent](provisioning) An agent implementation which binds typical SQL-based backend data sources to SPARQL.
  - [Remoting Agent](remoting) An agent implementation which binds typical REST services to SPARQL.
  - [Anything Agent](anything) An agent implementation which binds files in xml, excel, json and other formats to SPARQL.
- [Conforming Agent](conforming) An agent implementation which may play any role (Matchmaking Agent, Binding Agent, EDC Transfer) in order to test the conformity of all other parts of the architecture/standard.

The Tractus-X Knowledge Agents Reference Implementations are a product of the [Catena-X Knowledge Agents Kit](https://catenax-ng.github.io/product-knowledge)

Included in this repository are ready-made [Helm charts](charts). They can be installed from the [Catena-X Knowledge Agents Kit Helm Repository](https://docs.catenax-ng.github.io/product-knowledge/infrastructure).

Please also refer to:

* See the [copyright notice](COPYRIGHT.md)
* See the [authors file](AUTHORS.md)
* See the [license file](LICENSE.md)
* See the [code of conduct](CODE_OF_CONDUCT.md)
* See the [contribution guidelines](CONTRIBUTING.md)
* See the [dependencies and their licenses](DEPENDENCIES.md)

## Building

## Deploying



