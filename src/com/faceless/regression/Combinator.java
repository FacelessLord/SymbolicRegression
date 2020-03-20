package com.faceless.regression;

import com.faceless.abstraction.SyntacticTree;

import java.util.List;

public class Combinator<T> {
    public void combine(SyntacticTree<T> a, SyntacticTree<T> b) {
        var aList = TreeHelper.findRandomNodePath(a);
        var bList = TreeHelper.findRandomNodePath(b);
        var aTree = TreeHelper.extractTree(a, aList);
        var bTree = TreeHelper.extractTree(b, bList);
        TreeHelper.replaceSubTreeAtPath(a, bTree.tree, aList);
        TreeHelper.replaceSubTreeAtPath(b, aTree.tree, bList);
    }
}
