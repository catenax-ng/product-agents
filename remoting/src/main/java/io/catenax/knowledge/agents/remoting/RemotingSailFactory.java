//
// Remoting interface to the Storage and Inference Layer (SAIL)
// See copyright notice in the top folder
// See authors file in the top folder
// See license file in the top folder
//
package io.catenax.knowledge.agents.remoting;

import org.eclipse.rdf4j.sail.Sail;
import org.eclipse.rdf4j.sail.config.SailFactory;
import org.eclipse.rdf4j.sail.config.SailImplConfig;
import org.eclipse.rdf4j.sail.config.SailConfigException;


public class RemotingSailFactory implements SailFactory {
    public static final String SAIL_TYPE = "io.catenax.knowledge:Remoting";
    
    public RemotingSailFactory() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public String getSailType() {
        return SAIL_TYPE;
    }

    @Override
    public SailImplConfig getConfig() {
        return new RemotingSailConfig();
    }

    @Override
    public Sail getSail(SailImplConfig originalConfig) throws SailConfigException {
        if (!(originalConfig instanceof RemotingSailConfig)) {
            throw new SailConfigException(
                    "Wrong config type: " 
                            + originalConfig.getClass().getCanonicalName() + ". ");
        }
        RemotingSailConfig config = (RemotingSailConfig)originalConfig;
        
        RemotingSail sail = new RemotingSail(config.getUrl());
        return sail;
        
    }
}