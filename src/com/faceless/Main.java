package com.faceless;

import com.faceless.abstraction.IOperation;
import com.faceless.operations.*;
import com.faceless.operations.doubles.*;
import com.faceless.regression.*;

import java.util.*;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {

    public static final Pattern doublePattern = Pattern.compile("[0-9]+(\\.[0-9]+)?");
    public static final Pattern variablePattern = Pattern.compile("[a-zA-Z]");
    public static final Function<String, Constant<Double>> doubleParser = v -> new Constant<>(Double.parseDouble(v));

    public static Function<String, Variable<Double>> variableGenerator(Map<String, Double> variableDict) {
        return v -> new Variable<>(v, variableDict);
    }

    public static void main(String[] args) {
        IOperation<Double> umin = new UnaryMinusOperation();
        IOperation<Double> sum = new DoubleSumOperation();
        IOperation<Double> sub = new DoubleSubOperation();
        IOperation<Double> prod = new DoubleProductOperation();
        IOperation<Double> cos = new DoubleCosOperation();
        IOperation<Double> pow = new DoublePowerOperation();

        var expr = "(0.453 + 0.873)";
        // STOPSHIP: 09.03.2020 Try with floats and strings, start developing genetics
        var vars = new HashMap<String, Double>();
        vars.put("e", Math.E);
        vars.put("x", 0.001d);

        var operations = new ArrayList<IOperation<Double>>();
//        operations.add(pow);
//        operations.add(cos);
        operations.add(prod);
        operations.add(sum);
//        operations.add(sub);
//        operations.add(umin);
        var parser = new ExpressionParser<>(operations.stream(),
                Double[]::new,
                doublePattern,
                doubleParser,
                variablePattern,
                variableGenerator(vars));
        var result = parser.parse(expr);
        System.out.println(TreeOptimizer.subtreesAreConstants(result));
        IntFunction<Double[]> arrayGenerator = Double[]::new;
        Function<Random, Constant<Double>> constantGenerator = r -> new DoubleConstant(r.nextDouble());
        List<String> variables = new ArrayList<>(vars.keySet());
        var generator = new Generator<>(arrayGenerator, operations,
                constantGenerator, variables, vars);
        var mutator = new Mutator<>(arrayGenerator,
                operations, constantGenerator, variables, vars);
        var combinator = new Combinator<Double>();
        var populationSize = 20;
        var regressor = new Regressor<>(populationSize, vars, generator,
                4, combinator, mutator, System.out::println);
        Function<Double, Double> f = d -> d * d;
        int setSize = 20;
        Double[][] trainingSet = new Double[setSize][variables.size()];
        Double[] answers = new Double[setSize];
        for (int i = 0; i < setSize; i++) {
            for (int j = 0; j < variables.size(); j++) {
                trainingSet[i][j] = (double) i;
            }
            answers[i] = f.apply((double) i);
        }

        regressor.setTrainingSet(variables.toArray(new String[0]), trainingSet, answers);
        var tree = regressor.simulate(0.1d, 40);

        TreeOptimizer<Double> optimizer = new TreeOptimizer.DoubleTreeOptimizer(DoubleConstant::new);
        System.out.println(tree.toExpression());
        tree = optimizer.optimize(tree);
        System.out.println(tree.toExpression());
    }
}
