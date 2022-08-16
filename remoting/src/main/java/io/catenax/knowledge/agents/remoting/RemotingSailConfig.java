//
// Remoting interface to the Storage and Inference Layer (SAIL)
// See copyright notice in the top folder
// See authors file in the top folder
// See license file in the top folder
//
package io.catenax.knowledge.agents.remoting;

import org.eclipse.rdf4j.sail.config.AbstractSailImplConfig;
import org.eclipse.rdf4j.sail.config.SailConfigException;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.vocabulary.SP;
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
    public static String OUTPUT_ATTRIBUTE="output";

    /**
     * constant
     */
    public static String INVOCATION_PROPERTY="supportsInvocation";

    /**
     * constant
     */
    public static String URL_ATTRIBUTE="targetUri";

    /** when interacting with parser/exporter */
    protected ValueFactory vf = SimpleValueFactory.getInstance();

    /**
     * iri for predicate
     */
    protected IRI SUPPORTS_INVOCATION_PREDICATE=vf.createIRI(CONFIG_NAMESPACE,INVOCATION_PROPERTY);
    protected IRI TARGET_URI_PREDICATE=vf.createIRI(CONFIG_NAMESPACE, URL_ATTRIBUTE);
    protected IRI INPUT_PREDICATE=vf.createIRI(CONFIG_NAMESPACE, INPUT_ATTRIBUTE);
    protected IRI OUTPUT_PREDICATE=vf.createIRI(CONFIG_NAMESPACE, OUTPUT_ATTRIBUTE);
    protected IRI ARGUMENT_NAME_PREDICATE=vf.createIRI(CONFIG_NAMESPACE, ARGUMENT_ATTRIBUTE);
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
            configs.getValue().validate();
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
            BNode functionNode = vf.createBNode(func.getKey());
		    model.add(repoNode,SUPPORTS_INVOCATION_PREDICATE,vf.createIRI(functionNode.getID()));
            model.add(functionNode,A_PREDICATE,FUNCTION_CLASS);
            model.add(functionNode,TARGET_URI_PREDICATE,vf.createLiteral(func.getValue().targetUri));
            for(Map.Entry<String,ArgumentConfig> arg: func.getValue().arguments.entrySet()) {
                BNode argumentNode = vf.createBNode(arg.getKey());
                model.add(functionNode,INPUT_PREDICATE,vf.createIRI(argumentNode.getID()));
                model.add(argumentNode,A_PREDICATE,ARGUMENT_CLASS);
                model.add(argumentNode,ARGUMENT_NAME_PREDICATE,vf.createLiteral(arg.getValue().argumentName));
            }
            model.add(functionNode,OUTPUT_PREDICATE,vf.createIRI(func.getValue().output));
            model.add(vf.createBNode(func.getValue().output),A_PREDICATE,RETURN_CLASS);
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
            Models.objectIRI(model.filter(functionNode,OUTPUT_PREDICATE,null)).
                ifPresent(output -> ic.output=output.stringValue());
        });
    }

}