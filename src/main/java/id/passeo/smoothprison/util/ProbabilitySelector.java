package id.passeo.smoothprison.util;

import id.passeo.smoothprison.blockhandle.Blocks;

import java.util.Map;
import java.util.NavigableMap;
import java.util.SplittableRandom;
import java.util.TreeMap;

public class ProbabilitySelector {
    private final NavigableMap<Double, Blocks> cumulativeProbabilities = new TreeMap<>();
    private final double totalProbability;
    private final SplittableRandom rand;

    public ProbabilitySelector(Map<Blocks, Double> probabilityBlock) {
        double cumulative = 0.0;
        rand = new SplittableRandom();
        for (Map.Entry<Blocks, Double> entry : probabilityBlock.entrySet()) {
            cumulative += entry.getValue();
            cumulativeProbabilities.put(cumulative, entry.getKey());
        }
        totalProbability = cumulative;
    }

    public Blocks selectBlock() {
        double randomValue = rand.nextDouble()  * totalProbability;
        return cumulativeProbabilities.higherEntry(randomValue).getValue();
    }
}
