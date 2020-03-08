package com.faceless.abstraction;

public abstract class UnaryOperation<T> implements IOperation<T> {
    @Override
    public int getArgumentCount() {
        return 1;
    }

    @Override
    public abstract T apply(T[] args);
}
