package com.faceless.operations.doubles;

import com.faceless.abstraction.BinaryOperation;
import com.faceless.abstraction.UnaryOperation;

public class DoublePowerOperation extends BinaryOperation<Double> {
    @Override
    public String getExpressionString(String... args) {
        return args[0] + " ^ " + args[1];
    }

    @Override
    public String getOperationSymbol() {
        return "^";
    }

    @Override
    public int getOperationPriority() {
        return 2;
    }

    @Override
    public Double apply(Double[] args) {
        return Math.pow(args[0], args[1]);
    }

    @Override
    public boolean isAssociative() {
        return false;
    }
}
