package com.faceless.abstraction;

import com.faceless.abstraction.SyntacticTree;
import com.faceless.abstraction.UnaryOperation;

import java.util.function.IntFunction;

public abstract class Leaf<T> extends SyntacticTree<T> {

    public Leaf(IntFunction<T[]> arrayGenerator, UnaryOperation<T> valueProvider) {
        super(arrayGenerator, valueProvider);
    }
}
