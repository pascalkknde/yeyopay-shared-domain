package com.yeyopay.shared.domain.base;

public abstract class ValueObject {
    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract int hashCode();

    @Override
    public abstract String toString();

    /**
     * Protected constructor to prevent instantiation outside of subclasses.
     */
    protected ValueObject() {}
}
