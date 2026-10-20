
package com.sistemmanajemenpenyewaanperlengkapanpendakian.controller;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.App;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.*;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.dao.*;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.exception.*;

import javafx.scene.image.Image;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

import java.lang.RuntimeException;

import javafx.fxml.FXML;
import javafx.collections.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;


public class MainLayoutController {
//#region ini adalah section fxml untuk manggil field
    private Button tombolAktif;

    @FXML
    private VBox paneVbox;

    @FXML
    private VBox sidebarContainer;
    @FXML
    private Button tombolDashboard;

    @FXML
    private Button tombolDataPerleng;

    @FXML
    private Button tombolPengembalian;

    @FXML
    private Button tombolTransaksi;

    @FXML
    private Button tombolKeluar;

    @FXML
    private ImageView iconDashboard;

    @FXML
    private ImageView iconDataPerlengkapan;

    @FXML
    private ImageView iconPengembalian;

    @FXML
    private ImageView iconTransaksi;

    @FXML
    private ImageView iconKeluar;

    @FXML
    private ImageView fotoProfile;

    @FXML
    private Label labelUsername;

    @FXML
    private BorderPane layoutPage;

    @FXML
    private HBox headerContainer;

    @FXML
    private HBox searchContainer;

    @FXML
    private ImageView searchIcon;

    @FXML
    private TextField searchField;

    @FXML
    private HBox headerAction;

    @FXML
    private Button tombolNotifikasi;

    @FXML
    private ImageView iconNotifikasi;

    @FXML
    private ImageView elementHeader;
//#endregion

    @FXML void initialize(){
        styleSidebar();
        aturTombolAktif(tombolDashboard);
        
        styleHeader();

        try{
            tampilkanHalaman("dashboard");
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

//#region

    private void styleSidebar(){
        paneVbox.setStyle(
            "-fx-background-color: #FFFFFF;" +
            "-fx-border-color: transparent #2F5B3A transparent transparent;" +
            "-fx-border-width: 0 1 0 0;"+
            "-fx-background-radius: 14;"+
            "-fx-border-radius:14;"+
            "-fx-background-insets:0;"+
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 12, 0,2,2);"
        );
    }

    private void aturTombolAktif(Button dipilih){
        System.out.println("Menu dipilih: " + dipilih.getText());
        tombolAktif = dipilih;

        List<Button> tombolNavigasi = Arrays.asList(
            tombolDashboard, tombolDataPerleng, tombolKeluar,
            tombolPengembalian, tombolTransaksi
        );

        for (Button tombol: tombolNavigasi){
            if (tombol == tombolAktif){
                tombol.setStyle(
                    "-fx-background-color: #005A32;"+
                    "-fx-text-fill: #FFFFFF;"+
                    "-fx-background-radius: 10;"+
                    "-fx-border-color:transparent;"
                );
            } else {
                tombol.setStyle(
                    "-fx-background-color: transparent;"+
                    "-fx-text-fill: #005A32;"+
                    "-fx-background-radius: 10;"+
                    "-fx-border-color:transparent;"
                );
            }
        }
    }

    private void styleHeader(){
        headerContainer.setStyle(
            "-fx-background-color: #FFFFFF;"+
            "-fx-background-radius: 14;"
        );

        searchContainer.setStyle(
            "-fx-background-color: transparent;"+
            "-fx-border-color: #2F5B3A;"+
            "-fx-background-radius:24;"+
            "-fx-border-radius:24;"+
            "-fx-border-width: 1.5;"+
            "-fx-padding: 0 12 0 12;"+
            "-fx-spacing: 8;"
        );
        searchField.setStyle(
            "-fx-background-color: transparent;"+
            "-fx-border-color: transparent;"+
            "-fx-background-insets: 0, 0;" +
            "-fx-background-radius: 0,0;"+
            "-fx-border-insets: 0;"+
            "-fx-padding: 8 0 8 0;"+
            "-fx-focus-color: transparent;"+
            "-fx-faint-focus-color: transparent;"
        );

        tombolNotifikasi.setStyle(
            "-fx-background-color: #FFFFFF;"+
            "-fx-background-radius: 8;"+
            "-fx-background-color: transparent;"+
            "-fx-border-radius: 10;"+
            "-fx-focus-color: transparent;"+
            "-fx-faint-focus-color: transparent;"
        );
    }

    

//#endregion
    private void tampilkanHalaman(String namaFxml) throws IOException {
    String path = "/com/sistemmanajemenpenyewaanperlengkapanpendakian/fxml/"
            + namaFxml + ".fxml";

    FXMLLoader loader = new FXMLLoader(App.class.getResource(path));
    Parent halaman = loader.load();

    layoutPage.setCenter(halaman);
    }

    @FXML 
    private void bukaDashboard() throws IOException{
        tampilkanHalaman("dashboard");
        aturTombolAktif(tombolDashboard);
    }
    @FXML
    private void bukaDataPerleng() throws IOException {
        tampilkanHalaman("perlengkapan");
        aturTombolAktif(tombolDataPerleng);
    }
    
    @FXML
    private void bukaPengembalian() throws IOException {
        tampilkanHalaman("pengembalian");
        aturTombolAktif(tombolPengembalian);
    }
    
    @FXML
    private void bukaTransaksi() throws IOException {
        tampilkanHalaman("transaksi");
        aturTombolAktif(tombolTransaksi);
    }
    
    @FXML
    private void keluar() throws IOException {
        App.setRoot("login");
        aturTombolAktif(tombolKeluar);
    }
    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("login");
    }
}
