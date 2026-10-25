
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
//#region manggil komponen
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

    @FXML 
    private HBox itemProduk1;

    @FXML 
    private Region indikatorStatusProduk;
    @FXML 
    private Button btnDetail;
    @FXML 
    private Button btnKonfirmasi;
    @FXML 
    private Label labelJudulDaftarProduk;

    @FXML 
    private Label labelNamaPenyewa;
    @FXML 
    private Label labelNamaPerlengkapan;
    @FXML 
    private Label labelTanggalSewa;
    @FXML 
    private Label labelPemisahTanggalSewa;
    @FXML 
    private Label labelTanggalSelesaiSewa;
    @FXML 
    private Label labelStatusProduk;
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
        styleItemProduk();
        styleTombolProduk();
        styleLabelJudulDaftarProduk();
        styleTeksProduk();
        styleLabelStatusProduk("#FFFFFF", "#861010", "#861010");
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
            "-fx-border-color: #861010;"+
            "-fx-border-radius:14;"+
            "-fx-background-radius:14;"+
            "-fx-background-color: #F8DEDE;"
        );
    }
    private void styleCardTotalSeluruhBarang(){
        cardSeluruhBarang.setStyle(
            "-fx-border-color: #122A7A;"+
            "-fx-border-radius:14;"+
            "-fx-background-radius:14;"+
            "-fx-background-color: #D1DDFE;"
        );
    }

    private void styleDaftarBarang(){
        productSectionContainer.setStyle(
            "-fx-background-color: #FFFFFF;"+
            "-fx-background-radius:14;"

        );
    }

    private  void styleItemProduk(){
        itemProduk1.setStyle(
            "-fx-background-color: #F5F5F5;"+
            "-fx-background-radius:12;"
        );
    }
    private void styleTombolProduk(){
        btnDetail.setStyle(
            "-fx-background-color: #FFFFFF;"+
            "-fx-border-color: #005A32;"+
            "-fx-border-radius: 14;"+
            "-fx-background-radius: 14;"+
            "-fx-text-fill: #005A32;"
            
        );
        btnKonfirmasi.setStyle(
            "-fx-background-color: #005A32;"+
            "-fx-border-color: #005A32;"+
            "-fx-background-radius: 14;"+
            "-fx-border-radius: 14;"+
            "-fx-text-fill: #FFFFFF;"
        );
    }
    
    private void styleLabelJudulDaftarProduk(){
        labelJudulDaftarProduk.setStyle(
            "-fx-text-fill: #005A32;"
        );
    }
    private void styleTeksProduk(){
        labelNamaPerlengkapan.setStyle(
            "-fx-font-size: 18px;"+
            "-fx-text-fill:#000000;"+
            "-fx-font-weight: Bold;"
        );
        labelNamaPenyewa.setStyle(
            "-fx-font-size: 14px;"+
            "-fx-text-fill:#868686;"
        );
        labelPemisahTanggalSewa.setStyle(
            "-fx-font-size: 14px;"+
            "-fx-text-fill:#868686;"
        );
        labelTanggalSelesaiSewa.setStyle(
            "-fx-font-size: 14px;"+
            "-fx-text-fill:#868686;"
        );
        labelTanggalSewa.setStyle(
            "-fx-font-size: 14px;"+
            "-fx-text-fill:#868686;"
        );
    }

    private void styleLabelStatusProduk(String warnaBg, String warnaBorder, String warnaTeks){
        labelStatusProduk.setStyle(
            "-fx-border-color: " +warnaBorder+";"+
            "-fx-background-color: "+warnaBg+";"+
            "-fx-border-radius: 14;"+
            "-fx-background-radius: 14;"+
            "-fx-text-fill: "+warnaTeks+";"
        );
    }
}