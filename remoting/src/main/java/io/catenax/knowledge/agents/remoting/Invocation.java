//
// Remoting interface to the Storage and Inference Layer (SAIL)
// See copyright notice in the top folder
// See authors file in the top folder
// See license file in the top folder
//

package io.catenax.knowledge.agents.remoting;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigInteger;
import java.util.HashMap;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.query.algebra.Var;
import org.eclipse.rdf4j.sail.SailException;

import com.github.jsonldjava.core.RDFDataset.Literal;

/**
 * Implements an invocation.
 */
public class Invocation {
    /** the config of the service invoked */
    public InvocationConfig service=null;
    /** unique key for the invocation */
    public IRI key=null;
    /** start time */
    public long startTime=-1;
    /** end time of the invocation */
    public long endTime=-1;
    /** success code */
    public int success=0;
    /** failure condition */
    public Throwable exception=null;
    /** input bindings */
    public Map<IRI,Value> inputs=new HashMap<IRI,Value>();
    /** output bindings */
    public Map<Var,IRI> outputs=new HashMap<Var,IRI>();
    /** the actual result as a value */
    public Value result=null;

    /**
     * converter from the literal to the type system
     */
    public Object convertToObject(Value binding, Class target) throws SailException  {
        if(target.isAssignableFrom(String.class)) {
            return binding.stringValue();
        } else if(target.isAssignableFrom(int.class)) {
            return Integer.parseInt(binding.stringValue());
        } else if(target.isAssignableFrom(long.class)) {
            return Long.parseLong(binding.stringValue());
        } else if(target.isAssignableFrom(double.class)) {
            return Double.parseDouble(binding.stringValue());
        } else if(target.isAssignableFrom(float.class)) {
            return Float.parseFloat(binding.stringValue());
        }
        throw new SailException(String.format("No conversion from %s to %s possible.",binding,target));
    }

    /**
     * converter from the literal to the type system
     */
    public Value convertToValue(ValueFactory vf, Object result, Class target) throws SailException  {
        if(target.isAssignableFrom(int.class)) {
            return vf.createLiteral((int) result);
        } else if(target.isAssignableFrom(long.class)) {
            return vf.createLiteral((long) result);
        } else if(target.isAssignableFrom(double.class)) {
            return vf.createLiteral((double) result);
        } 
        return vf.createLiteral(String.valueOf(result));
    }

    /**
     * perform execution
     * @param connection sail connection in which to perform the invocation
     * @return flag indicating whether execution has been attempted (or was already done)
     */
    public boolean execute(RemotingSailConnection connection) throws SailException {
        if(result==null) {
            Class targetClass = null;
            try {
                targetClass=getClass().getClassLoader().loadClass(service.matcher.group("class"));   
            }  catch(ClassNotFoundException e) {
                throw new SailException(e);
            }
            if(connection.logger.isTraceEnabled()) {
                connection.logger.trace(String.format("Found class %s ",targetClass));
            }
            Method targetMethod = null;
            Object[] targetParams=null;
            try {
                for(Method meth : targetClass.getMethods()) {
                    if(meth.getName().equals(service.matcher.group("method"))) {
                        if(connection.logger.isTraceEnabled()) {
                            connection.logger.trace(String.format("Found method %s ",meth));
                        }
                        targetParams=new Object[meth.getParameterTypes().length];
                        int argIndex=0;
                        for(Parameter param: meth.getParameters()) {
                            if(connection.logger.isTraceEnabled()) {
                                connection.logger.trace(String.format("Checking parameter %s",param));
                            }
                            ArgumentConfig config=null;
                            for(Map.Entry<String,ArgumentConfig> argument:service.arguments.entrySet()) {
                                if(connection.logger.isTraceEnabled()) {
                                    connection.logger.trace(String.format("Agains argument %s %s",argument.getKey(),argument.getValue().argumentName));
                                }
                                if(argument.getValue().argumentName.equals(param.getName())) {
                                    config=argument.getValue();
                                    Value binding=inputs.get(connection.remotingSail.config.vf.createIRI(argument.getKey()));
                                    targetParams[argIndex++]=convertToObject(binding,param.getType());
                                    break;
                                }
                            }
                            if(connection.logger.isTraceEnabled()) {
                                connection.logger.trace(String.format("Parameter %s resulted to argument %s ",param,config));
                            }
                            if(config==null) {
                                targetParams=null;
                                break;
                            }
                        }
                        if(targetParams!=null) {
                            targetMethod=meth;
                            break;
                        }
                    }
                }
                if(targetMethod==null) {
                    throw new SailException(String.format("Target method %s with suitable arguments could not be found in class %s.",service.matcher.group("method"),targetClass));
                }
            }  finally {

            }
            Object targetInstance = null;
            try {
                targetInstance=targetClass.getConstructor().newInstance();
            } catch(NoSuchMethodException  e) {
                throw new SailException(e);
            } catch(IllegalAccessException e) {
                throw new SailException(e);
            } catch(InstantiationException e) {
                throw new SailException(e);
            } catch(InvocationTargetException e) {
                throw new SailException(e.getCause());
            }
            startTime=System.currentTimeMillis();
            try {
                Object res=targetMethod.invoke(targetInstance,targetParams);
                result=convertToValue(connection.remotingSail.getValueFactory(), res, targetMethod.getReturnType());
            } catch(InvocationTargetException e) {
                result=connection.remotingSail.config.vf.createBNode();
                exception=e.getCause();
                success=500;
            } catch(Exception e) {
                result=connection.remotingSail.config.vf.createBNode();
                exception=e;
                success=500;
            } finally {
                endTime=System.currentTimeMillis();
            }
            return true;
        } else {
            return false;
        }
    }
}