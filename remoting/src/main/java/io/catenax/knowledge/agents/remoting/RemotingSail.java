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

/**
 * Implements a pseudo-storage that is backed by a remote service
 */
public class RemotingSail extends AbstractSail {
    String url;
    ValueFactory valueFactory=SimpleValueFactory.getInstance();

    public RemotingSail(String url) {
       this.url=url;
    }

    @Override
    protected RemotingSailConnection getConnectionInternal() throws SailException {
      return new RemotingSailConnection(this);
    }

    @Override
    protected void shutDownInternal() {

    }

    @Override
    public ValueFactory getValueFactory() {
        return valueFactory;
    }

    @Override
    public boolean isWritable() {
        return false;
    }
}
