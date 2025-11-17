package evcc.exception;

public class ApiException extends Exception {
    
    private final int statusCode;
    private final String errorMessage;
    
    public ApiException(int statusCode, String errorMessage) {
        super(errorMessage);
        this.statusCode = statusCode;
        this.errorMessage = errorMessage;
    }
    
    public ApiException(int statusCode, String errorMessage, Throwable cause) {
        super(errorMessage, cause);
        this.statusCode = statusCode;
        this.errorMessage = errorMessage;
    }
    
    public int getStatusCode() {
        return statusCode;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
}