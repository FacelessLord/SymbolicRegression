package com.faceless.abstraction;

public abstract class NullaryOperation<T> implements IOperation<T> {
    @Override
    public int getArgumentCount() {
        return 0;
    }

    @Override
    public abstract T apply(T[] args);
}
