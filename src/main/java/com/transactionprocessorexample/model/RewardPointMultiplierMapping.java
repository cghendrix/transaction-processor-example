package com.transactionprocessorexample.model;

import java.util.HashMap;
import java.util.Map;

public class RewardPointMultiplierMapping {
    private final int DEFAULT_POINTS = 1;
    private final Map<Integer, Integer> mapping = new HashMap<>();

    public void addMapping(int mcc, int multiplier) {
        this.mapping.put(mcc, multiplier);
    }

    public int getMultiplierForMCC(int mcc) {
        return this.mapping.getOrDefault(mcc, DEFAULT_POINTS);
    }
}
