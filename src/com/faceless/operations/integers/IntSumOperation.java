package com.faceless.operations.integers;

import com.faceless.abstraction.BinaryOperation;

public class IntSumOperation extends BinaryOperation<Integer> {
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
    public Integer apply(Integer[] args) {
        return args[0] + args[1];
    }
}
