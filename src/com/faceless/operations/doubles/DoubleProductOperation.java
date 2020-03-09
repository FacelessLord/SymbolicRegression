package com.faceless.operations.doubles;

import com.faceless.abstraction.BinaryOperation;

import java.util.Arrays;

public class DoubleProductOperation extends BinaryOperation<Double> {
    @Override
    public String getExpressionString(String... args) {
        return args[0] + " * " + args[1];
    }

    @Override
    public String getOperationSymbol() {
        return "*";
    }

    @Override
    public int getOperationPriority() {
        return 1;
    }

    @Override
    public Double apply(Double[] args) {
        return args[0] * args[1];
    }
}
