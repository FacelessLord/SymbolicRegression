package com.faceless.operations.doubles;

import com.faceless.abstraction.BinaryOperation;
import com.faceless.abstraction.UnaryOperation;

public class DoubleCosOperation extends UnaryOperation<Double> {
    @Override
    public String getExpressionString(String... args) {
        return "cos " + args[0] + "";
    }

    @Override
    public String getOperationSymbol() {
        return "cos";
    }

    @Override
    public int getOperationPriority() {
        return 4;
    }

    @Override
    public Double apply(Double[] args) {
        return Math.cos(args[0]);
    }
}
