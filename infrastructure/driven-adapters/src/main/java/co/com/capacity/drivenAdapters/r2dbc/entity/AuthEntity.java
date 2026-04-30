package co.com.capacity.drivenAdapters.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table("auth")
public class AuthEntity {
    @Id
    private Long id;
    private String email;
    private String token;
    private Integer expiresIn;
    @Column("created_at")
    private LocalDateTime createdAt;
}