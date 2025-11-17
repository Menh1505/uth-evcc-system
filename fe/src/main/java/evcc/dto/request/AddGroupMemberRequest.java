package evcc.dto.request;

public class AddGroupMemberRequest {

    private String userId;

    public AddGroupMemberRequest() {
    }

    public AddGroupMemberRequest(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}

