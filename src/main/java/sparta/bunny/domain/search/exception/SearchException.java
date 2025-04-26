package sparta.bunny.domain.search.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import sparta.bunny.common.exception.BaseException;
import sparta.bunny.common.response.ResponseCode;

@Getter
public class SearchException extends BaseException {
    private final ResponseCode responseCode;
    private final HttpStatus httpStatus;

    public SearchException(ResponseCode responseCode) {
        this.responseCode = responseCode;
        this.httpStatus = responseCode.getHttpStatus();
    }
}
