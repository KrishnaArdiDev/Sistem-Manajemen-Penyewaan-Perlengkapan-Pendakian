module com.sistemmanajemenpenyewaanperlengkapanpendakian {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql; // <-- ini harus ada biar bisa connect ke sql
    requires transitive javafx.graphics;

    opens com.sistemmanajemenpenyewaanperlengkapanpendakian to javafx.fxml;
    exports com.sistemmanajemenpenyewaanperlengkapanpendakian;
}