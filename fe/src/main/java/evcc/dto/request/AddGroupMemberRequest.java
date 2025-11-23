package evcc.dto.request;

public class AddGroupMemberRequest {

    private String userId;
    private String vehicleID;
// toàn getter seter
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
      public void setVehicle(String vehicleID) {
        this.vehicleID =vehicleID;
    }
}

