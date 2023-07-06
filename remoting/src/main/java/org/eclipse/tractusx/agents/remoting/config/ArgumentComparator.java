//
// Application to provide REST APIs as SPARQL services
// See copyright notice in the top folder
// See authors file in the top folder
// See license file in the top folder
//
package org.eclipse.tractusx.agents.remoting.config;

import java.util.Comparator;
import java.util.Map;

/**
 * A comparator for arguments according to priority
 */
public class ArgumentComparator implements Comparator<Map.Entry<String, ArgumentConfig>> {

    /**
     * use the configs priority
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     * @return comparison according to priority
     */
    public int compare(Map.Entry<String, ArgumentConfig> o1, Map.Entry<String, ArgumentConfig> o2) {
        return Integer.compare(o1.getValue().getPriority(),o2.getValue().getPriority());
    }
}
