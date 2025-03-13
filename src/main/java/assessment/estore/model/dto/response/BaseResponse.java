package assessment.estore.model.dto.response;

import assessment.estore.model.dto.AbstractBaseResponse;

import java.io.Serializable;

public class BaseResponse extends AbstractBaseResponse implements Serializable {

    public BaseResponse() {
    }

    public BaseResponse(String message) {
        super(message, false);
    }

    public BaseResponse(String message, Boolean error) {
        super(message, error);
    }
}
