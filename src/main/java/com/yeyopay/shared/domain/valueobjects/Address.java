package com.yeyopay.shared.domain.valueobjects;

import com.yeyopay.shared.domain.base.ValueObject;

import java.util.Objects;

public class Address extends ValueObject {
    private final String streetLine1;
    private final String streetLine2;
    private final String city;
    private final String stateProvince;
    private final String postalCode;
    private final String countryCode;
    private final AddressType type;

    public Address(String streetLine1, String city, String countryCode, AddressType type) {
        this(streetLine1, null, city, null, null, countryCode, type);
    }

    public Address(String streetLine1, String streetLine2, String city, String stateProvince,
                   String postalCode, String countryCode, AddressType type) {
        if (streetLine1 == null || streetLine1.trim().isEmpty()) {
            throw new IllegalArgumentException("Street line 1 cannot be null or empty");
        }
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("City cannot be null or empty");
        }
        if (countryCode == null || countryCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Country code cannot be null or empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("Address type cannot be null");
        }

        this.streetLine1 = streetLine1.trim();
        this.streetLine2 = streetLine2 != null ? streetLine2.trim() : null;
        this.city = city.trim();
        this.stateProvince = stateProvince != null ? stateProvince.trim() : null;
        this.postalCode = postalCode != null ? postalCode.trim() : null;
        this.countryCode = countryCode.toUpperCase().trim();
        this.type = type;
    }

    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        sb.append(streetLine1);

        if (streetLine2 != null && !streetLine2.isEmpty()) {
            sb.append(", ").append(streetLine2);
        }

        sb.append(", ").append(city);

        if (stateProvince != null && !stateProvince.isEmpty()) {
            sb.append(", ").append(stateProvince);
        }

        if (postalCode != null && !postalCode.isEmpty()) {
            sb.append(" ").append(postalCode);
        }

        sb.append(", ").append(countryCode);

        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Address address = (Address) obj;
        return Objects.equals(streetLine1, address.streetLine1) &&
                Objects.equals(streetLine2, address.streetLine2) &&
                Objects.equals(city, address.city) &&
                Objects.equals(stateProvince, address.stateProvince) &&
                Objects.equals(postalCode, address.postalCode) &&
                Objects.equals(countryCode, address.countryCode) &&
                type == address.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(streetLine1, streetLine2, city, stateProvince, postalCode, countryCode, type);
    }

    @Override
    public String toString() {
        return getFullAddress();
    }

    public enum AddressType {
        PRIMARY, BILLING, SHIPPING, WORK, TEMPORARY
    }
}
