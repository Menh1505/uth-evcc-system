package evcc.config;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import java.io.IOException;
import org.springframework.http.client.ClientHttpResponse;

public class RestTemplateErrorHandler extends DefaultResponseErrorHandler {
    
    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        int statusCodeValue = response.getStatusCode().value();
        HttpStatus statusCode = HttpStatus.resolve(statusCodeValue);
        
        System.err.println("=== HTTP Error ===");
        System.err.println("Status Code: " + statusCodeValue);
        System.err.println("Status Text: " + response.getStatusText());
        
        if (statusCode != null) {
            switch (statusCode.series()) {
                case CLIENT_ERROR:
                    throw new HttpClientErrorException(statusCode, response.getStatusText(),
                            response.getHeaders(), getResponseBody(response), getCharset(response));
                case SERVER_ERROR:
                    throw new HttpServerErrorException(statusCode, response.getStatusText(),
                            response.getHeaders(), getResponseBody(response), getCharset(response));
                default:
                    throw new RuntimeException("Unknown HTTP status: " + statusCode);
            }
        }
    }
}
