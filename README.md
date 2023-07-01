# Tractus-X Knowledge Agents (Hey Catena!) Reference Implementations

This repository provides FOSS implementations for the [Catena-X Knowledge Agents](https://catenax-ng.github.io/product-knowledge/docs/adoption-view/CX-0084-Federated_Queries_In_Data_Spaces_v1.0.0) standard. These implementations may be used in conjunction with the [Tractus-X Extensions for the Eclipse Dataspace Components](https://github.com/catenax-ng/product-agents-edc).

We provide the following implementations for different roles

- [Matchmaking Agent](matchmaking) An agent implementation needed by consumer and all other dataspace participants which is able to interact with the Dataspace/EDC (and hence: other Matchmaking Agents behind) as well as potential internal agents providing data and functions (Binding Agents). Currently, this folder is empty because the reference matchmaking agent is yet integrated into the [EDC data plane (Agent Plane)](http://github.com/eclipse-tractusx/knowledge-agents-edc). This may change in the near future.
- Binding Agents are used by Providers to link the Dataspace to the actual Data and Functional Resources 
  - [Provisioning Agent](provisioning) An agent implementation for provider-side deployment which will bind typical backend data sources to the dataspace.
  - [Remoting Agent](remoting) An agent implementation for binding simple remote (REST) services to the dataspace.
  - [Anything Agent](anything) An agent implementation for binding xml, excel, json and other files to the dataspace via an approach called Facade-X.
- [Conforming Agent](conforming) An agent implementation that is used to test the conformity of all other parts of the architecture.

The Tractus-X Knowledge Agents Reference Implementations are a product of the [Catena-X Knowledge Agents Kit](https://catenax-ng.github.io/product-knowledge)

* See the [copyright notice](COPYRIGHT.md)
* See the [authors file](AUTHORS.md)
* See the [license file](LICENSE.md)
* See the [code of conduct](CODE_OF_CONDUCT.md)
* See the [contribution guidelines](CONTRIBUTING.md)
* See the [dependencies and their licenses](DEPENDENCIES.md)
