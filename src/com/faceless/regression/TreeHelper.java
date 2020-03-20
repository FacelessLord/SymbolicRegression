package com.faceless.regression;

import com.faceless.abstraction.SyntacticTree;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TreeHelper {
    private static Random random = new Random();

    public static <T> int[] findRandomNodePath(SyntacticTree<T> a) {
        final double percentage = 0.3d;
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
        if (path.length - 1 > 0) {
            for (int i = 0; i < path.length - 1; i++) {
                tree = tree.subTrees.get(path[i]);
            }
            tree.subTrees.set(path[path.length - 1], replacement);
        }
    }

    public static <T> List<SubTree<T>> getSubTrees(SyntacticTree<T> tree) {
        List<SubTree<T>> subTrees = new ArrayList<>(tree.subTrees.size());
        for (int i = 0; i < tree.subTrees.size(); i++) {
            subTrees.add(new SubTree<>(tree.subTrees.get(i), new int[]{i}));
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
        for (var step : path) {
            tree = tree.subTrees.get(step);
        }
        return new SubTree<>(tree, path);
    }

    public static class SubTree<T> {
        public SyntacticTree<T> tree;
        public int[] path;

        public SubTree(SyntacticTree<T> tree, int[] path) {
            this.tree = tree;
            this.path = path;
        }
    }
}
