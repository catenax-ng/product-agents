# Dependencies of Tractus-X Knowledge Agents Reference Implementations

The following is a simple type of single-level Software-BOM for all official open source products of Catena-X Knowledge Agents. 

* Product - The name of the Epic/Product (* for all)
* Component - The specific sub-component of the Epic/Product (* for all)
* Library/Module - The library or module that the Product/Component is depending on
* Stage - The kind of dependency 
  * Compile - The library is needed to compile the source code of the component into the target artifact (runtime)
  * Test - The library is needed to test the target artifact
  * Packaging - The library is needed to test the target artifact before, while and/or after packaging it
  * Runtime - The library is shipped as a part of the target artifact (runtime)
  * Provided - The library is not shipped as a part of the target artifact, but needed in it runtime
  * All - The library is needed at all Stages
* Version - the version of the library that the component is dependant upon
* License - the license identifier
* Comment - any further remarks on the kind of dependency

| Component | Library/Module  | Version | Stage | License | Comment |
| -- | --- | --- | --- | --- | ---| 
| * | [Apache Maven](https://maven.apache.org) | >=3.8 | Compile + Test + Packaging | Apache License 2.0 |     |
| * | Docker Engine | >=20.10.17 | Packaging + Provided | Apache License 2.0 |     |
| * | [kubernetes](https://kubernetes.io/de/)/[helm](https://helm.sh/) | >=1.20/3.9 | Provided | Apache License 2.0 |     |
| * | [Python](https://www.python.org/) | >=3.9 | Test + Packaging + Provided | Zero Clause BSD |     |
| * | [Java Runtime Environment (JRE)](https://de.wikipedia.org/wiki/Java-Laufzeitumgebung) | >=11 | Test + Provided | * | License (GPL, BCL, ...) depends on choosen runtime. |
| * | [Java Development Kit (JDK)](https://de.wikipedia.org/wiki/Java_Development_Kit) | >=11 | Compile + Packaging | * | License (GPL, BCL, ...) depends on choosen kit. |
| * | [Junit Jupiter](https://junit.org) | >=5 | Test | MIT |     |
| Agents>Provisioning | [Ontop VKP](https://ontop-vkg.org/) | >=4.2.1 | All | Apache License 2.0 |     |
| Agents>Provisioning | [Apache Calcite/Avatica](https://calcite.apache.org/avatica/) | >=1.22 | All | Apache License 2.0 |     |
| Agents>Provisioning | [H2 Database](http://h2database.com/) | >=2.1 | Runtime | Mozilla Public License (2.0) and Eclipse Public License (1.0) |     |
| Agents>Provisioning | [Dremio JDBC](https://docs.dremio.com/software/drivers/jdbc/) | >=22.1.1 | All | Proprietary |     |
| Agents>Remoting | [Eclipse RDF4J](https://rdf4j.org/) | >=3.7 | All | Eclipse Public License (1.0) |     |
| Agents>Remoting | [SLF4J](https://www.slf4j.org) | >=2.0.0 | All | MIT |     |
| Agents>Remoting | [Jackson](https://github.com/FasterXML/jackson) | >=2.13.4 | All | Apache License 2.0 |     |
| Agents>Remoting | [Apache HttpComponents](https://hc.apache.org/) | >=4.5.13 | All | Apache License 2.0 |     |
