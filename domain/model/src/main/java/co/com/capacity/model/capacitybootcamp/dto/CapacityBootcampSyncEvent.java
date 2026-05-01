package co.com.capacity.model.capacitybootcamp.dto;

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
public class CapacityBootcampSyncEvent {
    private Long bootcampId;
    private List<Long> capacityIds;
}