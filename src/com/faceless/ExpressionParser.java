package com.faceless;

import com.faceless.abstraction.IOperation;
import com.faceless.abstraction.SyntacticTree;
import com.faceless.operations.Constant;
import com.faceless.operations.Variable;

import java.util.*;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExpressionParser<T> {
    public ExpressionParser(Stream<IOperation<T>> operations,
                            IntFunction<T[]> arrayGenerator,
                            Pattern constPattern,
                            Function<String, Constant<T>> parser,
                            Pattern variablePattern,
                            Function<String, Variable<T>> varParser) {
        this.operations = operations.collect(Collectors.groupingBy(IOperation::getOperationPriority));
        this.arrayGenerator = arrayGenerator;
        this.constPattern = constPattern;
        this.constParser = parser;
        this.variablePattern = variablePattern;
        this.varParser = varParser;
        this.priorities = this.operations.keySet().stream().sorted((i, j) -> j - i).collect(Collectors.toList());
    }

    public Map<Integer, List<IOperation<T>>> operations;
    private IntFunction<T[]> arrayGenerator;
    private final Pattern constPattern;
    private final Function<String, Constant<T>> constParser;
    private final Pattern variablePattern;
    private final Function<String, Variable<T>> varParser;
    public List<Integer> priorities;

    public SyntacticTree<T> parse(String expression) {
        if (constPattern.matcher(expression).matches()) {
            return new SyntacticTree<T>(arrayGenerator, constParser.apply(expression));
        }
        if (variablePattern.matcher(expression).matches()) {
            return new SyntacticTree<T>(arrayGenerator, varParser.apply(expression));
        }
        if (expression.length() == 0)
            throw new NullPointerException("Zero expr");
        expression = tryRemoveParentheses(expression);

        List<StringBlock> blocks = new ArrayList<>();
        findFreeBlocks(expression, blocks);

        var fullBlock = blocks.stream().map(b -> b.value).reduce((a, b) -> a + b).orElse("");

        IOperation<T> operation = findMostNonPriorFreeOperation(fullBlock);
        int firstOperationIndex = getFirstOperationOccurrence(blocks, operation);

        String symbol = operation.getOperationSymbol();
        if (operation.getArgumentCount() == 2) {//Infix notation
            return getBinarySyntacticTree(expression, operation, firstOperationIndex, symbol);
        } else {
            return getNonBinarySyntacticTree(expression, operation, firstOperationIndex, symbol);
        }
    }

    private SyntacticTree<T> getNonBinarySyntacticTree(String expression,
                                                       IOperation<T> operation,
                                                       int firstOperationIndex,
                                                       String symbol) {
        var args = getArguments(expression, operation, firstOperationIndex, symbol);
        var expr = tryRemoveParentheses(args);

        if (operation.getArgumentCount() > 2) {
            var arg = Arrays.stream(expr.split(",")).map(this::parse);
            return new SyntacticTree<>(arrayGenerator, operation, arg);
        } else {
            var arg = this.parse(expr);
            return new SyntacticTree<>(arrayGenerator, operation, arg);
        }
    }

    private String getArguments(String expression, IOperation<T> operation, int firstOperationIndex, String symbol) {
        if (operation.isLeftAssociative()) {
            return expression.substring(0, firstOperationIndex).trim();
        }
        return expression.substring(firstOperationIndex + symbol.length()).trim();

    }

    private SyntacticTree<T> getBinarySyntacticTree(String expression, IOperation<T> operation, int firstOperationIndex, String symbol) {
        int right = firstOperationIndex + symbol.length();
        var leftExpr = expression.substring(0, firstOperationIndex).trim();
        var rightExpr = expression.substring(right).trim();
        var leftArg = parse(leftExpr);
        var rightArg = parse(rightExpr);
        return new SyntacticTree<>(arrayGenerator,
                operation, leftArg,
                rightArg);
    }

    private int getFirstOperationOccurrence(List<StringBlock> blocks, IOperation<T> operation) {
        var firstOperationBlockOpt = blocks.stream()
                .filter(b -> b.value.contains(operation.getOperationSymbol()))
                .findFirst();
        if (firstOperationBlockOpt.isEmpty())
            throw new NullPointerException("Magical Expression, please fix");

        var firstOperationBlock = firstOperationBlockOpt.get();
        var firstOperationBlockIndex = firstOperationBlock.position;

        return firstOperationBlockIndex + firstOperationBlock.value.indexOf(operation.getOperationSymbol());
    }

    private IOperation<T> findMostNonPriorFreeOperation(String fullBlock) {
        Optional<IOperation<T>> operationOpt = operations.keySet()
                .stream()
                .flatMap(key -> operations.get(key).stream())
                .filter(a -> fullBlock.contains(a.getOperationSymbol()))
                .min(Comparator.comparingInt(IOperation::getOperationPriority));

        if (operationOpt.isEmpty())
            throw new NullPointerException("Wrong Expression");
        return operationOpt.get();
    }

    private void findFreeBlocks(String expression, List<StringBlock> blocks) {
        StringBuilder collector = new StringBuilder();
        int nestedness = 0;
        int blockStart = 0;
        int i = 0;
        while (i < expression.length()) {
            while (nestedness > 0) {
                if (expression.charAt(i) == '(')
                    nestedness++;
                if (expression.charAt(i) == ')')
                    nestedness--;
                i++;
            }
            blockStart = i;
            while (nestedness == 0 && i < expression.length()) {
                if (expression.charAt(i) == '(') {
                    nestedness++;
                    i++;
                    break;
                }
                collector.append(expression.charAt(i));
                i++;
            }
            blocks.add(new StringBlock(collector.toString(), blockStart));
            collector = new StringBuilder();
        }
    }

    private String tryRemoveParentheses(String expression) {
        if (expression.startsWith("(") && expression.endsWith(")")) {
            var cutExpr = expression.substring(1, expression.length() - 1);
            if (cutExpr.indexOf("(") < cutExpr.indexOf(")") && cutExpr.contains("(") || !cutExpr.contains(")"))
                return cutExpr;
        }
        return expression;
    }

    private static class StringBlock {
        private final String value;
        private final int position;

        public StringBlock(String value, int position) {

            this.value = value;
            this.position = position;
        }
    }
}
