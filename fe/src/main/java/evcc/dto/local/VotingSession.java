package evcc.dto.local;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO cho voting session của contract (demo)
 */
public class VotingSession {

    public static class VoteRecord {

        private UUID userId;
        private String username;
        private String vote; // APPROVE, REJECT
        private String reason;
        private LocalDateTime votedAt;

        public VoteRecord() {
        }

        public VoteRecord(UUID userId, String username, String vote, String reason, LocalDateTime votedAt) {
            this.userId = userId;
            this.username = username;
            this.vote = vote;
            this.reason = reason;
            this.votedAt = votedAt;
        }

        // Getters and Setters
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

        public String getVote() {
            return vote;
        }

        public void setVote(String vote) {
            this.vote = vote;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public LocalDateTime getVotedAt() {
            return votedAt;
        }

        public void setVotedAt(LocalDateTime votedAt) {
            this.votedAt = votedAt;
        }
    }

    private Long contractId;
    private String title;
    private String description;
    private LocalDateTime startedAt;
    private LocalDateTime endsAt;
    private String status; // ACTIVE, ENDED
    private int totalMembers;
    private int requiredVotes; // số vote cần để approve
    private List<VoteRecord> votes;

    public VotingSession() {
    }

    // Getters and Setters
    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
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

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getEndsAt() {
        return endsAt;
    }

    public void setEndsAt(LocalDateTime endsAt) {
        this.endsAt = endsAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTotalMembers() {
        return totalMembers;
    }

    public void setTotalMembers(int totalMembers) {
        this.totalMembers = totalMembers;
    }

    public int getRequiredVotes() {
        return requiredVotes;
    }

    public void setRequiredVotes(int requiredVotes) {
        this.requiredVotes = requiredVotes;
    }

    public List<VoteRecord> getVotes() {
        return votes;
    }

    public void setVotes(List<VoteRecord> votes) {
        this.votes = votes;
    }

    // Helper methods
    public long getApproveCount() {
        return votes.stream().filter(v -> "APPROVE".equals(v.getVote())).count();
    }

    public long getRejectCount() {
        return votes.stream().filter(v -> "REJECT".equals(v.getVote())).count();
    }

    public boolean hasUserVoted(UUID userId) {
        return votes.stream().anyMatch(v -> v.getUserId().equals(userId));
    }

    public boolean isApproved() {
        return getApproveCount() >= requiredVotes;
    }

    public boolean isRejected() {
        // Nếu số vote reject + số vote chưa vote không đủ để đạt required votes
        long remainingVotes = totalMembers - votes.size();
        return getApproveCount() + remainingVotes < requiredVotes;
    }
}
