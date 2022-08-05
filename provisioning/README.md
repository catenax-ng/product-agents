# Catena-X Knowledge Agents (Hey Catena!) Provisioning Agent

This is a folder providing the FOSS implementations of a Hey Catena! agent for provider-side deployment which will bind typical backend data sources to the dataspace.

It relies on the [Ontop Virtual Knowledge Graph System](https://ontop-vkg.org) for mapping relational data sources into knowledge graphs (or rather: for translating SparQL queries into SQL queries).

## Containerizing

```console
docker build -t ghcr.io/catenax-ng/product-knowledge/dataspace/provisioning-agent:0.5.1 .
```

## Interact with the Provisioning Agent

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





