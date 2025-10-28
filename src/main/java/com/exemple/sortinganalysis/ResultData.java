package com.exemple.sortinganalysis;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

public class ResultData {
    private final SimpleStringProperty algorithm;
    private final SimpleDoubleProperty timeMillis;
    private final SimpleLongProperty comparisons;
    private final SimpleLongProperty swaps;

    public ResultData(String algorithm, double timeMillis, long comparisons, long swaps) {
        this.algorithm = new SimpleStringProperty(algorithm);
        this.timeMillis = new SimpleDoubleProperty(timeMillis);
        this.comparisons = new SimpleLongProperty(comparisons);
        this.swaps = new SimpleLongProperty(swaps);
    }

    public String getAlgorithm() {
        return algorithm.get();
    }

    public SimpleStringProperty algorithmProperty() {
        return algorithm;
    }

    public double getTimeMillis() {
        return timeMillis.get();
    }

    public SimpleDoubleProperty timeMillisProperty() {
        return timeMillis;
    }

    public long getComparisons() {
        return comparisons.get();
    }

    public SimpleLongProperty comparisonsProperty() {
        return comparisons;
    }

    public long getSwaps() {
        return swaps.get();
    }

    public SimpleLongProperty swapsProperty() {
        return swaps;
    }

    public void setTimeMillis(double timeMillis) {
        this.timeMillis.set(timeMillis);
    }
}
