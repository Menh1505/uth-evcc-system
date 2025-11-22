package evcc.config;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class RestTemplateInterceptor implements ClientHttpRequestInterceptor {
    
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        // Add custom headers or logging here
        logRequest(request);
        
        ClientHttpResponse response = execution.execute(request, body);
        
        // Log response
        logResponse(response);
        
        return response;
    }
    
    private void logRequest(HttpRequest request) {
        System.out.println("=== HTTP Request ===");
        System.out.println("Method: " + request.getMethod());
        System.out.println("URI: " + request.getURI());
        System.out.println("Headers: " + request.getHeaders());
    }
    
    private void logResponse(ClientHttpResponse response) throws IOException {
        System.out.println("=== HTTP Response ===");
        System.out.println("Status Code: " + response.getStatusCode());
        System.out.println("Status Text: " + response.getStatusText());
    }
}
