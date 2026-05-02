package co.com.capacity.model.capacitybootcamp;

import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
public class CapacityBootcamp {
    private Long id;
    private Long capacityId;
    private Long bootcampId;
}