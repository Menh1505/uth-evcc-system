package evcc.dto.local;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO cho contract lưu trữ local (demo)
 */
public class LocalContract {

    public static class LocalVehicle {

        private Long id;
        private String name;
        private String licensePlate;
        private String make;
        private String model;
        private Integer year;
        private BigDecimal purchasePrice;

        public LocalVehicle() {
        }

        public LocalVehicle(Long id, String name, String licensePlate, String make, String model, Integer year, BigDecimal purchasePrice) {
            this.id = id;
            this.name = name;
            this.licensePlate = licensePlate;
            this.make = make;
            this.model = model;
            this.year = year;
            this.purchasePrice = purchasePrice;
        }

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLicensePlate() {
            return licensePlate;
        }

        public void setLicensePlate(String licensePlate) {
            this.licensePlate = licensePlate;
        }

        public String getMake() {
            return make;
        }

        public void setMake(String make) {
            this.make = make;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public Integer getYear() {
            return year;
        }

        public void setYear(Integer year) {
            this.year = year;
        }

        public BigDecimal getPurchasePrice() {
            return purchasePrice;
        }

        public void setPurchasePrice(BigDecimal purchasePrice) {
            this.purchasePrice = purchasePrice;
        }
    }

    public static class LocalOwnership {

        private Long id;
        private UUID userId;
        private String username;
        private BigDecimal ownershipPercentage;
        private BigDecimal contributionAmount;
        private String paymentStatus;
        private String notes;

        public LocalOwnership() {
        }

        public LocalOwnership(Long id, UUID userId, String username, BigDecimal ownershipPercentage,
                BigDecimal contributionAmount, String paymentStatus, String notes) {
            this.id = id;
            this.userId = userId;
            this.username = username;
            this.ownershipPercentage = ownershipPercentage;
            this.contributionAmount = contributionAmount;
            this.paymentStatus = paymentStatus;
            this.notes = notes;
        }

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public UUID getUserId() {
            return userId;
        }

        public void setUserId(UUID userId) {
            this.userId = userId;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public BigDecimal getOwnershipPercentage() {
            return ownershipPercentage;
        }

        public void setOwnershipPercentage(BigDecimal ownershipPercentage) {
            this.ownershipPercentage = ownershipPercentage;
        }

        public BigDecimal getContributionAmount() {
            return contributionAmount;
        }

        public void setContributionAmount(BigDecimal contributionAmount) {
            this.contributionAmount = contributionAmount;
        }

        public String getPaymentStatus() {
            return paymentStatus;
        }

        public void setPaymentStatus(String paymentStatus) {
            this.paymentStatus = paymentStatus;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }
    }

    private Long id;
    private String contractNumber;
    private String title;
    private String description;
    private Long groupId;
    private String groupName;
    private LocalVehicle vehicle;
    private BigDecimal agreedPrice;
    private LocalDate signingDate;
    private LocalDate effectiveDate;
    private LocalDate expiryDate;
    private String status; // DRAFT, PENDING_VOTES, APPROVED, REJECTED, ACTIVE, EXPIRED
    private String termsAndConditions;
    private String notes;
    private List<LocalOwnership> ownerships;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID createdBy;
    private String createdByUsername;

    public LocalContract() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public LocalVehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(LocalVehicle vehicle) {
        this.vehicle = vehicle;
    }

    public BigDecimal getAgreedPrice() {
        return agreedPrice;
    }

    public void setAgreedPrice(BigDecimal agreedPrice) {
        this.agreedPrice = agreedPrice;
    }

    public LocalDate getSigningDate() {
        return signingDate;
    }

    public void setSigningDate(LocalDate signingDate) {
        this.signingDate = signingDate;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTermsAndConditions() {
        return termsAndConditions;
    }

    public void setTermsAndConditions(String termsAndConditions) {
        this.termsAndConditions = termsAndConditions;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<LocalOwnership> getOwnerships() {
        return ownerships;
    }

    public void setOwnerships(List<LocalOwnership> ownerships) {
        this.ownerships = ownerships;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedByUsername() {
        return createdByUsername;
    }

    public void setCreatedByUsername(String createdByUsername) {
        this.createdByUsername = createdByUsername;
    }
}
