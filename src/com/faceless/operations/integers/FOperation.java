package com.faceless.operations.integers;

import com.faceless.abstraction.UnaryOperation;

public class FOperation extends UnaryOperation<Integer> {
    @Override
    public String getExpressionString(String... args) {
        return getOperationSymbol() + args[0];
    }

    @Override
    public String getOperationSymbol() {
        return "$";
    }

    @Override
    public int getOperationPriority() {
        return 2;
    }

    @Override
    public Integer apply(Integer[] args) {
        return f(args[0]);
    }

    @Override
    public boolean isLeftAssociative() {
        return false;
    }

    public Integer f(Integer n) {
        return n*n;
    }
}
