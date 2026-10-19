
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
    }

//#region

    private void styleSidebar(){
        paneVbox.setStyle(
            "-fx-background-color: #FFFFFF;" +
            "-fx-border-color: transparent #2F5B3A transparent transparent;" +
            "-fx-border-width: 0 1 0 0;"
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
