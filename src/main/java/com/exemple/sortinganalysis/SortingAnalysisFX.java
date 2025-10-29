package com.exemple.sortinganalysis;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Screen;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * Classe principal da aplicação JavaFX que gerencia a interface de análise
 * de algoritmos de ordenação. Permite leitura e ordenação de dados
 * (números e textos), exibe resultados em tabelas, atualiza gráfico de desempenho
 * e mostra descrição da complexidade Big O dos algoritmos.
 * Contém métodos para leitura de arquivos, entrada manual, execução e medição
 * dos algoritmos, bem como lógica de interface e eventos.
 */

public class SortingAnalysisFX extends Application {

    // Tabela que exibe resultados da ordenação (algoritmo, tempo, comparações, trocas)
    private TableView<ResultData> table = new TableView<>();
    // Lista observável para alimentar a tabela dinamicamente
    private ObservableList<ResultData> data = FXCollections.observableArrayList();

    // ComboBox para o usuário escolher o tipo dos dados do arquivo (números ou textos)
    private ComboBox<String> fileTypeCombo = new ComboBox<>();
    // ComboBox para escolher o tipo de dados da ordenação (números, textos ou imagens)
    private ComboBox<String> typeCombo = new ComboBox<>();
    // Área para entrada manual de dados
    private TextArea manualInputArea = new TextArea();
    // Botão para executar ordenação da entrada manual
    private Button btnRunManual = new Button("Executar ordenação dos dados manuais");
    // Área para mostrar a lista ordenada (após processamento)
    private TextArea sortedListArea = new TextArea();

    // Gráfico de barras para visualização das métricas de desempenho
    private BarChart<String, Number> barChart;
    // Label que mostra a descrição da complexidade Big O, com opção de ocultar/exibir
    private Label bigODescriptionLabel = new Label();

    @Override
    public void start(Stage primaryStage) {
        // Configuração inicial da janela principal
        primaryStage.setTitle("Análise de Algoritmos de Ordenação");

        // Configura ComboBox para seleção do tipo do arquivo (antes da leitura)
        fileTypeCombo.getItems().addAll("Números", "Textos");
        fileTypeCombo.getSelectionModel().selectFirst();

        // Botão para abrir arquivo, chamando método openFile()
        Button btnOpen = new Button("Selecionar arquivo de dados");
        btnOpen.setOnAction(e -> openFile(primaryStage));

        // Botão para salvar resultados em CSV
        Button btnSave = new Button("Salvar resultados CSV");
        btnSave.setOnAction(e -> saveCSV());

        // ComboBox para seleção do tipo de dado a ordenar
        typeCombo.getItems().addAll("Números", "Textos", "Imagens");
        typeCombo.getSelectionModel().selectFirst();

        // Configura a área de texto para entrada manual, com instruções no placeholder
        manualInputArea.setPromptText("Digite números ou textos separados por vírgula, espaço ou nova linha");

        // Ao clicar, handle para processar a entrada manual
        btnRunManual.setOnAction(e -> processManualInput());

        // Configura área de texto para mostrar lista ordenada: apenas leitura, com wrap
        sortedListArea.setEditable(false);
        sortedListArea.setPrefRowCount(10);
        sortedListArea.setWrapText(true);

        // Cria e configura as colunas da tabela para exibir resultados
        TableColumn<ResultData, String> algoCol = new TableColumn<>("Algoritmo");
        algoCol.setCellValueFactory(cellData -> cellData.getValue().algorithmProperty());

        TableColumn<ResultData, Double> timeCol = new TableColumn<>("Tempo (ms)");
        timeCol.setCellValueFactory(cellData -> cellData.getValue().timeMillisProperty().asObject());

        TableColumn<ResultData, Long> compCol = new TableColumn<>("Comparações");
        compCol.setCellValueFactory(cellData -> cellData.getValue().comparisonsProperty().asObject());

        TableColumn<ResultData, Long> swapCol = new TableColumn<>("Trocas");
        swapCol.setCellValueFactory(cellData -> cellData.getValue().swapsProperty().asObject());

        // Adiciona colunas na tabela e associa os dados observáveis
        table.getColumns().addAll(algoCol, timeCol, compCol, swapCol);
        table.setItems(data);

        // ScrollPane para lista ordenada para quando for extensa
        ScrollPane scrollPane = new ScrollPane(sortedListArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(180);

        // Left Panel: VBox com todo o conteúdo interativo e visualização da tabela
        VBox leftPanel = new VBox(10);
        leftPanel.setPadding(new Insets(10));
        leftPanel.getChildren().addAll(
                new Label("Tipo dos dados do arquivo:"), // label explicativa sobre o campo abaixo
                fileTypeCombo,
                btnOpen,
                new Label("Tipo de dados para ordenação:"), // label para escolher tipo entrada manual
                typeCombo,
                new Label("Entrada manual de dados:"),
                manualInputArea,
                btnRunManual,
                btnSave,
                new Label("Lista ordenada:"),
                scrollPane,
                table
        );
        leftPanel.setPrefWidth(600);

        // Eixo X categórico (nome dos algoritmos) para o gráfico
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Algoritmo");

        // Eixo Y numérico para valores das métricas do gráfico
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Valor");

        // Gráfico de barras para métricas de desempenho
        barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Comparação de Algoritmos");
        barChart.setPrefWidth(400);

        // Layout para descrição Big O, inicialmente oculta, texto com quebra de linha
        bigODescriptionLabel.setWrapText(true);
        bigODescriptionLabel.setVisible(false);

        // Botão para mostrar/ocultar descrição da complexidade Big O
        Button toggleBigODescriptionBtn = new Button("Mostrar/Ocultar Big O");
        toggleBigODescriptionBtn.setOnAction(e -> bigODescriptionLabel.setVisible(!bigODescriptionLabel.isVisible()));

        // Right Panel: VBox que contém o gráfico e botão + label de Big O
        VBox rightPanel = new VBox(10);
        rightPanel.setPadding(new Insets(10));
        rightPanel.getChildren().addAll(barChart, toggleBigODescriptionBtn, bigODescriptionLabel);
        rightPanel.setPrefWidth(450);

        // Layout pai HBox divide a janela em dois painéis: controle (esquerda) e gráfico (direita)
        HBox root = new HBox(leftPanel, rightPanel);

        // Configura a aplicação para abrir em tela cheia, respeitando a barra de tarefas
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        Scene scene = new Scene(root, visualBounds.getWidth(), visualBounds.getHeight());
        primaryStage.setScene(scene);
        primaryStage.setX(visualBounds.getMinX());
        primaryStage.setY(visualBounds.getMinY());
        primaryStage.setWidth(visualBounds.getWidth());
        primaryStage.setHeight(visualBounds.getHeight());

        primaryStage.show();
    }

    /**
     * Lê dados da entrada manual, valida e dispara os algoritmos conforme o tipo selecionado.
     * Mostra alertas para tipos não suportados ou entradas inválidas.
     */
    private void processManualInput() {
        String type = typeCombo.getValue();
        String input = manualInputArea.getText().trim();

        if (type.equals("Imagens")) {
            showAlert("Ordenação de imagens ainda não está disponível.");
            return;
        }

        if (input.isEmpty()) {
            showAlert("Por favor, insira dados para ordenar.");
            return;
        }

        // Limpa resultados anteriores
        data.clear();

        // Dispara ordenações e atualiza visualizações conforme tipo escolhido
        if (type.equals("Números")) {
            try {
                int[] numbers = parseNumbers(input);
                runAlgorithms(numbers);
                updateSortedListAreaFromIntArray(numbers);
                updateChart();
                updateBigODescriptionForLastAlgorithm();
            } catch (NumberFormatException e) {
                showAlert("Entrada inválida: certifique-se de inserir apenas números inteiros.");
            }
        } else if (type.equals("Textos")) {
            String[] texts = parseTexts(input);
            runStringAlgorithms(texts);
            updateSortedListAreaFromStringArray(texts);
            updateChart();
            updateBigODescriptionForLastAlgorithm();
        }
    }

    /**
     * Transforma a string da entrada manual em array de inteiros.
     * @throws NumberFormatException caso algum token não seja um número válido
     */
    private int[] parseNumbers(String input) throws NumberFormatException {
        String[] tokens = input.split("[,\\s]+");
        int[] numbers = new int[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            numbers[i] = Integer.parseInt(tokens[i].trim());
        }
        return numbers;
    }

    /**
     * Transforma a string da entrada manual em array de strings (tokens limpos).
     */
    private String[] parseTexts(String input) {
        return Arrays.stream(input.split("[,\\s]+"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);
    }

    /**
     * Método chamado ao abrir arquivo. Lê o arquivo, valida e executa ordenação conforme o tipo selecionado.
     * atualiza visualizações na interface.
     */
    private void openFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Abrir arquivo de dados");
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            String selectedFileType = fileTypeCombo.getValue();
            try {
                if ("Números".equals(selectedFileType)) {
                    int[] inputData = readAndValidateNumbersFromFile(file);
                    data.clear();
                    runAlgorithms(inputData);
                    updateSortedListAreaFromIntArray(inputData);
                    updateChart();
                    updateBigODescriptionForLastAlgorithm();
                } else {
                    String[] inputData = readTextsFromFile(file);
                    if (inputData.length == 0) {
                        showAlert("Arquivo vazio ou inválido.");
                        return;
                    }
                    data.clear();
                    runStringAlgorithms(inputData);
                    updateSortedListAreaFromStringArray(inputData);
                    updateChart();
                    updateBigODescriptionForLastAlgorithm();
                }
            } catch (Exception ex) {
                showAlert("Erro ao abrir arquivo: " + ex.getMessage());
            }
        }
    }

    /**
     * Método que lê e valida arquivo de números, gerando exceção se achar dados inválidos
     */
    private int[] readAndValidateNumbersFromFile(File file) throws IOException {
        List<Integer> numbers = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    try {
                        numbers.add(Integer.parseInt(line));
                    } catch (NumberFormatException e) {
                        throw new IOException("Arquivo contém valores não numéricos.");
                    }
                }
            }
        }
        return numbers.stream().mapToInt(i -> i).toArray();
    }

    /**
     * Método que lê arquivo de texto linha a linha
     */
    private String[] readTextsFromFile(File file) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    lines.add(line);
                }
            }
        }
        return lines.toArray(new String[0]);
    }

    /**
     * Método que executa todos os algoritmos de ordenação para números
     */
    private void runAlgorithms(int[] inputData) {
        runAlgorithm("Bubble Sort", inputData, SortingComparisonEnhanced::bubbleSort);
        runAlgorithm("Quick Sort", inputData, (arr) -> SortingComparisonEnhanced.quickSort(arr, 0, arr.length - 1));
        runAlgorithm("Merge Sort", inputData, (arr) -> SortingComparisonEnhanced.mergeSort(arr, 0, arr.length - 1));
        runAlgorithm("Heap Sort", inputData, SortingComparisonEnhanced::heapSort);
    }

    /**
     * Método que executa um algoritmo de ordenação para números e armazena os resultados (tempo, comparações, trocas)
     */
    private void runAlgorithm(String name, int[] inputData, Consumer<int[]> algorithm) {
        int[] copy = Arrays.copyOf(inputData, inputData.length);
        SortingComparisonEnhanced.comparacoes = 0;
        SortingComparisonEnhanced.trocas = 0;
        long start = System.nanoTime();
        algorithm.accept(copy);
        long end = System.nanoTime();

        if (!SortingComparisonEnhanced.isSorted(copy)) {
            showAlert("Erro: " + name + " não ordenou corretamente.");
        }

        double timeMs = (end - start) / 1_000_000.0;
        ResultData rd = new ResultData(name, timeMs, SortingComparisonEnhanced.comparacoes, SortingComparisonEnhanced.trocas);
        data.add(rd);
    }

    /**
     * Método que executa todos os algoritmos de ordenação para textos
     */
    private void runStringAlgorithms(String[] inputData) {
        runStringAlgorithm("Bubble Sort (Textos)", inputData, SortingComparisonEnhanced::bubbleSortStrings);
        runStringAlgorithm("Quick Sort (Textos)", inputData, (arr) -> SortingComparisonEnhanced.quickSortStrings(arr, 0, arr.length - 1));
        runStringAlgorithm("Merge Sort (Textos)", inputData, (arr) -> SortingComparisonEnhanced.mergeSortStrings(arr, 0, arr.length - 1));
        runStringAlgorithm("Heap Sort (Textos)", inputData, SortingComparisonEnhanced::heapSortStrings);
    }

    /**
     * Método que executa um algoritmo de ordenação para textos e armazena os resultados (tempo, comparações, trocas)
     */
    private void runStringAlgorithm(String name, String[] inputData, Consumer<String[]> algorithm) {
        String[] copy = Arrays.copyOf(inputData, inputData.length);
        SortingComparisonEnhanced.comparacoes = 0;
        SortingComparisonEnhanced.trocas = 0;
        long start = System.nanoTime();
        algorithm.accept(copy);
        long end = System.nanoTime();

        if (!isSortedStrings(copy)) {
            showAlert("Erro: " + name + " não ordenou corretamente.");
        }

        double timeMs = (end - start) / 1_000_000.0;
        ResultData rd = new ResultData(name, timeMs, SortingComparisonEnhanced.comparacoes, SortingComparisonEnhanced.trocas);
        data.add(rd);
    }

    /**
     * Verifica se um array de strings está ordenado lexicograficamente (ignorando case)
     */
    private boolean isSortedStrings(String[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            if (arr[i].compareToIgnoreCase(arr[i + 1]) > 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Atualiza a área de texto com o array de números ordenados para visualização
     */
    private void updateSortedListAreaFromIntArray(int[] arr) {
        StringBuilder sb = new StringBuilder();
        for (int n : arr) {
            sb.append(n).append("\n");
        }
        sortedListArea.setText(sb.toString());
    }

    /**
     * Atualiza a área de texto com o array de strings ordenadas para visualização
     */
    private void updateSortedListAreaFromStringArray(String[] arr) {
        StringBuilder sb = new StringBuilder();
        for (String s : arr) {
            sb.append(s).append("\n");
        }
        sortedListArea.setText(sb.toString());
    }

    /**
     * Atualiza o gráfico de barras com as métricas atuais armazenadas na lista data
     */
    private void updateChart() {
        barChart.getData().clear();

        XYChart.Series<String, Number> timeSeries = new XYChart.Series<>();
        timeSeries.setName("Tempo (ms)");

        XYChart.Series<String, Number> compSeries = new XYChart.Series<>();
        compSeries.setName("Comparações");

        XYChart.Series<String, Number> swapSeries = new XYChart.Series<>();
        swapSeries.setName("Trocas");

        for (ResultData rd : data) {
            timeSeries.getData().add(new XYChart.Data<>(rd.getAlgorithm(), rd.getTimeMillis()));
            compSeries.getData().add(new XYChart.Data<>(rd.getAlgorithm(), rd.getComparisons()));
            swapSeries.getData().add(new XYChart.Data<>(rd.getAlgorithm(), rd.getSwaps()));
        }

        barChart.getData().addAll(timeSeries, compSeries, swapSeries);
    }

    /**
     * Atualiza o rótulo da descrição Big O com a última linha da tabela
     */
    private void updateBigODescriptionForLastAlgorithm() {
        if (data.isEmpty()) {
            bigODescriptionLabel.setText("");
            return;
        }
        String lastAlgorithm = data.get(data.size() - 1).getAlgorithm();
        bigODescriptionLabel.setText(getBigONotationDescription(lastAlgorithm));
    }

    /**
     * Retorna a descrição da notação Big O para cada algoritmo suportado
     */
    private String getBigONotationDescription(String algorithmName) {
        switch (algorithmName) {
            case "Bubble Sort":
            case "Bubble Sort (Textos)":
                return "Tempo (pior caso): O(n²), Espaço: O(1)";
            case "Quick Sort":
            case "Quick Sort (Textos)":
                return "Tempo (pior caso): O(n²), Espaço: O(log n)";
            case "Merge Sort":
            case "Merge Sort (Textos)":
                return "Tempo (pior caso): O(n log n), Espaço: O(n)";
            case "Heap Sort":
            case "Heap Sort (Textos)":
                return "Tempo (pior caso): O(n log n), Espaço: O(1)";
            default:
                return "Complexidade não disponível";
        }
    }

    /**
     * Salva os resultados atuais em arquivo CSV (usuário escolhe local)
     */
    private void saveCSV() {
        if (data.isEmpty()) {
            showAlert("Nenhum resultado para salvar.");
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar arquivo CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                SortingComparisonEnhanced.writeResultsToCSV(file.getAbsolutePath(), data);
                showAlert("Resultados salvos com sucesso.");
            } catch (IOException ex) {
                showAlert("Erro ao salvar arquivo: " + ex.getMessage());
            }
        }
    }

    /**
     * Exibe uma caixa de alerta modal para o usuário
     */
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
