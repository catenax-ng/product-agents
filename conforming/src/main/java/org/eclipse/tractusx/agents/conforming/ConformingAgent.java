//
// Conforming Agent Implementation
// See copyright notice in the top folder
// See authors file in the top folder
// See license file in the top folder
//
package org.eclipse.tractusx.agents.conforming;

import org.eclipse.tractusx.agents.conforming.api.AgentApi;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;

import org.eclipse.tractusx.agents.conforming.api.NotFoundException;

import javax.ws.rs.core.*;
import javax.validation.constraints.*;

public class ConformingAgent extends AgentApi {

    public static String emptyJson="{\n" +
            "    \"head\": {\n" +
            "        \"vars\": [\n" +
            "        ]\n" +
            "    },\n" +
            "    \"results\": {\n" +
            "        \"bindings\": [\n" +
            "        ]\n" +
            "    }\n" +
            "}";

    public static String simpleJson="{\n" +
            "    \"head\": {\n" +
            "        \"vars\": [\n" +
            "            \"subject\",\n" +
            "            \"predicate\",\n" +
            "            \"object\"\n" +
            "        ]\n" +
            "    },\n" +
            "    \"results\": {\n" +
            "        \"bindings\": [\n" +
            "            {\n" +
            "                \"subject\": {\n" +
            "                    \"type\": \"uri\",\n" +
            "                    \"value\": \"urn:cx:AnonymousSerializedPart#GB4711\"\n" +
            "                },\n" +
            "                \"predicate\": {\n" +
            "                    \"type\": \"uri\",\n" +
            "                    \"value\": \"https://github.com/catenax-ng/product-knowledge/ontology/cx.ttl#hasPartType\"\n" +
            "                },\n" +
            "                \"object\": {\n" +
            "                    \"type\": \"literal\",\n" +
            "                    \"value\": \"engine control module\",\n" +
            "                    \"datatype\": \"http://www.w3.org/1999/02/22-rdf-syntax-ns#langString\",\n" +
            "                    \"xml:lang\": \"en\"\n" +
            "                }\n" +
            "            }\n" +
            "        ]\n" +
            "    }\n" +
            "}";

    public static String simpleBindJson="{\n" +
            "    \"head\": {\n" +
            "        \"vars\": [\n" +
            "            \"subject\",\n" +
            "            \"predicate\",\n" +
            "            \"object\",\n" +
            "            \"bindingVar\"\n" +
            "        ]\n" +
            "    },\n" +
            "    \"results\": {\n" +
            "        \"bindings\": [\n" +
            "            {\n" +
            "                \"bindingVar\": {\n" +
            "                    \"type\": \"literal\",\n" +
            "                    \"value\": \"0\"\n" +
            "                },\n" +
            "                \"subject\": {\n" +
            "                    \"type\": \"uri\",\n" +
            "                    \"value\": \"urn:cx:AnonymousSerializedPart#GB4711\"\n" +
            "                },\n" +
            "                \"predicate\": {\n" +
            "                    \"type\": \"uri\",\n" +
            "                    \"value\": \"https://github.com/catenax-ng/product-knowledge/ontology/cx.ttl#hasPartType\"\n" +
            "                },\n" +
            "                \"object\": {\n" +
            "                    \"type\": \"literal\",\n" +
            "                    \"value\": \"engine control module\",\n" +
            "                    \"datatype\": \"http://www.w3.org/1999/02/22-rdf-syntax-ns#langString\",\n" +
            "                    \"xml:lang\": \"en\"\n" +
            "                }\n" +
            "            }\n" +
            "        ]\n" +
            "    }\n" +
            "}";

    public static String emptyXml="<?xml version=\"1.0\"?>\n" +
            "<sparql xmlns=\"http://www.w3.org/2005/sparql-results#\">\n" +
            "    <head>\n" +
            "    </head>\n" +
            "    <results>\n" +
            "    </results>\n" +
            "</sparql>";

    public static String simpleXml="<?xml version=\"1.0\"?>\n" +
            "<sparql xmlns=\"http://www.w3.org/2005/sparql-results#\">\n" +
            "    <head>\n" +
            "        <variable name=\"subject\"/>\n" +
            "        <variable name=\"predicate\"/>\n" +
            "        <variable name=\"object\"/>\n" +
            "    </head>\n" +
            "    <results>\n" +
            "        <result>\n" +
            "            <binding name=\"subject\">\n" +
            "                <uri>urn:cx:AnonymousSerializedPart#GB4711</uri>\n" +
            "            </binding>\n" +
            "            <binding name=\"predicate\">\n" +
            "                <uri>https://github.com/catenax-ng/product-knowledge/ontology/cx.ttl#hasPartType</uri>\n" +
            "            </binding>\n" +
            "            <binding name=\"object\">\n" +
            "                <literal xml:lang=\"en\" datatype=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#langString\">engine control module</literal>\n" +
            "            </binding>\n" +
            "        </result>\n" +
            "    </results>\n" +
            "</sparql>";

    public static String simpleBindXml="<?xml version=\"1.0\"?>\n" +
            "<sparql xmlns=\"http://www.w3.org/2005/sparql-results#\">\n" +
            "    <head>\n" +
            "        <variable name=\"subject\"/>\n" +
            "        <variable name=\"predicate\"/>\n" +
            "        <variable name=\"object\"/>\n" +
            "        <variable name=\"bindingVar\"/>\n" +
            "    </head>\n" +
            "    <results>\n" +
            "        <result>\n" +
            "            <binding name=\"bindingVar\">\n" +
            "                <literal>0</literal>\n" +
            "            </binding>\n" +
            "            <binding name=\"subject\">\n" +
            "                <uri>urn:cx:AnonymousSerializedPart#GB4711</uri>\n" +
            "            </binding>\n" +
            "            <binding name=\"predicate\">\n" +
            "                <uri>https://github.com/catenax-ng/product-knowledge/ontology/cx.ttl#hasPartType</uri>\n" +
            "            </binding>\n" +
            "            <binding name=\"object\">\n" +
            "                <literal xml:lang=\"en\" datatype=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#langString\">engine control module</literal>\n" +
            "            </binding>\n" +
            "        </result>\n" +
            "    </results>\n" +
            "</sparql>";

    public static MediaType srj = MediaType.valueOf("application/sparql-results+json");
    public static MediaType srx = MediaType.valueOf("application/sparql-results+xml");
    public static MediaType sq = MediaType.valueOf("application/sparql-query");

    public boolean useSimple=true;
    public int status=200;
    public String warnings = "[ { \"source-tenant\": \"BPNL00000003CQI9\", \"target-tenant\": \"BPNL00000003COJN\", \"target-asset\": \"urn:cx:GraphAsset#TestAsset\", \"problem\": \"Could not access graph.\", \"context\": \"SPARQL-QUERY 1027\" } ]";

    protected MediaType getDefaultResultType() {
        if(headers.getHeaderString("Accept")!=null) {
            String[] compartments = headers.getHeaderString("Accept").split(",");
            for (String compartment : compartments) {
                String[] qualifiers = compartment.split(";");
                try {
                    MediaType wantedEncoding = MediaType.valueOf(qualifiers[0]);
                    if (wantedEncoding.isCompatible(srj)) {
                        return srj;
                    } else if (wantedEncoding.isCompatible(srx)) {
                        return srx;
                    }
                } catch(IllegalArgumentException iae) {
                    System.err.printf("Warning: Ingoring unsupported accepted mediatype %s%n",qualifiers[0]);
                }
            }
            return null;
        }
        return srj;
    }

    /** produces a standard response */
    protected Map<String,byte[]> computeBody(MediaType resultType, String bindingVar) {
        String target;
        if(useSimple) {
            if(resultType.isCompatible(srj)) {
                target = getSimpleJson(bindingVar);
            } else {
                target=getSimpleXml(bindingVar);
            }
        } else {
            if(resultType.isCompatible(srj)) {
                target=emptyJson;
            } else {
                target=emptyXml;
            }
        }
        return Map.of(resultType.toString(),target.getBytes());
    }


    protected String getSimpleJson(String bindingVar) {
        if(bindingVar!=null) {
            return simpleBindJson.replaceAll("bindingVar",bindingVar);
        }
        return simpleJson;
    }

    protected String getSimpleXml(String bindingVar) {
        if(bindingVar!=null) {
            return simpleBindXml.replaceAll("bindingVar", bindingVar);
        } else {
            return simpleXml;
        }
    }

    /** produces a standard response */
    protected Response.ResponseBuilder compute(MediaType resultType, String bindingVar) {
        AtomicReference<Response.ResponseBuilder> response= new AtomicReference<>(Response.status(status));
        Map<String,byte[]> body=computeBody(resultType, bindingVar);
        body.forEach((key, value) -> response.set(response.get().type(key).entity(value)));
        return response.get();
    }

    protected Response annotate(Response.ResponseBuilder builder) {
        return builder.build();
    }

    java.util.regex.Pattern pattern= java.util.regex.Pattern.compile("\\?(binding[0-9]+)");

    @Override
    public Response getAgent(String asset, String queryLn, String query, String _vin, List<String> troubleCode) throws NotFoundException {
        MediaType resultType=getDefaultResultType();
        if(resultType==null) {
            return annotate(Response.status(400,"KA-BIND/KA-MATCH: Only supports application/sparql-results+json|xml compatible Accept header"));
        }
        String bindingVar=null;
        if(query!=null) {
            Matcher matcher=pattern.matcher(query);
            if(matcher.find()) {
                bindingVar=matcher.group(1);
            }
        }
        return annotate(compute(resultType,bindingVar));
    }

    @Override
    public Response postAgent(Object body, String asset,  String queryLn, String query, String _vin,  List<String> troubleCode) throws NotFoundException {
        MediaType resultType=getDefaultResultType();
        if(resultType==null) {
            return annotate(Response.status(400,"KA-BIND/KA-MATCH: Only supports application/sparql-results+json|xml compatible Accept header"));
        }
        MediaType bodyType=MediaType.valueOf(headers.getHeaderString("Content-Type"));
        if(!bodyType.isCompatible(sq) && !bodyType.isCompatible(srj) && !bodyType.isCompatible(srx)) {
            return annotate(Response.status(400,"KA-BIND/KA-MATCH postAgent only accepts application/sparql-query|results+json|xml in body."));
        }
        String bindingVar=null;
        String toCheck=query;
        if(query==null) {
            toCheck=String.valueOf(body);
        }
        if(toCheck!=null) {
            Matcher matcher=pattern.matcher(toCheck);
            if(matcher.find()) {
                bindingVar=matcher.group(1);
            }
        }
        return annotate(compute(resultType,bindingVar));
    }

    @Override
    public Response postSkill(String body,  @NotNull String asset) throws NotFoundException {
        if(asset.length()==0) {
            return Response.status(400,"KA-BIND/KA-MATCH postSkill requires a non-empty asset name.").build();
        }
        MediaType bodyType=MediaType.valueOf(headers.getHeaderString("Content-Type"));
        if(body!=null || body.isEmpty() || !bodyType.isCompatible(sq)) {
            return Response.status(400,"KA-BIND/KA-MATCH postSkill only accepts application/sparql-query|results+json|xml in body.").build();
        }
        return Response.ok().build();
    }
}
