
//
// Application to provide REST APIs as SPARQL services
// See copyright notice in the top folder
// See authors file in the top folder
// See license file in the top folder
//
package org.eclipse.tractusx.agents.remoting;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.query.MutableBindingSet;
import org.eclipse.rdf4j.query.algebra.Var;
import org.eclipse.rdf4j.sail.SailException;
import org.eclipse.tractusx.agents.remoting.callback.CallbackController;
import org.eclipse.tractusx.agents.remoting.config.ArgumentComparator;
import org.eclipse.tractusx.agents.remoting.config.ArgumentConfig;
import org.eclipse.tractusx.agents.remoting.config.ReturnValueConfig;
import org.eclipse.tractusx.agents.remoting.config.ServiceConfig;
import org.eclipse.tractusx.agents.remoting.util.BatchKey;
import org.eclipse.tractusx.agents.remoting.callback.CallbackToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Implements a (batch) invocation and represents an instance of the rdf:type cx-fx:Function
 * One function/invocation binding may result in several invocations as being
 * determined by the batch size of the config.
 * Invocation targets can be local java objects or remote REST services.
 */
@SuppressWarnings("ALL")
public class Invocation {

    protected static Logger logger = LoggerFactory.getLogger(Invocation.class);

    /** the config of the service invoked */
    public ServiceConfig service = null;
    /** unique key for the invocation */
    public IRI key = null;
    /** start time */
    public long startTime = -1;
    /** end time of the invocation */
    public long endTime = -1;
    /** success code */
    public int success = 0;
    /** input bindings */
    public Map<String, Var> inputs = new HashMap<>();
    /** output bindings */
    public Map<Var, IRI> outputs = new HashMap<>();
    /** the connection */
    protected final RemotingSailConnection connection;

    public static ObjectMapper objectMapper=new ObjectMapper();

    static {
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sssX"));
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public static SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");

    /**
     * creates a new invocaiton
     * @param connection the sourrounding graph connection
     */
    public Invocation(RemotingSailConnection connection) {
        this.connection = connection;
    }

    /**
     * converter from the literal to the type system
     * @param binding value to convert
     * @param target class to convert to
     * @return converted value
     */
    public static <Target> Target convertToObject(Value binding, Class<Target> target) throws SailException {
        if (target.isAssignableFrom(String.class)) {
            return (Target) binding.stringValue();
        } else if (target.isAssignableFrom(int.class)) {
            try {
                return (Target) Integer.valueOf(Integer.parseInt(binding.stringValue()));
            } catch(NumberFormatException nfe) {
                throw new SailException(String.format("Conversion from %s to %s failed.", binding, target), nfe);
            }
        } else if (target.isAssignableFrom(long.class)) {
            try {
                return (Target) Long.valueOf(Long.parseLong(binding.stringValue()));
            } catch(NumberFormatException nfe) {
                throw new SailException(String.format("Conversion from %s to %s failed.", binding, target), nfe);
            }
        } else if (target.isAssignableFrom(double.class)) {
            try {
                return (Target) Double.valueOf(Double.parseDouble(binding.stringValue()));
            } catch(NumberFormatException nfe) {
                throw new SailException(String.format("Conversion from %s to %s failed.", binding, target), nfe);
            }
        } else if (target.isAssignableFrom(float.class)) {
            try {
                return (Target) Float.valueOf(Float.parseFloat(binding.stringValue()));
            } catch(NumberFormatException nfe) {
                throw new SailException(String.format("Conversion from %s to %s failed.", binding, target), nfe);
            }
        } else if (target.isAssignableFrom(JsonNode.class)) {
            if(binding.isLiteral()) {
                IRI dataType=((Literal) binding).getDatatype();
                String dataTypeName=dataType.stringValue();
                switch(dataTypeName) {
                    case "http://www.w3.org/2001/XMLSchema#string":
                        return (Target) objectMapper.getNodeFactory().textNode(binding.stringValue());
                    case "http://www.w3.org/2001/XMLSchema#int":
                        try {
                            return (Target) objectMapper.getNodeFactory().numberNode(Integer.valueOf(binding.stringValue()));
                        } catch(NumberFormatException nfe) {
                            throw new SailException(String.format("Could not convert %s to datatype %s.", binding.stringValue(),dataTypeName),nfe);
                        }
                    case "http://www.w3.org/2001/XMLSchema#long":
                        try {
                            return (Target) objectMapper.getNodeFactory().numberNode(Long.valueOf(binding.stringValue()));
                        } catch(NumberFormatException nfe) {
                            throw new SailException(String.format("Could not convert %s to datatype %s.", binding.stringValue(),dataTypeName),nfe);
                        }
                    case "http://www.w3.org/2001/XMLSchema#double":
                        try {
                            return (Target) objectMapper.getNodeFactory().numberNode(Double.valueOf(binding.stringValue()));
                        } catch(NumberFormatException nfe) {
                            throw new SailException(String.format("Could not convert %s to datatype %s.", binding.stringValue(),dataTypeName),nfe);
                        }
                    case "http://www.w3.org/2001/XMLSchema#float":
                        try {
                            return (Target) objectMapper.getNodeFactory().numberNode(Float.valueOf(binding.stringValue()));
                        } catch(NumberFormatException nfe) {
                            throw new SailException(String.format("Could not convert %s to datatype %s.", binding.stringValue(),dataTypeName),nfe);
                        }
                    case "http://www.w3.org/2001/XMLSchema#dateTime":
                        try {
                            return (Target) objectMapper.getNodeFactory().textNode(objectMapper.getDateFormat().format(objectMapper.getDateFormat().parse(binding.stringValue())));
                        } catch(ParseException pe) {
                            throw new SailException(String.format("Could not convert %s to json date.", binding),pe);
                        }
                    case "http://www.w3.org/2001/XMLSchema#date":
                        try {
                            return (Target) objectMapper.getNodeFactory().textNode(dateFormat.format(dateFormat.parse(binding.stringValue())));
                        } catch(ParseException pe) {
                            throw new SailException(String.format("Could not convert %s to json date.", binding),pe);
                        }
                    case "https://json-schema.org/draft/2020-12/schema#Object":
                        try {
                            String representation=binding.stringValue();
                            // remove UTF8 linefeeds.
                            representation=representation.replace("\\x0A","");
                            return (Target) objectMapper.readTree(representation);
                        } catch(JsonProcessingException jpe) {
                            throw new SailException(String.format("Could not convert %s to json object.", binding),jpe);
                        }
                    default:
                        throw new SailException(String.format("Could not convert %s of data type %s.", binding,dataTypeName));
                }
            } else if(binding.isIRI()) {
                return (Target) objectMapper.getNodeFactory().textNode(binding.stringValue());
            } else {
                throw new SailException(String.format("Could not convert %s.", binding));
            }
        }
        throw new SailException(String.format("No conversion from %s to %s possible.", binding, target));
    }

    @Override
    public String toString() {
        return super.toString()+"/invocation";
    }

    /**
     * traverse path
     * @param source object
     * @param path under source
     * @return path object, will be source if path is empty
     */
    public static Object traversePath(Object source, String... path) throws SailException {
        if (logger.isTraceEnabled()) {
            logger.trace(String.format("Accessing a path of length %d under %s", path.length, source));
        }
        for (String elem : path) {
            if (elem != null && elem.length() > 0) {
                if (source instanceof Element) {
                    Element element = (Element) source;
                    if (element.hasAttribute(elem)) {
                        source = element.getAttribute(elem);
                    } else {
                        NodeList nl = element.getElementsByTagName(elem);
                        if (!(nl.getLength() > 0)) {
                            throw new SailException(String.format("No such path %s under object %s", elem, source));
                        }
                        source = nl.item(0);
                    }
                } else if (source instanceof JsonNode) {
                    JsonNode node = (JsonNode) source;
                    if (!node.has(elem)) {
                        throw new SailException(String.format("No such path %s under object %s", elem, source));
                    }
                    source = node.get(elem);
                } else {
                    throw new SailException(String.format("Cannot access path %s under object %s", elem, source));
                }
            }
        }
        return source;
    }

    /**
     * provide a string rep for a given object
     * @param source
     * @return string rep
     * @throws SailException
     */
    public static String convertObjectToString(Object source) throws SailException {
        if(source instanceof JsonNode) {
            JsonNode node=(JsonNode) source;
            if (node.isNumber() || node.isTextual()) {
                return node.asText();
            } else {
                try {
                    return objectMapper.writeValueAsString(node);
                } catch(JsonProcessingException jpe) {
                    throw new SailException(jpe);
                }
            }
        } else if(source instanceof Element) {
            TransformerFactory transFactory = TransformerFactory.newInstance();
            try {
                Transformer transformer = transFactory.newTransformer();
                StringWriter buffer = new StringWriter();
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                transformer.transform(new DOMSource((Element) source), new StreamResult(buffer));
                return buffer.toString();
            } catch (TransformerException e) {
                throw new SailException(e);
            }
        } else {
            return String.valueOf(source);
        }
    }

    /**
     * converter from the type system to a literal
     * @param target internal rep
     * @param resultKey eventual batch selector
     * @param output config name to use for mapping
     * @return mapped value
     * @throws SailException
     */
    public Value convertOutputToValue(Object target, String resultKey, IRI output) throws SailException {
        if (service.getResult().getOutputProperty() != null) {
            String[] resultPath=service.getResult().getOutputProperty().split("\\.");
            target = traversePath(target, resultPath);
        }
        ReturnValueConfig cf = service.getResult().getOutputs().get(output.stringValue());
        if (cf == null) {
            throw new SailException(String.format("No output specification for %s", output));
        }
        if (resultKey != null) {
            if (target.getClass().isArray()) {
                if(service.getResult().getResultIdProperty()!=null) {
                    String[] resultPath=service.getResult().getResultIdProperty().split("\\.");
                    target=Arrays.stream(((Object[]) target)).filter( tt -> resultKey.equals(convertObjectToString(traversePath(tt,resultPath))))
                            .findFirst().get();
                } else {
                    try {
                        target = Array.get(target, Integer.parseInt(resultKey));
                    } catch (NumberFormatException nfwe) {
                        throw new SailException(String.format("Could not access index %s of target %s which should be integer.", resultKey, target));
                    }
                }
            } else if (target instanceof ArrayNode) {
                if(service.getResult().getResultIdProperty()!=null) {
                    String[] resultPath=service.getResult().getResultIdProperty().split("\\.");
                    ArrayNode array=(ArrayNode) target;
                    boolean found=false;
                    for(int count=0;count<array.size();count++) {
                        if(resultKey.equals(convertObjectToString(traversePath(array.get(count),resultPath)))) {
                            target=array.get(count);
                            found=true;
                            break;
                        }
                    }
                    if(!found) {
                        throw new SailException(String.format("Could not find result with key %s under property %s.", resultKey,service.getResult().getResultIdProperty()));
                    }
                } else {
                    try {
                        target = ((ArrayNode) target).get(Integer.parseInt(resultKey));
                    } catch (NumberFormatException nfwe) {
                        throw new SailException(String.format("Could not access index %s of target %s which should be integer.", resultKey, target));
                    }
                }
            } else if (target instanceof Element) {
                if(service.getResult().getResultIdProperty()!=null) {
                    String[] resultPath=service.getResult().getResultIdProperty().split("\\.");
                    NodeList nl =((Element) target).getChildNodes();
                    boolean found=false;
                    for(int count=0;count<nl.getLength();count++) {
                        if(resultKey.equals(convertObjectToString(traversePath(nl.item(count),resultPath)))) {
                            target=nl.item(count);
                            found=true;
                            break;
                        }
                    }
                    if(!found) {
                        throw new SailException(String.format("Could not find result with key %s under property %s.", resultKey,service.getResult().getResultIdProperty()));
                    }
                } else {
                    try {
                        target = ((Element) target).getChildNodes().item(Integer.parseInt(resultKey));
                    } catch (NumberFormatException nfwe) {
                        throw new SailException(String.format("Could not access index %s of target %s which should be integer.", resultKey, target));
                    }
                }
            }
        }
        return convertOutputToValue(target, connection.remotingSail.getValueFactory(), cf.getPath(), cf.getDataType());
    }

    /**
     * converter from the type system to a literal
     * @param target source object
     * @param vf factory for creating literals
     * @param cfPath path under source object
     * @param dataType name of the target literal type
     * @return a literal
     */
    public static Value convertOutputToValue(Object target, ValueFactory vf, String cfPath, String dataType) throws SailException {
        String[] path = new String[0];
        if (cfPath != null) {
            path = cfPath.split("\\.");
        }
        Object pathObj = traversePath(target, path);
        switch (dataType) {
            case "https://json-schema.org/draft/2020-12/schema#Object":
                return vf.createLiteral(convertObjectToString(pathObj),vf.createIRI("https://json-schema.org/draft/2020-12/schema#Object"));
            case "http://www.w3.org/2001/XMLSchema#dateTime":
                return vf.createLiteral(convertObjectToString(pathObj),vf.createIRI("http://www.w3.org/2001/XMLSchema#dateTime"));
            case "http://www.w3.org/2001/XMLSchema#int":
                try {
                    return vf.createLiteral(Integer.parseInt(convertObjectToString(pathObj)));
                } catch(NumberFormatException nfwe) {
                    throw new SailException(String.format("Could not convert %s to integer.", String.valueOf(pathObj)));
                }
            case "http://www.w3.org/2001/XMLSchema#long":
                try {
                    return vf.createLiteral(Long.parseLong(convertObjectToString(pathObj)));
                } catch(NumberFormatException nfwe) {
                    throw new SailException(String.format("Could not convert %s to integer.", String.valueOf(pathObj)));
                }
            case "http://www.w3.org/2001/XMLSchema#double":
                try {
                    return vf.createLiteral(Double.parseDouble(convertObjectToString(pathObj)));
                } catch(NumberFormatException nfwe) {
                    throw new SailException(String.format("Could not convert %s to integer.", String.valueOf(pathObj)));
                }
            case "http://www.w3.org/2001/XMLSchema#float":
                try {
                    return vf.createLiteral(Float.parseFloat(convertObjectToString(pathObj)));
                } catch(NumberFormatException nfwe) {
                    throw new SailException(String.format("Could not convert %s to float.", String.valueOf(pathObj)));
                }
            case "http://www.w3.org/2001/XMLSchema#string":
                return vf.createLiteral(convertObjectToString(pathObj));
            case "http://www.w3.org/2001/XMLSchema#Element":
                return vf.createLiteral(convertObjectToString(pathObj),vf.createIRI("http://www.w3.org/2001/XMLSchema#Element"));// xml rendering?
            default:
                throw new SailException(String.format("Data Type %s is not supported.", dataType));
        }
    }

    /**
     * perform execution
     * @param connection sail connection in which to perform the invocation
     * @param host a binding host
     * @return flag indicating whether execution has been attempted (or was already
     *         done)
     */
    public boolean execute(RemotingSailConnection connection, IBindingHost host) throws SailException {
        
        startTime = System.currentTimeMillis();

        if (logger.isTraceEnabled()) {
            logger.trace(String.format("Starting execution on connection %s with binding host %s at clock %d",connection,host,startTime));
        }

        try {
            if (service.getMatcher().group("classType") != null) {
                    executeClass(connection, host);
            } else if (service.getMatcher().group("restType") != null) {
                    executeRest(connection, host);
                    return true;
            } else {
                    throw new SailException("No class or rest binding found.");
            }
            return true;
        } finally {
            endTime = System.currentTimeMillis();
        }
    }

    /**
     * perform REST based executions
     * @param connection sail connection in which to perform the invocation
     * @param host binding host
     */
    public void executeRest(RemotingSailConnection connection, IBindingHost host) throws SailException {
        if (logger.isTraceEnabled()) {
            logger.trace(String.format("About to invoke REST call to connection %s at host %s", connection,host));
        }
        try (final CloseableHttpClient httpclient = HttpClients.createDefault()) {
            String ourl = service.getMatcher().group("restType") + "://" + service.getMatcher().group("url");
            if (logger.isTraceEnabled()) {
                logger.trace(String.format("About to invoke REST call to %s ", ourl));
            }
            CloseableHttpResponse response = null;
            CallbackToken asyncToken=null;
            Iterator<Collection<MutableBindingSet>> batches;
            batches = produceBatches(host);
            for (int batchCount=0; batches.hasNext(); batchCount++) {
                Collection<MutableBindingSet> batch = batches.next();
                final String[] url = {ourl};
                switch (service.getMethod()) {
                    case "GET":
                        boolean isFirst = true;
                        for (MutableBindingSet binding : batch) {
                            if (logger.isTraceEnabled()) {
                                logger.trace(String.format("About to process binding set %s", binding));
                            }
                            if (batch.size() > 1) {
                                if (isFirst) {
                                    url[0] = url[0] + "?(";
                                } else {
                                    url[0] = url[0] + "&(";
                                }
                            } else {
                                if (isFirst) {
                                    url[0] = url[0] + "?";
                                } else {
                                    url[0] = url[0] + "&";
                                }
                            }
                            isFirst = false;
                            final boolean[] isFirstArg = {true};
                            service.getArguments().entrySet().stream().sorted(new ArgumentComparator()).forEach(argument -> {
                                if (logger.isTraceEnabled()) {
                                    logger.trace(String.format("About to process argument %s %s", argument.getKey(), argument.getValue()));
                                }
                                Var mapping = inputs.get(argument.getKey());
                                Value value;
                                if (mapping.hasValue()) {
                                    value = mapping.getValue();
                                } else {
                                    value = binding.getValue(mapping.getName());
                                }
                                Object render = convertToObject(value, String.class);
                                if (isFirstArg[0]) {
                                    url[0] = url[0] + argument.getValue().getArgumentName();
                                } else {
                                    url[0] = url[0] + "&" + argument.getValue().getArgumentName();
                                }
                                isFirstArg[0] = false;
                                url[0] = url[0] + "=" + render;
                            });
                            if (batch.size() > 1) {
                                url[0] = url[0] + ")";
                            }
                        }
                        if (logger.isTraceEnabled()) {
                            logger.trace(String.format("Instantiated REST call target with parameters to %s ", url[0]));
                        }
                        final HttpGet httpget = new HttpGet(url[0]);
                        if(service.getAuthentication()!=null) {
                            httpget.addHeader(service.getAuthentication().getAuthKey(),service.getAuthentication().getAuthCode());
                        }

                        if (logger.isDebugEnabled()) {
                            logger.debug(String.format("Performing %s ", httpget));
                        }
                        response = httpclient.execute(httpget);
                        break;

                    case "POST-JSON":
                    case "POST-JSON-MF":
                        ObjectMapper objectMapper = new ObjectMapper();

                        ObjectNode body = objectMapper.createObjectNode();
                        ObjectNode message = body;
                        ObjectNode input = body;
                        ArrayNode array = objectMapper.createArrayNode();

                        if (service.getInputProperty() != null) {
                            String[] path = service.getInputProperty().split("\\.");
                            for (int count = 0; count < path.length; count++) {
                                message = input;
                                input = objectMapper.createObjectNode();
                                message.set(path[count], input);
                            }
                            if (service.getBatch() > 1) {
                                message.set(path[path.length - 1], array);
                            }
                        } else {
                            if (service.getBatch() > 1) {
                                throw new SailException(String.format("Cannot use batch mode without inputProperty."));
                            }
                        }

                        final ObjectNode finalinput=input;
                        for (MutableBindingSet binding : batch) {
                            AtomicBoolean isCorrect= new AtomicBoolean(true);
                            service.getArguments().entrySet().stream().sorted(new ArgumentComparator()).forEach(argument -> {
                                if (logger.isTraceEnabled()) {
                                    logger.trace(String.format("About to process argument %s %s", argument.getKey(), argument.getValue()));
                                }
                                JsonNode render = resolve(binding,argument.getKey(),argument.getValue().getDefaultValue(),JsonNode.class);
                                if(render!=null) {
                                    String paths=argument.getValue().getArgumentName();
                                    Pattern pattern=Pattern.compile("\\{(?<arg>.*)\\}");
                                    Matcher matcher=pattern.matcher(paths);
                                    StringBuilder resultPaths=new StringBuilder();
                                    int end=0;
                                    while(matcher.find()) {
                                        resultPaths.append(paths.substring(end,matcher.start()));
                                        String result=resolve(binding,matcher.group("arg"),"",String.class);
                                        resultPaths.append(result);
                                        end=matcher.end();
                                    }
                                    resultPaths.append(paths.substring(end));
                                    setNode(objectMapper, finalinput, resultPaths.toString(), render);
                                } else {
                                    if(argument.getValue().isMandatory()) {
                                      // TODO optional arguments
                                      logger.warn(String.format("Mandatory argument %s has no binding. Leaving the hole tuple.", argument.getKey()));
                                      isCorrect.set(false);
                                    }                                
                                }
                            });
                            if(isCorrect.get()) {
                                array.add(input);
                            }
                            input = objectMapper.createObjectNode();
                        }

                        String invocationId=key.stringValue()+String.format("&batch=%d",batchCount);
                        if (service.getInvocationIdProperty() != null) {
                            if (!message.isObject()) {
                                throw new SailException(String.format("Cannot use invocationIdProperty in batch mode without inputProperty."));
                            } else {
                                setNode(objectMapper,((ObjectNode) message), service.getInvocationIdProperty(), objectMapper.getNodeFactory().textNode(invocationId));
                            }
                        }

                        if(service.getCallbackProperty() !=null) {
                            setNode(objectMapper,((ObjectNode) message), service.getCallbackProperty(),objectMapper.getNodeFactory().textNode(connection.remotingSail.config.getCallbackAddress()));
                            if(service.getResult().getCallbackProperty() !=null) {
                                asyncToken= CallbackController.register(service.getResult().getCallbackProperty(),invocationId);
                            }
                        }

                        if (logger.isTraceEnabled()) {
                            logger.trace(String.format("Derived body %s", body));
                        }

                        final HttpPost httppost = new HttpPost(url[0]);
                        httppost.addHeader("accept","application/json");
                        if(service.getAuthentication() !=null) {
                            httppost.addHeader(service.getAuthentication().getAuthKey(), service.getAuthentication().getAuthCode());
                        }

                        if (service.getMethod().equals("POST-JSON")) {
                            httppost.addHeader("Content-Type","application/json");
                            httppost.setEntity(new StringEntity(objectMapper.writeValueAsString(body)));
                        } else {
                            MultipartEntityBuilder mpeb = MultipartEntityBuilder.create();
                            mpeb.setBoundary("XXX");
                            Iterator<String> fields = body.fieldNames();
                            while (fields.hasNext()) {
                                String field = fields.next();
                                JsonNode node = body.get(field);
                                String content = objectMapper.writeValueAsString(node);
                                mpeb.addBinaryBody(field, content.getBytes(), ContentType.APPLICATION_JSON, field + ".json");
                            }
                            httppost.setEntity(mpeb.build());
                        }
                        if (logger.isDebugEnabled()) {
                            logger.debug(String.format("Performing %s ", httppost));
                        }
                        response = httpclient.execute(httppost);
                        break;

                    default:
                        throw new SailException(String.format("Cannot invoke method %s", service.getMethod()));
                }

                int lsuccess = response.getStatusLine().getStatusCode();
                if (lsuccess >= 200 && lsuccess < 300) {
                    try {
                        Object result;

                        final HttpEntity entity = response.getEntity();
                        boolean isJson = false;
                        boolean isXml = false;
                        for (Header contentType : response.getHeaders("Content-Type")) {
                            if (contentType.getValue().contains("json")) {
                                isJson = true;
                            } else if (contentType.getValue().contains("xml")) {
                                isXml = true;
                            }
                        }

                        if (isXml) {
                            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                            DocumentBuilder builder = factory.newDocumentBuilder();
                            ByteArrayInputStream in = new ByteArrayInputStream(EntityUtils.toByteArray(entity));
                            result = builder.parse(in).getDocumentElement();
                        } else if (isJson) {
                            ObjectMapper mapper = new ObjectMapper();
                            ByteArrayInputStream in = new ByteArrayInputStream(EntityUtils.toByteArray(entity));
                            result = mapper.readTree(in);
                        } else {
                            result = EntityUtils.toString(entity);
                        }

                        if(asyncToken!=null) {
                            result=CallbackController.synchronize(asyncToken);
                        }

                        if(result==null) {
                            logger.warn(String.format("Did not get any response."));
                            success=Math.max(success,500);
                        } else {
                            for (MutableBindingSet binding : batch) {
                                String key = null;
                                if (service.getResult().getCorrelationInput() != null) {
                                    key = resolve(binding, service.getResult().getCorrelationInput(),null,String.class);
                                } else if(service.getBatch() >1) {
                                    key = "0";
                                }
                                for (Map.Entry<Var, IRI> output : outputs.entrySet()) {
                                    binding.addBinding(output.getKey().getName(), convertOutputToValue(result, key, output.getValue()));
                                }
                            }
                        }
                    } catch (Exception e) {
                        logger.warn(String.format("Got an exception %s when processing invocation results of %s. Ignoring.",e,ourl));
                        success=Math.max(success,500);
                    }
                } else {
                    logger.warn(String.format("Got an unsuccessful status %d from invoking %s. Ignoring.",lsuccess,ourl));
                    success = Math.max(lsuccess,success);
                }
            }
        } catch(IOException ioe){
            logger.warn(String.format("Got an exception %s when processing invocation. Ignoring.",ioe));
            success = Math.max(500,success);
        }
    }

    /**
     * resolves a given input predicate against a  binding
     * @param binding the binding
     * @param input predicate as uri string
     * @param defaultValue a possible default value
     * @param forClass target class to convert binding into
     * @return found binding of predicate, null if not bound
     * @param <TargetClass>
     */
    private <TargetClass> TargetClass resolve(MutableBindingSet binding, String input, Object defaultValue, Class<TargetClass> forClass) {
        String key;
        Var variable = inputs.get(input);
        Value value=null;
        if(variable!=null) {
            if (variable.hasValue()) {
                value = variable.getValue();
            } else {
                value = binding.getValue(variable.getName());
            }
        }

        if(value!=null) {
            return convertToObject(value, forClass);
        }

        if (defaultValue!=null) {
            return (TargetClass) defaultValue;
        }

        return null;
    }

    /**
     * produces a set of batches to call
     * @param host binding host
     * @return an iterator over the batches as collections of bindingsets
     */
    protected Iterator<Collection<MutableBindingSet>> produceBatches(IBindingHost host) {
        var batchGroup= service.getArguments().entrySet().stream().filter(argument -> argument.getValue().isFormsBatchGroup()).
                collect(Collectors.toList());
        final Map<Object,Collection<MutableBindingSet>> batches=new HashMap<>();
        long bindingCount=0;
        for(MutableBindingSet binding : host.getBindings()) {
            bindingCount++;
            Object key;
            if(batchGroup.isEmpty()) {
                if(service.getBatch() >1) {
                    key=bindingCount / service.getBatch();
                } else {
                    key=bindingCount;
                }
            } else {
                key = new BatchKey(batchGroup.stream().map(
                        batch -> resolve(binding, batch.getKey(), null, String.class)
                ).toArray(size -> new String[size]));
            }
            Collection<MutableBindingSet> targetCollection;
            if (batches.containsKey(key)) {
                targetCollection = batches.get(key);
            } else {
               targetCollection = new ArrayList<>();
               batches.put(key, targetCollection);
            }
            targetCollection.add(binding);
        }
        return batches.values().iterator();
    }

    /**
     * sets a given node under a possible recursive path
     * @param objectMapper factory
     * @param finalinput target subject
     * @param pathSpec a path sepcification
     * @param render the target object
     */
    public static void setNode(ObjectMapper objectMapper, ObjectNode finalinput, String pathSpec, JsonNode render) {
        String[] pathNames = pathSpec.split(",");
        for(String pathName : pathNames) {
            String[] argPath = pathName.split("\\.");
            JsonNode traverse = finalinput;
            int depth = 0;
            if (argPath.length==depth) {
                finalinput.setAll((ObjectNode)render);
                return;
            }
            for (String argField : argPath) {
                if (depth != argPath.length - 1) {
                    if (hasField(traverse, argField)) {
                        JsonNode next = getField(traverse, argField);
                        if (next==null || (!next.isArray() && !next.isObject()) ) {
                            throw new SailException(String
                                    .format("Field %s was occupied by a non-object object", argField,next));
                        } else {
                            traverse = next;
                        }
                    } else {
                        ObjectNode next = objectMapper.createObjectNode();
                        setObject(objectMapper,traverse,argField,next);
                        traverse = next;
                    }
                } else {
                    setObject(objectMapper, traverse, argField, render);
                }
                depth++;
            } // set argument in input
        }
    }

    /**
     * accesses a certain field
     * @param traverse object or array
     * @param argField field name or index
     * @return embedded object
     */
    public static JsonNode getField(JsonNode traverse, String argField) {
        return traverse.isArray() ? ((ArrayNode) traverse).get(Integer.parseInt(argField)) : traverse.get(argField);
    }

    /**
     * checks whether this node has a certain field
     * @param traverse object or array
     * @param argField field name or index
     * @return existance of the field
     */
    public static boolean hasField(JsonNode traverse, String argField) {
        return traverse.isArray() ? traverse.size() > Integer.parseInt(argField) : traverse.has(argField);
    }

    /**
     * sets the given object into the given single-level path into a given subject
     * @param objectMapper factory
     * @param render target object
     * @param traverse target subject
     * @param argField target field
     */
    public static void setObject(ObjectMapper objectMapper, JsonNode traverse, String argField, JsonNode render) {
        if(traverse.isObject()) {
            ((ObjectNode) traverse).set(argField, render);
        } else if(traverse.isArray()) {
            ArrayNode traverseArray=(ArrayNode)  traverse;
            int targetIndex=Integer.valueOf(argField);
            while(traverseArray.size()<=targetIndex) {
                traverseArray.add(objectMapper.createObjectNode());
            }
            traverseArray.set(targetIndex, render);
        }
    }

    /**
     * perform class-based reflection execution 
     * @param connection sail connection in which to perform the invocation
     * @param host binding host
     */
    public void executeClass(RemotingSailConnection connection, IBindingHost host) throws SailException {
        if (logger.isTraceEnabled()) {
            logger.trace(String.format("About to invoke Java call to connection %s at host %s", connection,host));
        }
        Class targetClass = null;
        try {
            targetClass = getClass().getClassLoader().loadClass(service.getMatcher().group("class"));
        } catch (ClassNotFoundException e) {
            throw new SailException(e);
        }
        if (logger.isTraceEnabled()) {
            logger.trace(String.format("Found class %s ", targetClass));
        }
        Object targetInstance = null;
        try {
            targetInstance = targetClass.getConstructor().newInstance();
        } catch (NoSuchMethodException e) {
            throw new SailException(e);
        } catch (IllegalAccessException e) {
            throw new SailException(e);
        } catch (InstantiationException e) {
            throw new SailException(e);
        } catch (InvocationTargetException e) {
            throw new SailException(e.getCause());
        }
        for(MutableBindingSet binding : host.getBindings()) {
            Method targetMethod = null;
            Object[] targetParams = null;
            try {
                for (Method meth : targetClass.getMethods()) {
                    if (meth.getName().equals(service.getMatcher().group("method"))) {
                        if (logger.isTraceEnabled()) {
                            logger.trace(String.format("Found method %s ", meth));
                        }
                        targetParams = new Object[meth.getParameterTypes().length];
                        int argIndex = 0;
                        for (Parameter param : meth.getParameters()) {
                            if (logger.isTraceEnabled()) {
                                logger.trace(String.format("Checking parameter %s", param));
                            }
                            ArgumentConfig aconfig = null;
                            for (Map.Entry<String, ArgumentConfig> argument : service.getArguments().entrySet()) {
                                if (logger.isTraceEnabled()) {
                                    logger.trace(String.format("Agains argument %s %s", argument.getKey(),
                                            argument.getValue().getArgumentName()));
                                }
                                if (argument.getValue().getArgumentName().contains(param.getName())) {
                                    aconfig = argument.getValue();
                                    Var arg = inputs.get(argument.getKey());
                                    Value value;
                                    if(!arg.hasValue()) {
                                        value = binding.getValue(arg.getName());
                                    } else {
                                        value = arg.getValue();
                                    }
                                    targetParams[argIndex++] = convertToObject(value, param.getType());
                                    break;
                                }
                            }
                            if (logger.isTraceEnabled()) {
                                logger.trace(String.format("Parameter %s resulted to argument %s ", param, aconfig));
                            }
                            if (aconfig == null) {
                                targetParams = null;
                                break;
                            }
                        }
                        if (targetParams != null) {
                            targetMethod = meth;
                            break;
                        }
                    }
                }
                if (targetMethod == null) {
                    throw new SailException(
                            String.format("Target method %s with suitable arguments could not be found in class %s.",
                                    service.getMatcher().group("method"), targetClass));
                }
            } finally {
            }
            try {
                Object result = targetMethod.invoke(targetInstance, targetParams);
                for(Map.Entry<Var,IRI> output : outputs.entrySet()) {
                    binding.addBinding(output.getKey().getName(),convertOutputToValue(result,null,output.getValue()));
                }
            } catch (Exception e) {
                logger.warn(String.format("Invocation to %s (method %s) resulted in exception %s",targetInstance,targetMethod,e));
                success = Math.max(500,success);
            }
        }
    }

}