CREATE TABLE password_reset_token (
    id BIGINT NOT NULL AUTO_INCREMENT,
    id_secretaria INT NOT NULL,
    token_hash VARCHAR(64) NOT NULL,
    expires_at DATETIME NOT NULL,
    created_at DATETIME NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_password_reset_token_hash (token_hash),
    KEY idx_password_reset_token_secretaria (id_secretaria),
    CONSTRAINT fk_password_reset_token_secretaria
        FOREIGN KEY (id_secretaria) REFERENCES secretaria (id_secretaria)
        ON DELETE CASCADE
) ENGINE=InnoDB;
