package evcc.service;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import evcc.dto.response.VoteResponseDto;
import evcc.exception.ApiException;

/**
 * Service để giao tiếp với backend voting API
 */
@Service
public class VotingService {

    private final RestTemplate restTemplate;
    private static final String BASE_URL = "http://localhost:8080/api/votes";

    public VotingService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Lấy danh sách vote của nhóm
     */
    public List<VoteResponseDto> getGroupVotes(Long groupId, String token) throws ApiException {
        try {
            HttpHeaders headers = createHeaders(token);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<List<VoteResponseDto>> response = restTemplate.exchange(
                    BASE_URL + "/group/" + groupId,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<VoteResponseDto>>() {
            }
            );

            return response.getBody();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ApiException(404, "Không tìm thấy nhóm");
            } else if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                throw new ApiException(403, "Bạn không có quyền truy cập nhóm này");
            }
            throw new ApiException(500, "Lỗi khi lấy danh sách vote: " + e.getMessage());
        }
    }

    /**
     * Lấy vote đang chờ user
     */
    public List<VoteResponseDto> getPendingVotes(Long groupId, String token) throws ApiException {
        try {
            HttpHeaders headers = createHeaders(token);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<List<VoteResponseDto>> response = restTemplate.exchange(
                    BASE_URL + "/group/" + groupId + "/pending",
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<VoteResponseDto>>() {
            }
            );

            return response.getBody();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ApiException(404, "Không tìm thấy nhóm");
            }
            throw new ApiException(500, "Lỗi khi lấy vote đang chờ: " + e.getMessage());
        }
    }

    /**
     * Lấy chi tiết vote
     */
    public VoteResponseDto getVoteDetail(Long voteId, String token) throws ApiException {
        try {
            HttpHeaders headers = createHeaders(token);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<VoteResponseDto> response = restTemplate.exchange(
                    BASE_URL + "/" + voteId,
                    HttpMethod.GET,
                    entity,
                    VoteResponseDto.class
            );

            return response.getBody();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ApiException(404, "Không tìm thấy vote");
            } else if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                throw new ApiException(403, "Bạn không có quyền truy cập vote này");
            }
            throw new ApiException(500, "Lỗi khi lấy chi tiết vote: " + e.getMessage());
        }
    }

    /**
     * Cast vote
     */
    public VoteResponseDto castVote(Long voteId, Long optionId, String comment, String token) throws ApiException {
        try {
            HttpHeaders headers = createHeaders(token);

            // Tạo request body
            String requestBody = String.format(
                    "{\"voteId\":%d,\"optionId\":%d,\"comment\":\"%s\"}",
                    voteId, optionId, comment != null ? comment : ""
            );

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<VoteResponseDto> response = restTemplate.exchange(
                    BASE_URL + "/cast",
                    HttpMethod.POST,
                    entity,
                    VoteResponseDto.class
            );

            return response.getBody();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new ApiException(400, "Yêu cầu không hợp lệ hoặc bạn đã vote");
            } else if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                throw new ApiException(403, "Bạn không có quyền vote");
            }
            throw new ApiException(500, "Lỗi khi bỏ phiếu: " + e.getMessage());
        }
    }

    /**
     * Bắt đầu vote
     */
    public VoteResponseDto startVote(Long voteId, String token) throws ApiException {
        try {
            HttpHeaders headers = createHeaders(token);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<VoteResponseDto> response = restTemplate.exchange(
                    BASE_URL + "/" + voteId + "/start",
                    HttpMethod.PUT,
                    entity,
                    VoteResponseDto.class
            );

            return response.getBody();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new ApiException(400, "Không thể bắt đầu vote - vote đã được bắt đầu hoặc kết thúc");
            } else if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                throw new ApiException(403, "Chỉ người tạo vote mới có thể bắt đầu vote");
            }
            throw new ApiException(500, "Lỗi khi bắt đầu vote: " + e.getMessage());
        }
    }

    /**
     * Kết thúc vote
     */
    public VoteResponseDto closeVote(Long voteId, String token) throws ApiException {
        try {
            HttpHeaders headers = createHeaders(token);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<VoteResponseDto> response = restTemplate.exchange(
                    BASE_URL + "/" + voteId + "/close",
                    HttpMethod.PUT,
                    entity,
                    VoteResponseDto.class
            );

            return response.getBody();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new ApiException(400, "Không thể kết thúc vote - vote không đang hoạt động");
            } else if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                throw new ApiException(403, "Chỉ người tạo vote mới có thể kết thúc vote");
            }
            throw new ApiException(500, "Lỗi khi kết thúc vote: " + e.getMessage());
        }
    }

    private HttpHeaders createHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");
        return headers;
    }
}
