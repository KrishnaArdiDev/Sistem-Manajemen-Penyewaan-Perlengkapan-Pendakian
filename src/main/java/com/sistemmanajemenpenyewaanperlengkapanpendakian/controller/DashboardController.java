
package com.sistemmanajemenpenyewaanperlengkapanpendakian.controller;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.App;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.*;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.dao.*;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.exception.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

import javafx.fxml.FXML;
import javafx.collections.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;

public class DashboardController {
//#region
    @FXML 
    private VBox containerDashboard;

    @FXML 
    private VBox cardBarangTersedia;

    @FXML 
    private VBox cardBarangDisewakan;

    @FXML 
    private VBox cardSeluruhBarang;

    @FXML 
    private VBox productSectionContainer;
//#endregion
    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("login");
    }

    @FXML void initialize(){
        styleContainerDashboard();
        styleCardTotalTersedia();
        styleCardDisewa();
        styleCardTotalSeluruhBarang();
        styleDaftarBarang();
    }

    private void styleContainerDashboard(){
        containerDashboard.setStyle(
            "-fx-border-radius: 14;"+
            "-fx-background-color: transparent;"+
            "-fx-background-radius: 14;"
        );
    }

    private void styleCardTotalTersedia(){
        cardBarangTersedia.setStyle(
            "-fx-border-color: #005A32;"+
            "-fx-border-radius:14;"+
            "-fx-background-radius:14;"+
            "-fx-background-color: #E2FBE0;"
        );
    }
    private void styleCardDisewa(){
        cardBarangDisewakan.setStyle(
            "-fx-border-color: #005A32;"+
            "-fx-border-radius:14;"+
            "-fx-background-radius:14;"+
            "-fx-background-color: #E2FBE0;"
        );
    }
    private void styleCardTotalSeluruhBarang(){
        cardSeluruhBarang.setStyle(
            "-fx-border-color: #005A32;"+
            "-fx-border-radius:14;"+
            "-fx-background-radius:14;"+
            "-fx-background-color: #E2FBE0;"
        );
    }

    private void styleDaftarBarang(){
        productSectionContainer.setStyle(
            "-fx-background-color: #FFFFFF;"+
            "-fx-background-radius:14;"

        );
    }
}



