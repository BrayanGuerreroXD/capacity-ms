package co.com.capacity.model.technologycatalog;

import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
public class TechnologyCatalog {
    private Long id;
    private Long externalId;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}