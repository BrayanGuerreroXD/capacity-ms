package co.com.capacity.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CapacityTechnologyRequest {
    @NotNull(message = "Capacity ID is required")
    private Long capacityId;

    @NotNull(message = "Technology ID is required")
    private Long technologyId;
}