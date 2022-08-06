//
// Remoting interface to the Storage and Inference Layer (SAIL)
// See copyright notice in the top folder
// See authors file in the top folder
// See license file in the top folder
//
package io.catenax.knowledge.agents.remoting;

import org.eclipse.rdf4j.sail.helpers.AbstractSailConnection;
import org.eclipse.rdf4j.common.iteration.CloseableIteration;
import org.eclipse.rdf4j.sail.SailException;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.query.algebra.TupleExpr;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.Dataset;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.common.iteration.AbstractCloseableIteration;
import org.eclipse.rdf4j.model.impl.SimpleNamespace;

/**
 * Implements a connection to a remote service
 */
public class RemotingSailConnection extends AbstractSailConnection {

    java.util.Map<String,String> namespaces=new java.util.HashMap<String,String>();

    public RemotingSailConnection(RemotingSail remotingSail) {
        super(remotingSail);
    }

    @Override
    protected void clearNamespacesInternal() {
        namespaces.clear();
    }

    @Override
    protected void removeNamespaceInternal(String prefix) {
        namespaces.remove(prefix);
    }

    @Override
    protected void setNamespaceInternal(String prefix, String value) {
        namespaces.put(prefix,value);
    }

    @Override
    protected String getNamespaceInternal(String prefix) {
        return namespaces.get(prefix);
    }

    @Override
    protected CloseableIteration<? extends Namespace, SailException> getNamespacesInternal() {
        return new AbstractCloseableIteration<Namespace,SailException>() {
            java.util.Iterator<java.util.Map.Entry<String,String>> iterator=namespaces.entrySet().iterator();
            public void remove() {
                iterator.remove();
            }
            public Namespace next() {
                java.util.Map.Entry<String,String> nextEntry=iterator.next();
                return new SimpleNamespace(nextEntry.getKey(),nextEntry.getValue());               
            }
            public boolean hasNext() {
                return iterator.hasNext();
            }
        };
    }

    @Override
    protected void closeInternal() throws SailException {
    }

    @Override
	protected CloseableIteration<? extends BindingSet, QueryEvaluationException> evaluateInternal(
			TupleExpr tupleExpr, Dataset dataset, BindingSet bindings, boolean includeInferred) throws SailException {
                return new AbstractCloseableIteration<BindingSet,QueryEvaluationException>() {
                    
                    public void remove() {
                    }

                    public BindingSet next() {
                        return null;             
                    }
                    public boolean hasNext() {
                        return false;
                    }
                };
            }

    @Override
	protected CloseableIteration<? extends Resource, SailException> getContextIDsInternal()
			throws SailException {
                return new AbstractCloseableIteration<Resource,SailException>() {
                    
                    public void remove() {
                    }

                    public Resource next() {
                        return null;             
                    }
                    public boolean hasNext() {
                        return false;
                    }
                };
            }

    @Override
	protected CloseableIteration<? extends Statement, SailException> getStatementsInternal(Resource subj,
			IRI pred, Value obj, boolean includeInferred, Resource... contexts) throws SailException {
                return new AbstractCloseableIteration<Statement,SailException>() {
                    
                    public void remove() {
                    }

                    public Statement next() {
                        return null;             
                    }
                    public boolean hasNext() {
                        return false;
                    }
                };
        }


    @Override
	protected long sizeInternal(Resource... contexts) throws SailException {
        return -1;
    }

    @Override
	protected void startTransactionInternal() throws SailException {

    }

    @Override
	protected void commitInternal() throws SailException {

    }

    @Override
	protected void rollbackInternal() throws SailException {

    }

    @Override
	protected void addStatementInternal(Resource subj, IRI pred, Value obj, Resource... contexts)
			throws SailException {

            }

    @Override
	protected void removeStatementsInternal(Resource subj, IRI pred, Value obj, Resource... contexts)
			throws SailException {

            }

	
    @Override
    protected void clearInternal(Resource... contexts) throws SailException {

    }


}
