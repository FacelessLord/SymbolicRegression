package com.faceless.regression;

import com.faceless.abstraction.SyntacticTree;

import java.util.*;
import java.util.function.Consumer;
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

    public Regressor(int populationSize, Map<String, T> variableDict, Generator<T> generator,
                     int maxDepth, Combinator<T> combinator, Mutator<T> mutator) {
        this.populationSize = populationSize;
        population = new ArrayList<>();
        this.variableDict = variableDict;
        this.generator = generator;
        this.maxDepth = maxDepth;
        this.combinator = combinator;
        this.mutator = mutator;
    }

    public Regressor(int populationSize, Map<String, T> variableDict, Generator<T> generator,
                     int maxDepth, Combinator<T> combinator, Mutator<T> mutator, Consumer<String> writer) {
        this.populationSize = populationSize;
        population = new ArrayList<>();
        this.variableDict = variableDict;
        this.generator = generator;
        this.maxDepth = maxDepth;
        this.combinator = combinator;
        this.mutator = mutator;
        this.writer = writer;
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
                return population.get(0);
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
            if (minDeviation > 1e50) {
                debug("Errored with high deviation");
                return population.get(0);
            }
            if (minDeviation < minSimulationDeviation) {
                minSimulationDeviation = minDeviation;
                minTree = population.get(0).copy();
            }
            debug("Simulation step #" + generationNumber + "; deviation=" + minDeviation);
            generationNumber++;
            if (generationNumber > maxGenerations)
                return minTree;

        }
        return population.get(0);
    }

    private void debug(String text) {
        if (writer != null)
            writer.accept(text);
    }

    private void simulationStep() {
        var okTrees = population.stream()
                .sorted(Comparator.comparingDouble(this::evaluateTree).reversed())
                .skip(populationSize / 2)
                .map(mutator::mutate)
                .collect(Collectors.toList());
        minDeviation = evaluateTree(okTrees.get(0));
        int length = okTrees.size();
        for (int i = 0; i < length; i++) {
            SyntacticTree<T> bTree = okTrees.get(i).copy();
            combinator.combine(bTree, okTrees.get((i + 1) % length));
            okTrees.add(bTree);
        }
        for (int i = 0; i < okTrees.size(); i++) {
            population = okTrees;
        }
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
