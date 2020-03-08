package com.faceless.operations;

import com.faceless.abstraction.NullaryOperation;

import java.util.Dictionary;
import java.util.Map;

public class Variable<T> extends NullaryOperation<T> {
    private String name;
    private Map<String, T> valueContainer;

    public Variable(String name, Map<String, T> valueContainer) {
        this.name = name;
        this.valueContainer = valueContainer;
    }

    @Override
    public final String getExpressionString(String... args) {
        return name;
    }

    @Override
    public String getOperationSymbol() {
        return getExpressionString();
    }

    @Override
    public int getOperationPriority() {
        return -1;
    }

    @Override
    public T apply(Object[] args) {
        return valueContainer.get(this.name);
    }
}
