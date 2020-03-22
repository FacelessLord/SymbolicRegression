package com.faceless.regression;

import com.faceless.abstraction.SyntacticTree;
import com.faceless.operations.Constant;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Regressor<T extends Number> {
    private List<SyntacticTree<T>> population;
    private Map<String, T> variableDict;
    private Generator<T> generator;
    private int maxDepth;
    private Combinator<T> combinator;
    private Mutator<T> mutator;
    private int populationSize;
    private String[] trainingVariables;
    private T[][] trainingValues;
    private T[] trainingAnswers;
    private double minDeviation = Double.MAX_VALUE / 2;


    private Consumer<String> writer = null;
    private double maxDeviation = 1e50;
    private Constant<T> zero;

    public Regressor(int populationSize, Map<String, T> variableDict, Generator<T> generator,
                     int maxDepth, Combinator<T> combinator, Mutator<T> mutator, Constant<T> zero) {
        this.populationSize = populationSize;
        this.population = new ArrayList<>();
        this.variableDict = variableDict;
        this.generator = generator;
        this.maxDepth = maxDepth;
        this.combinator = combinator;
        this.mutator = mutator;
        this.zero = zero;
    }

    public void setWriter(Consumer<String> writer) {
        this.writer = writer;
    }

    public void setMaxDeviation(double maxDeviation) {
        this.maxDeviation = maxDeviation;
    }

    private void generatePopulation() {
        for (int i = 0; i < populationSize; i++) {
            population.add(generator.generateTree(maxDepth));
        }
    }

    public SyntacticTree<T> simulate(double deviation) {
        generatePopulation();
        debug("Population Created");
        while (minDeviation > deviation) {
            simulationStep();
            if (minDeviation > 1e50) {
                debug("Errored with high deviation");
                return population.get(populationSize - 1);
            }
            debug("Simulation step; deviation=" + minDeviation);
        }
        return population.get(0);
    }

    public SyntacticTree<T> simulate(double deviation, int maxGenerations) {
        generatePopulation();
        debug("Population Created");
        double minSimulationDeviation = minDeviation;
        SyntacticTree<T> minTree = null;
        int generationNumber = 0;
        while (minDeviation > deviation) {
            simulationStep();
            if (minDeviation > maxDeviation) {
                debug("Errored with high deviation");
                return population.get(populationSize - 1);
            }
            if (minDeviation < minSimulationDeviation) {
                minSimulationDeviation = minDeviation;
                minTree = population.get(populationSize - 1).copy();
            }
            debug("Simulation step #" + generationNumber + "; deviation=" + minDeviation);
            assert minTree != null;
            debug("Min Deviation At All=" + minSimulationDeviation + "; minTree=" + minTree.toExpression());
            generationNumber++;
            if (generationNumber > maxGenerations)
                return minTree;

        }
        return population.get(populationSize - 1);
    }

    private void debug(String text) {
        if (writer != null)
            writer.accept(text);
    }

    private void simulationStep() {
        var okTrees = population.stream()
                .sorted(Comparator.comparingDouble(this::evaluateTree).reversed())
                .skip(populationSize / 2)
                .map(t -> mutator.mutate(t))
                .peek(t -> t.cut(maxDepth, zero))
                .collect(Collectors.toList());
        minDeviation = evaluateTree(okTrees.get(okTrees.size() - 1));
        int length = okTrees.size();
        for (int i = 0; i < length; i++) {
            SyntacticTree<T> bTree = okTrees.get(i).copy();
            combinator.combine(bTree, okTrees.get((i + 1) % length));
            okTrees.add(bTree);
        }
        for (int i = 0; i < populationSize / 2; i++) {
            population.get(i).dispose();
        }
        population = okTrees;
    }

    @SuppressWarnings("RedundantCast")
    private double evaluateTree(SyntacticTree<T> tree) {
        double sum = 0d;
        for (int i = 0; i < trainingAnswers.length; i++) {
            setTrainingSet(i);
            sum += sqr((Double) tree.evaluate() - (Double) trainingAnswers[i]);
        }
        return sum;
    }

    private double sqr(double x) {
        return x * x;
    }

    private void setTrainingSet(int setId) {
        T[] set = trainingValues[setId];
        for (int i = 0; i < trainingVariables.length; i++) {
            variableDict.remove(trainingVariables[i]);
            variableDict.put(trainingVariables[i], set[i]);
        }
    }

    public void setTrainingSet(String[] variables, T[][] values, T[] answers) {
        trainingVariables = variables;
        trainingValues = values;
        trainingAnswers = answers;
    }

}
