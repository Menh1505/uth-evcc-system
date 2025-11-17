package evcc.dto.request;

public class UpdateUserProfileRequest {

    private String citizenId;
    private String driverLicense;

    public UpdateUserProfileRequest() {
    }

    public UpdateUserProfileRequest(String citizenId, String driverLicense) {
        this.citizenId = citizenId;
        this.driverLicense = driverLicense;
    }

    public String getCitizenId() {
        return citizenId;
    }

    public void setCitizenId(String citizenId) {
        this.citizenId = citizenId;
    }

    public String getDriverLicense() {
        return driverLicense;
    }

    public void setDriverLicense(String driverLicense) {
        this.driverLicense = driverLicense;
    }
}

