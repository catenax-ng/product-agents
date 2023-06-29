package org.eclipse.tractusx.agents.remoting;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
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
 * a test controller based on a json notification structure that sends a callback
 */
@Controller
@RequestMapping("/async")
public class AsyncTestController implements org.springframework.web.servlet.mvc.Controller {

    public static ObjectMapper objectMapper=new ObjectMapper();

    public static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    /**
     * the actual request handler
     * @param request
     * @param response
     * @return an empty redirection
     */
    @Override
    @PostMapping
    public ModelAndView handleRequest(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response)  {
        try (final CloseableHttpClient httpclient = HttpClients.createDefault()) {
            JsonNode callback;
            callback=objectMapper.readTree(request.getInputStream());
            String callbackAddress=Invocation.convertObjectToString(Invocation.traversePath(callback,"header","respondAssetId"));
            String callId=Invocation.convertObjectToString(Invocation.traversePath(callback, "header","notificationID"));
            String callbackBody=String.format("{ \"header\": { \"referencedNotificationID\": \"%s\" }, \"content\": { \"requestRefId\": \"98f507d5-175d-4945-8d06-6aa1fcef9a0c\", \"endurancePredictorOutputs\": [ 0.721, 0.852, 0.432 ]}}",callId);
            HttpPost post = new HttpPost(callbackAddress);
            post.addHeader("accept","application/json");
            post.addHeader("Content-Type","application/json");
            post.setEntity(new StringEntity(callbackBody));
            CloseableHttpResponse callbackResponse=httpclient.execute(post);
            if(callbackResponse.getStatusLine().getStatusCode()>=200 && callbackResponse.getStatusLine().getStatusCode()<300) {
                byte[] responseBody = String.format("{ \"status\": \"ok\" }").getBytes();
                IOUtils.write(responseBody, response.getOutputStream());
                response.setContentType("application/json");
                response.setContentLength(responseBody.length);
                response.setStatus(200);
            } else {
                byte[] responseBody = String.format("{ \"status\": \"no callback possible\" }").getBytes();
                IOUtils.write(responseBody, response.getOutputStream());
                response.setContentType("application/json");
                response.setContentLength(responseBody.length);
                response.setStatus(400);
            }
        } catch(IOException jpe) {
            response.setStatus(500);
        }
        return null;
    }
}
