package com.faceless.operations.doubles;

import com.faceless.ExpressionParser;
import com.faceless.abstraction.IOperation;
import com.faceless.operations.Constant;
import com.faceless.operations.Variable;

import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class DoubleExpressionParser extends ExpressionParser<Double> {
    public static final Pattern doublePattern = Pattern.compile("[0-9]+(\\.[0-9]+)?");
    public static final Pattern variablePattern = Pattern.compile("[a-zA-Z]");
    public static final Function<String, Constant<Double>> doubleParser = v -> new Constant<>(Double.parseDouble(v));

    public DoubleExpressionParser(Stream<IOperation<Double>> operations, Function<String, Variable<Double>> varParser) {
        super(operations, Double[]::new, doublePattern, doubleParser, variablePattern, varParser);
    }
}
