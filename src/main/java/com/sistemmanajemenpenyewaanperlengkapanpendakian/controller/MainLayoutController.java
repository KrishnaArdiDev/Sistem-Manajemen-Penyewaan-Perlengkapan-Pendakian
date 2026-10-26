package com.sistemmanajemenpenyewaanperlengkapanpendakian.controller;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.App;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.*;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.dao.*;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.exception.*;

import javafx.scene.image.Image;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

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
import javafx.geometry.Insets;
import javafx.scene.Parent;

public class MainLayoutController {

//#region Sumber foto profil bersama

    private static final ObjectProperty<Image> sharedProfileImage =
            new SimpleObjectProperty<>();

    private static String sharedProfilePhotoUri;

    public static ObjectProperty<Image> sharedProfileImageProperty() {
        initializeSharedProfileImage();
        return sharedProfileImage;
    }

    public static String getSharedProfilePhotoUri() {
        initializeSharedProfileImage();
        return sharedProfilePhotoUri;
    }

    public static void initializeSharedProfileImage() {
        if (sharedProfileImage.get() != null) {
            return;
        }

        java.net.URL resource =
                App.class.getResource("/images/profile.png");

        if (resource != null) {
            String uri = resource.toExternalForm();
            Image image = new Image(uri, false);

            if (!image.isError()) {
                sharedProfilePhotoUri = uri;
                sharedProfileImage.set(image);
            }
        }
    }

    public static void setSharedProfilePhoto(String uri) {
        if (uri == null || uri.trim().isEmpty()) {
            return;
        }

        Image image = new Image(uri, false);

        if (!image.isError()) {
            sharedProfilePhotoUri = uri;
            sharedProfileImage.set(image);
        }
    }

//#endregion

//#region Ini adalah section FXML untuk memanggil field

    private Button tombolAktif;

    @FXML
    private VBox paneVbox;

    @FXML
    private Button tombolProfile;

    @FXML
    private Circle borderProfile;

    @FXML
    private Label labelWelcome;

    @FXML
    private VBox welcomeContainer;

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

    @FXML
    void initialize() {
        // Foto profil sidebar menggunakan sumber foto bersama.
        initializeSharedProfileImage();

        if (!fotoProfile.imageProperty().isBound()) {
            fotoProfile.imageProperty().bind(sharedProfileImage);
        }

        // Teks sambutan di bagian kiri bawah sidebar.
        labelWelcome.setText("WELCOME");
        labelUsername.setText("ADMIN TOKO JAYAA");

        styleSidebar();
        aturTombolAktif(tombolDashboard);

        styleHeader();

        try {
            tampilkanHalaman("dashboard");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//#region Styling

    private void styleSidebar() {
        paneVbox.setStyle(
            "-fx-background-color: #FFFFFF;" +
            "-fx-border-color: transparent #2F5B3A transparent transparent;" +
            "-fx-border-width: 0 1 0 0;" +
            "-fx-background-radius: 14;" +
            "-fx-border-radius: 14;" +
            "-fx-background-insets: 0;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 12, 0,2,2);"
        );
    }

    private void ikonAktifSide(
            ImageView ikon,
            boolean aktif,
            String ikonNormal,
            String ikonAktif) {

        String path = aktif ? ikonAktif : ikonNormal;

        ikon.setImage(
            new Image(App.class.getResource(path).toExternalForm())
        );
    }

    private void aturTombolAktif(Button dipilih) {
        System.out.println("Menu dipilih: " + dipilih.getText());

        tombolAktif = dipilih;

        List<Button> tombolNavigasi = Arrays.asList(
            tombolDashboard,
            tombolDataPerleng,
            tombolKeluar,
            tombolPengembalian,
            tombolTransaksi,
            tombolProfile
        );

        for (Button tombol : tombolNavigasi) {
            if (tombol == tombolAktif) {
                tombol.setStyle(
                    "-fx-background-color: #005A32;" +
                    "-fx-text-fill: #FFFFFF;" +
                    "-fx-background-radius: 10;" +
                    "-fx-border-color: transparent;"
                );

                if (tombolAktif == tombolProfile) {
                    labelUsername.setStyle("-fx-text-fill: #FFFFFF;");
                    labelWelcome.setStyle("-fx-text-fill: #FFFFFF;");
                    borderProfile.setStyle("-fx-stroke: #FFFFFF;");
                }

            } else {
                tombol.setStyle(
                    "-fx-background-color: transparent;" +
                    "-fx-text-fill: #005A32;" +
                    "-fx-background-radius: 10;" +
                    "-fx-border-color: transparent;"
                );
            }
        }

        // PERBAIKAN:
        // Warna tulisan profil harus selalu disesuaikan dengan
        // menu yang sedang aktif, bukan hanya ketika Profile dipilih.
        boolean profileAktif = tombolAktif == tombolProfile;

        String warnaTeks = profileAktif
                ? "#FFFFFF"
                : "#005A32";

        String warnaBorder = profileAktif
                ? "#FFFFFF"
                : "#2F5B3A";

        labelWelcome.setStyle(
            "-fx-text-fill: " + warnaTeks + ";"
        );

        labelUsername.setStyle(
            "-fx-text-fill: " + warnaTeks + ";"
        );

        borderProfile.setStyle(
            "-fx-stroke: " + warnaBorder + ";"
        );

        // Latar panel profil mengikuti menu yang aktif.
        if (profileAktif) {
            welcomeContainer.setStyle(
                "-fx-background-color: #005A32;" +
                "-fx-background-radius: 12;"
            );
        } else {
            welcomeContainer.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-background-radius: 12;"
            );
        }

        ikonAktifSide(
            iconDashboard,
            tombolAktif == tombolDashboard,
            "/images/logo-home-hijau.png",
            "/images/logo-home-putih.png"
        );

        ikonAktifSide(
            iconDataPerlengkapan,
            tombolAktif == tombolDataPerleng,
            "/images/logo-dataPerlengkapan-hijau.png",
            "/images/logo-dataPerlengkapan-putih.png"
        );

        ikonAktifSide(
            iconPengembalian,
            tombolAktif == tombolPengembalian,
            "/images/logo-pengembalian-hijau.png",
            "/images/logo-pengembalian-putih.png"
        );

        ikonAktifSide(
            iconTransaksi,
            tombolAktif == tombolTransaksi,
            "/images/logo-transaksi-hijau.png",
            "/images/logo-transaksi-putih.png"
        );

        ikonAktifSide(
            iconKeluar,
            tombolAktif == tombolKeluar,
            "/images/logo-keluar-hijau.png",
            "/images/logo-keluar-hijau.png"
        );
    }

    private void styleHeader() {
        headerContainer.setStyle(
            "-fx-background-color: #FFFFFF;" +
            "-fx-background-radius: 14;"
        );

        searchContainer.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-border-color: #2F5B3A;" +
            "-fx-background-radius: 24;" +
            "-fx-border-radius: 24;" +
            "-fx-border-width: 1.5;" +
            "-fx-padding: 0 12 0 12;" +
            "-fx-spacing: 8;"
        );

        searchField.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-border-color: transparent;" +
            "-fx-background-insets: 0, 0;" +
            "-fx-background-radius: 0,0;" +
            "-fx-border-insets: 0;" +
            "-fx-padding: 8 0 8 0;" +
            "-fx-focus-color: transparent;" +
            "-fx-faint-focus-color: transparent;"
        );

        tombolNotifikasi.setStyle(
            "-fx-background-color: #FFFFFF;" +
            "-fx-background-radius: 8;" +
            "-fx-background-color: transparent;" +
            "-fx-border-radius: 10;" +
            "-fx-focus-color: transparent;" +
            "-fx-faint-focus-color: transparent;"
        );
    }

//#endregion

    private void tampilkanHalaman(String namaFxml)
            throws IOException {

        String path =
            "/com/sistemmanajemenpenyewaanperlengkapanpendakian/fxml/pages/"
            + namaFxml + ".fxml";

        FXMLLoader loader =
            new FXMLLoader(App.class.getResource(path));

        Parent halaman = loader.load();

        BorderPane.setMargin(halaman, new Insets(16));
        layoutPage.setCenter(halaman);
    }

    @FXML
    private void bukaDashboard() throws IOException {
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
        System.out.println("Tombol Transaksi diklik");

        tampilkanHalaman("transaksi");
        aturTombolAktif(tombolTransaksi);
    }

    @FXML
    private void bukaProfile() throws IOException {
        tampilkanHalaman("profile");
        aturTombolAktif(tombolProfile);
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