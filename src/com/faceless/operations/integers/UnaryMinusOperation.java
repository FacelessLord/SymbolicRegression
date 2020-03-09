package com.faceless.operations.integers;

import com.faceless.abstraction.UnaryOperation;

public class UnaryMinusOperation extends UnaryOperation<Integer> {
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
    public Integer apply(Integer[] args) {
        return -args[0];
    }
}
