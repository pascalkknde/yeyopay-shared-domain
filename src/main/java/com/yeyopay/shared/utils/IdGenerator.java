package com.yeyopay.shared.utils;

import java.util.UUID;
import java.security.SecureRandom;

/**
 * Utilities for generating IDs and other common operations.
 */
public class IdGenerator {
    private static final SecureRandom RANDOM = new SecureRandom();

    public static UUID newId() {
        return UUID.randomUUID();
    }

    public static String newCorrelationId() {
        return UUID.randomUUID().toString();
    }

    public static String newTransactionReference() {
        return "TXN-" + System.currentTimeMillis() + "-" + RANDOM.nextInt(10000);
    }

    public static String newPaymentReference() {
        return "PAY-" + System.currentTimeMillis() + "-" + RANDOM.nextInt(10000);
    }
}
