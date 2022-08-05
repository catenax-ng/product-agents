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

/**
 * sail config
 */
public class RemotingSailConfig extends AbstractSailImplConfig {
   String url = null;

    public RemotingSailConfig() {

    }

    public RemotingSailConfig(String type) {
        super(type);
    }

    // Validate the config object
    @Override
    public void validate() throws SailConfigException {
        super.validate();
        if (url==null || url.length()==0) {
            throw new SailConfigException("REST service URL is not provided");
        }
    }

    // Save the config values into the RDF model
    @Override
    public Resource export(Model model) {
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
        super.parse(model, implNode);
        Models.objectLiteral(model.filter(implNode, SP.SERVICE_URI_PROPERTY, null))
                .ifPresent(lit -> setUrl(lit.stringValue()));
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}