package com.faceless.operations.doubles;

import com.faceless.operations.Constant;

public class DoubleConstant extends Constant<Double> {
    public DoubleConstant(Double value) {
        super(value);
    }

    @Override
    public String getExpressionString(String... args) {
        return getCutDouble(4);
    }

    protected String getCutDouble(int precision) {
        var str = value + "";
        if (str.contains(".")) {
            int pointPos = str.indexOf(".");
            str += "00000000000000000000";
            str = str.substring(0, pointPos + Math.min(str.length() - pointPos, precision));
        }
        return str;
    }

}
