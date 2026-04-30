package co.com.capacity.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorData {
    private final String errorCode;
    private final String message;
    private final String description;

    public static ErrorData of(co.com.capacity.model.utils.GlobalExceptionEnum error) {
        return new ErrorData(error.name(), error.getMessage(), error.getDescription());
    }
}