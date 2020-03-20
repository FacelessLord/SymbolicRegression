package com.faceless.operations.doubles;

import com.faceless.operations.Constant;

public class DoubleConstant extends Constant<Double> {
    public DoubleConstant(Double value) {
        super(value);
    }

    @Override
    public final String getExpressionString(String... args) {
        return getCutDouble(4);
    }

    private String getCutDouble(int precision) {
        var str = value + "";
        if (str.contains(".")) {
            int pointPos = str.indexOf(".");
            str = str.substring(0, Math.min(str.length() - pointPos, pointPos + precision));
        }
        return str;
    }

}
