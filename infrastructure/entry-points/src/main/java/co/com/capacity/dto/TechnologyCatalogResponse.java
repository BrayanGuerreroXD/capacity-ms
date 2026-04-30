package co.com.capacity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TechnologyCatalogResponse {
    private Long id;
    private Long externalId;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}