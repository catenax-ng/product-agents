//
// Conforming Agent Implementation
// See copyright notice in the top folder
// See authors file in the top folder
// See license file in the top folder
//
package org.eclipse.tractusx.agents.conforming.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.tractusx.agents.conforming.ConformingAgent;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
public class JsonProvider implements MessageBodyReader, MessageBodyWriter {

    ObjectMapper objectMapper=new ObjectMapper();

    @Override
    public boolean isReadable(Class aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return mediaType.isCompatible(ConformingAgent.srj);
    }

    @Override
    public Object readFrom(Class aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap multivaluedMap, InputStream inputStream) throws IOException, WebApplicationException {
        return objectMapper.readTree(inputStream);
    }

    @Override
    public boolean isWriteable(Class aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return mediaType.isCompatible(ConformingAgent.srj);
    }

    @Override
    public void writeTo(Object o, Class aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap multivaluedMap, OutputStream outputStream) throws IOException, WebApplicationException {
        objectMapper.writeValue(outputStream,o);
    }
}
