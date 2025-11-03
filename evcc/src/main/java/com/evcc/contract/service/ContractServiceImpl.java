package com.evcc.contract.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evcc.contract.dto.ContractResponse;
import com.evcc.contract.dto.ContractSummaryResponse;
import com.evcc.contract.dto.CreateContractRequest;
import com.evcc.contract.dto.UpdateContractRequest;
import com.evcc.contract.entity.Contract;
import com.evcc.contract.entity.ContractOwnership;
import com.evcc.contract.enums.ContractStatus;
import com.evcc.contract.repository.ContractOwnershipRepository;
import com.evcc.contract.repository.ContractRepository;
import com.evcc.group.entity.Group;
import com.evcc.group.repository.GroupRepository;
import com.evcc.user.entity.User;
import com.evcc.user.repository.UserRepository;
import com.evcc.vehicle.entity.Vehicle;
import com.evcc.vehicle.repository.VehicleRepository;

/**
 * Implementation cho ContractService
 */
@Service
@Transactional
public class ContractServiceImpl implements ContractService {

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private ContractOwnershipRepository ownershipRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Override
    public ContractResponse createContract(CreateContractRequest request) {
        // Kiểm tra nhóm tồn tại
        Group group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhóm với ID: " + request.getGroupId()));

        // Kiểm tra xe nếu có
        Vehicle vehicle = null;
        if (request.getVehicleId() != null) {
            vehicle = vehicleRepository.findById(request.getVehicleId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy xe với ID: " + request.getVehicleId()));
        }

        // Validate tổng tỉ lệ sở hữu = 100%
        BigDecimal totalPercentage = request.getOwnerships().stream()
                .map(CreateContractRequest.OwnershipRequest::getOwnershipPercentage)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalPercentage.compareTo(new BigDecimal("100.00")) != 0) {
            throw new RuntimeException("Tổng tỉ lệ sở hữu phải bằng 100%");
        }

        // Tạo hợp đồng
        Contract contract = Contract.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .group(group)
                .vehicle(vehicle)
                .agreedPrice(request.getAgreedPrice())
                .signingDate(request.getSigningDate())
                .effectiveDate(request.getEffectiveDate())
                .expiryDate(request.getExpiryDate())
                .termsAndConditions(request.getTermsAndConditions())
                .notes(request.getNotes())
                .status(ContractStatus.DRAFT)
                .build();

        contract = contractRepository.save(contract);

        // Tạo ownership records
        final Contract savedContract = contract;
        List<ContractOwnership> ownerships = request.getOwnerships().stream()
                .map(ownershipReq -> {
                    User user = userRepository.findById(ownershipReq.getUserId())
                            .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + ownershipReq.getUserId()));

                    return ContractOwnership.builder()
                            .contract(savedContract)
                            .user(user)
                            .ownershipPercentage(ownershipReq.getOwnershipPercentage())
                            .contributionAmount(ownershipReq.getContributionAmount())
                            .paymentStatus(ownershipReq.getContributionAmount().compareTo(BigDecimal.ZERO) > 0 ? "PARTIAL" : "PENDING")
                            .notes(ownershipReq.getNotes())
                            .contributionDate(LocalDateTime.now())
                            .usageEligible(true)
                            .build();
                })
                .collect(Collectors.toList());

        ownershipRepository.saveAll(ownerships);

        return convertToContractResponse(savedContract);
    }

    @Override
    public ContractResponse updateContract(Long contractId, UpdateContractRequest request) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng với ID: " + contractId));

        // Cập nhật thông tin cơ bản
        contract.setTitle(request.getTitle());
        contract.setDescription(request.getDescription());
        contract.setAgreedPrice(request.getAgreedPrice());
        contract.setSigningDate(request.getSigningDate());
        contract.setEffectiveDate(request.getEffectiveDate());
        contract.setExpiryDate(request.getExpiryDate());
        contract.setTermsAndConditions(request.getTermsAndConditions());
        contract.setNotes(request.getNotes());

        if (request.getStatus() != null) {
            contract.setStatus(request.getStatus());
        }

        // Gán xe nếu có
        if (request.getVehicleId() != null) {
            Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy xe với ID: " + request.getVehicleId()));
            contract.setVehicle(vehicle);
        }

        // Cập nhật ownerships nếu có
        if (request.getOwnerships() != null && !request.getOwnerships().isEmpty()) {
            updateOwnerships(contract, request.getOwnerships());
        }

        contract = contractRepository.save(contract);
        return convertToContractResponse(contract);
    }

    @Override
    @Transactional(readOnly = true)
    public ContractResponse getContractById(Long contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng với ID: " + contractId));
        return convertToContractResponse(contract);
    }

    @Override
    @Transactional(readOnly = true)
    public ContractResponse getContractByNumber(String contractNumber) {
        Contract contract = contractRepository.findByContractNumber(contractNumber)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng với mã: " + contractNumber));
        return convertToContractResponse(contract);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContractSummaryResponse> getContractsByGroup(Long groupId) {
        List<Contract> contracts = contractRepository.findByGroupId(groupId);
        return contracts.stream()
                .map(this::convertToContractSummary)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ContractSummaryResponse> getContractsByGroup(Long groupId, Pageable pageable) {
        Page<Contract> contracts = contractRepository.findByGroupId(groupId, pageable);
        return contracts.map(this::convertToContractSummary);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContractSummaryResponse> getContractsByUser(UUID userId) {
        List<Contract> contracts = contractRepository.findContractsByUserId(userId);
        return contracts.stream()
                .map(this::convertToContractSummary)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ContractSummaryResponse> getContractsByUser(UUID userId, Pageable pageable) {
        Page<Contract> contracts = contractRepository.findContractsByUserId(userId, pageable);
        return contracts.map(this::convertToContractSummary);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContractSummaryResponse> searchContractsByTitle(String title) {
        List<Contract> contracts = contractRepository.findByTitleContainingIgnoreCase(title);
        return contracts.stream()
                .map(this::convertToContractSummary)
                .collect(Collectors.toList());
    }

    @Override
    public ContractResponse changeContractStatus(Long contractId, ContractStatus newStatus) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng với ID: " + contractId));

        // Validate trạng thái chuyển đổi
        validateStatusTransition(contract.getStatus(), newStatus);

        contract.setStatus(newStatus);

        // Nếu chuyển sang ACTIVE, kiểm tra tính hợp lệ
        if (newStatus == ContractStatus.ACTIVE) {
            if (!validateContract(contractId)) {
                throw new RuntimeException("Hợp đồng chưa hợp lệ để kích hoạt");
            }
        }

        contract = contractRepository.save(contract);
        return convertToContractResponse(contract);
    }

    @Override
    public ContractResponse assignVehicleToContract(Long contractId, Long vehicleId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng với ID: " + contractId));

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy xe với ID: " + vehicleId));

        contract.setVehicle(vehicle);
        contract = contractRepository.save(contract);

        return convertToContractResponse(contract);
    }

    @Override
    public void deleteContract(Long contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng với ID: " + contractId));

        // Chỉ cho phép xóa hợp đồng ở trạng thái DRAFT
        if (contract.getStatus() != ContractStatus.DRAFT) {
            throw new RuntimeException("Chỉ có thể xóa hợp đồng ở trạng thái bản nháp");
        }

        contractRepository.delete(contract);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validateContract(Long contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng với ID: " + contractId));

        // Kiểm tra các điều kiện cần thiết
        if (contract.getVehicle() == null) {
            return false;
        }

        // Kiểm tra tổng tỉ lệ sở hữu = 100%
        BigDecimal totalPercentage = ownershipRepository.sumOwnershipPercentageByContractId(contractId);
        if (totalPercentage == null || totalPercentage.compareTo(new BigDecimal("100.00")) != 0) {
            return false;
        }

        // Có thể thêm các validation khác

        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateContributionPercentage(Long contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng với ID: " + contractId));

        BigDecimal totalContributed = ownershipRepository.sumContributionAmountByContractId(contractId);
        if (totalContributed == null) {
            totalContributed = BigDecimal.ZERO;
        }

        if (contract.getAgreedPrice().compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return totalContributed.divide(contract.getAgreedPrice(), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContractResponse.OwnershipInfo> getVehicleUsagePriority(Long contractId) {
        List<ContractOwnership> ownerships = ownershipRepository
                .findEligibleOwnershipsByContractIdOrderedByPriority(contractId);

        return ownerships.stream()
                .map(this::convertToOwnershipInfo)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canUserUseVehicle(Long contractId, UUID userId) {
        return ownershipRepository.findByContractIdAndUserId(contractId, userId)
                .map(ownership -> ownership.getUsageEligible() && ownership.isValid())
                .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public int calculateUserPriority(Long contractId, UUID userId) {
        ContractOwnership ownership = ownershipRepository.findByContractIdAndUserId(contractId, userId)
                .orElse(null);

        if (ownership == null || !ownership.getUsageEligible()) {
            return 0;
        }

        // Tính điểm ưu tiên dựa trên tỉ lệ sở hữu
        int ownershipScore = ownership.getOwnershipPercentage().intValue();

        // Có thể thêm các yếu tố khác như lịch sử sử dụng
        // int usageHistoryScore = calculateUsageHistoryScore(userId);

        return Math.min(ownershipScore, 100); // Giới hạn tối đa 100 điểm
    }

    // Helper methods

    private void updateOwnerships(Contract contract, List<UpdateContractRequest.OwnershipUpdateRequest> ownershipRequests) {
        for (UpdateContractRequest.OwnershipUpdateRequest req : ownershipRequests) {
            if (req.getId() != null) {
                // Cập nhật existing ownership
                ContractOwnership ownership = ownershipRepository.findById(req.getId())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy ownership với ID: " + req.getId()));

                if (req.getOwnershipPercentage() != null) {
                    ownership.setOwnershipPercentage(req.getOwnershipPercentage());
                }
                if (req.getContributionAmount() != null) {
                    ownership.setContributionAmount(req.getContributionAmount());
                }
                if (req.getPaymentStatus() != null) {
                    ownership.setPaymentStatus(req.getPaymentStatus());
                }
                if (req.getUsageEligible() != null) {
                    ownership.setUsageEligible(req.getUsageEligible());
                }
                if (req.getNotes() != null) {
                    ownership.setNotes(req.getNotes());
                }

                ownershipRepository.save(ownership);
            } else if (req.getUserId() != null) {
                // Tạo mới ownership
                User user = userRepository.findById(req.getUserId())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + req.getUserId()));

                ContractOwnership newOwnership = ContractOwnership.builder()
                        .contract(contract)
                        .user(user)
                        .ownershipPercentage(req.getOwnershipPercentage())
                        .contributionAmount(req.getContributionAmount())
                        .paymentStatus(req.getPaymentStatus() != null ? req.getPaymentStatus() : "PENDING")
                        .usageEligible(req.getUsageEligible() != null ? req.getUsageEligible() : true)
                        .notes(req.getNotes())
                        .build();

                ownershipRepository.save(newOwnership);
            }
        }
    }

    private void validateStatusTransition(ContractStatus currentStatus, ContractStatus newStatus) {
        // Implement business rules cho status transitions
        switch (currentStatus) {
            case DRAFT:
                if (newStatus != ContractStatus.PENDING && newStatus != ContractStatus.CANCELLED) {
                    throw new RuntimeException("Từ DRAFT chỉ có thể chuyển sang PENDING hoặc CANCELLED");
                }
                break;
            case PENDING:
                if (newStatus != ContractStatus.ACTIVE && newStatus != ContractStatus.CANCELLED) {
                    throw new RuntimeException("Từ PENDING chỉ có thể chuyển sang ACTIVE hoặc CANCELLED");
                }
                break;
            case ACTIVE:
                if (newStatus != ContractStatus.COMPLETED) {
                    throw new RuntimeException("Từ ACTIVE chỉ có thể chuyển sang COMPLETED");
                }
                break;
            case COMPLETED:
            case CANCELLED:
                throw new RuntimeException("Không thể thay đổi trạng thái từ " + currentStatus);
            default:
                throw new RuntimeException("Trạng thái không hợp lệ: " + currentStatus);
        }
    }

    private ContractResponse convertToContractResponse(Contract contract) {
        List<ContractOwnership> ownerships = ownershipRepository.findByContractId(contract.getId());

        return ContractResponse.builder()
                .id(contract.getId())
                .contractNumber(contract.getContractNumber())
                .title(contract.getTitle())
                .description(contract.getDescription())
                .group(convertToGroupInfo(contract.getGroup()))
                .vehicle(contract.getVehicle() != null ? convertToVehicleInfo(contract.getVehicle()) : null)
                .agreedPrice(contract.getAgreedPrice())
                .signingDate(contract.getSigningDate())
                .effectiveDate(contract.getEffectiveDate())
                .expiryDate(contract.getExpiryDate())
                .status(contract.getStatus())
                .termsAndConditions(contract.getTermsAndConditions())
                .notes(contract.getNotes())
                .ownerships(ownerships.stream().map(this::convertToOwnershipInfo).collect(Collectors.toList()))
                .createdAt(contract.getCreatedAt())
                .updatedAt(contract.getUpdatedAt())
                .build();
    }

    private ContractSummaryResponse convertToContractSummary(Contract contract) {
        BigDecimal totalContributed = ownershipRepository.sumContributionAmountByContractId(contract.getId());
        if (totalContributed == null) totalContributed = BigDecimal.ZERO;

        long totalOwners = ownershipRepository.countByContractId(contract.getId());

        BigDecimal contributionPercentage = BigDecimal.ZERO;
        if (contract.getAgreedPrice().compareTo(BigDecimal.ZERO) > 0) {
            contributionPercentage = totalContributed.divide(contract.getAgreedPrice(), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
        }

        return ContractSummaryResponse.builder()
                .id(contract.getId())
                .contractNumber(contract.getContractNumber())
                .title(contract.getTitle())
                .groupName(contract.getGroup().getName())
                .vehicleName(contract.getVehicle() != null ? contract.getVehicle().getName() : null)
                .vehicleLicensePlate(contract.getVehicle() != null ? contract.getVehicle().getLicensePlate() : null)
                .agreedPrice(contract.getAgreedPrice())
                .signingDate(contract.getSigningDate())
                .status(contract.getStatus())
                .totalOwners((int) totalOwners)
                .totalContributed(totalContributed)
                .contributionPercentage(contributionPercentage)
                .build();
    }

    private ContractResponse.GroupInfo convertToGroupInfo(Group group) {
        return ContractResponse.GroupInfo.builder()
                .id(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .build();
    }

    private ContractResponse.VehicleInfo convertToVehicleInfo(Vehicle vehicle) {
        return ContractResponse.VehicleInfo.builder()
                .id(vehicle.getId())
                .name(vehicle.getName())
                .licensePlate(vehicle.getLicensePlate())
                .make(vehicle.getMake())
                .model(vehicle.getModel())
                .year(vehicle.getYear())
                .build();
    }

    private ContractResponse.OwnershipInfo convertToOwnershipInfo(ContractOwnership ownership) {
        return ContractResponse.OwnershipInfo.builder()
                .id(ownership.getId())
                .user(convertToUserInfo(ownership.getUser()))
                .ownershipPercentage(ownership.getOwnershipPercentage())
                .contributionAmount(ownership.getContributionAmount())
                .contributionDate(ownership.getContributionDate())
                .paymentStatus(ownership.getPaymentStatus())
                .usageEligible(ownership.getUsageEligible())
                .notes(ownership.getNotes())
                .createdAt(ownership.getCreatedAt())
                .updatedAt(ownership.getUpdatedAt())
                .build();
    }

    private ContractResponse.UserInfo convertToUserInfo(User user) {
        return ContractResponse.UserInfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(null) // User entity chưa có email
                .firstName(null) // User entity chưa có firstName
                .lastName(null) // User entity chưa có lastName
                .build();
    }
}