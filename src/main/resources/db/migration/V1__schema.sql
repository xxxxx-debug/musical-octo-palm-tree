CREATE TABLE users (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(500) NOT NULL,
    date_of_birth   DATE NOT NULL,
    password        VARCHAR(500) NOT NULL
);

CREATE TABLE account (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL UNIQUE REFERENCES users (id),
    balance         DECIMAL(19, 2) NOT NULL,
    max_balance     DECIMAL(19, 2) NOT NULL,
    CONSTRAINT chk_account_balance_nonneg CHECK (balance >= 0),
    CONSTRAINT chk_account_max_balance CHECK (max_balance >= balance)
);

CREATE TABLE email_data (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users (id),
    email           VARCHAR(200) NOT NULL,
    CONSTRAINT uq_email_data_email UNIQUE (email)
);

CREATE TABLE phone_data (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users (id),
    phone           VARCHAR(13) NOT NULL,
    CONSTRAINT uq_phone_data_phone UNIQUE (phone)
);

CREATE INDEX idx_email_data_user_id ON email_data (user_id);
CREATE INDEX idx_phone_data_user_id ON phone_data (user_id);
CREATE INDEX idx_users_name ON users (name);
CREATE INDEX idx_users_date_of_birth ON users (date_of_birth);
