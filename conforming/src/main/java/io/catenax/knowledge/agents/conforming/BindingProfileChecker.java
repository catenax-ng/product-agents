//
// Conforming Agent Implementation
// See copyright notice in the top folder
// See authors file in the top folder
// See license file in the top folder
//
package io.catenax.knowledge.agents.conforming;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.query.algebra.StatementPattern;
import org.eclipse.rdf4j.query.algebra.helpers.AbstractQueryModelVisitor;

public class BindingProfileChecker extends AbstractQueryModelVisitor<Exception> {
    @Override
    public void meet(StatementPattern node) throws Exception {
        if(!node.getPredicateVar().isConstant()) {
            throw new Exception("Predicate must be not constant.");
        }
        if(!node.getPredicateVar().getValue().isIRI()) {
            throw new Exception("Predicate must be IRI.");
        }
        if("http://www.w3.org/1999/02/22-rdf-syntax-ns#type".equals(node.getPredicateVar().getValue().stringValue())) {
            if(!node.getObjectVar().isConstant()) {
                throw new Exception("Object of rdf:type must be constant.");
            }
        }
        super.meet(node);
    }
}
