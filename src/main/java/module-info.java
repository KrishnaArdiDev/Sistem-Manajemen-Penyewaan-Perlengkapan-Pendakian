module com.sistemmanajemenpenyewaanperlengkapanpendakian {

    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    requires transitive javafx.graphics;

    exports com.sistemmanajemenpenyewaanperlengkapanpendakian;

    exports com.sistemmanajemenpenyewaanperlengkapanpendakian.controller
        to javafx.fxml;

    opens com.sistemmanajemenpenyewaanperlengkapanpendakian
        to javafx.fxml;

    opens com.sistemmanajemenpenyewaanperlengkapanpendakian.controller
        to javafx.fxml;
}