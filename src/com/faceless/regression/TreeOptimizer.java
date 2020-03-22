package com.faceless.regression;

import com.faceless.abstraction.BinaryOperation;
import com.faceless.abstraction.IOperation;
import com.faceless.abstraction.NullaryOperation;
import com.faceless.abstraction.SyntacticTree;
import com.faceless.operations.Constant;
import com.faceless.operations.doubles.DoubleConstant;
import com.faceless.operations.doubles.DoubleOperations;
import com.faceless.operations.doubles.DoubleProductOperation;
import com.faceless.operations.doubles.DoubleSumOperation;

import java.util.*;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class TreeOptimizer<T> {

    protected Function<T, Constant<T>> constantGenerator;
    protected IntFunction<T[]> arrayGenerator;

    public TreeOptimizer(IntFunction<T[]> arrayGenerator, Function<T, Constant<T>> constantGenerator) {
        this.arrayGenerator = arrayGenerator;
        this.constantGenerator = constantGenerator;
    }

    public SyntacticTree<T> optimize(SyntacticTree<T> tree) {
        SyntacticTree<T> copy = tree.copy();
        int depth = TreeHelper.maxDepth(copy);
        for (int i = 0; i < depth * 2; i++)
            copy = optimizeTree(copy);
        return copy;
    }


    protected abstract void optimize(SyntacticTree<T> tree, TreeHelper.SubTree<T> subTree);

    protected SyntacticTree<T> optimizeTree(SyntacticTree<T> tree) {
        for (TreeHelper.SubTree<T> t : TreeHelper.getSubTrees(tree))
            optimize(tree, t);
        return tree;
    }

    protected abstract T evaluateBinaryMultiTree(Stream<SyntacticTree<T>> multTree, BinaryOperation<T> operation);

    protected SyntacticTree<T> unfoldBinaryMultiTree(List<SyntacticTree<T>> multTree, BinaryOperation<T> operation) {
        if (multTree.size() > 2)
            return new SyntacticTree<>(arrayGenerator, operation, multTree.remove(0), unfoldBinaryMultiTree(multTree, operation));
        return new SyntacticTree<>(arrayGenerator, operation, multTree.stream());
    }

    public static class DoubleTreeOptimizer extends TreeOptimizer<Double> {
        public DoubleTreeOptimizer() {
            super(Double[]::new, DoubleConstant::new);
//            distributivity.put(DoubleOperations.SUM, DoubleOperations.PRODUCT);
//            distributivity.put(DoubleOperations.PRODUCT, DoubleOperations.SUM);
        }

        private Map<IOperation<Double>, IOperation<Double>> distributivity = new HashMap<>();

        @Override
        protected void optimize(SyntacticTree<Double> tree, TreeHelper.SubTree<Double> subTree) {
            if (TreeHelper.subtreesAreConstants(subTree.tree) && !(subTree.tree.operation instanceof NullaryOperation)) {
                optimizeConstantExpression(subTree);
                return;
            }
            if (subTree.tree.operation instanceof BinaryOperation) {
                rebalanceOptimizeTree(subTree);
                optimizeByDistributivity(subTree);
            }

            super.optimizeTree(subTree.tree);
        }

        private void optimizeByDistributivity(TreeHelper.SubTree<Double> subTree) {
            if (distributivity.containsKey(subTree.tree.operation)) {
                var crossOperation = distributivity.get(subTree.tree.operation);
                if (subTree.tree.subTrees.size() == 2 && subTree.tree.subTrees.stream().allMatch(t -> t.operation == crossOperation)) {
                    var firstArg = new ArrayList<>(subTree.tree.subTrees.get(0).subTrees);
                    var secondArg = new ArrayList<>(subTree.tree.subTrees.get(1).subTrees);
                    secondArg.retainAll(firstArg);
                    redistributeOperations(subTree, crossOperation, secondArg);
                }
            }
        }

        private void redistributeOperations(TreeHelper.SubTree<Double> subTree, IOperation<Double> crossOperation, ArrayList<SyntacticTree<Double>> secondArg) {
            if (secondArg.size() == 1) {
                var multiplier = secondArg.get(0);
                var leftMultIndex = subTree.tree.subTrees.get(0).subTrees.indexOf(multiplier);
                var leftMult = subTree.tree.subTrees.get(0).subTrees.get(1 - leftMultIndex);

                var rightMultIndex = subTree.tree.subTrees.get(1).subTrees.indexOf(multiplier);
                var rightMult = subTree.tree.subTrees.get(1).subTrees.get(1 - rightMultIndex);

                var argSum = new SyntacticTree<>(arrayGenerator, subTree.tree.operation,
                        leftMult, rightMult);

                subTree.tree.subTrees.get(0).dispose();
                subTree.tree.subTrees.get(1).dispose();

                updateTree(subTree.tree, crossOperation, multiplier, argSum);
            }
        }

        private void updateTree(SyntacticTree<Double> tree, IOperation<Double> operation,
                                SyntacticTree<Double> arg1, SyntacticTree<Double> arg2) {
            tree.operation = operation;
            tree.subTrees.set(0, arg1);
            tree.subTrees.set(1, arg2);
        }

        @Override
        protected Double evaluateBinaryMultiTree(Stream<SyntacticTree<Double>> multTree, BinaryOperation<Double> operation) {
            return multTree.map(SyntacticTree::evaluate).reduce((a, b) -> operation.apply(a, b)).orElse(0d);
        }

        private void rebalanceOptimizeTree(TreeHelper.SubTree<Double> subTree) {
            BinaryOperation<Double> op = (BinaryOperation<Double>) subTree.tree.operation;
            List<SyntacticTree<Double>> multiTree = new ArrayList<>();
            TreeHelper.getMultiTreeLeaf(subTree.tree, multiTree, true);
            var consts = multiTree.stream().filter(t -> t.operation instanceof Constant);
            var constArg = evaluateBinaryMultiTree(consts, op);

            var nonConsts = multiTree.stream().filter(t -> !(t.operation instanceof Constant)).collect(Collectors.toList());
            if (constArg != 0) {
                var constTree = new SyntacticTree<>(arrayGenerator, constantGenerator.apply(constArg));
                nonConsts.add(constTree);
            }
            subTree.tree.subTrees = unfoldBinaryMultiTree(nonConsts, op).subTrees;
        }

        private void optimizeConstantExpression(TreeHelper.SubTree<Double> subTree) {
            var parent = TreeHelper.getParentSubTree(subTree);
            System.out.println(parent.tree.toExpression());
            double c = subTree.tree.operation.apply(subTree.tree.subTrees.stream()
                    .map(SyntacticTree::evaluateAndDispose)
                    .toArray(Double[]::new));
            subTree.tree.operation = constantGenerator.apply(c);
            subTree.tree.subTrees.clear();
        }
    }
}
