CREATE TABLE IF NOT EXISTS capacity_bootcamps (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    capacity_id BIGINT NOT NULL,
    bootcamp_id BIGINT NOT NULL,
    FOREIGN KEY (capacity_id) REFERENCES capacities(id) ON DELETE CASCADE,
    UNIQUE KEY unique_capacity_bootcamp (capacity_id, bootcamp_id)
);