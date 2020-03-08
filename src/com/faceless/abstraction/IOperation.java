package com.faceless.abstraction;

public interface IOperation<T> {
    String getExpressionString(String... args);

    String getOperationSymbol();

    default boolean isLeftAssociative()
    {
        return false;
    }

    int getOperationPriority();

    int getArgumentCount();

    T apply(T... args);

}
