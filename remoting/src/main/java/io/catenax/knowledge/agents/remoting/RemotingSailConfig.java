//
// Application to provide REST APIs as SPARQL services
// See copyright notice in the top folder
// See authors file in the top folder
// See license file in the top folder
//
package io.catenax.knowledge.agents.remoting;

import org.eclipse.rdf4j.sail.config.AbstractSailImplConfig;
import org.eclipse.rdf4j.sail.config.SailConfigException;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.Models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.HashMap;

/**
 * A remoting SAIL config caotures anything that a remoting
 * inference service needs to know to expose its capabilities.
 */
public class RemotingSailConfig extends AbstractSailImplConfig {
    /** logger */
    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * constant
     */
    public static String CONFIG_NAMESPACE="https://github.com/catenax-ng/product-knowledge/ontology/fx.ttl#";

    /**
     * constant
     */
    public static String FUNCTION_NAME="Function";

    /**
     * constant
     */
    public static String ARGUMENT_NAME="Argument";

    /**
     * constant
     */
    public static String RETURN_NAME="ReturnValue";

    /**
     * constant
     */
    public static String INPUT_ATTRIBUTE="input";

    /**
     * constant
     */
    public static String ARGUMENT_ATTRIBUTE="argumentName";

    /**
     * constant
     */
    public static String PATH_ATTRIBUTE="valuePath";

    /**
     * constant
     */
    public static String TYPE_RELATION="dataType";

    /**
     * constant
     */
    public static String OUTPUT_ATTRIBUTE="output";

    /**
     * constant
     */
    public static String INVOCATION_PROPERTY="supportsInvocation";

    /**
     * constant
     */
    public static String URL_ATTRIBUTE="targetUri";

    /**
     * constant
     */
    public static String METHOD_ATTRIBUTE="invocationMethod";

    /** when interacting with parser/exporter */
    protected ValueFactory vf = SimpleValueFactory.getInstance();

    /**
     * iri for predicate
     */
    protected IRI SUPPORTS_INVOCATION_PREDICATE=vf.createIRI(CONFIG_NAMESPACE,INVOCATION_PROPERTY);
    protected IRI TARGET_URI_PREDICATE=vf.createIRI(CONFIG_NAMESPACE, URL_ATTRIBUTE);
    protected IRI INVOCATION_METHOD_PREDICATE=vf.createIRI(CONFIG_NAMESPACE, METHOD_ATTRIBUTE);
    protected IRI INPUT_PREDICATE=vf.createIRI(CONFIG_NAMESPACE, INPUT_ATTRIBUTE);
    protected IRI OUTPUT_PREDICATE=vf.createIRI(CONFIG_NAMESPACE, OUTPUT_ATTRIBUTE);
    protected IRI ARGUMENT_NAME_PREDICATE=vf.createIRI(CONFIG_NAMESPACE, ARGUMENT_ATTRIBUTE);
    protected IRI RETURN_PATH_PREDICATE=vf.createIRI(CONFIG_NAMESPACE, PATH_ATTRIBUTE);
    protected IRI DATA_TYPE_PREDICATE=vf.createIRI(CONFIG_NAMESPACE, TYPE_RELATION);
    protected IRI A_PREDICATE=vf.createIRI("http://www.w3.org/1999/02/22-rdf-syntax-ns#","type");
    protected IRI FUNCTION_CLASS=vf.createIRI(CONFIG_NAMESPACE,FUNCTION_NAME);
    protected IRI ARGUMENT_CLASS=vf.createIRI(CONFIG_NAMESPACE,ARGUMENT_NAME);
    protected IRI RETURN_CLASS=vf.createIRI(CONFIG_NAMESPACE,RETURN_NAME);

    /**
     * keeps a list of invocation configs
     */
    protected Map<String,InvocationConfig> invocations=new HashMap<String,InvocationConfig>();

    /**
     * create a new config
     */
    public RemotingSailConfig() {
        if(logger.isDebugEnabled()) {
            logger.debug("Creating new remoting SAIL config.");
        } 
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

    /**
     * validates the config
     */
    @Override
    public void validate() throws SailConfigException {
        if(logger.isDebugEnabled()) {
            logger.debug("About to validate.");
        } 
        super.validate();
        for(Map.Entry<String,InvocationConfig> configs:invocations.entrySet()) {
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
        for(Map.Entry<String,InvocationConfig> func : invocations.entrySet()) {
            IRI functionNode = vf.createIRI(func.getKey());
		    model.add(repoNode,SUPPORTS_INVOCATION_PREDICATE,functionNode);
            model.add(functionNode,A_PREDICATE,FUNCTION_CLASS);
            model.add(functionNode,TARGET_URI_PREDICATE,vf.createLiteral(func.getValue().targetUri));
            model.add(functionNode,INVOCATION_METHOD_PREDICATE,vf.createLiteral(func.getValue().method));
            for(Map.Entry<String,ArgumentConfig> arg: func.getValue().arguments.entrySet()) {
                IRI argumentNode = vf.createIRI(arg.getKey());
                model.add(functionNode,INPUT_PREDICATE,argumentNode);
                model.add(argumentNode,A_PREDICATE,ARGUMENT_CLASS);
                model.add(argumentNode,ARGUMENT_NAME_PREDICATE,vf.createLiteral(arg.getValue().argumentName));
            }
            for(Map.Entry<String,ReturnValueConfig> arg: func.getValue().outputs.entrySet()) {
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
        model.getStatements(implNode,SUPPORTS_INVOCATION_PREDICATE,null).forEach(statement -> {
            if(logger.isDebugEnabled()) {
                logger.debug(String.format("About to process function from statement %s.",statement));
            } 
            if(!statement.getObject(). isIRI()) {
                throw new SailConfigException(String.format("Object of the %s predicate must be IRI but was %s",SUPPORTS_INVOCATION_PREDICATE,statement.getObject()));    
            }
            IRI functionNode=(IRI) statement.getObject();
            InvocationConfig ic=new InvocationConfig();
            invocations.put(functionNode.stringValue(),ic);
            Models.objectLiteral(model.filter(functionNode,TARGET_URI_PREDICATE,null)).
                ifPresent(targetUri -> ic.targetUri=targetUri.stringValue());
            Models.objectLiteral(model.filter(functionNode,INVOCATION_METHOD_PREDICATE,null)).
                ifPresent(invocationMethod -> ic.method=invocationMethod.stringValue());
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
                    }
                );
            model.getStatements(functionNode,OUTPUT_PREDICATE,null).forEach(
                    outputStatement -> {
                        if(logger.isDebugEnabled()) {
                            logger.debug(String.format("About to process output from statement %s.",outputStatement));
                        } 
                        if(!outputStatement.getObject().isIRI()) {
                            throw new SailConfigException(String.format("Object of the %s predicate must be IRI but was %s",INPUT_PREDICATE,outputStatement.getObject()));    
                        }
                        IRI outputNode=(IRI) outputStatement.getObject();
                        ReturnValueConfig rc=new ReturnValueConfig();
                        ic.outputs.put(outputNode.stringValue(),rc);
                        Models.objectLiteral(model.filter(outputNode,RETURN_PATH_PREDICATE,null)).
                        ifPresent(path -> rc.path=path.stringValue());
                        Models.objectIRI(model.filter(outputNode,DATA_TYPE_PREDICATE,null)).
                        ifPresent(dataType -> rc.dataType=dataType.stringValue());
                    }
            );
        });
    }

}