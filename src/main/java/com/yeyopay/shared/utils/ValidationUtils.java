package com.yeyopay.shared.utils;

/**
 * Validation utilities.
 */
public class ValidationUtils {
    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    public static boolean isValidPhoneNumber(String phone) {
        return phone != null && phone.matches("^\\+?[1-9]\\d{1,14}$");
    }

    public static boolean isValidCountryCode(String countryCode) {
        return countryCode != null && countryCode.matches("^[A-Z]{2,3}$");
    }

    public static boolean isValidCurrencyCode(String currencyCode) {
        return currencyCode != null && currencyCode.matches("^[A-Z]{3}$");
    }
}
