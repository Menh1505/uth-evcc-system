package evcc.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ContractResponseDto {

    public static class GroupInfoDto {
        private Long id;
        private String name;
        private String description;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class VehicleInfoDto {
        private Long id;
        private String name;
        private String licensePlate;
        private String make;
        private String model;
        private Integer year;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getLicensePlate() { return licensePlate; }
        public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
        public String getMake() { return make; }
        public void setMake(String make) { this.make = make; }
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        public Integer getYear() { return year; }
        public void setYear(Integer year) { this.year = year; }
    }

    public static class OwnershipInfoDto {
        public static class UserInfoDto {
            private java.util.UUID id;
            private String username;

            public java.util.UUID getId() { return id; }
            public void setId(java.util.UUID id) { this.id = id; }
            public String getUsername() { return username; }
            public void setUsername(String username) { this.username = username; }
        }

        private Long id;
        private UserInfoDto user;
        private BigDecimal ownershipPercentage;
        private BigDecimal contributionAmount;
        private LocalDateTime contributionDate;
        private String paymentStatus;
        private Boolean usageEligible;
        private String notes;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public UserInfoDto getUser() { return user; }
        public void setUser(UserInfoDto user) { this.user = user; }
        public BigDecimal getOwnershipPercentage() { return ownershipPercentage; }
        public void setOwnershipPercentage(BigDecimal ownershipPercentage) { this.ownershipPercentage = ownershipPercentage; }
        public BigDecimal getContributionAmount() { return contributionAmount; }
        public void setContributionAmount(BigDecimal contributionAmount) { this.contributionAmount = contributionAmount; }
        public LocalDateTime getContributionDate() { return contributionDate; }
        public void setContributionDate(LocalDateTime contributionDate) { this.contributionDate = contributionDate; }
        public String getPaymentStatus() { return paymentStatus; }
        public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
        public Boolean getUsageEligible() { return usageEligible; }
        public void setUsageEligible(Boolean usageEligible) { this.usageEligible = usageEligible; }
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }

    private Long id;
    private String contractNumber;
    private String title;
    private String description;
    private GroupInfoDto group;
    private VehicleInfoDto vehicle;
    private BigDecimal agreedPrice;
    private LocalDate signingDate;
    private LocalDate effectiveDate;
    private LocalDate expiryDate;
    private String status;
    private String termsAndConditions;
    private String notes;
    private List<OwnershipInfoDto> ownerships;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getContractNumber() { return contractNumber; }
    public void setContractNumber(String contractNumber) { this.contractNumber = contractNumber; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public GroupInfoDto getGroup() { return group; }
    public void setGroup(GroupInfoDto group) { this.group = group; }
    public VehicleInfoDto getVehicle() { return vehicle; }
    public void setVehicle(VehicleInfoDto vehicle) { this.vehicle = vehicle; }
    public BigDecimal getAgreedPrice() { return agreedPrice; }
    public void setAgreedPrice(BigDecimal agreedPrice) { this.agreedPrice = agreedPrice; }
    public LocalDate getSigningDate() { return signingDate; }
    public void setSigningDate(LocalDate signingDate) { this.signingDate = signingDate; }
    public LocalDate getEffectiveDate() { return effectiveDate; }
    public void setEffectiveDate(LocalDate effectiveDate) { this.effectiveDate = effectiveDate; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getTermsAndConditions() { return termsAndConditions; }
    public void setTermsAndConditions(String termsAndConditions) { this.termsAndConditions = termsAndConditions; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public List<OwnershipInfoDto> getOwnerships() { return ownerships; }
    public void setOwnerships(List<OwnershipInfoDto> ownerships) { this.ownerships = ownerships; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

