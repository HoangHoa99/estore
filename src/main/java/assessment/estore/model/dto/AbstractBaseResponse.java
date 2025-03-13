package assessment.estore.model.dto;

public abstract class AbstractBaseResponse {

    private Boolean error = false;
    private String message;

    public AbstractBaseResponse(String message, Boolean error) {
        this.message = message;
        this.error = error;
    }

    public AbstractBaseResponse() {
    }

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
