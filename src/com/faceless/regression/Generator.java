package com.faceless.regression;

import com.faceless.abstraction.IOperation;
import com.faceless.abstraction.SyntacticTree;
import com.faceless.operations.Constant;
import com.faceless.operations.Variable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Generator<T> {
    private IntFunction<T[]> arrayGenerator;
    private List<IOperation<T>> operationList;
    private Function<Random, Constant<T>> constantGenerator;
    private List<String> variables;
    private Map<String, T> variableDict;

    public Generator(IntFunction<T[]> arrayGenerator, List<IOperation<T>> operationList, Function<Random, Constant<T>> constantGenerator, List<String> variables, Map<String, T> variableDict) {
        this.arrayGenerator = arrayGenerator;
        this.operationList = operationList;
        this.constantGenerator = constantGenerator;
        this.variables = variables;
        this.variableDict = variableDict;
    }

    private static Random random = new Random();

    public SyntacticTree<T> generateTree(int maxDepth) {
        if (maxDepth == 0) {
            if (random.nextBoolean()) {//Generate var
                var variable = new Variable<>(variables.get(random.nextInt(variables.size())), variableDict);
                return new SyntacticTree<>(arrayGenerator, variable);
            } else {//Generate constant
                var constant = constantGenerator.apply(random);
                return new SyntacticTree<>(arrayGenerator, constant);
            }
        } else {
            IOperation<T> operation = operationList.get(random.nextInt(operationList.size()));
            List<SyntacticTree<T>> subtrees = new ArrayList<>(operation.getArgumentCount());
            for (int i = 0; i < operation.getArgumentCount(); i++) {
                subtrees.add(generateTree(maxDepth - 1));
            }

            return new SyntacticTree<>(arrayGenerator, operation, subtrees.stream());
        }
    }
}
