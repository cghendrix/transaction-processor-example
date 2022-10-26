CREATE TABLE reward_mapping
(
    id                          BIGSERIAL NOT NULL PRIMARY KEY,
    merchant_category_code      BIGINT NOT NULL UNIQUE,
    reward_points_multiplier    BIGINT NOT NULL,
    description                 TEXT NOT NULL
);

INSERT INTO reward_mapping
    (merchant_category_code, reward_points_multiplier, description)
VALUES
    (5812, 3, 'Dining');
