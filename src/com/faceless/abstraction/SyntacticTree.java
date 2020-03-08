package com.faceless.abstraction;

import java.util.Arrays;
import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SyntacticTree<T> {
    public List<SyntacticTree<T>> subTrees;
    public IOperation<T> operation;
    private IntFunction<T[]> arrayGenerator;

    @SafeVarargs
    public SyntacticTree(IntFunction<T[]> arrayGenerator, IOperation<T> operation, SyntacticTree<T>... subTrees) {
        assert operation.getArgumentCount() == subTrees.length;
        this.arrayGenerator = arrayGenerator;
        this.operation = operation;
        this.subTrees = Arrays.stream(subTrees).collect(Collectors.toList());
    }

    public SyntacticTree(IntFunction<T[]> arrayGenerator, IOperation<T> operation, Stream<SyntacticTree<T>> subtrees) {
        this.arrayGenerator = arrayGenerator;
        this.operation = operation;
        this.subTrees = subtrees.collect(Collectors.toList());
        assert operation.getArgumentCount() == this.subTrees.size();
    }

    public String toExpression() {
        String[] subfolded = subTrees.stream()
                .map(t -> addParenthesesIfNeeded(t.toExpression(),
                        t.operation.getOperationPriority(),
                        operation.getOperationPriority()))
                .toArray(String[]::new);

        return operation.getExpressionString(subfolded);

    }

    public String addParenthesesIfNeeded(String expr, int exprPriorty, int operationPriority) {
        if (exprPriorty < operationPriority && exprPriorty != -1)
            return "(" + expr + ")";
        return expr;
    }

    public T evaluate() {
        return operation.apply(subTrees
                .stream()
                .map(SyntacticTree::evaluate)
                .toArray(arrayGenerator));
    }
}
