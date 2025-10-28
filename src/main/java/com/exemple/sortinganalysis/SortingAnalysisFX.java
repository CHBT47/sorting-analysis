package com.exemple.sortinganalysis;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;

public class SortingAnalysisFX extends Application {

    private TableView<ResultData> table = new TableView<>();
    private ObservableList<ResultData> data = FXCollections.observableArrayList();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Análise de Algoritmos de Ordenação");

        Button btnOpen = new Button("Selecionar arquivo de dados");
        btnOpen.setOnAction(e -> openFile(primaryStage));

        Button btnSave = new Button("Salvar resultados CSV");
        btnSave.setOnAction(e -> saveCSV());

        TableColumn<ResultData, String> algoCol = new TableColumn<>("Algoritmo");
        algoCol.setCellValueFactory(cellData -> cellData.getValue().algorithmProperty());

        TableColumn<ResultData, Double> timeCol = new TableColumn<>("Tempo (ms)");
        timeCol.setCellValueFactory(cellData -> cellData.getValue().timeMillisProperty().asObject());

        TableColumn<ResultData, Long> compCol = new TableColumn<>("Comparações");
        compCol.setCellValueFactory(cellData -> cellData.getValue().comparisonsProperty().asObject());

        TableColumn<ResultData, Long> swapCol = new TableColumn<>("Trocas");
        swapCol.setCellValueFactory(cellData -> cellData.getValue().swapsProperty().asObject());

        table.getColumns().addAll(algoCol, timeCol, compCol, swapCol);
        table.setItems(data);

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        vbox.getChildren().addAll(btnOpen, btnSave, table);

        Scene scene = new Scene(vbox, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void openFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Abrir arquivo de dados");
        File file = fileChooser.showOpenDialog(stage);
        if(file != null) {
            try {
                int[] inputData = SortingComparisonEnhanced.readDataFromFile(file.getAbsolutePath());
                data.clear();
                runAlgorithms(inputData);
            } catch(IOException ex) {
                showAlert("Erro ao abrir arquivo: " + ex.getMessage());
            }
        }
    }

    private void runAlgorithms(int[] inputData) {
        runAlgorithm("Bubble Sort", inputData, SortingComparisonEnhanced::bubbleSort);
        runAlgorithm("Quick Sort", inputData, (arr) -> SortingComparisonEnhanced.quickSort(arr, 0, arr.length-1));
        runAlgorithm("Merge Sort", inputData, (arr) -> SortingComparisonEnhanced.mergeSort(arr, 0,arr.length-1));
        runAlgorithm("Heap Sort", inputData, SortingComparisonEnhanced::heapSort);
    }

    private void runAlgorithm(String name, int[] inputData, java.util.function.Consumer<int[]> algorithm) {
        int[] copy = java.util.Arrays.copyOf(inputData, inputData.length);
        SortingComparisonEnhanced.comparacoes = 0;
        SortingComparisonEnhanced.trocas = 0;
        long start = System.nanoTime();
        algorithm.accept(copy);
        long end = System.nanoTime();

        if(!SortingComparisonEnhanced.isSorted(copy)) {
            showAlert("Erro: " + name + " não ordenou corretamente.");
        }

        double timeMs = (end - start) / 1_000_000.0;
        ResultData rd = new ResultData(name, timeMs, SortingComparisonEnhanced.comparacoes, SortingComparisonEnhanced.trocas);
        data.add(rd);
    }

    private void saveCSV() {
        if(data.isEmpty()) {
            showAlert("Nenhum resultado para salvar.");
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar arquivo CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(null);
        if(file != null) {
            try {
                SortingComparisonEnhanced.writeResultsToCSV(file.getAbsolutePath(), data);
                showAlert("Resultados salvos com sucesso.");
            } catch(IOException ex) {
                showAlert("Erro ao salvar arquivo: " + ex.getMessage());
            }
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informação");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
