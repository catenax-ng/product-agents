//
// Remoting interface to the Storage and Inference Layer (SAIL)
// See copyright notice in the top folder
// See authors file in the top folder
// See license file in the top folder
//
package io.catenax.knowledge.agents.remoting;

import org.eclipse.rdf4j.model.*;

import org.eclipse.rdf4j.sail.helpers.AbstractSail;
import org.eclipse.rdf4j.sail.SailException;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements a pseudo-storage that is backed by a remote service
 */
public class RemotingSail extends AbstractSail {
    /** the logger */
    protected Logger logger = LoggerFactory.getLogger(getClass());

    /** the remoting target */
    protected String url;
    /** a factory for values */
    protected ValueFactory valueFactory;

    /**
     * creates the remoting sail
     * @param url to the target service
     */
    public RemotingSail(String url) {
       this.url=url;
       this.valueFactory=SimpleValueFactory.getInstance();
       if(logger.isDebugEnabled()) {
        logger.debug(String.format("Starting remoting inference on url %s with value factory %s",this.url,this.valueFactory));
       }
    }

    /**
     * exposes a connection to this sail.
     * We may hide state in the connection implementation
     */
    @Override
    protected RemotingSailConnection getConnectionInternal() throws SailException {
        if(logger.isTraceEnabled()) {
            logger.trace(String.format("returning a new remoting connection to %s",url));
        }
        return new RemotingSailConnection(this);
    }

    /**
     * shutdown the sail
     */
    @Override
    protected void shutDownInternal() {
        if(logger.isTraceEnabled()) {
            logger.trace(String.format("shutting down remoting to %s",url));
        }
    }

    /**
     * link to the factory
     */
    @Override
    public ValueFactory getValueFactory() {
        return valueFactory;
    }

    /**
     * a service is not "writable", only queriable
     */
    @Override
    public boolean isWritable() {
        return false;
    }
}
