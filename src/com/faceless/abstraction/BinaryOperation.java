package com.faceless.abstraction;

public abstract class BinaryOperation<T> implements IOperation<T> {
    @Override
    public int getArgumentCount() {
        return 2;
    }

    @Override
    public abstract T apply(T[] args);
}
