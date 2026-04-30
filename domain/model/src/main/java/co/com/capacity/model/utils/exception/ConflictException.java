package co.com.capacity.model.utils.exception;

import co.com.capacity.model.utils.GlobalExceptionEnum;
import lombok.Getter;

@Getter
public class ConflictException extends RuntimeException {
    private final GlobalExceptionEnum error;

    public ConflictException(GlobalExceptionEnum error) {
        super(error.getMessage());
        this.error = error;
    }
}