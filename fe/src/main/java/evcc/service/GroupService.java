package evcc.service;

import evcc.dto.request.CreateGroupRequest;
import evcc.dto.response.GroupResponseDto;
import evcc.exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class GroupService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${backend.base.url:http://localhost:8080}")
    private String backendBaseUrl;

    public List<GroupResponseDto> getAllGroups(String authToken) throws ApiException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(authToken);
            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<List<GroupResponseDto>> response = restTemplate.exchange(
                    backendBaseUrl + "/api/groups",
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<GroupResponseDto>>() {
            }
            );
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new ApiException(e.getStatusCode().value(), "Không thể lấy danh sách nhóm: " + e.getMessage());
        } catch (Exception e) {
            throw new ApiException(500, "Lỗi kết nối: " + e.getMessage());
        }
    }

    public GroupResponseDto getGroupById(Long groupId, String authToken) throws ApiException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(authToken);
            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<GroupResponseDto> response = restTemplate.exchange(
                    backendBaseUrl + "/api/groups/" + groupId,
                    HttpMethod.GET,
                    entity,
                    GroupResponseDto.class
            );
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new ApiException(e.getStatusCode().value(), "Không thể lấy thông tin nhóm: " + e.getMessage());
        } catch (Exception e) {
            throw new ApiException(500, "Lỗi kết nối: " + e.getMessage());
        }
    }

    public GroupResponseDto createGroup(CreateGroupRequest request, String authToken) throws ApiException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(authToken);
            HttpEntity<CreateGroupRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<GroupResponseDto> response = restTemplate.exchange(
                    backendBaseUrl + "/api/groups",
                    HttpMethod.POST,
                    entity,
                    GroupResponseDto.class
            );
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new ApiException(e.getStatusCode().value(), "Không thể tạo nhóm: " + e.getMessage());
        } catch (Exception e) {
            throw new ApiException(500, "Lỗi kết nối: " + e.getMessage());
        }
    }

    public void deleteGroup(Long groupId, String authToken) throws ApiException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(authToken);
            HttpEntity<?> entity = new HttpEntity<>(headers);

            restTemplate.exchange(
                    backendBaseUrl + "/api/groups/" + groupId,
                    HttpMethod.DELETE,
                    entity,
                    Void.class
            );
        } catch (HttpClientErrorException e) {
            throw new ApiException(e.getStatusCode().value(), "Không thể xóa nhóm: " + e.getMessage());
        } catch (Exception e) {
            throw new ApiException(500, "Lỗi kết nối: " + e.getMessage());
        }
    }

    public GroupResponseDto getGroupDetail(Long groupId, String authToken) throws ApiException {
        return getGroupById(groupId, authToken);
    }

    public List<GroupResponseDto> getUserGroups(String authToken) throws ApiException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(authToken);
            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<List<GroupResponseDto>> response = restTemplate.exchange(
                    backendBaseUrl + "/api/groups/user",
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<GroupResponseDto>>() {
            }
            );
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new ApiException(e.getStatusCode().value(), "Không thể lấy nhóm của người dùng: " + e.getMessage());
        } catch (Exception e) {
            throw new ApiException(500, "Lỗi kết nối: " + e.getMessage());
        }
    }
}
