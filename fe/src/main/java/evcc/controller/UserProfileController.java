package evcc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import evcc.dto.response.UserProfileResponseDto;
import evcc.exception.ApiException;
import evcc.service.UserLocalService;

@RestController
@RequestMapping("/api/users")
public class UserProfileController {

    private static final Logger logger = LoggerFactory.getLogger(UserProfileController.class);

    private final UserLocalService userLocalService;

    public UserProfileController(UserLocalService userLocalService) {
        this.userLocalService = userLocalService;
    }

    /**
     * Lấy thông tin profile cá nhân GET /api/users/profile
     *
     * Header: Authorization: Bearer <JWT_TOKEN>
     */
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponseDto> getMyProfile(
            @RequestHeader("Authorization") String authorizationHeader) {

        logger.info("API call lấy thông tin profile cá nhân");

        try {
            UserProfileResponseDto profile = userLocalService.getUserProfile(authorizationHeader);
            return ResponseEntity.ok(profile);
        } catch (ApiException e) {
            logger.error("Lỗi khi lấy profile user: {}", e.getMessage());

            HttpStatus status;
            try {
                status = HttpStatus.valueOf(e.getStatusCode());
            } catch (IllegalArgumentException ex) {
                status = HttpStatus.INTERNAL_SERVER_ERROR;
            }

            return ResponseEntity.status(status).build();
        }
    }
}
