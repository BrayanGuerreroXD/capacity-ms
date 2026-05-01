package co.com.capacity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CapacityRequest {
    @NotBlank(message = "Name is required")
    @Size(max = 50, message = "Name must be less than 50 characters")
    private String name;

    @NotBlank(message = "Description is required")
    @Size(max = 90, message = "Description must be less than 90 characters")
    private String description;

    @NotEmpty(message = "Technology list cannot be empty")
    @Size(min = 3, max = 20, message = "Technology list must have between 3 and 20 items")
    private List<CapacityTechnologyRequest> technologies;
}