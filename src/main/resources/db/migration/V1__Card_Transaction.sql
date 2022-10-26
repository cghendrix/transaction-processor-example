CREATE TABLE card_transaction
(
    id              bigserial not null primary key,
    user_id         bigint not null,
    transaction_id  text not null unique,
    amount          numeric(8,2) not null,
    mcc             int not null,
    points          bigint not null
);
