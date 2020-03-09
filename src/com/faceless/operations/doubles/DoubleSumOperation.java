package com.faceless.operations.doubles;

import com.faceless.abstraction.BinaryOperation;

public class DoubleSumOperation extends BinaryOperation<Double> {
    @Override
    public String getExpressionString(String... args) {
        return args[0] + " + " + args[1];
    }

    @Override
    public String getOperationSymbol() {
        return "+";
    }

    @Override
    public int getOperationPriority() {
        return 0;
    }

    @Override
    public Double apply(Double[] args) {
        return args[0] + args[1];
    }
}
