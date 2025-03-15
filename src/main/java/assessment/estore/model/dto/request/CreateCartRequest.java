package assessment.estore.model.dto.request;

import jakarta.validation.constraints.NotEmpty;

public class CreateCartRequest {
    @NotEmpty
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
