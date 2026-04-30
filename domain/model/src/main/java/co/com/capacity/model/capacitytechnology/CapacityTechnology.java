package co.com.capacity.model.capacitytechnology;

import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
public class CapacityTechnology {
    private Long id;
    private Long capacityId;
    private Long technologyId;
}