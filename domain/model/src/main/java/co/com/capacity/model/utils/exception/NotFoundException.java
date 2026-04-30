package co.com.capacity.model.utils.exception;

import co.com.capacity.model.utils.GlobalExceptionEnum;
import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {
    private final GlobalExceptionEnum error;

    public NotFoundException(GlobalExceptionEnum error) {
        super(error.getMessage());
        this.error = error;
    }
}