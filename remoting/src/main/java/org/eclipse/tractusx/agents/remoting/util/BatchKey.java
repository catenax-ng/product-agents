//
// Application to provide REST APIs as SPARQL services
// See copyright notice in the top folder
// See authors file in the top folder
// See license file in the top folder
//
package org.eclipse.tractusx.agents.remoting.util;

import java.util.Arrays;

/**
 * implements a compound key based on individual hash codes
 */
public class BatchKey<TargetClass> {

    TargetClass[] components;

    public TargetClass[] getComponents() {
        return components;
    }

    /**
     * create a new batch key
     * @param theComponents of the batch key
     */
    public BatchKey(TargetClass... theComponents) {
        this.components=theComponents;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BatchKey<TargetClass> batchKey = (BatchKey<TargetClass>) o;
        return Arrays.equals(components, batchKey.components);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(components);
    }
}
