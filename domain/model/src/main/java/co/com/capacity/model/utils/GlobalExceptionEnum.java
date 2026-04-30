package co.com.capacity.model.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GlobalExceptionEnum {
    CAPACITY_NOT_FOUND("Capacity not found", "No capacity found with the provided identifier"),
    CAPACITY_NAME_ALREADY_EXISTS("Capacity name already exists", "A capacity with the provided name already exists"),
    CAPACITY_TECHNOLOGY_NOT_FOUND("Capacity technology not found", "No capacity technology found with the provided identifier"),
    CAPACITY_TECHNOLOGY_ALREADY_EXISTS("Capacity technology already exists", "A capacity technology with the provided data already exists"),
    TECHNOLOGY_CATALOG_NOT_FOUND("Technology catalog not found", "No technology catalog found with the provided identifier"),
    TECHNOLOGY_NOT_FOUND("Technology not found", "One or more technologies not found"),
    TECHNOLOGY_IN_USE("Technology in use", "Cannot delete technology because it is being used in a capacity"),
    AUTH_NOT_FOUND("Auth not found", "No auth found with the provided token"),
    UNAUTHORIZED("Unauthorized", "Token is missing or invalid"),
    TOKEN_EXPIRED("Token expired", "The provided token has expired"),
    INVALID_REQUEST("Invalid request", "The provided request is invalid");

    private final String message;
    private final String description;
}