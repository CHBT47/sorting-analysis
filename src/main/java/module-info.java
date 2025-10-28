module com.exemple.sortinganalysis {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.exemple.sortinganalysis to javafx.fxml;
    exports com.exemple.sortinganalysis;
}