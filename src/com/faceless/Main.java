package com.faceless;

import com.faceless.abstraction.IOperation;
import com.faceless.operations.*;
import com.faceless.operations.doubles.*;

import java.util.*;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {
        IOperation<Double> umin = new UnaryMinusOperation();
        IOperation<Double> sum = new DoubleSumOperation();
        IOperation<Double> sub = new DoubleSubOperation();
        IOperation<Double> prod = new DoubleProductOperation();
        IOperation<Double> cos = new DoubleCosOperation();
        IOperation<Double> pow = new DoublePowerOperation();

        var expr = "(x^e)";
        // STOPSHIP: 09.03.2020 Try with floats and strings, start developing genetics
        var vars = new HashMap<String, Double>();
        vars.put("e",Math.E);
        vars.put("x", 7d);

        var operations = new ArrayList<IOperation<Double>>();
        operations.add(pow);
        operations.add(cos);
        operations.add(prod);
        operations.add(sum);
        operations.add(sub);
        operations.add(umin);
        var parser = new ExpressionParser<>(operations.stream(),
                Double[]::new,
                Pattern.compile("[0-9]+(\\.[0-9]+)?"),
                v -> new Constant<>(Double.parseDouble(v)),
                Pattern.compile("[a-zA-Z]"),
                v -> new Variable<>(v, vars));
        var result = parser.parse(expr);

        System.out.println(result.toExpression());
        System.out.println(result.evaluate());
    }
}
