package com.faceless.regression;

import com.faceless.abstraction.NullaryOperation;
import com.faceless.abstraction.SyntacticTree;
import com.faceless.abstraction.UnaryOperation;
import com.faceless.operations.Constant;
import com.faceless.operations.doubles.DoubleSumOperation;
import com.sun.source.tree.Tree;

import java.util.Comparator;
import java.util.function.Function;

public abstract class TreeOptimizer<T> {

    protected Function<T, Constant<T>> constantGenerator;

    public TreeOptimizer(Function<T, Constant<T>> constantGenerator) {
        this.constantGenerator = constantGenerator;
    }

    public SyntacticTree<T> optimize(SyntacticTree<T> tree) {
        SyntacticTree<T> copy = tree.copy();
        for (int i = 0; i < maxDepth(copy); i++)
            copy = optimizeTree(copy);
        return copy;
    }

    private int maxDepth(SyntacticTree<T> tree) {
        return 1 + tree.subTrees.stream().map(this::maxDepth).max(Comparator.naturalOrder()).orElse(0);
    }

    protected abstract void optimize(SyntacticTree<T> tree, TreeHelper.SubTree<T> subTree);

    protected SyntacticTree<T> optimizeTree(SyntacticTree<T> tree) {
        for (TreeHelper.SubTree<T> t : TreeHelper.getSubTrees(tree))
            optimize(tree, t);
        return tree;
    }

    public static <T> boolean subtreesAreConstants(SyntacticTree<T> tree) {
        return tree.subTrees.stream().allMatch(t -> t.operation instanceof Constant);
    }

    public static <T> TreeHelper.SubTree<T> getParentSubTree(SyntacticTree<T> mainTree, TreeHelper.SubTree<T> tree) {
        int[] path = TreeHelper.removeValueFromArray(tree.path);
        return TreeHelper.extractTree(mainTree, path);
    }

    public static class DoubleTreeOptimizer extends TreeOptimizer<Double> {
        public DoubleTreeOptimizer(Function<Double, Constant<Double>> constantGenerator) {
            super(constantGenerator);
        }

        @Override
        protected void optimize(SyntacticTree<Double> tree, TreeHelper.SubTree<Double> subTree) {
            if (subtreesAreConstants(subTree.tree) && !(subTree.tree.operation instanceof NullaryOperation)) {
                double c = subTree.tree.operation.apply(subTree.tree.subTrees.stream()
                        .map(SyntacticTree::evaluate)
                        .toArray(Double[]::new));
                System.out.println("From: "+subTree.tree.toExpression());
                subTree.tree.operation = constantGenerator.apply(c);
                subTree.tree.subTrees.clear();
                System.out.println("To: "+subTree.tree.toExpression());
            } else
                super.optimizeTree(subTree.tree);
            // TODO: 21.03.2020 add multiple argument product and sum
        }
    }
}
