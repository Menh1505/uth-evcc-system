package evcc.dto.response;

public class UserStatsResponseDto {

    private long totalUsers;
    private long verifiedUsers;
    private long unverifiedUsers;
    private long usersWithCompleteInfo;
    private long usersWithIncompleteInfo;

    public UserStatsResponseDto() {
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getVerifiedUsers() {
        return verifiedUsers;
    }

    public void setVerifiedUsers(long verifiedUsers) {
        this.verifiedUsers = verifiedUsers;
    }

    public long getUnverifiedUsers() {
        return unverifiedUsers;
    }

    public void setUnverifiedUsers(long unverifiedUsers) {
        this.unverifiedUsers = unverifiedUsers;
    }

    public long getUsersWithCompleteInfo() {
        return usersWithCompleteInfo;
    }

    public void setUsersWithCompleteInfo(long usersWithCompleteInfo) {
        this.usersWithCompleteInfo = usersWithCompleteInfo;
    }

    public long getUsersWithIncompleteInfo() {
        return usersWithIncompleteInfo;
    }

    public void setUsersWithIncompleteInfo(long usersWithIncompleteInfo) {
        this.usersWithIncompleteInfo = usersWithIncompleteInfo;
    }
}

