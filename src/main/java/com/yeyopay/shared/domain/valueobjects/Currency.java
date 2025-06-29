package com.yeyopay.shared.domain.valueobjects;

import com.yeyopay.shared.domain.base.ValueObject;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
public class Currency extends ValueObject {
    private final String code;
    private final String name;
    private final String symbol;
    private final int decimalPlaces;
    private final boolean active;

    // Common currencies
    public static final Currency USD = new Currency("USD", "US Dollar", "$", 2, true);
    public static final Currency EUR = new Currency("EUR", "Euro", "€", 2, true);
    public static final Currency GBP = new Currency("GBP", "British Pound", "£", 2, true);
    public static final Currency JPY = new Currency("JPY", "Japanese Yen", "¥", 0, true);
    public static final Currency CAD = new Currency("CAD", "Canadian Dollar", "C$", 2, true);
    public static final Currency AUD = new Currency("AUD", "Australian Dollar", "A$", 2, true);
    public static final Currency CHF = new Currency("CHF", "Swiss Franc", "Fr", 2, true);
    public static final Currency CNY = new Currency("CNY", "Chinese Yuan", "¥", 2, true);
    public static final Currency BTC = new Currency("BTC", "Bitcoin", "₿", 8, true);
    public static final Currency ETH = new Currency("ETH", "Ethereum", "Ξ", 18, true);

    public Currency(String code, String name, String symbol, int decimalPlaces, boolean active) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency code cannot be null or empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency name cannot be null or empty");
        }
        if (symbol == null || symbol.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency symbol cannot be null or empty");
        }
        if (decimalPlaces < 0) {
            throw new IllegalArgumentException("Decimal places cannot be negative");
        }

        this.code = code.toUpperCase().trim();
        this.name = name.trim();
        this.symbol = symbol.trim();
        this.decimalPlaces = decimalPlaces;
        this.active = active;
    }

    public static Currency of(String code) {
        return switch (code.toUpperCase()) {
            case "USD" -> USD;
            case "EUR" -> EUR;
            case "GBP" -> GBP;
            case "JPY" -> JPY;
            case "CAD" -> CAD;
            case "AUD" -> AUD;
            case "CHF" -> CHF;
            case "CNY" -> CNY;
            case "BTC" -> BTC;
            case "ETH" -> ETH;
            default -> throw new IllegalArgumentException("Unsupported currency code: " + code);
        };
    }

    public String format(BigDecimal amount) {
        return String.format("%s %." + decimalPlaces + "f", symbol, amount);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Currency currency = (Currency) obj;
        return Objects.equals(code, currency.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return code;
    }
}
