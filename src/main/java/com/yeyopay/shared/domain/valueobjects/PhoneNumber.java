package com.yeyopay.shared.domain.valueobjects;

import com.yeyopay.shared.domain.base.ValueObject;
import lombok.Getter;

import java.util.Objects;

@Getter
public class PhoneNumber extends ValueObject {
    private final String countryCode;
    private final String number;
    private final String extension;
    private final PhoneType type;

    public PhoneNumber(String countryCode, String number, PhoneType type) {
        this(countryCode, number, null, type);
    }

    public PhoneNumber(String countryCode, String number, String extension, PhoneType type) {
        if (countryCode == null || countryCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Country code cannot be null or empty");
        }
        if (number == null || number.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be null or empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("Phone type cannot be null");
        }

        this.countryCode = countryCode.trim();
        this.number = number.replaceAll("[^0-9]", ""); // Remove non-numeric characters
        this.extension = extension != null ? extension.trim() : null;
        this.type = type;

        if (this.number.isEmpty()) {
            throw new IllegalArgumentException("Phone number must contain at least one digit");
        }
    }

    public String getInternationalFormat() {
        StringBuilder sb = new StringBuilder();
        sb.append("+").append(countryCode).append(" ").append(number);

        if (extension != null && !extension.isEmpty()) {
            sb.append(" ext. ").append(extension);
        }

        return sb.toString();
    }

    public String getE164Format() {
        return "+" + countryCode + number;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PhoneNumber that = (PhoneNumber) obj;
        return Objects.equals(countryCode, that.countryCode) &&
                Objects.equals(number, that.number) &&
                Objects.equals(extension, that.extension) &&
                type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(countryCode, number, extension, type);
    }

    @Override
    public String toString() {
        return getInternationalFormat();
    }

    public enum PhoneType {
        MOBILE, LANDLINE, VOIP, TOLL_FREE
    }
}
