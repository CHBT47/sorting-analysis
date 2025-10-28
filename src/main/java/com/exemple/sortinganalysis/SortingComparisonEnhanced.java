package com.exemple.sortinganalysis;

import java.io.*;
import java.util.*;

public class SortingComparisonEnhanced {

    public static long comparacoes;
    public static long trocas;

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

    public static void bubbleSort(int[] arr) {
        comparacoes = 0; trocas = 0;
        int n = arr.length;
        for(int i = 0; i < n - 1; i++) {
            for(int j = 0; j < n - 1 - i; j++) {
                comparacoes++;
                if(arr[j] > arr[j+1]) {
                    trocas++;
                    int temp = arr[j];
                    arr[j] = arr[j+1];
                    arr[j+1] = temp;
                }
            }
        }
    }

    // QuickSort, MergeSort, HeapSort mantido igual ao original, sem mudanças

    public static void quickSort(int[] arr, int low, int high) {
        if(low < high) {
            int pi = partition(arr, low, high);
            quickSort(arr, low, pi - 1);
            quickSort(arr, pi + 1, high);
        }
    }

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

    public static void mergeSort(int[] arr, int left, int right) {
        if(left < right) {
            int mid = (left + right) / 2;
            mergeSort(arr, left, mid);
            mergeSort(arr, mid + 1, right);
            merge(arr, left, mid, right);
        }
    }

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

    public static void heapSort(int[] arr) {
        comparacoes = 0; trocas = 0;
        int n = arr.length;

        for(int i = n / 2 - 1; i >= 0; i--) {
            heapify(arr, n, i);
        }

        for(int i = n - 1; i >= 0; i--) {
            trocas++;
            int temp = arr[0];
            arr[0] = arr[i];
            arr[i] = temp;

            heapify(arr, i, 0);
        }
    }

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

    public static boolean isSorted(int[] arr) {
        for(int i = 0; i < arr.length - 1; i++) {
            if(arr[i] > arr[i+1]) return false;
        }
        return true;
    }
}
