package com.exemple.sortinganalysis;

import java.io.*;
import java.util.*;

/**
 * Classe utilitária que implementa algoritmos de ordenação para arrays de
 * inteiros e strings, com contadores estáticos para comparar número de
 * comparações e trocas. Também contém métodos para leitura de arquivos e
 * gravação de resultados exportáveis, encapsulando a lógica dos algoritmos
 * e métricas associadas.
 */

public class SortingComparisonEnhanced {

    // Variáveis estáticas globais para contagem de comparações e trocas
    public static long comparacoes;
    public static long trocas;

    /**
     * Lê um arquivo de texto contendo números, uma linha por número.
     * Retorna um array de inteiros lido no arquivo.
     */
    public static int[] readDataFromFile(String filename) throws IOException {
        List<Integer> numbers = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while((line = br.readLine()) != null) {
                line = line.trim();
                if(!line.isEmpty()) {
                    numbers.add(Integer.parseInt(line));
                }
            }
        }
        return numbers.stream().mapToInt(i -> i).toArray();
    }

    /**
     * Escreve os resultados de ordenação em formato CSV no arquivo especificado.
     * Inclui colunas de algoritmo, tempo em ms, comparações e trocas.
     */
    public static void writeResultsToCSV(String filename, List<ResultData> results) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            bw.write("Algoritmo;Tempo (ms);Comparacoes;Trocas");
            bw.newLine();
            for(ResultData r : results) {
                bw.write(String.format("%s;%.3f;%d;%d", r.getAlgorithm(), r.getTimeMillis(), r.getComparisons(), r.getSwaps()));
                bw.newLine();
            }
        }
    }

    // -----------------------------------------------------
    // Métodos de ordenação para arrays de inteiros:

    /**
     * Implementação do Bubble Sort para inteiros.
     * Conta comparações e trocas feitas.
     */
    public static void bubbleSort(int[] arr) {
        comparacoes = 0; trocas = 0;
        int n = arr.length;
        for(int i = 0; i < n - 1; i++) {
            for(int j = 0; j < n - 1 - i; j++) {
                comparacoes++;
                if(arr[j] > arr[j+1]) {
                    trocas++;
                    // Swap
                    int temp = arr[j];
                    arr[j] = arr[j+1];
                    arr[j+1] = temp;
                }
            }
        }
    }

    /**
     * Implementação do Quick Sort para inteiros.
     * Método recursivo que usa o método partition para dividir.
     */
    public static void quickSort(int[] arr, int low, int high) {
        if(low < high) {
            int pi = partition(arr, low, high);
            quickSort(arr, low, pi - 1);
            quickSort(arr, pi + 1, high);
        }
    }

    /**
     * Particiona o array em torno de um pivô para o Quick Sort.
     * Coloca elementos menores que pivô à esquerda e maiores à direita.
     * Conta comparações e trocas.
     */
    private static int partition(int[] arr, int low, int high) {
        int pivot = arr[high];
        int i = (low - 1);
        for(int j = low; j < high; j++) {
            comparacoes++;
            if(arr[j] <= pivot) {
                i++;
                trocas++;
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }
        trocas++;
        int temp = arr[i+1];
        arr[i+1] = arr[high];
        arr[high] = temp;
        return i+1;
    }

    /**
     * Implementação do Merge Sort para inteiros, método recursivo.
     * Divide o array recursivamente e mescla as partes ordenadas.
     */
    public static void mergeSort(int[] arr, int left, int right) {
        if(left < right) {
            int mid = (left + right) / 2;
            mergeSort(arr, left, mid);
            mergeSort(arr, mid + 1, right);
            merge(arr, left, mid, right);
        }
    }

    /**
     * Faz a mesclagem das duas metades do array para o Merge Sort,
     * contando comparações e trocas no processo.
     */
    private static void merge(int[] arr, int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        int[] L = new int[n1];
        int[] R = new int[n2];

        System.arraycopy(arr, left, L, 0, n1);
        System.arraycopy(arr, mid + 1, R, 0, n2);

        int i = 0, j = 0, k = left;

        while(i < n1 && j < n2) {
            comparacoes++;
            if(L[i] <= R[j]) {
                arr[k++] = L[i++];
                trocas++;
            } else {
                arr[k++] = R[j++];
                trocas++;
            }
        }

        while(i < n1) {
            arr[k++] = L[i++];
            trocas++;
        }

        while(j < n2) {
            arr[k++] = R[j++];
            trocas++;
        }
    }

    /**
     * Implementação do Heap Sort para inteiros.
     * Constroi um heap máximo e extrai os elementos ordenados.
     */
    public static void heapSort(int[] arr) {
        comparacoes = 0; trocas = 0;
        int n = arr.length;

        // Constroi a heap (max heap)
        for(int i = n / 2 - 1; i >= 0; i--) {
            heapify(arr, n, i);
        }

        // Extrai os elementos um a um da heap
        for(int i = n - 1; i >= 0; i--) {
            trocas++;
            int temp = arr[0];
            arr[0] = arr[i];
            arr[i] = temp;

            heapify(arr, i, 0);
        }
    }

    /**
     * Refaz o heap para manter a propriedade depois da troca.
     * Contabiliza comparações e trocas.
     */
    private static void heapify(int[] arr, int n, int i) {
        int largest = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;

        if(left < n) {
            comparacoes++;
            if(arr[left] > arr[largest]) {
                largest = left;
            }
        }

        if(right < n) {
            comparacoes++;
            if(arr[right] > arr[largest]) {
                largest = right;
            }
        }

        if(largest != i) {
            trocas++;
            int swap = arr[i];
            arr[i] = arr[largest];
            arr[largest] = swap;

            heapify(arr, n, largest);
        }
    }

    /**
     * Verifica se o array de inteiros está ordenado de forma crescente.
     */
    public static boolean isSorted(int[] arr) {
        for(int i = 0; i < arr.length - 1; i++) {
            if(arr[i] > arr[i+1]) return false;
        }
        return true;
    }

    // -----------------------------------------------------
    // Versões para arrays de String[], similares às anteriores,
    // com comparação ignorando case para ordenar alfabeticamente

    /**
     * Bubble Sort para strings, incrementa comparações e trocas.
     */
    public static void bubbleSortStrings(String[] arr) {
        comparacoes = 0; trocas = 0;
        int n = arr.length;
        for(int i = 0; i < n - 1; i++) {
            for(int j = 0; j < n - 1 - i; j++) {
                comparacoes++;
                if(arr[j].compareToIgnoreCase(arr[j+1]) > 0) {
                    trocas++;
                    String temp = arr[j];
                    arr[j] = arr[j+1];
                    arr[j+1] = temp;
                }
            }
        }
    }

    /**
     * Quick Sort para strings, método recursivo usando partitionStrings.
     */
    public static void quickSortStrings(String[] arr, int low, int high) {
        if(low < high) {
            int pi = partitionStrings(arr, low, high);
            quickSortStrings(arr, low, pi - 1);
            quickSortStrings(arr, pi + 1, high);
        }
    }

    /**
     * Particiona array de strings para Quick Sort considerando case ignorado,
     * atualizando contadores.
     */
    private static int partitionStrings(String[] arr, int low, int high) {
        String pivot = arr[high];
        int i = (low - 1);
        for(int j = low; j < high; j++) {
            comparacoes++;
            if(arr[j].compareToIgnoreCase(pivot) <= 0) {
                i++;
                trocas++;
                String temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }
        trocas++;
        String temp = arr[i+1];
        arr[i+1] = arr[high];
        arr[high] = temp;
        return i+1;
    }

    /**
     * Merge Sort para strings, recursivo, chamando mergeStrings.
     */
    public static void mergeSortStrings(String[] arr, int left, int right) {
        if(left < right) {
            int mid = (left + right) / 2;
            mergeSortStrings(arr, left, mid);
            mergeSortStrings(arr, mid + 1, right);
            mergeStrings(arr, left, mid, right);
        }
    }

    /**
     * Mescla as partes do vetor string para ordenação,
     * contabilizando comparações e trocas.
     */
    private static void mergeStrings(String[] arr, int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        String[] L = new String[n1];
        String[] R = new String[n2];

        System.arraycopy(arr, left, L, 0, n1);
        System.arraycopy(arr, mid + 1, R, 0, n2);

        int i = 0, j = 0, k = left;

        while(i < n1 && j < n2) {
            comparacoes++;
            if(L[i].compareToIgnoreCase(R[j]) <= 0) {
                arr[k++] = L[i++];
                trocas++;
            } else {
                arr[k++] = R[j++];
                trocas++;
            }
        }

        while(i < n1) {
            arr[k++] = L[i++];
            trocas++;
        }

        while(j < n2) {
            arr[k++] = R[j++];
            trocas++;
        }
    }

    /**
     * Heap Sort para array de strings, usa heapifyStrings.
     */
    public static void heapSortStrings(String[] arr) {
        comparacoes = 0; trocas = 0;
        int n = arr.length;

        // Constroi heap
        for(int i = n / 2 - 1; i >= 0; i--) {
            heapifyStrings(arr, n, i);
        }

        // Remove elemento da heap um por um
        for(int i = n - 1; i >= 0; i--) {
            trocas++;
            String temp = arr[0];
            arr[0] = arr[i];
            arr[i] = temp;

            heapifyStrings(arr, i, 0);
        }
    }

    /**
     * Reorganiza o heap para manter a propriedade máxima,
     * aplicando para array de strings e contabilizando métricas.
     */
    private static void heapifyStrings(String[] arr, int n, int i) {
        int largest = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;

        if(left < n) {
            comparacoes++;
            if(arr[left].compareToIgnoreCase(arr[largest]) > 0) {
                largest = left;
            }
        }

        if(right < n) {
            comparacoes++;
            if(arr[right].compareToIgnoreCase(arr[largest]) > 0) {
                largest = right;
            }
        }

        if(largest != i) {
            trocas++;
            String swap = arr[i];
            arr[i] = arr[largest];
            arr[largest] = swap;

            heapifyStrings(arr, n, largest);
        }
    }
}
