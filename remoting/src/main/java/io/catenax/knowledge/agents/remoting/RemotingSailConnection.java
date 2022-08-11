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
import org.eclipse.rdf4j.query.algebra.Join;
import org.eclipse.rdf4j.query.algebra.Projection;
import org.eclipse.rdf4j.query.algebra.QueryRoot;
import org.eclipse.rdf4j.query.algebra.StatementPattern;
import org.eclipse.rdf4j.query.algebra.TupleExpr;
import org.eclipse.rdf4j.query.algebra.Var;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.Dataset;
import org.eclipse.rdf4j.query.Query;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.common.iteration.AbstractCloseableIteration;
import org.eclipse.rdf4j.model.impl.SimpleNamespace;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements a connection to a remote service
 * This is the main magic for hiding functions as graphs.
 */
public class RemotingSailConnection extends AbstractSailConnection {
    /** logger */
    protected Logger logger = LoggerFactory .getLogger(getClass());
    /** namespaces map */
    protected java.util.Map<String,String> namespaces=new java.util.HashMap<String,String>();
    /** link to the sail / service wrapper */
    protected RemotingSail remotingSail;

    /**
     * creates a new connection
     * @param remotingSail the remoting sail (just a wrapper around the config, since it keeps no state)
     */
    public RemotingSailConnection(RemotingSail remotingSail) {
        super(remotingSail);
        this.remotingSail=remotingSail;
        if(logger.isInfoEnabled()) {
            logger.info(String.format("Creating a new connection to the remoting sail %s wrapping url %s",remotingSail,remotingSail.url));
        }
    }

    /**
     * clear namespaces
     */
    @Override
    protected void clearNamespacesInternal() {
        if(logger.isDebugEnabled()) {
            logger.debug(String.format("Clearing connection namespaces to the remoting sail %s wrapping url %s",remotingSail,remotingSail.url));
        }
        namespaces.clear();
    }

    /**
     * unset namespace
     */
    @Override
    protected void removeNamespaceInternal(String prefix) {
       String oldValue=namespaces.remove(prefix);
       if(logger.isDebugEnabled()) {
         logger.debug(String.format("Removed namespace prefix %s (previous value %s) to the remoting sail %s wrapping url %s",prefix,oldValue,remotingSail,remotingSail.url));
       }
    }

    /**
     * set namespace
     */
    @Override
    protected void setNamespaceInternal(String prefix, String value) {
        String oldValue=namespaces.put(prefix,value);
        if(logger.isDebugEnabled()) {
            logger.debug(String.format("Set namespace prefix %s to value %s (previous value was %s) to the remoting sail %s wrapping url %s",prefix,value,oldValue,remotingSail,remotingSail.url));
        }
    }

    /**
     * resolve namespace
     */
    @Override
    protected String getNamespaceInternal(String prefix) {
        String value= namespaces.get(prefix);
        if(logger.isDebugEnabled()) {
            logger.debug(String.format("Resolve namespace prefix %s to value %s to the remoting sail %s wrapping url %s",prefix,value,remotingSail,remotingSail.url));
        }
        return value;
    }

    /**
     * return namespace list
     */
    @Override
    protected CloseableIteration<? extends Namespace, SailException> getNamespacesInternal() {
        if(logger.isDebugEnabled()) {
            logger.debug(String.format("Iterate namespaces to the remoting sail %s wrapping url %s",remotingSail,remotingSail.url));
        }
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

    /**
     * close the connection
     */
    @Override
    protected void closeInternal() throws SailException {
        if(logger.isInfoEnabled()) {
            logger.info(String.format("Closing connection to the remoting sail %s wrapping url %s",remotingSail,remotingSail.url));
        }
    }

    /**
     * helper to climb a query tree, prepare the invocations
     * and map variables.
     * @param tree
     */
    protected void climb(TupleExpr tree) throws SailException {
        if(tree instanceof QueryRoot) {
            QueryRoot query=(QueryRoot) tree;
            logger.debug(String.format("Need to query"));
            climb(query.getArg());
        } else if(tree instanceof Projection) {
            Projection projection=(Projection) tree;
            logger.debug(String.format("Need to project to %s",projection.getProjectionElemList()));
            climb(projection.getArg());
        } else if(tree instanceof Join) {
            logger.debug(String.format("Need to join"));
            Join join=(Join) tree;
            climb(join.getLeftArg());
            climb(join.getRightArg()); 
        } else if(tree instanceof StatementPattern) {
            StatementPattern statement=(StatementPattern) tree;
            logger.debug(String.format("Need to process statement pattern %s",statement));
        } else {
            throw new SailException(String.format("No support for tuple expression %s",tree));
        }
    }

    /**
     * is that the actual query part?
     */
    @Override
	protected CloseableIteration<? extends BindingSet, QueryEvaluationException> evaluateInternal(
			TupleExpr tupleExpr, Dataset dataset, BindingSet bindings, boolean includeInferred) throws SailException {
                if(logger.isInfoEnabled()) {
                    logger.info(String.format("Evaluating tuples %s on dataset %s and bindings %s (including inferred %b) connection to the remoting sail %s wrapping url %s",tupleExpr,dataset,bindings,includeInferred,remotingSail,remotingSail.url));
                }
                climb(tupleExpr);
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

    /**
     * iterate resources (graphs?)
     */
    @Override
	protected CloseableIteration<? extends Resource, SailException> getContextIDsInternal()
			throws SailException {
                if(logger.isInfoEnabled()) {
                    logger.info(String.format("Iterating resources for connection on remoting sail %s wrapping url %s",remotingSail,remotingSail.url));
                }
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

    /**
     * return all statements
     */
    @Override
	protected CloseableIteration<? extends Statement, SailException> getStatementsInternal(Resource subj,
			IRI pred, Value obj, boolean includeInferred, Resource... contexts) throws SailException {
                if(logger.isInfoEnabled()) {
                    logger.info(String.format("Returning all statements on subject %s for predicate %s and object %s (including inferred %b) in contexts %s for connection to the remoting sail %s wrapping url %s",subj,pred,obj,includeInferred,contexts,remotingSail,remotingSail.url));
                }
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


    /**
     * compute graph sizes
     */
    @Override
	protected long sizeInternal(Resource... contexts) throws SailException {
        if(logger.isInfoEnabled()) {
            logger.info(String.format("Returning siteo for contexts %s for connection to the remoting sail %s wrapping url %s",contexts,remotingSail,remotingSail.url));
        }
        return -1;
    }

    /**
     * compute graph sizes
     */
    @Override
	protected void startTransactionInternal() throws SailException {
        if(logger.isInfoEnabled()) {
            logger.info(String.format("Start transaction on connection to the remoting sail %s wrapping url %s",remotingSail,remotingSail.url));
        }
    }

    /**
     * compute graph sizes
     */
    @Override
	protected void commitInternal() throws SailException {
        if(logger.isInfoEnabled()) {
            logger.info(String.format("Commit transaction on connection to the remoting sail %s wrapping url %s",remotingSail,remotingSail.url));
        }
    }

    /**
     * compute graph sizes
     */
    @Override
	protected void rollbackInternal() throws SailException {
        if(logger.isInfoEnabled()) {
            logger.info(String.format("Rollback transaction on connection to the remoting sail %s wrapping url %s",remotingSail,remotingSail.url));
        }
    }

    /**
     * adds a statement
     */
    @Override
	protected void addStatementInternal(Resource subj, IRI pred, Value obj, Resource... contexts)
			throws SailException {
                if(logger.isInfoEnabled()) {
                    logger.info(String.format("Adding statements on subject %s for predicate %s and object %s in contexts %s for connection to the remoting sail %s wrapping url %s",subj,pred,obj,contexts,remotingSail,remotingSail.url));
                }
    }

    @Override
	protected void removeStatementsInternal(Resource subj, IRI pred, Value obj, Resource... contexts)
			throws SailException {
                if(logger.isInfoEnabled()) {
                    logger.info(String.format("Remove statements on subject %s for predicate %s and object %s in contexts %s for connection to the remoting sail %s wrapping url %s",subj,pred,obj,contexts,remotingSail,remotingSail.url));
                }
    }
	
    @Override
    protected void clearInternal(Resource... contexts) throws SailException {
        if(logger.isInfoEnabled()) {
            logger.info(String.format("Clearcontexts %s for connection to the remoting sail %s wrapping url %s",contexts,remotingSail,remotingSail.url));
        }
    }

}
