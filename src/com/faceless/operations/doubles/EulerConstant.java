package com.faceless.operations.doubles;

import com.faceless.operations.Constant;

public class EulerConstant extends DoubleConstant {
    public EulerConstant() {
        super(Math.E);
    }

    @Override
    public String getExpressionString(String... args) {
        return "e";
    }
}
