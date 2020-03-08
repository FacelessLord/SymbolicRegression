package com.faceless.operations;

import com.faceless.abstraction.NullaryOperation;

public class Constant<T> extends NullaryOperation<T> {
    private T value;

    public Constant(T value) {
        this.value = value;
    }

    @Override
    public final String getExpressionString(String... args) {
        return value + "";
    }

    @Override
    public String getOperationSymbol() {
        return getExpressionString();
    }

    @Override
    public int getOperationPriority() {
        return -1;
    }

    @Override
    public T apply(Object[] args) {
        return value;
    }
}
