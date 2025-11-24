package evcc.dto.response;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO response cho vote option (frontend)
 */
public class VoteOptionResponseDto {

    private Long id;
    private String optionText;
    private String description;
    private Integer displayOrder;

    private Long voteCount;
    private Double votePercentage;
    private Boolean isWinning;

    private List<VoteRecordResponseDto> voteRecords;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public VoteOptionResponseDto() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Long getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(Long voteCount) {
        this.voteCount = voteCount;
    }

    public Double getVotePercentage() {
        return votePercentage;
    }

    public void setVotePercentage(Double votePercentage) {
        this.votePercentage = votePercentage;
    }

    public Boolean getIsWinning() {
        return isWinning;
    }

    public void setIsWinning(Boolean isWinning) {
        this.isWinning = isWinning;
    }

    public List<VoteRecordResponseDto> getVoteRecords() {
        return voteRecords;
    }

    public void setVoteRecords(List<VoteRecordResponseDto> voteRecords) {
        this.voteRecords = voteRecords;
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
}
