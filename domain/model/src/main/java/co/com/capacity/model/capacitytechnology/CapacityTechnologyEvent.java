package co.com.capacity.model.capacitytechnology;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
public class CapacityTechnologyEvent {
    private Long capacityId;
    private Long technologyCatalogId;
}