# Catena-X Knowledge Agents KIT (Hey Catena!) Agent Reference Implementations

This is a folder providing FOSS implementations of Hey Catena! agents that may be 
used in conjunction with the extended Eclipse Dataspace Connector.

We provide the following implementations for different roles

- [Matchmaking Agent](matchmaking) An agent implementation needed by consumer and all other dataspace participants which is able to interact with the Dataspace/EDC (and hence: other Matchmaking Agents behind) as well as potential internal agents providing data and functions (Binding Agents). Currently, this folder is empty because the reference matchmaking agent is yet tightly integrated into the [EDC data plane (Agent Plane)](../edc/agent-plane). This may change in the near future.
- Binding Agents are used by Providers to link the Dataspace to the actual Data and Functional Resources 
  - [Provisioning Agent](provisioning) An agent implementation for provider-side deployment which will bind typical backend data sources to the dataspace.
  - [Remoting Agent](remoting) An agent implementation for binding simple remote (REST) services to the dataspace.
  - [Anything Agent](anything) An agent implementation for binding xml, excel, json and other files to the dataspace via an approach called Facade-X.
- [Conforming Agent](conforming) An agent implementation that is used to test the conformity of all other parts of the architecture.

## Notice

* see copyright notice in the top folder
* see license file in the top folder
* see authors file in the top folder





