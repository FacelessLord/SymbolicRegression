package com.faceless.regression;

import com.faceless.abstraction.IOperation;
import com.faceless.abstraction.SyntacticTree;
import com.faceless.operations.Constant;

import java.util.*;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Mutator<T> {
    private final Generator<T> treeGenerator;
    private IntFunction<T[]> arrayGenerator;
    private List<IOperation<T>> operationList;
    private Random random = new Random();
    private int subTreesDepth = 2;

    public Mutator(IntFunction<T[]> arrayGenerator, List<IOperation<T>> operationList, Function<Random, Constant<T>> constantGenerator, List<String> variables, Map<String, T> variableDict) {
        this.arrayGenerator = arrayGenerator;
        this.operationList = operationList;
        this.treeGenerator = new Generator<>(arrayGenerator, operationList, constantGenerator,
                variables, variableDict);
    }

    public SyntacticTree<T> mutate(SyntacticTree<T> tree) {
        var treeCopy = tree.copy();

        if (random.nextDouble() > 0.25)
            for (int i = 0; i < random.nextInt(2); i++) {
                int operationIndex = random.nextInt(5);
                switch (operationIndex) {
                    case 0:
                        changeRandomOperation(treeCopy);
                        break;
                    case 1:
                        insertSubtree(treeCopy);
                        break;
                    case 2:
                        removeUnaryNode(treeCopy);
                        break;
                    case 3:
                        treeCopy = createNewRoot(treeCopy);
                        break;
                    case 4:
                        shufleSubtreeArgs(treeCopy);
                        break;
                }
            }

        return treeCopy;
    }

    private void shufleSubtreeArgs(SyntacticTree<T> tree) {
        var path = TreeHelper.findRandomNodePath(tree);
        var subTree = TreeHelper.extractTree(tree, path);

        var list = new ArrayList<Integer>(subTree.path.length);
        Arrays.stream(subTree.path).forEach(list::add);
        Collections.shuffle(list);
        for (int i = 0; i < subTree.path.length; i++) {
            subTree.path[i] = list.get(i);
        }

    }

    private SyntacticTree<T> createNewRoot(SyntacticTree<T> tree) {
        var unaryOperations = this.operationList.stream().filter(o -> o.getArgumentCount() == 1).collect(Collectors.toList());
        if (unaryOperations.size() > 0) {
            var operation = unaryOperations.get(random.nextInt(unaryOperations.size()));
            return new SyntacticTree<>(arrayGenerator, operation, tree);
        }
        return tree;
    }

    private void removeUnaryNode(SyntacticTree<T> tree) {
        List<TreeHelper.SubTree<T>> unaryNodes = new ArrayList<>();
        getUnaryNodes(new TreeHelper.SubTree<>(tree, new int[0]), unaryNodes);
        if (unaryNodes.size() > 0) {
            int nodeIndex = random.nextInt(unaryNodes.size());
            var node = unaryNodes.get(nodeIndex);
            node.tree.operation = node.tree.subTrees.get(0).operation;
            node.tree.subTrees = node.tree.subTrees.get(0).subTrees;
        }
    }

    public void getUnaryNodes(TreeHelper.SubTree<T> tree, List<TreeHelper.SubTree<T>> nodes) {
        if (tree.tree.operation.getArgumentCount() == 1)
            nodes.add(tree);
        List<SyntacticTree<T>> subTrees = tree.tree.subTrees;
        for (int i = 0; i < subTrees.size(); i++) {
            SyntacticTree<T> subTree = subTrees.get(i);
            int[] newPath = TreeHelper.addValueToArray(tree.path, i);
            getUnaryNodes(new TreeHelper.SubTree<>(subTree, newPath), nodes);
        }
    }

    private void changeRandomOperation(SyntacticTree<T> tree) {
        var path = TreeHelper.findRandomNodePath(tree);
        var subTree = TreeHelper.extractTree(tree, path);
        var sameArityOperations = operationList.stream()
                .filter(o -> subTree.tree.operation.getArgumentCount() == o.getArgumentCount())
                .collect(Collectors.toList());
        if (sameArityOperations.size() > 0)
            subTree.tree.operation = sameArityOperations.get(random.nextInt(sameArityOperations.size()));
    }

    private void insertSubtree(SyntacticTree<T> tree) {
        var path = TreeHelper.findRandomNodePath(tree);
        var subTree = treeGenerator.generateTree(subTreesDepth);
        TreeHelper.replaceSubTreeAtPath(tree, subTree, path);
    }
}
