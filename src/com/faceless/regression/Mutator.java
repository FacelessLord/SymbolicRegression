package com.faceless.regression;

import com.faceless.abstraction.IOperation;
import com.faceless.abstraction.SyntacticTree;
import com.faceless.operations.Constant;
import com.faceless.operations.Variable;

import java.util.*;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

public class Mutator<T> {
    private Generator<T> treeGenerator;
    private IntFunction<T[]> arrayGenerator;
    private List<IOperation<T>> operationList;
    private Random random = new Random();
    private int subTreesDepth = 2;
    private Function<Random, Constant<T>> constantGenerator;
    private List<Variable<T>> variables;
    private double mutationChance;
    private int maxMutations;

    public Mutator(IntFunction<T[]> arrayGenerator, List<IOperation<T>> operationList, Generator<T> generator, Function<Random, Constant<T>> constantGenerator, List<Variable<T>> variables, double mutationChance, int maxMutations) {
        this.arrayGenerator = arrayGenerator;
        this.operationList = operationList;
        this.constantGenerator = constantGenerator;
        this.variables = variables;
        this.mutationChance = mutationChance;
        this.maxMutations = maxMutations + 1;
        this.treeGenerator = generator;
    }

    /**
     * Tries to preform {@link #maxMutations} mutations with chance of every mutation equal to {@link #mutationChance}
     *
     * @param tree tree to mutate
     * @return mutated version of given tree
     */
    public SyntacticTree<T> mutate(SyntacticTree<T> tree) {
        var treeCopy = tree.copy();

        int mutationCount = random.nextInt(maxMutations);
        for (int i = 0; i < mutationCount; i++) {
            if (random.nextDouble() > mutationChance) {
                treeCopy = mutateTree(treeCopy, random.nextInt(7));
            }
        }

        return treeCopy;
    }

    /**
     * Performs single mutation
     *
     * @param tree       tree to mutate
     * @param mutationId id of mutation to perform
     * @return mutated version of given tree
     */
    private SyntacticTree<T> mutateTree(SyntacticTree<T> tree, int mutationId) {
        switch (mutationId) {
            case 0:
                return changeRandomOperation(tree);
            case 1:
                return replaceSubtree(tree);
            case 2:
                return removeUnaryNode(tree);
            case 3:
                return createNewRoot(tree);
            case 4:
                return shuffleSubtreeArgs(tree);
            case 5:
                return randomVariableConstantSwitch(tree);
            case 6:
                return fullTreeReplace(tree);
        }
        return tree;
    }

    /**
     * Changes both given tree operation and subtrees making it different tree
     *
     * @param tree tree to mutate
     * @return mutated version of given tree
     */
    private SyntacticTree<T> fullTreeReplace(SyntacticTree<T> tree) {
        SyntacticTree<T> treeCopy = tree.copy();
        SyntacticTree<T> newTree = treeGenerator.generateTree(subTreesDepth);
        treeCopy.operation = newTree.operation;
        treeCopy.subTrees = newTree.subTrees;
        newTree.dispose();
        return treeCopy;
    }

    /**
     * Finds leaf node and changes its operation between variable and constant
     *
     * @param tree tree to mutate
     * @return mutated version of given tree
     */
    private SyntacticTree<T> randomVariableConstantSwitch(SyntacticTree<T> tree) {
        SyntacticTree<T> treeCopy = tree.copy();

        List<TreeHelper.SubTree<T>> nodes = TreeHelper.getNodes(treeCopy, o -> o.getArgumentCount() == 0);
        TreeHelper.SubTree<T> node = nodes.get(random.nextInt(nodes.size()));
        if (node.tree.operation instanceof Constant) {
            node.tree.operation = variables.get(random.nextInt(variables.size()));
        } else {
            node.tree.operation = constantGenerator.apply(random);
        }
        return treeCopy;
    }

    /**
     * Finds random node and shuffles its subtrees
     *
     * @param tree tree to mutate
     * @return mutated version of given tree
     */
    private SyntacticTree<T> shuffleSubtreeArgs(SyntacticTree<T> tree) {
        SyntacticTree<T> treeCopy = tree.copy();
        var path = TreeHelper.findRandomNodePath(treeCopy);
        var subTree = TreeHelper.extractTree(treeCopy, path);

        var list = new ArrayList<Integer>(subTree.path.length);
        Arrays.stream(subTree.path).forEach(list::add);
        Collections.shuffle(list);
        for (int i = 0; i < subTree.path.length; i++) {
            subTree.path[i] = list.get(i);
        }
        return treeCopy;
    }

    /**
     * Creates new node with unary operation and makes given tree its subtree
     *
     * @param tree tree to mutate
     * @return mutated version of given tree
     */
    private SyntacticTree<T> createNewRoot(SyntacticTree<T> tree) {
        var unaryOperations = this.operationList.stream().filter(o -> o.getArgumentCount() == 1).collect(Collectors.toList());
        if (unaryOperations.size() > 0) {
            var operation = unaryOperations.get(random.nextInt(unaryOperations.size()));
            return new SyntacticTree<>(arrayGenerator, operation, tree);
        }
        return tree;
    }

    /**
     * Finds unary node and replaces it by its child
     *
     * @param tree tree to mutate
     * @return mutated version of given tree
     */
    private SyntacticTree<T> removeUnaryNode(SyntacticTree<T> tree) {
        SyntacticTree<T> treeCopy = tree.copy();
        List<TreeHelper.SubTree<T>> unaryNodes = TreeHelper.getNodes(treeCopy, o -> o.getArgumentCount() == 1);

        if (unaryNodes.size() > 0) {
            int nodeIndex = random.nextInt(unaryNodes.size());
            var node = unaryNodes.get(nodeIndex);
            removeNode(node, 0);
        }
        return treeCopy;
    }

    /**
     * Replaces node by its child
     *
     * @param node                 node to remove
     * @param subNodeReplacementId id of subNode to replace given node
     */
    private void removeNode(TreeHelper.SubTree<T> node, int subNodeReplacementId) {
        node.tree.operation = node.tree.subTrees.get(subNodeReplacementId).operation;
        node.tree.subTrees = node.tree.subTrees.get(subNodeReplacementId).subTrees;
    }

    /**
     * Finds node and replaces its operation with another with same arity
     *
     * @param tree tree to mutate
     * @return mutated version of given tree
     */
    private SyntacticTree<T> changeRandomOperation(SyntacticTree<T> tree) {
        SyntacticTree<T> treeCopy = tree.copy();

        var path = TreeHelper.findRandomNodePath(treeCopy);
        var subTree = TreeHelper.extractTree(treeCopy, path).tree;
        var sameArityOperations = operationList.stream()
                .filter(o -> subTree.operation.getArgumentCount() == o.getArgumentCount())
                .collect(Collectors.toList());
        if (sameArityOperations.size() > 1)
            subTree.operation = sameArityOperations.get(random.nextInt(sameArityOperations.size()));

        return treeCopy;
    }

    /**
     * Finds node and replaces it with newly generated tree
     *
     * @param tree tree to mutate
     * @return mutated version of given tree
     */
    private SyntacticTree<T> replaceSubtree(SyntacticTree<T> tree) {
        SyntacticTree<T> treeCopy = tree.copy();

        var path = TreeHelper.findRandomNodePath(treeCopy);
        var subTree = treeGenerator.generateTree(subTreesDepth);

        TreeHelper.replaceSubTreeAtPath(treeCopy, subTree, path);

        return treeCopy;
    }
}
