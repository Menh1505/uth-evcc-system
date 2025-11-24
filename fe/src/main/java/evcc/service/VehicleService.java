package evcc.service;

import java.util.Arrays;
import java.util.List;

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
import evcc.dto.request.CreateVehicleRequest;
import evcc.dto.response.GroupResponseDto;
import evcc.dto.response.VehicleResponseDto;
import evcc.exception.ApiException;

/**
 * Service để giao tiếp với Vehicle API
 */
@Service
public class VehicleService {

    private static final Logger logger = LoggerFactory.getLogger(VehicleService.class);

    private final RestTemplate restTemplate;
    private final RestTemplateConfig restTemplateConfig;

    public VehicleService(RestTemplate restTemplate, RestTemplateConfig restTemplateConfig) {
        this.restTemplate = restTemplate;
        this.restTemplateConfig = restTemplateConfig;
    }

    /**
     * Lấy danh sách tất cả xe
     */
    public List<VehicleResponseDto> getAllVehicles(String jwtToken) throws ApiException {
        logger.info("Lấy danh sách tất cả xe");
        HttpEntity<Void> entity = createAuthorizedRequest(jwtToken);
        String url = restTemplateConfig.getBaseUrl() + "/api/vehicles";
        try {
            ResponseEntity<VehicleResponseDto[]> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, VehicleResponseDto[].class
            );
            VehicleResponseDto[] body = response.getBody();
            return body != null ? Arrays.asList(body) : List.of();
        } catch (HttpClientErrorException e) {
            logger.error("Lỗi HTTP khi lấy danh sách xe: {}", e.getMessage());
            throw new ApiException(e.getStatusCode().value(), "Không thể lấy danh sách xe");
        } catch (RestClientException e) {
            logger.error("Lỗi kết nối khi lấy danh sách xe: {}", e.getMessage());
            throw new ApiException(503, "Không thể kết nối đến server API");
        }
    }

    /**
     * Lấy danh sách xe có sẵn (chưa được gán hợp đồng)
     */
    public List<VehicleResponseDto> getAvailableVehicles(String jwtToken) throws ApiException {
        logger.info("Lấy danh sách xe có sẵn");
        HttpEntity<Void> entity = createAuthorizedRequest(jwtToken);
        String url = restTemplateConfig.getBaseUrl() + "/api/vehicles/available";
        try {
            ResponseEntity<VehicleResponseDto[]> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, VehicleResponseDto[].class
            );
            VehicleResponseDto[] body = response.getBody();
            return body != null ? Arrays.asList(body) : List.of();
        } catch (HttpClientErrorException e) {
            logger.error("Lỗi HTTP khi lấy xe có sẵn: {}", e.getMessage());
            throw new ApiException(e.getStatusCode().value(), "Không thể lấy danh sách xe có sẵn");
        } catch (RestClientException e) {
            logger.error("Lỗi kết nối khi lấy xe có sẵn: {}", e.getMessage());
            throw new ApiException(503, "Không thể kết nối đến server API");
        }
    }

    /**
     * Lấy chi tiết xe theo ID
     */
    public VehicleResponseDto getVehicleById(String jwtToken, Long vehicleId) throws ApiException {
        logger.info("Lấy chi tiết xe {}", vehicleId);
        HttpEntity<Void> entity = createAuthorizedRequest(jwtToken);
        String url = restTemplateConfig.getBaseUrl() + "/api/vehicles/" + vehicleId;
        try {
            ResponseEntity<VehicleResponseDto> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, VehicleResponseDto.class
            );
            VehicleResponseDto body = response.getBody();
            if (body != null) {
                return body;
            }
            throw new ApiException(500, "Không nhận được response từ server");
        } catch (HttpClientErrorException e) {
            logger.error("Lỗi HTTP khi lấy chi tiết xe: {}", e.getMessage());
            throw new ApiException(e.getStatusCode().value(), "Không thể lấy chi tiết xe");
        } catch (RestClientException e) {
            logger.error("Lỗi kết nối khi lấy chi tiết xe: {}", e.getMessage());
            throw new ApiException(503, "Không thể kết nối đến server API");
        }
    }

    /**
     * Tạo xe mới (chỉ admin)
     */
    public VehicleResponseDto createVehicle(String jwtToken, CreateVehicleRequest request) throws ApiException {
        logger.info("Tạo xe mới: {}", request.getName());
        HttpEntity<CreateVehicleRequest> entity = createAuthorizedJsonRequest(jwtToken, request);
        String url = restTemplateConfig.getBaseUrl() + "/api/vehicles";
        try {
            ResponseEntity<VehicleResponseDto> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, VehicleResponseDto.class
            );
            VehicleResponseDto body = response.getBody();
            if (body != null) {
                return body;
            }
            throw new ApiException(500, "Không nhận được response từ server");
        } catch (HttpClientErrorException e) {
            logger.error("Lỗi HTTP khi tạo xe: {}", e.getMessage());
            throw new ApiException(e.getStatusCode().value(), "Không thể tạo xe: " + e.getMessage());
        } catch (RestClientException e) {
            logger.error("Lỗi kết nối khi tạo xe: {}", e.getMessage());
            throw new ApiException(503, "Không thể kết nối đến server API");
        }
    }

    /**
     * Xóa xe (chỉ admin)
     */
    public void deleteVehicle(String jwtToken, Long vehicleId) throws ApiException {
        logger.info("Xóa xe {}", vehicleId);
        HttpEntity<Void> entity = createAuthorizedRequest(jwtToken);
        String url = restTemplateConfig.getBaseUrl() + "/api/vehicles/" + vehicleId;
        try {
            restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);
        } catch (HttpClientErrorException e) {
            logger.error("Lỗi HTTP khi xóa xe: {}", e.getMessage());
            throw new ApiException(e.getStatusCode().value(), "Không thể xóa xe: " + e.getMessage());
        } catch (RestClientException e) {
            logger.error("Lỗi kết nối khi xóa xe: {}", e.getMessage());
            throw new ApiException(503, "Không thể kết nối đến server API");
        }
    }

    /**
     * Lấy danh sách nhóm của user (để chọn khi tạo xe)
     */
    public List<GroupResponseDto> getMyGroups(String jwtToken) throws ApiException {
        logger.info("Lấy danh sách nhóm của user");
        HttpEntity<Void> entity = createAuthorizedRequest(jwtToken);
        String url = restTemplateConfig.getBaseUrl() + "/api/groups/my-groups";
        try {
            ResponseEntity<GroupResponseDto[]> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, GroupResponseDto[].class
            );
            GroupResponseDto[] body = response.getBody();
            return body != null ? Arrays.asList(body) : List.of();
        } catch (HttpClientErrorException e) {
            logger.error("Lỗi HTTP khi lấy danh sách nhóm: {}", e.getMessage());
            throw new ApiException(e.getStatusCode().value(), "Không thể lấy danh sách nhóm");
        } catch (RestClientException e) {
            logger.error("Lỗi kết nối khi lấy danh sách nhóm: {}", e.getMessage());
            throw new ApiException(503, "Không thể kết nối đến server API");
        }
    }

    /**
     * Tạo HTTP Entity với JWT token
     */
    private HttpEntity<Void> createAuthorizedRequest(String jwtToken) throws ApiException {
        if (jwtToken == null || jwtToken.isBlank()) {
            throw new ApiException(401, "Thiếu token xác thực");
        }

        String tokenValue = jwtToken.trim();
        if (tokenValue.toLowerCase().startsWith("bearer ")) {
            tokenValue = tokenValue.substring(7).trim();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(tokenValue);

        return new HttpEntity<>(headers);
    }

    /**
     * Tạo HTTP Entity với JWT token và request body
     */
    private <T> HttpEntity<T> createAuthorizedJsonRequest(String jwtToken, T requestBody) throws ApiException {
        if (jwtToken == null || jwtToken.isBlank()) {
            throw new ApiException(401, "Thiếu token xác thực");
        }

        String tokenValue = jwtToken.trim();
        if (tokenValue.toLowerCase().startsWith("bearer ")) {
            tokenValue = tokenValue.substring(7).trim();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(tokenValue);

        return new HttpEntity<>(requestBody, headers);
    }
}
