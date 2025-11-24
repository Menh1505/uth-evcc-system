package evcc.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
import evcc.dto.request.AddGroupMemberRequest;
import evcc.dto.request.CreateContractRequestDto;
import evcc.dto.request.CreateGroupRequest;
import evcc.dto.request.UpdateUserProfileRequest;
import evcc.dto.request.UserLoginRequest;
import evcc.dto.request.UserRegisterRequest;
import evcc.dto.response.ContractResponseDto;
import evcc.dto.response.ContractSummaryResponseDto;
import evcc.dto.response.GroupResponseDto;
import evcc.dto.response.UserLoginResponse;
import evcc.dto.response.UserProfileResponseDto;
import evcc.dto.response.UserRegisterResponse;
import evcc.dto.response.UserStatsResponseDto;
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
     * Lấy thông tin profile cá nhân của user từ API
     *
     * @param jwtToken token xác thực dạng JWT
     * @return thông tin profile trả về từ API
     * @throws ApiException khi không thể lấy được profile
     */
    public UserProfileResponseDto getUserProfile(String jwtToken) throws ApiException {
        logger.info("Lấy thông tin profile user hiện tại");

        HttpEntity<Void> entity = createAuthorizedRequest(jwtToken);
        String url = restTemplateConfig.getBaseUrl() + "/api/users/profile";

        try {
            ResponseEntity<UserProfileResponseDto> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, UserProfileResponseDto.class
            );

            UserProfileResponseDto responseBody = response.getBody();
            if (responseBody != null) {
                logger.debug("Nhận được profile cho user: {}", responseBody.getUsername());
                return responseBody;
            }
            throw new ApiException(500, "Không nhận được response từ server");

        } catch (HttpClientErrorException e) {
            logger.error("Lỗi HTTP khi lấy profile user: {}", e.getMessage());
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new ApiException(401, "Token không hợp lệ hoặc đã hết hạn");
            } else if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ApiException(404, "Không tìm thấy thông tin người dùng");
            } else if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                throw new ApiException(403, "Bạn không có quyền truy cập profile này");
            } else {
                throw new ApiException(e.getStatusCode().value(), "Lỗi từ server: " + e.getMessage());
            }
        } catch (RestClientException e) {
            logger.error("Lỗi kết nối khi lấy profile user: {}", e.getMessage());
            throw new ApiException(503, "Không thể kết nối đến server API");
        } catch (Exception e) {
            logger.error("Lỗi không xác định khi lấy profile user: {}", e.getMessage(), e);
            throw new ApiException(500, "Lỗi hệ thống: " + e.getMessage());
        }
    }

    /**
     * Lấy danh sách tất cả user (dùng cho admin)
     */
    public List<UserProfileResponseDto> getAllUsers(String jwtToken) throws ApiException {
        logger.info("Lấy danh sách tất cả user (admin)");
        HttpEntity<Void> entity = createAuthorizedRequest(jwtToken);
        String url = restTemplateConfig.getBaseUrl() + "/api/users";
        try {
            ResponseEntity<UserProfileResponseDto[]> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, UserProfileResponseDto[].class
            );
            UserProfileResponseDto[] body = response.getBody();
            return body != null ? Arrays.asList(body) : List.of();
        } catch (HttpClientErrorException e) {
            logger.error("Lỗi HTTP khi lấy danh sách user: {}", e.getMessage());
            throw new ApiException(e.getStatusCode().value(), "Không thể lấy danh sách người dùng");
        } catch (RestClientException e) {
            logger.error("Lỗi kết nối khi lấy danh sách user: {}", e.getMessage());
            throw new ApiException(503, "Không thể kết nối đến server API");
        }
    }

    /**
     * Lấy danh sách user chưa xác minh (admin)
     */
    public List<UserProfileResponseDto> getUnverifiedUsers(String jwtToken) throws ApiException {
        logger.info("Lấy danh sách user chưa xác minh (admin)");
        HttpEntity<Void> entity = createAuthorizedRequest(jwtToken);
        String url = restTemplateConfig.getBaseUrl() + "/api/users/unverified";
        try {
            ResponseEntity<UserProfileResponseDto[]> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, UserProfileResponseDto[].class
            );
            UserProfileResponseDto[] body = response.getBody();
            return body != null ? Arrays.asList(body) : List.of();
        } catch (HttpClientErrorException e) {
            logger.error("Lỗi HTTP khi lấy danh sách user chưa xác minh: {}", e.getMessage());
            throw new ApiException(e.getStatusCode().value(), "Không thể lấy danh sách user chưa xác minh");
        } catch (RestClientException e) {
            logger.error("Lỗi kết nối khi lấy danh sách user chưa xác minh: {}", e.getMessage());
            throw new ApiException(503, "Không thể kết nối đến server API");
        }
    }

    /**
     * Xác minh tài khoản user (admin)
     */
    public UserProfileResponseDto verifyUser(String jwtToken, String userId) throws ApiException {
        logger.info("Xác minh user {} (admin)", userId);
        HttpEntity<Void> entity = createAuthorizedRequest(jwtToken);
        String url = restTemplateConfig.getBaseUrl() + "/api/users/" + userId + "/verify";
        try {
            ResponseEntity<UserProfileResponseDto> response = restTemplate.exchange(
                    url, HttpMethod.PUT, entity, UserProfileResponseDto.class
            );
            UserProfileResponseDto body = response.getBody();
            if (body != null) {
                return body;
            }
            throw new ApiException(500, "Không nhận được response từ server");
        } catch (HttpClientErrorException e) {
            logger.error("Lỗi HTTP khi xác minh user: {}", e.getMessage());
            throw new ApiException(e.getStatusCode().value(), "Không thể xác minh user: " + e.getMessage());
        } catch (RestClientException e) {
            logger.error("Lỗi kết nối khi xác minh user: {}", e.getMessage());
            throw new ApiException(503, "Không thể kết nối đến server API");
        }
    }

    /**
     * Lấy thống kê user (admin)
     */
    public UserStatsResponseDto getUserStats(String jwtToken) throws ApiException {
        logger.info("Lấy thống kê user (admin)");
        HttpEntity<Void> entity = createAuthorizedRequest(jwtToken);
        String url = restTemplateConfig.getBaseUrl() + "/api/users/stats";
        try {
            ResponseEntity<UserStatsResponseDto> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, UserStatsResponseDto.class
            );
            UserStatsResponseDto body = response.getBody();
            if (body != null) {
                return body;
            }
            throw new ApiException(500, "Không nhận được response từ server");
        } catch (HttpClientErrorException e) {
            logger.error("Lỗi HTTP khi lấy thống kê user: {}", e.getMessage());
            throw new ApiException(e.getStatusCode().value(), "Không thể lấy thống kê user");
        } catch (RestClientException e) {
            logger.error("Lỗi kết nối khi lấy thống kê user: {}", e.getMessage());
            throw new ApiException(503, "Không thể kết nối đến server API");
        }
    }

    // ===== Group Management (frontend client cho /api/groups) =====
    public GroupResponseDto createGroup(String jwtToken, CreateGroupRequest request) throws ApiException {
        logger.info("Tạo nhóm mới: {}", request.getName());
        HttpEntity<CreateGroupRequest> entity = createAuthorizedJsonRequest(jwtToken, request);
        String url = restTemplateConfig.getBaseUrl() + "/api/groups";
        try {
            ResponseEntity<GroupResponseDto> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, GroupResponseDto.class
            );
            GroupResponseDto body = response.getBody();
            if (body != null) {
                return body;
            }
            throw new ApiException(500, "Không nhận được response từ server");
        } catch (HttpClientErrorException e) {
            logger.error("Lỗi HTTP khi tạo nhóm: {}", e.getMessage());
            throw new ApiException(e.getStatusCode().value(), "Không thể tạo nhóm: " + e.getMessage());
        } catch (RestClientException e) {
            logger.error("Lỗi kết nối khi tạo nhóm: {}", e.getMessage());
            throw new ApiException(503, "Không thể kết nối đến server API");
        }
    }

    public List<GroupResponseDto> getMyGroups(String jwtToken) throws ApiException {
        logger.info("Lấy danh sách nhóm của user hiện tại");
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

    public GroupResponseDto getGroupDetail(String jwtToken, Long groupId) throws ApiException {
        logger.info("Lấy chi tiết nhóm {}", groupId);
        HttpEntity<Void> entity = createAuthorizedRequest(jwtToken);
        String url = restTemplateConfig.getBaseUrl() + "/api/groups/" + groupId;
        try {
            ResponseEntity<GroupResponseDto> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, GroupResponseDto.class
            );
            GroupResponseDto body = response.getBody();
            if (body != null) {
                return body;
            }
            throw new ApiException(500, "Không nhận được response từ server");
        } catch (HttpClientErrorException e) {
            logger.error("Lỗi HTTP khi lấy chi tiết nhóm: {}", e.getMessage());
            throw new ApiException(e.getStatusCode().value(), "Không thể lấy chi tiết nhóm");
        } catch (RestClientException e) {
            logger.error("Lỗi kết nối khi lấy chi tiết nhóm: {}", e.getMessage());
            throw new ApiException(503, "Không thể kết nối đến server API");
        }
    }

    public void addMemberToGroup(String jwtToken, Long groupId, AddGroupMemberRequest request) throws ApiException {
        logger.info("Thêm thành viên {} vào nhóm {}", request.getUserId(), groupId);
        HttpEntity<AddGroupMemberRequest> entity = createAuthorizedJsonRequest(jwtToken, request);
        String url = restTemplateConfig.getBaseUrl() + "/api/groups/" + groupId + "/members";
        try {
            restTemplate.exchange(url, HttpMethod.POST, entity, Void.class);
        } catch (HttpClientErrorException e) {
            logger.error("Lỗi HTTP khi thêm thành viên: {}", e.getMessage());
            throw new ApiException(e.getStatusCode().value(), "Không thể thêm thành viên: " + e.getMessage());
        } catch (RestClientException e) {
            logger.error("Lỗi kết nối khi thêm thành viên: {}", e.getMessage());
            throw new ApiException(503, "Không thể kết nối đến server API");
        }
    }

    public void removeMemberFromGroup(String jwtToken, Long groupId, Long membershipId) throws ApiException {
        logger.info("Xóa thành viên {} khỏi nhóm {}", membershipId, groupId);
        HttpEntity<Void> entity = createAuthorizedRequest(jwtToken);
        String url = restTemplateConfig.getBaseUrl() + "/api/groups/" + groupId + "/members/" + membershipId;
        try {
            restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);
        } catch (HttpClientErrorException e) {
            logger.error("Lỗi HTTP khi xóa thành viên: {}", e.getMessage());
            throw new ApiException(e.getStatusCode().value(), "Không thể xóa thành viên: " + e.getMessage());
        } catch (RestClientException e) {
            logger.error("Lỗi kết nối khi xóa thành viên: {}", e.getMessage());
            throw new ApiException(503, "Không thể kết nối đến server API");
        }
    }

    // ===== Contract Management (frontend client cho /api/contracts) =====
    public ContractResponseDto createContract(String jwtToken, CreateContractRequestDto request) throws ApiException {
        logger.info("Tạo hợp đồng mới cho nhóm {}", request.getGroupId());
        HttpEntity<CreateContractRequestDto> entity = createAuthorizedJsonRequest(jwtToken, request);
        String url = restTemplateConfig.getBaseUrl() + "/api/contracts";
        try {
            ResponseEntity<ContractResponseDto> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, ContractResponseDto.class
            );
            ContractResponseDto body = response.getBody();
            if (body != null) {
                return body;
            }
            throw new ApiException(500, "Không nhận được response từ server");
        } catch (HttpClientErrorException e) {
            logger.error("Lỗi HTTP khi tạo hợp đồng: {}", e.getMessage());
            throw new ApiException(e.getStatusCode().value(), "Không thể tạo hợp đồng: " + e.getMessage());
        } catch (RestClientException e) {
            logger.error("Lỗi kết nối khi tạo hợp đồng: {}", e.getMessage());
            throw new ApiException(503, "Không thể kết nối đến server API");
        }
    }

    public List<ContractSummaryResponseDto> getContractsByGroup(String jwtToken, Long groupId) throws ApiException {
        logger.info("Lấy danh sách hợp đồng của nhóm {}", groupId);
        HttpEntity<Void> entity = createAuthorizedRequest(jwtToken);
        String url = restTemplateConfig.getBaseUrl() + "/api/contracts/group/" + groupId;
        try {
            // Backend trả Page<ContractSummaryResponse>, đọc field "content"
            ResponseEntity<Map> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, Map.class
            );
            Map<?, ?> body = response.getBody();
            if (body == null || body.get("content") == null) {
                return List.of();
            }

            Object contentObj = body.get("content");
            if (!(contentObj instanceof List<?> list)) {
                return List.of();
            }

            List<ContractSummaryResponseDto> result = new ArrayList<>();
            for (Object item : list) {
                if (!(item instanceof Map<?, ?> itemMap)) {
                    continue;
                }
                ContractSummaryResponseDto dto = new ContractSummaryResponseDto();

                Object id = itemMap.get("id");
                if (id instanceof Number) {
                    dto.setId(((Number) id).longValue());
                }
                dto.setContractNumber((String) itemMap.get("contractNumber"));
                dto.setTitle((String) itemMap.get("title"));
                dto.setGroupName((String) itemMap.get("groupName"));
                dto.setVehicleName((String) itemMap.get("vehicleName"));
                dto.setVehicleLicensePlate((String) itemMap.get("vehicleLicensePlate"));

                Object agreedPrice = itemMap.get("agreedPrice");
                if (agreedPrice != null) {
                    dto.setAgreedPrice(new BigDecimal(agreedPrice.toString()));
                }

                Object signingDate = itemMap.get("signingDate");
                if (signingDate instanceof String s && !s.isBlank()) {
                    dto.setSigningDate(LocalDate.parse(s));
                }

                Object status = itemMap.get("status");
                dto.setStatus(status != null ? status.toString() : null);

                Object totalOwners = itemMap.get("totalOwners");
                if (totalOwners instanceof Number) {
                    dto.setTotalOwners(((Number) totalOwners).intValue());
                }

                Object totalContributed = itemMap.get("totalContributed");
                if (totalContributed != null) {
                    dto.setTotalContributed(new BigDecimal(totalContributed.toString()));
                }

                Object contributionPercentage = itemMap.get("contributionPercentage");
                if (contributionPercentage != null) {
                    dto.setContributionPercentage(new BigDecimal(contributionPercentage.toString()));
                }

                result.add(dto);
            }// chán 

            return result;
        } catch (HttpClientErrorException e) {
            logger.error("Lỗi HTTP khi lấy hợp đồng theo nhóm: {}", e.getMessage());
            throw new ApiException(e.getStatusCode().value(), "Không thể lấy danh sách hợp đồng của nhóm");
        } catch (RestClientException e) {
            logger.error("Lỗi kết nối khi lấy hợp đồng theo nhóm: {}", e.getMessage());
            throw new ApiException(503, "Không thể kết nối đến server API");
        }
    }

    public ContractResponseDto getContract(String jwtToken, Long contractId) throws ApiException {
        logger.info("Lấy chi tiết hợp đồng từ {}", contractId);
        HttpEntity<Void> entity = createAuthorizedRequest(jwtToken);
        String url = restTemplateConfig.getBaseUrl() + "/api/contracts/" + contractId;
        try {
            ResponseEntity<ContractResponseDto> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, ContractResponseDto.class
            );
            ContractResponseDto body = response.getBody();
            if (body != null) {
                return body;
            }
            throw new ApiException(500, "Không nhận được response từ server");
        } catch (HttpClientErrorException e) {
            logger.error("Lỗi HTTP khi lấy chi tiết hợp đồng: {}", e.getMessage());
            throw new ApiException(e.getStatusCode().value(), "Không thể lấy chi tiết hợp đồng");
        } catch (RestClientException e) {
            logger.error("Lỗi kết nối khi lấy chi tiết hợp đồng: {}", e.getMessage());
            throw new ApiException(503, "Không thể kết nối đến server API");
        }
    }

    /**
     * Cập nhật thông tin profile (citizenId, driverLicense) của user hiện tại
     *
     * @param jwtToken token xác thực dạng JWT
     * @param request dữ liệu cập nhật profile
     * @return profile sau khi cập nhật
     * @throws ApiException khi cập nhật thất bại
     */
    public UserProfileResponseDto updateUserProfile(String jwtToken, UpdateUserProfileRequest request) throws ApiException {
        logger.info("Cập nhật profile user hiện tại liên tục");

        HttpEntity<UpdateUserProfileRequest> entity = createAuthorizedJsonRequest(jwtToken, request);
        String url = restTemplateConfig.getBaseUrl() + "/api/users/profile";

        try {
            ResponseEntity<UserProfileResponseDto> response = restTemplate.exchange(
                    url, HttpMethod.PUT, entity, UserProfileResponseDto.class
            );

            UserProfileResponseDto responseBody = response.getBody();
            if (responseBody != null) {
                logger.debug("Cập nhật profile thành công cho user: {}", responseBody.getUsername());
                return responseBody;
            }
            throw new ApiException(500, "Không nhận được response từ server");

        } catch (HttpClientErrorException e) {
            logger.error("Lỗi HTTP khi cập nhật profile user: {}", e.getMessage());
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new ApiException(400, "Dữ liệu cập nhật không hợp lệ");
            } else if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new ApiException(401, "Token không hợp lệ hoặc đã hết hạn");
            } else if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                throw new ApiException(403, "Bạn không có quyền cập nhật profile này");
            } else {
                throw new ApiException(e.getStatusCode().value(), "Lỗi từ server: " + e.getMessage());
            }
        } catch (RestClientException e) {
            logger.error("Lỗi kết nối khi cập nhật profile user: {}", e.getMessage());
            throw new ApiException(503, "Không thể kết nối đến server API");
        } catch (Exception e) {
            logger.error("Lỗi không xác định khi cập nhật profile user: {}", e.getMessage(), e);
            throw new ApiException(500, "Lỗi hệ thống: " + e.getMessage());
        }
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
