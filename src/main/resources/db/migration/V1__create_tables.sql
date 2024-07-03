CREATE TABLE IF NOT EXISTS users (
    average_rating DOUBLE DEFAULT 0,
    id BIGINT NOT NULL AUTO_INCREMENT,
    designation VARCHAR(50) DEFAULT NULL,
    email VARCHAR(50) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    PRIMARY KEY (ID),
    UNIQUE KEY `UK_email` (email)
);

CREATE TABLE IF NOT EXISTS roles (
    user_id bigint NOT NULL,
    role varchar(255) NOT NULL,
    CONSTRAINT FK97mxvrajhkq19dmvboprimeg1 FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS mentor_mentee_relationship (
    id bigint NOT NULL AUTO_INCREMENT,
    mentee_id bigint NOT NULL,
    mentor_id bigint NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT FK4679caixn7y5xjoj19uei1odq FOREIGN KEY (mentor_id) REFERENCES users (id),
    CONSTRAINT FKn36qkqh26rf48t8bwpnrb8wmj FOREIGN KEY (mentee_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS objectives (
    id bigint NOT NULL AUTO_INCREMENT,
    user_id bigint NOT NULL,
    objective_description varchar(500) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT FK7jhgmhub4b90lxgagq7jjrg0a FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS key_results (
    rating int NOT NULL,
    id bigint NOT NULL AUTO_INCREMENT,
    objectives_id bigint NOT NULL,
    comment tinytext,
    key_result_description tinytext NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT FKbfrc97538rv7cdjy1nf5mul8r FOREIGN KEY (objectives_id) REFERENCES objectives (id),
    CONSTRAINT key_results_chk_1 CHECK (((rating <= 5) and (rating >= 0)))
);

CREATE TABLE IF NOT EXISTS passwordresettoken (
    id int NOT NULL AUTO_INCREMENT,
    expiry_date_time datetime(6) DEFAULT NULL,
    user_id bigint DEFAULT NULL,
    token varchar(255) DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY UK_9dbcvg1tus9vsxrly63i6l16n (user_id),
    CONSTRAINT FKky4yy1iru766swgx0f2v0iv11 FOREIGN KEY (user_id) REFERENCES users (id)
);
