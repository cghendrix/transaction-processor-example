package com.transactionprocessorexample.model;

public class BonusRequest {
    // Identifier for the merchant for which the bonus was issued
    public String merchantId;
    // Number of bonus points issued
    public Long points;
    // Idempotency key
    public String requestId;
}
