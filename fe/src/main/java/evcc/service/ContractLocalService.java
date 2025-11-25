package evcc.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import evcc.dto.local.LocalContract;
import evcc.dto.local.VotingSession;
import evcc.dto.response.GroupResponseDto;
import evcc.dto.response.VehicleResponseDto;
import evcc.exception.ApiException;

/**
 * Service để quản lý contract local (demo) với vehicle và voting
 */
@Service
public class ContractLocalService {

    private final AtomicLong contractIdGenerator = new AtomicLong(1);
    private final Map<Long, LocalContract> contracts = new HashMap<>();
    private final Map<Long, VotingSession> votingSessions = new HashMap<>();

    // Demo vehicles - trong thực tế sẽ lấy từ VehicleService
    private final List<LocalContract.LocalVehicle> demoVehicles = new ArrayList<>();

    // Demo users - trong thực tế sẽ lấy từ group members
    private final Map<UUID, String> demoUsers = new HashMap<>();

    public ContractLocalService() {
        initializeDemoData();
    }

    private void initializeDemoData() {
        // Demo vehicles
        demoVehicles.add(new LocalContract.LocalVehicle(1L, "Tesla Model 3", "30A-12345", "Tesla", "Model 3", 2023, new BigDecimal("1200000000")));
        demoVehicles.add(new LocalContract.LocalVehicle(2L, "VinFast VF8", "30A-67890", "VinFast", "VF8", 2024, new BigDecimal("1500000000")));
        demoVehicles.add(new LocalContract.LocalVehicle(3L, "BYD Tang", "30A-11111", "BYD", "Tang", 2024, new BigDecimal("1000000000")));

        // Demo users
        demoUsers.put(UUID.fromString("11111111-1111-1111-1111-111111111111"), "user1@example.com");
        demoUsers.put(UUID.fromString("22222222-2222-2222-2222-222222222222"), "user2@example.com");
        demoUsers.put(UUID.fromString("33333333-3333-3333-3333-333333333333"), "user3@example.com");
        demoUsers.put(UUID.fromString("44444444-4444-4444-4444-444444444444"), "user4@example.com");
    }

    public List<LocalContract.LocalVehicle> getAvailableVehicles() {
        // Lọc ra những vehicle chưa được sử dụng trong contract nào
        List<Long> usedVehicleIds = contracts.values().stream()
                .map(contract -> contract.getVehicle())
                .filter(vehicle -> vehicle != null)
                .map(LocalContract.LocalVehicle::getId)
                .collect(Collectors.toList());

        return demoVehicles.stream()
                .filter(vehicle -> !usedVehicleIds.contains(vehicle.getId()))
                .collect(Collectors.toList());
    }

    public LocalContract createContract(
            String title, String description, Long groupId, String groupName,
            Long vehicleId, BigDecimal agreedPrice,
            String termsAndConditions, String notes,
            List<LocalContract.LocalOwnership> ownerships,
            UUID createdBy, String createdByUsername
    ) throws ApiException {

        // Tìm vehicle
        LocalContract.LocalVehicle selectedVehicle = demoVehicles.stream()
                .filter(v -> v.getId().equals(vehicleId))
                .findFirst()
                .orElseThrow(() -> new ApiException(404, "Không tìm thấy vehicle"));

        // Kiểm tra vehicle đã được sử dụng chưa
        boolean vehicleUsed = contracts.values().stream()
                .anyMatch(contract -> contract.getVehicle() != null
                && contract.getVehicle().getId().equals(vehicleId));

        if (vehicleUsed) {
            throw new ApiException(400, "Vehicle đã được sử dụng trong contract khác");
        }

        Long contractId = contractIdGenerator.getAndIncrement();
        LocalContract contract = new LocalContract();

        contract.setId(contractId);
        contract.setContractNumber("CONTRACT-" + String.format("%06d", contractId));
        contract.setTitle(title);
        contract.setDescription(description);
        contract.setGroupId(groupId);
        contract.setGroupName(groupName);
        contract.setVehicle(selectedVehicle);
        contract.setAgreedPrice(agreedPrice);
        contract.setStatus("PENDING_VOTES");
        contract.setTermsAndConditions(termsAndConditions);
        contract.setNotes(notes);
        contract.setOwnerships(ownerships);
        contract.setCreatedAt(LocalDateTime.now());
        contract.setUpdatedAt(LocalDateTime.now());
        contract.setCreatedBy(createdBy);
        contract.setCreatedByUsername(createdByUsername);

        contracts.put(contractId, contract);

        // Tạo voting session
        createVotingSession(contractId, title, description, ownerships.size());

        return contract;
    }

    public List<LocalContract> getContractsByGroup(Long groupId) {
        return contracts.values().stream()
                .filter(contract -> contract.getGroupId().equals(groupId))
                .collect(Collectors.toList());
    }

    public LocalContract getContractById(Long contractId) throws ApiException {
        LocalContract contract = contracts.get(contractId);
        if (contract == null) {
            throw new ApiException(404, "Không tìm thấy contract");
        }
        return contract;
    }

    public VotingSession getVotingSession(Long contractId) throws ApiException {
        VotingSession session = votingSessions.get(contractId);
        if (session == null) {
            throw new ApiException(404, "Không tìm thấy phiên voting");
        }
        return session;
    }

    public void voteContract(Long contractId, UUID userId, String username, String vote, String reason) throws ApiException {
        VotingSession session = getVotingSession(contractId);

        if (!"ACTIVE".equals(session.getStatus())) {
            throw new ApiException(400, "Phiên voting đã kết thúc");
        }

        if (session.hasUserVoted(userId)) {
            throw new ApiException(400, "Bạn đã vote cho contract này rồi");
        }

        if (!"APPROVE".equals(vote) && !"REJECT".equals(vote)) {
            throw new ApiException(400, "Vote không hợp lệ. Chỉ chấp nhận APPROVE hoặc REJECT");
        }

        // Thêm vote
        VotingSession.VoteRecord voteRecord = new VotingSession.VoteRecord(
                userId, username, vote, reason, LocalDateTime.now()
        );
        session.getVotes().add(voteRecord);

        // Kiểm tra kết quả
        if (session.isApproved()) {
            session.setStatus("ENDED");
            LocalContract contract = getContractById(contractId);
            contract.setStatus("APPROVED");
            contract.setUpdatedAt(LocalDateTime.now());
        } else if (session.isRejected()) {
            session.setStatus("ENDED");
            LocalContract contract = getContractById(contractId);
            contract.setStatus("REJECTED");
            contract.setUpdatedAt(LocalDateTime.now());
        }
    }

    public List<LocalContract> getAllContracts() {
        return new ArrayList<>(contracts.values());
    }

    public List<VotingSession> getActiveVotingSessions() {
        return votingSessions.values().stream()
                .filter(session -> "ACTIVE".equals(session.getStatus()))
                .collect(Collectors.toList());
    }

    public List<VotingSession> getVotingSessionsForUser(UUID userId) {
        // Trả về các session mà user chưa vote và đang active
        return votingSessions.values().stream()
                .filter(session -> "ACTIVE".equals(session.getStatus()))
                .filter(session -> !session.hasUserVoted(userId))
                .collect(Collectors.toList());
    }

    /**
     * Get contract by vehicle ID
     */
    public LocalContract getContractByVehicleId(Long vehicleId) {
        return contracts.values().stream()
                .filter(contract -> contract.getVehicle() != null
                && contract.getVehicle().getId().equals(vehicleId))
                .findFirst()
                .orElse(null);
    }

    private void createVotingSession(Long contractId, String title, String description, int totalMembers) {
        VotingSession session = new VotingSession();
        session.setContractId(contractId);
        session.setTitle("Vote cho contract: " + title);
        session.setDescription(description);
        session.setStartedAt(LocalDateTime.now());
        session.setEndsAt(LocalDateTime.now().plusDays(7)); // 7 ngày để vote
        session.setStatus("ACTIVE");
        session.setTotalMembers(Math.max(totalMembers, 1)); // ít nhất 1 member
        session.setRequiredVotes(session.getTotalMembers()); // cần 100% approve (tất cả thành viên)
        session.setVotes(new ArrayList<>());

        votingSessions.put(contractId, session);
    }

    // Utility methods for demo
    public String getUsernameById(UUID userId) {
        return demoUsers.getOrDefault(userId, "Unknown User");
    }

    public Map<UUID, String> getDemoUsers() {
        return new HashMap<>(demoUsers);
    }
}
