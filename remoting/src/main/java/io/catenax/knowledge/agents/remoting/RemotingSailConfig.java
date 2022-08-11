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

/**
 * A remoting SAIL config caotures anything that a remoting
 * inference service needs to know to expose its capabilities.
 */
public class RemotingSailConfig extends AbstractSailImplConfig {
    /** logger */
    protected Logger logger = LoggerFactory.getLogger(getClass());
    /** url of the target service */
    protected String url = null;

    public RemotingSailConfig() {
        if(logger.isDebugEnabled()) {
            logger.debug("Creating new remoting SAIL config.");
        } 
    }

    public RemotingSailConfig(String type) {
        super(type);
        if(logger.isDebugEnabled()) {
            logger.debug(String.format("Creating new remoting SAIL config for type %s.",type));
        } 
    }

    // Validate the config object
    @Override
    public void validate() throws SailConfigException {
        if(logger.isDebugEnabled()) {
            logger.debug("About to validate.");
        } 
        super.validate();
        if (url==null || url.length()==0) {
            throw new SailConfigException("REST service URL is not provided");
        }
    }

    // Save the config values into the RDF model
    @Override
    public Resource export(Model model) {
        if(logger.isDebugEnabled()) {
            logger.debug(String.format("About to export to model %s.",model));
        } 
        Resource implNode = super.export(model);
        if (url!=null && url.length()!=0) {
            model.add(implNode, SP.SERVICE_URI_PROPERTY,
                    SimpleValueFactory.getInstance().createLiteral(url));
        }
        return implNode;
    }

    // Parse the RDF model to read the custom parameters
    @Override
    public void parse(Model model, Resource implNode) throws SailConfigException {
        if(logger.isDebugEnabled()) {
            logger.debug(String.format("About to parse from model %s and resource %s.",model,implNode));
        } 
        super.parse(model, implNode);
        Models.objectLiteral(model.filter(implNode, SP.SERVICE_URI_PROPERTY, null))
                .ifPresent(lit -> setUrl(lit.stringValue()));
    }

    /**
     * access
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * set 
     * @param url the url
     */
    public void setUrl(String url) {
        if(logger.isDebugEnabled()) {
            logger.debug(String.format("Set url %s.",url));
        } 
        this.url = url;
    }
}