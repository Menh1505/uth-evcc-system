package evcc.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class CreateContractRequestDto {

    private String title;
    private String description;
    private Long groupId;
    private Long vehicleId;
    private BigDecimal agreedPrice;
    private LocalDate signingDate;
    private LocalDate effectiveDate;
    private LocalDate expiryDate;
    private String termsAndConditions;
    private String notes;
    private List<OwnershipRequestDto> ownerships;

    public static class OwnershipRequestDto {
        private UUID userId;
        private BigDecimal ownershipPercentage;
        private BigDecimal contributionAmount;
        private String notes;

        public UUID getUserId() {
            return userId;
        }

        public void setUserId(UUID userId) {
            this.userId = userId;
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

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }
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

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
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

    public List<OwnershipRequestDto> getOwnerships() {
        return ownerships;
    }

    public void setOwnerships(List<OwnershipRequestDto> ownerships) {
        this.ownerships = ownerships;
    }
}

