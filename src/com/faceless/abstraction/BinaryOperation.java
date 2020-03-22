package com.faceless.abstraction;

public abstract class BinaryOperation<T> implements IOperation<T> {
    @Override
    public String getExpressionString(String... args) {
        return null;
    }

    @Override
    public String getOperationSymbol() {
        return null;
    }

    @Override
    public int getOperationPriority() {
        return 0;
    }

    @Override
    public int getArgumentCount() {
        return 2;
    }

    @Override
    public abstract T apply(T... args);
}
