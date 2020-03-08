package com.faceless.operations.integers;

import com.faceless.abstraction.UnaryOperation;

public class FactorialOperation extends UnaryOperation<Integer> {
    @Override
    public String getExpressionString(String... args) {
        return args[0] + getOperationSymbol();
    }

    @Override
    public String getOperationSymbol() {
        return "!";
    }

    @Override
    public int getOperationPriority() {
        return 2;
    }

    @Override
    public Integer apply(Integer[] args) {
        return factorial(args[0]);
    }

    @Override
    public boolean isLeftAssociative() {
        return true;
    }

    public Integer factorial(Integer n) {
        var fact = n;
        for (int i = 2; i < n; i++) {
            fact *= i;
        }
        return fact;
    }
}
