package com.exemple.sortinganalysis;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Classe que modela os dados de resultado de uma ordenação
 * para ser usada no TableView JavaFX com propriedades observáveis.
 */
public class ResultData {
    // Propriedade observável para nome do algoritmo
    private final SimpleStringProperty algorithm;
    // Propriedade observável para tempo de execução em milissegundos
    private final SimpleDoubleProperty timeMillis;
    // Propriedade observável para o número de comparações feitas
    private final SimpleLongProperty comparisons;
    // Propriedade observável para o número de trocas feitas
    private final SimpleLongProperty swaps;

    /**
     * Construtor que inicializa todas as propriedades com os valores passados.
     *
     * @param algorithm Nome do algoritmo
     * @param timeMillis Tempo de execução em milissegundos
     * @param comparisons Quantidade de comparações feitas
     * @param swaps Quantidade de trocas feitas
     */
    public ResultData(String algorithm, double timeMillis, long comparisons, long swaps) {
        this.algorithm = new SimpleStringProperty(algorithm);
        this.timeMillis = new SimpleDoubleProperty(timeMillis);
        this.comparisons = new SimpleLongProperty(comparisons);
        this.swaps = new SimpleLongProperty(swaps);
    }

    // Getters e propriedades para "binding" em JavaFX TableView

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

    /**
     * Exemplo de setter apenas para o tempo, utilizando propriedade observável.
     * Pode ser expandido para outras propriedades se necessário.
     *
     * @param timeMillis Novo valor de tempo de execução em milissegundos
     */
    public void setTimeMillis(double timeMillis) {
        this.timeMillis.set(timeMillis);
    }
}
