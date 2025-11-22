package evcc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import java.time.Duration;

@Configuration
public class RestTemplateConfig {
    
    @Value("${evcc.api.base-url:http://localhost:3000}")
    private String baseUrl;
    
    @Value("${evcc.api.timeout.connect:5000}")
    private int connectTimeout;
    
    @Value("${evcc.api.timeout.read:10000}")
    private int readTimeout;
    
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder, RestTemplateInterceptor interceptor) {
        return builder
                .requestFactory(this::clientHttpRequestFactory)
                .setConnectTimeout(Duration.ofMillis(connectTimeout))
                .setReadTimeout(Duration.ofMillis(readTimeout))
                .interceptors(interceptor)
                .errorHandler(new RestTemplateErrorHandler())
                .build();
    }
    
    private ClientHttpRequestFactory clientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeout);
        factory.setReadTimeout(readTimeout);
        return new BufferingClientHttpRequestFactory(factory);
    }
    
    public String getBaseUrl() {
        return baseUrl;
    }
    
    public int getConnectTimeout() {
        return connectTimeout;
    }
    
    public int getReadTimeout() {
        return readTimeout;
    }
}