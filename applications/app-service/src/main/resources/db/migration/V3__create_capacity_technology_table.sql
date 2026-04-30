CREATE TABLE IF NOT EXISTS capacity_technologies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    capacity_id BIGINT NOT NULL,
    technology_id BIGINT NOT NULL,
    FOREIGN KEY (capacity_id) REFERENCES capacities(id) ON DELETE CASCADE,
    FOREIGN KEY (technology_id) REFERENCES technology_catalogs(id) ON DELETE CASCADE,
    UNIQUE KEY unique_capacity_technology (capacity_id, technology_id)
);