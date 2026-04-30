package co.com.capacity.drivenAdapters.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table("capacity_technologies")
public class CapacityTechnologyEntity {
    @Id
    private Long id;
    private Long capacityId;
    private Long technologyId;
}