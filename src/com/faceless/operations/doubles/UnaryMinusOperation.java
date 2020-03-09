package com.faceless.operations.doubles;

import com.faceless.abstraction.UnaryOperation;

public class UnaryMinusOperation extends UnaryOperation<Double> {
    @Override
    public String getExpressionString(String... args) {
        return "~" + args[0];
    }

    @Override
    public String getOperationSymbol() {
        return "~";
    }

    @Override
    public int getOperationPriority() {
        return 4;
    }

    @Override
    public Double apply(Double[] args) {
        return -args[0];
    }
}
