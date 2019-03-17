-- Spring Boot runs 'schema-${platform}.sql' file automatically
-- during startup. '-all' is the default for all platforms.

-- DROP TABLE IF EXISTS person;

CREATE TABLE IF NOT EXISTS person (
    person_id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255),
    department VARCHAR(255),
    update_time DATE
);