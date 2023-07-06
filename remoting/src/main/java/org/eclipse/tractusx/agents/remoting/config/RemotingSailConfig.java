//
// Application to provide REST APIs as SPARQL services
// See copyright notice in the top folder
// See authors file in the top folder
// See license file in the top folder
//
package org.eclipse.tractusx.agents.remoting.config;

import com.fasterxml.jackson.databind.JsonNode;
import org.eclipse.rdf4j.sail.config.AbstractSailImplConfig;
import org.eclipse.rdf4j.sail.config.SailConfigException;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.Models;

import org.eclipse.tractusx.agents.remoting.Invocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;

/**
 * A remoting SAIL config captures anything that a remoting
 * inference service needs to know to expose its capabilities.
 */
public class RemotingSailConfig extends AbstractSailImplConfig {
    /** logger */
    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * constants
     */
    public static String CONFIG_NAMESPACE="https://w3id.org/catenax/ontology/function#";
    public static String COMMON_NAMESPACE="https://w3id.org/catenax/ontology/common#";
    public static String CALLBACK_NAME="callbackAddress";
    public static String FUNCTION_NAME="Function";
    public static String AUTHENTICATION_CODE="authenticationCode";
    public static String AUTHENTICATION_KEY="authenticationKey";
    public static String RESULT_NAME="Result";
    public static String ARGUMENT_NAME="Argument";
    public static String INPUT_ATTRIBUTE="input";
    public static String RESULT_ATTRIBUTE="result";
    public static String BATCH_ATTRIBUTE="batch";
    public static String CALLBACK_ATTRIBUTE="callbackProperty";
    public static String INVOCATION_ID_ATTRIBUTE="invocationIdProperty";
    public static String INPUT_PROPERTY_ATTRIBUTE="inputProperty";
    public static String RESULT_ID_ATTRIBUTE="resultIdProperty";
    public static String CORRELATION_INPUT_ATTRIBUTE="correlationInput";
    public static String OUTPUT_PROPERTY_ATTRIBUTE="outputProperty";
    public static String ARGUMENT_ATTRIBUTE="argumentName";
    public static String MANDATORY_ATTRIBUTE="mandatory";
    public static String DEFAULT_ATTRIBUTE="default";
    public static String GROUP_ATTRIBUTE="formsBatchGroup";
    public static String PRIORITY_ATTRIBUTE="priority";
    public static String PATH_ATTRIBUTE="valuePath";
    public static String TYPE_RELATION="dataType";
    public static String OUTPUT_ATTRIBUTE="output";
    public static String INVOCATION_PROPERTY="supportsInvocation";
    public static String URL_ATTRIBUTE="targetUri";
    public static String METHOD_ATTRIBUTE="invocationMethod";

    /** when interacting with parser/exporter */
    protected ValueFactory vf = SimpleValueFactory.getInstance();

    /**
     * iri for predicate
     */
    protected IRI SUPPORTS_INVOCATION_PREDICATE=vf.createIRI(CONFIG_NAMESPACE,INVOCATION_PROPERTY);
    protected IRI CALLBACK_ADDRESS_PREDICATE=vf.createIRI(CONFIG_NAMESPACE,CALLBACK_NAME);
    protected IRI TARGET_URI_PREDICATE=vf.createIRI(CONFIG_NAMESPACE, URL_ATTRIBUTE);
    protected IRI INVOCATION_METHOD_PREDICATE=vf.createIRI(CONFIG_NAMESPACE, METHOD_ATTRIBUTE);
    protected IRI INPUT_PREDICATE=vf.createIRI(CONFIG_NAMESPACE, INPUT_ATTRIBUTE);
    protected IRI OUTPUT_PREDICATE=vf.createIRI(CONFIG_NAMESPACE, OUTPUT_ATTRIBUTE);
    protected IRI RESULT_PREDICATE=vf.createIRI(CONFIG_NAMESPACE, RESULT_ATTRIBUTE);
    protected IRI ARGUMENT_NAME_PREDICATE=vf.createIRI(CONFIG_NAMESPACE, ARGUMENT_ATTRIBUTE);
    protected IRI MANDATORY_PREDICATE=vf.createIRI(CONFIG_NAMESPACE, MANDATORY_ATTRIBUTE);
    protected IRI DEFAULT_PREDICATE=vf.createIRI(CONFIG_NAMESPACE, DEFAULT_ATTRIBUTE);

    protected IRI GROUP_PREDICATE=vf.createIRI(CONFIG_NAMESPACE, GROUP_ATTRIBUTE);
    protected IRI PRIORITY_PREDICATE=vf.createIRI(CONFIG_NAMESPACE, PRIORITY_ATTRIBUTE);
    protected IRI RETURN_PATH_PREDICATE=vf.createIRI(CONFIG_NAMESPACE, PATH_ATTRIBUTE);
    protected IRI BATCH_PREDICATE=vf.createIRI(CONFIG_NAMESPACE, BATCH_ATTRIBUTE);
    protected IRI CALLBACK_PREDICATE=vf.createIRI(CONFIG_NAMESPACE, CALLBACK_ATTRIBUTE);
    protected IRI INVOCATION_ID_PREDICATE=vf.createIRI(CONFIG_NAMESPACE, INVOCATION_ID_ATTRIBUTE);
    protected IRI RESULT_ID_PREDICATE=vf.createIRI(CONFIG_NAMESPACE, RESULT_ID_ATTRIBUTE);
    protected IRI CORRELATION_INPUT_PREDICATE=vf.createIRI(CONFIG_NAMESPACE, CORRELATION_INPUT_ATTRIBUTE);
    protected IRI INPUT_PROPERTY_PREDICATE=vf.createIRI(CONFIG_NAMESPACE, INPUT_PROPERTY_ATTRIBUTE);
    protected IRI OUTPUT_PROPERTY_PREDICATE=vf.createIRI(CONFIG_NAMESPACE, OUTPUT_PROPERTY_ATTRIBUTE);
    protected IRI DATA_TYPE_PREDICATE=vf.createIRI(CONFIG_NAMESPACE, TYPE_RELATION);
    protected IRI A_PREDICATE=vf.createIRI("http://www.w3.org/1999/02/22-rdf-syntax-ns#","type");
    protected IRI FUNCTION_CLASS=vf.createIRI(CONFIG_NAMESPACE,FUNCTION_NAME);
    protected IRI RESULT_CLASS=vf.createIRI(CONFIG_NAMESPACE,RESULT_NAME);
    protected IRI ARGUMENT_CLASS=vf.createIRI(CONFIG_NAMESPACE,ARGUMENT_NAME);
    protected IRI AUTHENTICATION_CODE_PREDICATE=vf.createIRI(COMMON_NAMESPACE,AUTHENTICATION_CODE);
    protected IRI AUTHENTICATION_KEY_PREDICATE=vf.createIRI(COMMON_NAMESPACE,AUTHENTICATION_KEY);


    /**
     * keeps a list of invocation configs
     */
    protected Map<String, ServiceConfig> services =new HashMap<>();

    String callbackAddress;

    public ValueFactory getValueFactory() {
        return vf;
    }

    public String getCallbackAddress() {
        return callbackAddress;
    }

    /**
     * create a new config
     */
    public RemotingSailConfig() {
        if(logger.isDebugEnabled()) {
            logger.debug("Creating new remoting SAIL config.");
        } 
    }

    /**
     * gets a service config
     * @param iri of the service
     * @return service config
     */
    public ServiceConfig getService(String iri) {
        return services.get(iri);
    }

    /**
     * registers a service config
     * @param iri service name
     * @param service config
     */
    public void putService(String iri, ServiceConfig service) {
        services.put(iri,service);
    }

    /**
     * lists services
     * @return set of irirs
     */
    public Set<String> listServices() {
        return services.keySet();
    }

    /**
     * create a new config
     */
    public RemotingSailConfig(String type) {
        super(type);
        if(logger.isDebugEnabled()) {
            logger.debug(String.format("Creating new remoting SAIL config for type %s.",type));
        } 
    }

    @Override
    public String toString() {
        return super.toString()+"/config";
    }

    /**
     * validates the config
     */
    @Override
    public void validate() throws SailConfigException {
        if(logger.isDebugEnabled()) {
            logger.debug("About to validate.");
        } 
        super.validate();
        for(Map.Entry<String, ServiceConfig> configs: services.entrySet()) {
            if(configs.getValue().callbackProperty!=null) {
                if(callbackAddress==null) {
                    throw new SailConfigException(String.format("There should be a repository-wide callbackAddress configured when service %s has a callbackProperty.",configs.getKey()));
                }
            }
            configs.getValue().validate(configs.getKey());
        }
    }

    /**
     * Save the config
     */
    @Override
    public Resource export(Model model) {
        if(logger.isDebugEnabled()) {
            logger.debug(String.format("About to export to model %s.",model));
        } 
        Resource repoNode = super.export(model);
        if(callbackAddress!=null) {
            model.add(repoNode,CALLBACK_ADDRESS_PREDICATE, vf.createIRI(callbackAddress));
        }
        for(Map.Entry<String, ServiceConfig> func : services.entrySet()) {
            IRI functionNode = vf.createIRI(func.getKey());
		    model.add(repoNode,SUPPORTS_INVOCATION_PREDICATE,functionNode);
            model.add(functionNode,A_PREDICATE,FUNCTION_CLASS);
            model.add(functionNode,TARGET_URI_PREDICATE,vf.createLiteral(func.getValue().targetUri));
            model.add(functionNode,INVOCATION_METHOD_PREDICATE,vf.createLiteral(func.getValue().method));
            model.add(functionNode,BATCH_PREDICATE,vf.createLiteral(func.getValue().batch));
            if(func.getValue().callbackProperty!=null) {
                model.add(functionNode,CALLBACK_PREDICATE,vf.createLiteral(func.getValue().callbackProperty));
            }
            if(func.getValue().inputProperty!=null) {
                model.add(functionNode,INPUT_PROPERTY_PREDICATE,vf.createLiteral(func.getValue().inputProperty));
            }
            if(func.getValue().invocationIdProperty!=null) {
                model.add(functionNode,INVOCATION_ID_PREDICATE,vf.createLiteral(func.getValue().invocationIdProperty));
            }
            if(func.getValue().authentication!=null) {
                model.add(functionNode,AUTHENTICATION_CODE_PREDICATE,vf.createLiteral(func.getValue().authentication.authCode));
                model.add(functionNode,AUTHENTICATION_KEY_PREDICATE,vf.createLiteral(func.getValue().authentication.authKey));
            }
            for(Map.Entry<String, ArgumentConfig> arg: func.getValue().arguments.entrySet()) {
                IRI argumentNode = vf.createIRI(arg.getKey());
                model.add(functionNode,INPUT_PREDICATE,argumentNode);
                model.add(argumentNode,A_PREDICATE,ARGUMENT_CLASS);
                model.add(argumentNode,ARGUMENT_NAME_PREDICATE,vf.createLiteral(arg.getValue().argumentName));
                model.add(argumentNode,MANDATORY_PREDICATE,vf.createLiteral(arg.getValue().mandatory));
                model.add(argumentNode,PRIORITY_PREDICATE,vf.createLiteral(arg.getValue().priority));
                model.add(argumentNode,GROUP_PREDICATE,vf.createLiteral(arg.getValue().formsBatchGroup));
                model.add(argumentNode,DEFAULT_PREDICATE, Invocation.convertOutputToValue(arg.getValue().defaultValue,vf,"","https://json-schema.org/draft/2020-12/schema#Object"));
            }
            IRI resultNode = vf.createIRI(func.getValue().resultName);
            model.add(functionNode,RESULT_PREDICATE,resultNode);
            model.add(resultNode,A_PREDICATE,RESULT_CLASS);
            ResultConfig result=func.getValue().result;
            if(result.callbackProperty!=null) {
                model.add(resultNode,CALLBACK_PREDICATE,vf.createLiteral(result.callbackProperty));
            }
            if(result.outputProperty!=null) {
                model.add(resultNode,OUTPUT_PROPERTY_PREDICATE,vf.createLiteral(result.outputProperty));
            }
            if(result.resultIdProperty!=null) {
                model.add(functionNode,INVOCATION_ID_PREDICATE,vf.createLiteral(result.resultIdProperty));
            }
            if(result.correlationInput!=null) {
                model.add(functionNode,CORRELATION_INPUT_PREDICATE,vf.createLiteral(result.correlationInput));
            }
            for(Map.Entry<String, ReturnValueConfig> arg: result.outputs.entrySet()) {
                IRI argumentNode = vf.createIRI(arg.getKey());
                model.add(functionNode,OUTPUT_PREDICATE,argumentNode);
                model.add(argumentNode,A_PREDICATE,ARGUMENT_CLASS);
                model.add(argumentNode,RETURN_PATH_PREDICATE,vf.createLiteral(arg.getValue().path));
                model.add(argumentNode,DATA_TYPE_PREDICATE,vf.createIRI(arg.getValue().dataType));
            }
        }
        return repoNode;
    }

   /**
    * parse the config
    */
    @Override
    public void parse(Model model, Resource implNode) throws SailConfigException {
        if(logger.isDebugEnabled()) {
            logger.debug(String.format("About to parse from model %s and resource %s.",model,implNode));
        } 
        super.parse(model, implNode);
        model.getStatements(implNode,CALLBACK_ADDRESS_PREDICATE,null).forEach(statement -> callbackAddress=statement.getObject().stringValue());
        model.getStatements(implNode,SUPPORTS_INVOCATION_PREDICATE,null).forEach(statement -> {
            if(logger.isDebugEnabled()) {
                logger.debug(String.format("About to process function from statement %s.",statement));
            } 
            if(!statement.getObject(). isIRI()) {
                throw new SailConfigException(String.format("Object of the %s predicate must be IRI but was %s",SUPPORTS_INVOCATION_PREDICATE,statement.getObject()));    
            }
            IRI functionNode=(IRI) statement.getObject();
            ServiceConfig ic=new ServiceConfig();
            services.put(functionNode.stringValue(),ic);
            Models.objectLiteral(model.filter(functionNode,TARGET_URI_PREDICATE,null)).
                ifPresent(targetUri -> ic.targetUri=targetUri.stringValue());
            Models.objectLiteral(model.filter(functionNode,INVOCATION_METHOD_PREDICATE,null)).
                ifPresent(invocationMethod -> ic.method=invocationMethod.stringValue());
            Models.objectLiteral(model.filter(functionNode,BATCH_PREDICATE,null)).
                ifPresent(batch -> ic.batch=batch.longValue());
            Models.objectLiteral(model.filter(functionNode,CALLBACK_PREDICATE,null)).
                    ifPresent(async -> ic.callbackProperty=async.stringValue());
            Models.objectLiteral(model.filter(functionNode,INPUT_PROPERTY_PREDICATE,null)).
                ifPresent(ip -> ic.inputProperty=ip.stringValue());
            Models.objectLiteral(model.filter(functionNode,INVOCATION_ID_PREDICATE,null)).
                ifPresent(iid -> ic.invocationIdProperty=iid.stringValue());
            Models.objectLiteral(model.filter(functionNode,AUTHENTICATION_KEY_PREDICATE,null)).
                ifPresent(authKey -> { 
                    if(ic.authentication==null) { 
                        ic.authentication=new AuthenticationConfig();
                    }
                    ic.authentication.authKey=authKey.stringValue();
                });
            Models.objectLiteral(model.filter(functionNode,AUTHENTICATION_CODE_PREDICATE,null)).
                ifPresent(authCode -> {
                    if(ic.authentication==null) { 
                        ic.authentication=new AuthenticationConfig(); 
                    }
                    ic.authentication.authCode=authCode.stringValue();
                });
            model.getStatements(functionNode,INPUT_PREDICATE,null).forEach(
                    argumentStatement -> {
                        if(logger.isDebugEnabled()) {
                            logger.debug(String.format("About to process argument from statement %s.",argumentStatement));
                        } 
                        if(!argumentStatement.getObject().isIRI()) {
                            throw new SailConfigException(String.format("Object of the %s predicate must be IRI but was %s",INPUT_PREDICATE,argumentStatement.getObject()));    
                        }
                        IRI argumentNode=(IRI) argumentStatement.getObject();
                        ArgumentConfig ac=new ArgumentConfig();
                        ic.arguments.put(argumentNode.stringValue(),ac);
                        Models.objectLiteral(model.filter(argumentNode,ARGUMENT_NAME_PREDICATE,null)).
                        ifPresent(argumentName -> ac.argumentName=argumentName.stringValue());
                        Models.objectLiteral(model.filter(argumentNode,MANDATORY_PREDICATE,null)).
                            ifPresent(mandatory -> ac.mandatory=Boolean.parseBoolean(mandatory.stringValue()));
                        Models.objectLiteral(model.filter(argumentNode,PRIORITY_PREDICATE,null)).
                            ifPresent(priority -> ac.priority=Integer.parseInt(priority.stringValue()));
                        Models.objectLiteral(model.filter(argumentNode,GROUP_PREDICATE,null)).
                                ifPresent(group -> ac.formsBatchGroup=Boolean.parseBoolean(group.stringValue()));
                        Models.objectLiteral(model.filter(argumentNode,DEFAULT_PREDICATE,null)).
                            ifPresent(def -> ac.defaultValue=Invocation.convertToObject(def, JsonNode.class));
                    }
                );
            Models.objectIRI(model.filter(functionNode,RESULT_PREDICATE,null)).
                ifPresent(result -> {
                    if(logger.isDebugEnabled()) {
                        logger.debug(String.format("About to process result %s.",result));
                    }
                    ic.resultName=result.stringValue();
                    ResultConfig rc=new ResultConfig();
                    ic.result=rc;
                    Models.objectLiteral(model.filter(result,OUTPUT_PROPERTY_PREDICATE,null)).
                    ifPresent(op -> rc.outputProperty=op.stringValue());
                    Models.objectLiteral(model.filter(result,RESULT_ID_PREDICATE,null)).
                    ifPresent(rid -> rc.resultIdProperty=rid.stringValue());
                    Models.objectIRI(model.filter(result,CORRELATION_INPUT_PREDICATE,null)).
                            ifPresent(rid -> rc.correlationInput=rid.stringValue());
                    Models.objectLiteral(model.filter(result,CALLBACK_PREDICATE,null)).
                            ifPresent(async -> rc.callbackProperty=async.stringValue());
                    model.getStatements(result,OUTPUT_PREDICATE,null).forEach(
                        outputStatement -> {
                            if(logger.isDebugEnabled()) {
                                logger.debug(String.format("About to process output from statement %s.",outputStatement));
                            } 
                            if(!outputStatement.getObject().isIRI()) {
                                throw new SailConfigException(String.format("Object of the %s predicate must be IRI but was %s",INPUT_PREDICATE,outputStatement.getObject()));    
                            }
                            IRI outputNode=(IRI) outputStatement.getObject();
                            ReturnValueConfig rvc=new ReturnValueConfig();
                            rc.outputs.put(outputNode.stringValue(),rvc);
                            Models.objectLiteral(model.filter(outputNode,RETURN_PATH_PREDICATE,null)).
                            ifPresent(path -> rvc.path=path.stringValue());
                            Models.objectIRI(model.filter(outputNode,DATA_TYPE_PREDICATE,null)).
                            ifPresent(dataType -> rvc.dataType=dataType.stringValue());
                        }
                );
                    
                });

            
        });
    }

}