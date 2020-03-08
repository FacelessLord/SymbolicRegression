package com.faceless;

import com.faceless.abstraction.IOperation;
import com.faceless.operations.*;
import com.faceless.operations.integers.FOperation;
import com.faceless.operations.integers.FactorialOperation;
import com.faceless.operations.integers.IntProductOperation;
import com.faceless.operations.integers.IntSumOperation;

import java.util.*;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {
        IOperation<Integer> sum = new IntSumOperation();
        IOperation<Integer> prod = new IntProductOperation();
        IOperation<Integer> fact = new FactorialOperation();
        IOperation<Integer> f = new FOperation();
//
//        Constant<Integer> one = new Constant<>(1);
//        Constant<Integer> two = new Constant<>(2);
//        Constant<Integer> four = new Constant<>(4);
//        Constant<Integer> seven = new Constant<>(7);
//
//        IntegerTree left = new IntegerTree(sum,
//                new IntegerTree(one),
//                new IntegerTree(two));
//
//        IntegerTree right = new IntegerTree(prod,
//                new IntegerTree(four),
//                new IntegerTree(seven));
//
//        IntegerTree tree = new IntegerTree(prod, left, right);
//        System.out.println(tree.toExpression());

        var expr = "$(1 + 2 * 4) * x";
        var vars = new HashMap<String, Integer>();
        vars.put("x", 7);

        var operations = new ArrayList<IOperation<Integer>>();
        operations.add(f);
        operations.add(fact);
        operations.add(prod);
        operations.add(sum);
        var parser = new ExpressionParser<Integer>(operations.stream(),
                Integer[]::new,
                Pattern.compile("[0-9]+"),
                v -> new Constant<>(Integer.parseInt(v)),
                Pattern.compile("[a-zA-Z]"),
                v -> new Variable<Integer>(v, vars));
        var result = parser.parse(expr);

        System.out.println(result.toExpression());
        System.out.println(result.evaluate());
        vars.replace("x", 8);
        System.out.println(result.evaluate());
        vars.replace("x", 9);
        System.out.println(result.evaluate());
        vars.replace("x", 1);
        System.out.println(result.evaluate());
    }
}
