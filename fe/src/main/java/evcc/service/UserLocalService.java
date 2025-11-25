package evcc.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import evcc.dto.request.UserLoginRequest;
import evcc.dto.request.UserRegisterRequest;
import evcc.dto.response.UserLoginResponse;
import evcc.dto.response.UserRegisterResponse;
import evcc.exception.ApiException;
import org.springframework.stereotype.Service;

import evcc.dto.response.UserLoginResponse;
import evcc.dto.response.UserProfileResponseDto;
import evcc.exception.ApiException;

/**
 * Service để quản lý user local (demo) thay thế API backend
 */
@Service
public class UserLocalService {

    private final Map<String, LocalUser> usersByUsername = new HashMap<>();
    private final Map<UUID, LocalUser> usersById = new HashMap<>();
    private final Map<String, String> userPasswords = new HashMap<>();

    public static class LocalUser {

        private UUID id;
        private String username;
        private String email;
        private String citizenId;
        private String driverLicense;
        private String status; // VERIFIED, UNVERIFIED
        private List<String> roles; // USER, ADMIN

        public LocalUser() {
        }

        public LocalUser(UUID id, String username, String email, String status, List<String> roles) {
            this.id = id;
            this.username = username;
            this.email = email;
            this.status = status;
            this.roles = roles;
        }

        // Getters and Setters
        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getCitizenId() {
            return citizenId;
        }

        public void setCitizenId(String citizenId) {
            this.citizenId = citizenId;
        }

        public String getDriverLicense() {
            return driverLicense;
        }

        public void setDriverLicense(String driverLicense) {
            this.driverLicense = driverLicense;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public List<String> getRoles() {
            return roles;
        }

        public void setRoles(List<String> roles) {
            this.roles = roles;
        }
    }

    public UserLocalService() {
        initializeDemoData();
    }

    private void initializeDemoData() {
        // Tạo demo users
        LocalUser user1 = new LocalUser(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                "user1@example.com",
                "user1@example.com",
                "VERIFIED",
                List.of("USER")
        );
        user1.setCitizenId("123456789");
        user1.setDriverLicense("B1123456");

        LocalUser user2 = new LocalUser(
                UUID.fromString("22222222-2222-2222-2222-222222222222"),
                "user2@example.com",
                "user2@example.com",
                "VERIFIED",
                List.of("USER")
        );
        user2.setCitizenId("987654321");
        user2.setDriverLicense("B2987654");

        LocalUser user3 = new LocalUser(
                UUID.fromString("33333333-3333-3333-3333-333333333333"),
                "user3@example.com",
                "user3@example.com",
                "VERIFIED",
                List.of("USER")
        );
        user3.setCitizenId("456789123");
        user3.setDriverLicense("B3456789");

        LocalUser user4 = new LocalUser(
                UUID.fromString("44444444-4444-4444-4444-444444444444"),
                "user4@example.com",
                "user4@example.com",
                "VERIFIED",
                List.of("USER")
        );
        user4.setCitizenId("789123456");
        user4.setDriverLicense("B4789123");

        LocalUser admin = new LocalUser(
                UUID.fromString("00000000-0000-0000-0000-000000000000"),
                "admin@example.com",
                "admin@example.com",
                "VERIFIED",
                List.of("USER", "ADMIN")
        );

        // Add to maps
        addUser(user1, "password123");
        addUser(user2, "password123");
        addUser(user3, "password123");
        addUser(user4, "password123");
        addUser(admin, "admin123");
    }

    private void addUser(LocalUser user, String password) {
        usersByUsername.put(user.getUsername(), user);
        usersById.put(user.getId(), user);
        userPasswords.put(user.getUsername(), password);
    }

    public UserLoginResponse login(String username, String password) throws ApiException {
        LocalUser user = usersByUsername.get(username);
        if (user == null) {
            throw new ApiException(401, "Username không tồn tại");
        }

        String storedPassword = userPasswords.get(username);
        if (!password.equals(storedPassword)) {
            throw new ApiException(401, "Password không đúng");
        }

        UserLoginResponse response = new UserLoginResponse();
        response.setSuccess(true);
        response.setMessage("Đăng nhập thành công");
        response.setUserId(user.getId().toString());
        response.setUsername(user.getUsername());
        response.setRoles(user.getRoles());
        response.setToken("demo-jwt-token-" + user.getId());

        return response;
    }

    public UserProfileResponseDto getUserProfile(UUID userId) throws ApiException {
        LocalUser user = usersById.get(userId);
        if (user == null) {
            throw new ApiException(404, "Không tìm thấy user");
        }

        UserProfileResponseDto profile = new UserProfileResponseDto();
        profile.setId(user.getId());
        profile.setUsername(user.getUsername());
        profile.setEmail(user.getEmail());
        profile.setCitizenId(user.getCitizenId());
        profile.setDriverLicense(user.getDriverLicense());
        profile.setStatus(user.getStatus());
        profile.setRoles(user.getRoles());

        return profile;
    }

    public UserProfileResponseDto getUserProfile(String username) throws ApiException {
        LocalUser user = usersByUsername.get(username);
        if (user == null) {
            throw new ApiException(404, "Không tìm thấy user");
        }

        return getUserProfile(user.getId());
    }

    public Map<UUID, String> getAllUsers() {
        Map<UUID, String> result = new HashMap<>();
        for (LocalUser user : usersById.values()) {
            result.put(user.getId(), user.getUsername());
        }
        return result;
    }

    public String getUsernameById(UUID userId) {
        LocalUser user = usersById.get(userId);
        return user != null ? user.getUsername() : "Unknown User";
    }

    public LocalUser register(String username, String email, String password) throws ApiException {
        if (usersByUsername.containsKey(username)) {
            throw new ApiException(409, "Username đã tồn tại");
        }

        UUID userId = UUID.randomUUID();
        LocalUser user = new LocalUser(userId, username, email, "UNVERIFIED", List.of("USER"));

        addUser(user, password);
        return user;
    }

    // Methods for AuthController compatibility
    public UserLoginResponse loginUser(UserLoginRequest request) throws ApiException {
        LocalUser user = findByUsernameOrEmail(request.getUsername());

        if (user == null) {
            return new UserLoginResponse(false, "Tên đăng nhập hoặc email không tồn tại",
                    null, null, null);
        }

        String storedPassword = userPasswords.get(user.getUsername());
        if (!request.getPassword().equals(storedPassword)) {
            return new UserLoginResponse(false, "Mật khẩu không đúng",
                    null, null, null);
        }

        if (!"VERIFIED".equals(user.getStatus())) {
            return new UserLoginResponse(false, "Tài khoản chưa được xác thực",
                    null, null, null);
        }

        String token = "local-token-" + user.getId().toString();
        return new UserLoginResponse(true, "Đăng nhập thành công",
                user.getId().toString(), user.getUsername(), user.getRoles(), token);
    }

    public UserRegisterResponse registerUser(UserRegisterRequest request) throws ApiException {
        // Kiểm tra xem username đã tồn tại chưa
        if (findByUsernameOrEmail(request.getUsername()) != null) {
            return new UserRegisterResponse(false, "Tên đăng nhập đã được sử dụng",
                    null, null, null);
        }

        // Tạo user mới - sử dụng username làm email tạm thời
        UUID userId = UUID.randomUUID();
        String email = request.getUsername() + "@example.com"; // Email demo
        LocalUser newUser = new LocalUser(userId, request.getUsername(), email,
                "VERIFIED", List.of("USER")); // Tự động verify cho demo
        newUser.setCitizenId(null);
        newUser.setDriverLicense(null);

        addUser(newUser, request.getPassword());

        return new UserRegisterResponse(true, "Đăng ký thành công",
                newUser.getId().toString(), newUser.getUsername(), newUser.getRoles());
    }

    public void updateUserProfile(String token, evcc.dto.request.UpdateUserProfileRequest request) throws ApiException {
        UUID userId = extractUserIdFromToken(token);
        LocalUser user = usersById.get(userId);
        if (user == null) {
            throw new ApiException(404, "User không tồn tại");
        }

        // Cập nhật thông tin profile
        if (request.getCitizenId() != null && !request.getCitizenId().isEmpty()) {
            user.setCitizenId(request.getCitizenId());
        }
        if (request.getDriverLicense() != null && !request.getDriverLicense().isEmpty()) {
            user.setDriverLicense(request.getDriverLicense());
        }

        usersById.put(userId, user);
    }

    private UUID extractUserIdFromToken(String token) throws ApiException {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new ApiException(401, "Token không hợp lệ");
        }

        String actualToken = token.substring(7);
        if (actualToken.startsWith("demo-jwt-token-")) {
            String userIdStr = actualToken.substring("demo-jwt-token-".length());
            try {
                return UUID.fromString(userIdStr);
            } catch (IllegalArgumentException e) {
                throw new ApiException(401, "Token không hợp lệ");
            }
        }

        throw new ApiException(401, "Token không hợp lệ");
    }

    private LocalUser findByUsernameOrEmail(String usernameOrEmail) {
        for (LocalUser user : usersById.values()) {
            if (user.getUsername().equals(usernameOrEmail)
                    || user.getEmail().equals(usernameOrEmail)) {
                return user;
            }
        }
        return null;
    }
}
