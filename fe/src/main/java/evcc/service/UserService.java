package evcc.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import evcc.config.RestTemplateConfig;
import evcc.dto.request.UserLoginRequest;
import evcc.dto.request.UserRegisterRequest;
import evcc.dto.response.UserLoginResponse;
import evcc.dto.response.UserRegisterResponse;
import evcc.exception.ApiException;

@Service
public class UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    private final RestTemplate restTemplate;
    private final RestTemplateConfig restTemplateConfig;
    
    public UserService(RestTemplate restTemplate, RestTemplateConfig restTemplateConfig) {
        this.restTemplate = restTemplate;
        this.restTemplateConfig = restTemplateConfig;
    }
    
    /**
     * Đăng ký user mới
     * 
     * @param request thông tin đăng ký
     * @return response từ API
     * @throws ApiException khi có lỗi từ API
     */
    public UserRegisterResponse registerUser(UserRegisterRequest request) throws ApiException {
        logger.info("Đăng ký user mới: {}", request);
        
        String url = restTemplateConfig.getBaseUrl() + "/api/auth/register";
        HttpEntity<UserRegisterRequest> entity = createRequestEntity(request);
        
        try {
            ResponseEntity<UserRegisterResponse> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, UserRegisterResponse.class
            );
            
            return handleSuccessResponse(response);
            
        } catch (HttpClientErrorException e) {
            throw handleHttpClientError(e);
        } catch (RestClientException e) {
            logger.error("Lỗi kết nối khi đăng ký user: {}", e.getMessage());
            throw new ApiException(503, "Không thể kết nối đến server API");
        } catch (Exception e) {
            logger.error("Lỗi không xác định khi đăng ký user: {}", e.getMessage(), e);
            throw new ApiException(500, "Lỗi hệ thống: " + e.getMessage());
        }
    }
    
    private HttpEntity<UserRegisterRequest> createRequestEntity(UserRegisterRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(request, headers);
    }
    
    private UserRegisterResponse handleSuccessResponse(ResponseEntity<UserRegisterResponse> response) throws ApiException {
        UserRegisterResponse responseBody = response.getBody();
        
        if (responseBody != null) {
            logger.info("Đăng ký thành công cho user: {}", responseBody.getUsername());
            return responseBody;
        } else {
            throw new ApiException(500, "Không nhận được response từ server");
        }
    }
    
    private ApiException handleHttpClientError(HttpClientErrorException e) {
        logger.error("Lỗi HTTP khi đăng ký user: {}", e.getMessage());
        
        if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
            return handleBadRequestError(e);
        } else if (e.getStatusCode() == HttpStatus.CONFLICT) {
            return new ApiException(409, "Username đã tồn tại");
        } else {
            return new ApiException(e.getStatusCode().value(), "Lỗi từ server: " + e.getMessage());
        }
    }
    
    private ApiException handleBadRequestError(HttpClientErrorException e) {
        try {
            UserRegisterResponse errorResponse = e.getResponseBodyAs(UserRegisterResponse.class);
            if (errorResponse != null && errorResponse.getMessage() != null) {
                return new ApiException(400, errorResponse.getMessage());
            }
        } catch (Exception parseException) {
            logger.warn("Không thể parse error response: {}", parseException.getMessage());
        }
        return new ApiException(400, "Username đã tồn tại hoặc dữ liệu không hợp lệ");
    }
    
    /**
     * Đăng nhập user
     * 
     * @param request thông tin đăng nhập
     * @return response từ API
     * @throws ApiException khi có lỗi từ API
     */
    public UserLoginResponse loginUser(UserLoginRequest request) throws ApiException {
        logger.info("Đăng nhập user: {}", request);
        
        String url = restTemplateConfig.getBaseUrl() + "/api/auth/login";
        HttpEntity<UserLoginRequest> entity = createLoginRequestEntity(request);
        
        try {
            ResponseEntity<UserLoginResponse> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, UserLoginResponse.class
            );
            
            return handleLoginSuccessResponse(response);
            
        } catch (HttpClientErrorException e) {
            throw handleLoginHttpClientError(e);
        } catch (RestClientException e) {
            logger.error("Lỗi kết nối khi đăng nhập user: {}", e.getMessage());
            throw new ApiException(503, "Không thể kết nối đến server API");
        } catch (Exception e) {
            logger.error("Lỗi không xác định khi đăng nhập user: {}", e.getMessage(), e);
            throw new ApiException(500, "Lỗi hệ thống: " + e.getMessage());
        }
    }
    
    private HttpEntity<UserLoginRequest> createLoginRequestEntity(UserLoginRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(request, headers);
    }
    
    private UserLoginResponse handleLoginSuccessResponse(ResponseEntity<UserLoginResponse> response) throws ApiException {
        UserLoginResponse responseBody = response.getBody();
        
        if (responseBody != null) {
            logger.info("Đăng nhập thành công cho user: {}", responseBody.getUsername());
            return responseBody;
        } else {
            throw new ApiException(500, "Không nhận được response từ server");
        }
    }
    
    private ApiException handleLoginHttpClientError(HttpClientErrorException e) {
        logger.error("Lỗi HTTP khi đăng nhập user: {}", e.getMessage());
        
        if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            return handleUnauthorizedError(e);
        } else if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
            return handleLoginBadRequestError(e);
        } else {
            return new ApiException(e.getStatusCode().value(), "Lỗi từ server: " + e.getMessage());
        }
    }
    
    private ApiException handleUnauthorizedError(HttpClientErrorException e) {
        try {
            UserLoginResponse errorResponse = e.getResponseBodyAs(UserLoginResponse.class);
            if (errorResponse != null && errorResponse.getMessage() != null) {
                return new ApiException(401, errorResponse.getMessage());
            }
        } catch (Exception parseException) {
            logger.warn("Không thể parse error response: {}", parseException.getMessage());
        }
        return new ApiException(401, "Username hoặc password không đúng");
    }
    
    private ApiException handleLoginBadRequestError(HttpClientErrorException e) {
        try {
            UserLoginResponse errorResponse = e.getResponseBodyAs(UserLoginResponse.class);
            if (errorResponse != null && errorResponse.getMessage() != null) {
                return new ApiException(400, errorResponse.getMessage());
            }
        } catch (Exception parseException) {
            logger.warn("Không thể parse error response: {}", parseException.getMessage());
        }
        return new ApiException(400, "Dữ liệu đăng nhập không hợp lệ");
    }
    
    /**
     * Kiểm tra kết nối đến API server
     * 
     * @return true nếu server có thể kết nối
     */
    public boolean isApiServerAvailable() {
        try {
            String healthCheckUrl = restTemplateConfig.getBaseUrl() + "/api/health";
            ResponseEntity<String> response = restTemplate.getForEntity(healthCheckUrl, String.class);
            return response.getStatusCode() == HttpStatus.OK;
        } catch (RestClientException e) {
            logger.warn("API server không khả dụng: {}", e.getMessage());
            return false;
        }
    }
}