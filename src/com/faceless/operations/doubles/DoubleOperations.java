package com.faceless.operations.doubles;

import com.faceless.abstraction.IOperation;

public class DoubleOperations {
    public static final IOperation<Double> UNARY_MINUS = new UnaryMinusOperation();
    public static final IOperation<Double> SUM = new DoubleSumOperation();
    public static final IOperation<Double> SUBTRACT = new DoubleSubtractOperation();
    public static final IOperation<Double> PRODUCT = new DoubleProductOperation();
    public static final IOperation<Double> COS = new DoubleCosOperation();
    public static final IOperation<Double> POWER = new DoublePowerOperation();
    public static final EulerConstant EULER_CONSTANT = new EulerConstant();
}
