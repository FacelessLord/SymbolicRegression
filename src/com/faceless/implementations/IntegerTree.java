package com.faceless.implementations;

import com.faceless.abstraction.IOperation;
import com.faceless.abstraction.SyntacticTree;

public class IntegerTree extends SyntacticTree<Integer> {
    public IntegerTree(IOperation<Integer> operation, IntegerTree... subTrees) {
        super(Integer[]::new, operation, subTrees);
    }
}
