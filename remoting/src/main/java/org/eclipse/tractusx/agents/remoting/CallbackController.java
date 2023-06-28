package org.eclipse.tractusx.agents.remoting;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * a controller for receiving and synchronizing on
 * asynchronous callbacks
 */
@Controller
@RequestMapping("/callback")
public class CallbackController implements org.springframework.web.servlet.mvc.Controller {

    public static ObjectMapper objectMapper=new ObjectMapper();

    public static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    public static Map<CallbackToken, AtomicReference<Object>> pending=new HashMap<>();

    /**
     * registers a new asynchronous call
     * @param responsePath the path where to look for call ids in the response
     * @param callId id of the call/response
     * @return an atomic reference for the result
     */
    public static CallbackToken register(String responsePath, String callId) {
        CallbackToken token=new CallbackToken(responsePath,callId);
        synchronized (pending) {
            AtomicReference<Object> result = pending.get(token);
            if(result==null) {
                result=new AtomicReference<>();
                pending.put(token,result);
            }
        }
        return token;
    }

    /**
     * synchronizes on the given asynchronous call
     * @param token of the call
     * @return asynchronous result
     */
    public static Object synchronize(CallbackToken token) {
        AtomicReference<Object> result;
        synchronized(pending) {
            result=pending.get(token);
        }
        if(result==null) {
            return null;
        }
        int maxrounds=2;
        synchronized(result) {
            while(result.get()==null && 0<maxrounds--) {
                try {
                    result.wait(30000);
                } catch (InterruptedException e) {
                }
            }
            return result.get();
        }
    }

    /**
     * the actual request handler
     * @param request
     * @param response
     * @return an empty redirection
     */
    @Override
    @PostMapping
    public ModelAndView handleRequest(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response)  {
        try {
            Object callback;
            if(request.getContentType().contains("json")) {
                callback=objectMapper.readTree(request.getInputStream());
            } else if(request.getContentType().contains("xml")) {
                DocumentBuilder builder = factory.newDocumentBuilder();
                callback=builder.parse(request.getInputStream()).getDocumentElement();
            } else {
                callback= IOUtils.toString(request.getInputStream());
            }
            synchronized(pending) {
                for(Map.Entry<CallbackToken,AtomicReference<Object>> callbacks : pending.entrySet()) {
                    String[] paths=callbacks.getKey().getResponsePath().split("\\.");
                    String callId=Invocation.convertObjectToString(Invocation.traversePath(callback, paths));
                    if(callbacks.getKey().getCallId().equals(callId)) {
                        callbacks.getValue().set(callback);
                        synchronized(callbacks.getValue()) {
                            callbacks.getValue().notify();
                        };
                    }
                }
            }
            response.setStatus(200);
        } catch(IOException | ParserConfigurationException | SAXException jpe) {
            response.setStatus(400);
        }
        return null;
    }
}
