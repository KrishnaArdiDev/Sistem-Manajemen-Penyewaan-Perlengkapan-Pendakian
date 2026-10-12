package com.sistemmanajemenpenyewaanperlengkapanpendakian.controller;

import java.io.IOException;
import javafx.fxml.FXML;

public class DashboardController {

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("login");
    }
}