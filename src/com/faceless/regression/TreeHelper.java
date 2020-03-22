package com.faceless.regression;

import com.faceless.abstraction.BinaryOperation;
import com.faceless.abstraction.IOperation;
import com.faceless.abstraction.SyntacticTree;
import com.faceless.operations.Constant;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class TreeHelper {
    private static Random random = new Random();

    /**
     * Finds all tree nodes that satisfy given condition
     *
     * @param tree tree to search in
     * @return list of nodes that satisfy given condition
     */
    public static <T> List<TreeHelper.SubTree<T>> getNodes(SyntacticTree<T> tree, Predicate<IOperation<T>> condition) {
        List<TreeHelper.SubTree<T>> nodes = new ArrayList<>();
        TreeHelper.SubTree<T> subTree = new TreeHelper.SubTree<>(tree, tree, new int[0]);
        getNodes(subTree, condition, nodes);
        return nodes;
    }

    private static <T> void getNodes(TreeHelper.SubTree<T> tree, Predicate<IOperation<T>> condition, List<TreeHelper.SubTree<T>> nodes) {
        if (condition.test(tree.tree.operation))
            nodes.add(tree);

        for (TreeHelper.SubTree<T> t : TreeHelper.getSubTrees(tree.tree)) {
            getNodes(t, condition, nodes);
        }
    }

    public static <T> int[] findRandomNodePath(SyntacticTree<T> a) {
        final double percentage = 0.6d;
        var path = new ArrayList<Integer>();
        var currentTree = a;
        while (random.nextDouble() > percentage && currentTree.operation.getArgumentCount() > 0) {
            int step = random.nextInt(currentTree.operation.getArgumentCount());
            currentTree = currentTree.subTrees.get(step);
            path.add(step);
        }
        int[] arrPath = new int[path.size()];
        for (int i = 0; i < path.size(); i++) {
            arrPath[i] = path.get(i);
        }
        return arrPath;
    }

    public static <T> void replaceSubTreeAtPath(SyntacticTree<T> tree, SyntacticTree<T> replacement, int[] path) {
        extractTree(tree, removeValueFromArray(path)).tree.subTrees.set(path[path.length - 1], replacement);
    }

    public static <T> List<SubTree<T>> getSubTrees(SyntacticTree<T> tree) {
        List<SubTree<T>> subTrees = new ArrayList<>(tree.subTrees.size());
        for (int i = 0; i < tree.subTrees.size(); i++) {
            subTrees.add(new SubTree<>(tree, tree.subTrees.get(i), new int[]{i}));
        }
        return subTrees;
    }

    public static int[] addValueToArray(int[] arr, int i) {
        var newPath = new int[arr.length + 1];
        System.arraycopy(arr, 0, newPath, 0, arr.length);
        newPath[arr.length] = i;
        return newPath;
    }

    public static int[] removeValueFromArray(int[] arr) {
        var newPath = new int[arr.length - 1];
        System.arraycopy(arr, 0, newPath, 0, arr.length - 1);
        return newPath;
    }

    public static <T> SubTree<T> extractTree(SyntacticTree<T> tree, int[] path) {
        SyntacticTree<T> tempTree = tree;
        for (var step : path) {
            tempTree = tempTree.subTrees.get(step);
        }
        return new SubTree<>(tree, tempTree, path);
    }


    public static <T> boolean subtreesAreConstants(SyntacticTree<T> tree) {
        return tree.subTrees.stream().allMatch(t -> t.operation instanceof Constant);
    }

    public static <T> TreeHelper.SubTree<T> getParentSubTree(TreeHelper.SubTree<T> tree) {
        int[] path = removeValueFromArray(tree.path);
        return extractTree(tree.wholeTree, path);
    }

    public static <T> int maxDepth(SyntacticTree<T> tree) {
        return 1 + tree.subTrees.stream().map(TreeHelper::maxDepth).max(Comparator.naturalOrder()).orElse(0);
    }


    public static <T> void getMultiTreeLeaf(SyntacticTree<T> tree, List<SyntacticTree<T>> multTree, boolean dispose) {
        getMultiTreeLeaf(tree, tree.operation, multTree, dispose);
    }

    private static <T> void getMultiTreeLeaf(SyntacticTree<T> tree, IOperation<T> operation,
                                             List<SyntacticTree<T>> multTree, boolean dispose) {
        for (SyntacticTree<T> t : tree.subTrees) {
            if (t.operation != operation)
                multTree.add(t);
            else {
                getMultiTreeLeaf(t, operation, multTree, dispose);
                if (dispose)
                    t.dispose();
            }
        }
    }

    public static class SubTree<T> {
        public SyntacticTree<T> tree;
        public SyntacticTree<T> wholeTree;
        public int[] path;

        public SubTree(SyntacticTree<T> wholeTree, SyntacticTree<T> tree, int[] path) {
            this.wholeTree = wholeTree;
            this.tree = tree;
            this.path = path;
        }
    }
}
