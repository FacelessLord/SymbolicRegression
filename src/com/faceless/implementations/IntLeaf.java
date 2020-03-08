package com.faceless.implementations;

import com.faceless.abstraction.Leaf;
import com.faceless.abstraction.UnaryOperation;

import java.util.function.IntFunction;

public class IntLeaf extends Leaf<Integer> {
    public IntLeaf(IntFunction<Integer[]> arrayGenerator, UnaryOperation<Integer> valueProvider) {
        super(arrayGenerator, valueProvider);
    }
}
