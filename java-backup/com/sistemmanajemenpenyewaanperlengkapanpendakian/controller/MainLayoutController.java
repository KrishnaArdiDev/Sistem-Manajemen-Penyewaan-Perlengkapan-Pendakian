
package com.sistemmanajemenpenyewaanperlengkapanpendakian.controller;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.App;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

public class MainLayoutController {

    private static MainLayoutController instance;

    private Button tombolAktif;

    @FXML private VBox paneVbox;
    @FXML private Button tombolDashboard;
    @FXML private Button tombolDataPerleng;
    @FXML private Button tombolPengembalian;
    @FXML private Button tombolTransaksi;
    @FXML private Button tombolKeluar;
    @FXML private Button tombolProfile;

    @FXML private ImageView iconDashboard;
    @FXML private ImageView iconDataPerlengkapan;
    @FXML private ImageView iconPengembalian;
    @FXML private ImageView iconTransaksi;
    @FXML private ImageView iconKeluar;
    @FXML private ImageView fotoProfile;

    @FXML private BorderPane layoutPage;
    @FXML private HBox headerContainer;
    @FXML private HBox searchContainer;
    @FXML private TextField searchField;
    @FXML private HBox headerAction;
    @FXML private Button tombolNotifikasi;

    @FXML
    private void initialize() {
        instance = this;

        styleSidebar();
        styleHeader();
        muatFotoSidebar();

        if (tombolDashboard != null) {
            aturTombolAktif(tombolDashboard);
        }

        try {
            tampilkanHalaman("dashboard");
        } catch (IOException e) {
            tampilkanError("Gagal membuka dashboard", e);
        }
    }

    public static void perbaruiFotoProfil(Image foto) {
        if (instance == null || instance.fotoProfile == null) {
            return;
        }

        Platform.runLater(() -> {
            instance.fotoProfile.setImage(foto);
            instance.aturClipFotoBulat();
        });
    }

    private void aturClipFotoBulat() {
        if (fotoProfile == null) return;

        double ukuran = Math.min(
            fotoProfile.getFitWidth(),
            fotoProfile.getFitHeight()
        );

        if (ukuran <= 0) ukuran = 44;

        fotoProfile.setPreserveRatio(false);
        fotoProfile.setFitWidth(ukuran);
        fotoProfile.setFitHeight(ukuran);
        fotoProfile.setClip(
            new Circle(ukuran / 2, ukuran / 2, ukuran / 2)
        );
    }

    private void styleSidebar() {
        if (paneVbox == null) return;

        paneVbox.setStyle(
            "-fx-background-color: #FFFFFF;" +
            "-fx-border-color: transparent #2F5B3A transparent transparent;" +
            "-fx-border-width: 0 1 0 0;" +
            "-fx-background-radius: 14;" +
            "-fx-border-radius: 14;" +
            "-fx-background-insets: 0;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 10, 0, 1, 1);"
        );
    }

    private void styleHeader() {
        if (headerContainer != null) {
            headerContainer.setStyle("-fx-background-color: white;");
        }

        if (searchContainer != null) {
            searchContainer.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: #2F5B3A;" +
                "-fx-background-radius: 24;" +
                "-fx-border-radius: 24;" +
                "-fx-border-width: 1.5;" +
                "-fx-padding: 0 12 0 12;"
            );
        }

        if (searchField != null) {
            searchField.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-border-color: transparent;" +
                "-fx-focus-color: transparent;" +
                "-fx-faint-focus-color: transparent;"
            );
        }

        if (tombolNotifikasi != null) {
            tombolNotifikasi.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-cursor: hand;"
            );
        }
    }

    private void muatFotoSidebar() {
        if (fotoProfile == null) return;

        Image foto = ProfileController.getFotoProfilSaatIni();

        if (foto != null && !foto.isError()) {
            fotoProfile.setImage(foto);
            aturClipFotoBulat();
            return;
        }

        try {
            Path folder = Path.of("data", "profile");

            if (Files.isDirectory(folder)) {
                try (var daftar = Files.list(folder)) {
                    Path file = daftar
                        .filter(Files::isRegularFile)
                        .filter(p -> p.getFileName().toString()
                            .toLowerCase()
                            .matches("profile\\.(png|jpg|jpeg)"))
                        .findFirst()
                        .orElse(null);

                    if (file != null) {
                        Image image = new Image(
                            file.toUri().toString(),
                            false
                        );

                        if (!image.isError()) {
                            fotoProfile.setImage(image);
                            aturClipFotoBulat();
                            return;
                        }
                    }
                }
            }

            URL resource = App.class.getResource(
                "/com/sistemmanajemenpenyewaanperlengkapanpendakian/images/profile.png"
            );

            if (resource != null) {
                fotoProfile.setImage(
                    new Image(resource.toExternalForm(), false)
                );
            }

            aturClipFotoBulat();
        } catch (Exception e) {
            System.err.println(
                "Foto sidebar gagal dimuat: " + e.getMessage()
            );
        }
    }

    private void ikonAktifSide(
            ImageView ikon,
            boolean aktif,
            String normal,
            String aktifPath) {

        if (ikon == null) return;

        URL resource = App.class.getResource(
            aktif ? aktifPath : normal
        );

        if (resource != null) {
            ikon.setImage(new Image(resource.toExternalForm()));
        }
    }

    private void aturTombolAktif(Button dipilih) {
        if (dipilih == null) return;

        tombolAktif = dipilih;

        List<Button> navigasi = Arrays.asList(
            tombolDashboard,
            tombolDataPerleng,
            tombolPengembalian,
            tombolTransaksi
        );

        for (Button tombol : navigasi) {
            if (tombol == null) continue;

            boolean aktif = tombol == dipilih;

            tombol.setStyle(
                "-fx-background-color: " +
                (aktif ? "#E5F3EB;" : "transparent;") +
                "-fx-text-fill: #005A32;" +
                "-fx-font-weight: " +
                (aktif ? "bold;" : "normal;") +
                "-fx-background-radius: 10;" +
                "-fx-border-color: transparent;" +
                "-fx-cursor: hand;" +
                "-fx-alignment: CENTER_LEFT;" +
                "-fx-padding: 0 12 0 12;"
            );
        }

        ikonAktifSide(
            iconDashboard,
            dipilih == tombolDashboard,
            "/images/logo-home-hijau.png",
            "/images/logo-home-putih.png"
        );

        ikonAktifSide(
            iconDataPerlengkapan,
            dipilih == tombolDataPerleng,
            "/images/logo-dataPerlengkapan-hijau.png",
            "/images/logo-dataPerlengkapan-putih.png"
        );

        ikonAktifSide(
            iconPengembalian,
            dipilih == tombolPengembalian,
            "/images/logo-pengembalian-hijau.png",
            "/images/logo-pengembalian-putih.png"
        );

        ikonAktifSide(
            iconTransaksi,
            dipilih == tombolTransaksi,
            "/images/logo-transaksi-hijau.png",
            "/images/logo-transaksi-putih.png"
        );
    }

    private void tampilkanHalaman(String namaFxml) throws IOException {
        String path =
            "/com/sistemmanajemenpenyewaanperlengkapanpendakian/fxml/"
            + namaFxml + ".fxml";

        URL resource = App.class.getResource(path);

        if (resource == null) {
            throw new IOException("File FXML tidak ditemukan: " + path);
        }

        Parent halaman = new FXMLLoader(resource).load();

        BorderPane.setMargin(halaman, new Insets(16));
        layoutPage.setCenter(halaman);
    }

    private void navigasi(String halaman, Button tombol) {
        try {
            tampilkanHalaman(halaman);
            aturTombolAktif(tombol);
        } catch (IOException e) {
            tampilkanError("Gagal membuka halaman " + halaman, e);
        }
    }

    @FXML
    private void bukaDashboard() {
        navigasi("dashboard", tombolDashboard);
    }

    @FXML
    private void bukaDataPerleng() {
        navigasi("perlengkapan", tombolDataPerleng);
    }

    @FXML
    private void bukaPerlengkapan() {
        bukaDataPerleng();
    }

    @FXML
    private void bukaPengembalian() {
        navigasi("pengembalian", tombolPengembalian);
    }

    @FXML
    private void bukaTransaksi() {
        navigasi("transaksi", tombolTransaksi);
    }

    @FXML
    private void bukaProfile() {
        try {
            tampilkanHalaman("profile");
            Platform.runLater(this::muatFotoSidebar);
        } catch (IOException e) {
            tampilkanError("Gagal membuka halaman profil", e);
        }
    }

    @FXML
    private void keluar() throws IOException {
        App.setRoot("login");
    }

    @FXML
    private void keluarAplikasi() throws IOException {
        keluar();
    }

    @FXML
    private void switchToPrimary() throws IOException {
        keluar();
    }

    private void tampilkanError(String pesan, Exception e) {
        e.printStackTrace();

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Kesalahan TAPAK");
        alert.setHeaderText(pesan);
        alert.setContentText(
            e.getMessage() == null
                ? "Terjadi kesalahan yang tidak diketahui."
                : e.getMessage()
        );
        alert.showAndWait();
    }
}
