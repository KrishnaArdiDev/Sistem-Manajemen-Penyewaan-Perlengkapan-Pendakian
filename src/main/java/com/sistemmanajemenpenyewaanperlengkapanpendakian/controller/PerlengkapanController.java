
package com.sistemmanajemenpenyewaanperlengkapanpendakian.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;

public class PerlengkapanController {

    @FXML
    private VBox pageRoot;

    private final List<Produk> daftarProduk = new ArrayList<>();
    private String selectedFotoUri;

    @FXML
    private void initialize() {
        isiDataContoh();
        tampilkanDaftar();
    }

    private void isiDataContoh() {
        daftarProduk.clear();

        daftarProduk.add(new Produk(
                "Tas Carrier", "Tas", 45000, 4, "60 L", null));

        daftarProduk.add(new Produk(
                "Kompor Portable", "Kompor", 35000, 3,
                "Portable", null));

        daftarProduk.add(new Produk(
                "Sepatu Uk 40", "Sepatu", 45000, 0, "40", null));

        daftarProduk.add(new Produk(
                "Tas Carrier Puma", "Tas", 45000, 4, "40", null));

        daftarProduk.add(new Produk(
                "Tas Carrier 45L", "Tas", 45000, 3, "45 L", null));

        daftarProduk.add(new Produk(
                "Tas Carrier 50L", "Tas", 45000, 5, "50 L", null));

        daftarProduk.add(new Produk(
                "Kompor Outdoor", "Kompor", 35000, 2,
                "Outdoor", null));

        daftarProduk.add(new Produk(
                "Sepatu Gunung", "Sepatu", 40000, 2, "42", null));
    }

    // =========================================================
    // HALAMAN DAFTAR PERLENGKAPAN
    // =========================================================

    private void tampilkanDaftar() {
        VBox screen = new VBox(14);
        screen.setFillWidth(true);
        screen.setStyle("-fx-background-color: transparent;");

        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Data Perlengkapan");
        title.setStyle(
                "-fx-font-size: 22px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #00643D;"
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button tambahButton = buatTombol(
                "+  Tambah Produk", true, this::tampilkanFormTambah);

        header.getChildren().addAll(title, spacer, tambahButton);

        Label jumlah = new Label(
                daftarProduk.size() + " produk ditampilkan");

        jumlah.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-text-fill: #686868;"
        );

        FlowPane productContainer = new FlowPane();
        productContainer.setHgap(14);
        productContainer.setVgap(18);
        productContainer.setPrefWrapLength(850);
        productContainer.setAlignment(Pos.TOP_LEFT);
        productContainer.setPadding(new Insets(4, 4, 12, 4));

        for (Produk produk : daftarProduk) {
            productContainer.getChildren().add(
                    buatKartuProduk(produk));
        }

        javafx.scene.control.ScrollPane scroll =
                new javafx.scene.control.ScrollPane(productContainer);

        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(
                javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setPannable(true);
        scroll.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-background: transparent;" +
                "-fx-border-color: transparent;"
        );

        VBox.setVgrow(scroll, Priority.ALWAYS);

        screen.getChildren().addAll(header, jumlah, scroll);
        gantiHalaman(screen);
    }

    private VBox buatKartuProduk(Produk produk) {
        VBox kartu = new VBox(9);
        kartu.setPrefWidth(194);
        kartu.setMinWidth(178);
        kartu.setMaxWidth(205);
        kartu.setPadding(new Insets(8));

        kartu.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: #D3D3D3;" +
                "-fx-border-radius: 10;"
        );

        StackPane areaGambar = new StackPane();
        areaGambar.setPrefHeight(145);
        areaGambar.setMinHeight(145);
        areaGambar.setMaxHeight(145);
        areaGambar.setStyle(
                "-fx-background-color: #EEEEEE;" +
                "-fx-background-radius: 7;"
        );

        tampilkanFoto(areaGambar, produk, 160, 135);

        HBox informasi = new HBox(5);
        informasi.setAlignment(Pos.CENTER_LEFT);

        Label nama = new Label(produk.nama);
        nama.setWrapText(true);
        nama.setMaxWidth(100);
        nama.setStyle(
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #333333;"
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label status = new Label(
                produk.stok > 0 ? "Tersedia" : "Dipinjamkan");

        status.setStyle(produk.stok > 0
                ? "-fx-background-color: #C9F5CC;" +
                  "-fx-text-fill: #17652B;" +
                  "-fx-background-radius: 4;" +
                  "-fx-padding: 3 5 3 5;" +
                  "-fx-font-size: 9px;"
                : "-fx-background-color: #F8D7DA;" +
                  "-fx-text-fill: #A3212B;" +
                  "-fx-background-radius: 4;" +
                  "-fx-padding: 3 5 3 5;" +
                  "-fx-font-size: 9px;"
        );

        informasi.getChildren().addAll(nama, spacer, status);

        Label harga = new Label(
                "Rp " + formatRupiah(produk.harga) + " / Hari");

        harga.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #00643D;"
        );

        Button detailButton = buatTombol(
                "Detail", true, () -> tampilkanDetail(produk));

        detailButton.setMaxWidth(Double.MAX_VALUE);

        kartu.getChildren().addAll(
                areaGambar, informasi, harga, detailButton);

        return kartu;
    }

    // =========================================================
    // HALAMAN FORM TAMBAH PERLENGKAPAN
    // =========================================================

    private void tampilkanFormTambah() {
        selectedFotoUri = null;

        VBox screen = new VBox(14);
        screen.setFillWidth(true);

        screen.getChildren().add(
                buatHeader("Data Perlengkapan", this::tampilkanDaftar));

        VBox panel = new VBox(17);
        panel.setPadding(new Insets(25, 32, 25, 32));
        panel.setPrefWidth(760);
        panel.setMaxWidth(820);
        panel.setMaxHeight(Double.MAX_VALUE);
        panel.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 14;"
        );

        Label judul = new Label("FORMULIR TAMBAH PERLENGKAPAN");
        judul.setMaxWidth(Double.MAX_VALUE);
        judul.setAlignment(Pos.CENTER);
        judul.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #00643D;"
        );

        TextField namaField = buatInput("Masukkan nama barang");
        TextField hargaField = buatInput("Contoh: 45000");
        TextField ukuranField = buatInput("Contoh: 40 atau 60 L");
        TextField stokField = buatInput("Masukkan total stok");

        VBox namaFieldBox = buatField("Nama Barang", namaField);
        VBox hargaFieldBox = buatField(
                "Harga Barang/Hari", hargaField);
        VBox ukuranFieldBox = buatField(
                "Ukuran Barang", ukuranField);
        VBox stokFieldBox = buatField("Total Stok", stokField);

        Label namaFoto = new Label("Belum ada foto dipilih");
        namaFoto.setStyle(
                "-fx-font-size: 11px;" +
                "-fx-text-fill: #666666;"
        );

        StackPane uploadArea = buatAreaUpload(namaFoto);
        VBox fotoBox = new VBox(6);
        fotoBox.getChildren().addAll(
                labelForm("Foto Barang"), uploadArea, namaFoto);

        VBox formulir = new VBox(13);
        formulir.getChildren().addAll(
                namaFieldBox,
                hargaFieldBox,
                ukuranFieldBox,
                stokFieldBox,
                fotoBox
        );

        Button batalButton = buatTombol(
                "Batalkan", false, this::tampilkanDaftar);

        Button konfirmasiButton = buatTombol(
                "Konfirmasi",
                true,
                () -> konfirmasiTambah(
                        namaField, hargaField, ukuranField, stokField));

        HBox tombol = new HBox(10, batalButton, konfirmasiButton);
        tombol.setAlignment(Pos.CENTER_RIGHT);

        panel.getChildren().addAll(judul, formulir, tombol);

        HBox holder = new HBox(panel);
        holder.setAlignment(Pos.TOP_CENTER);
        holder.setMaxHeight(Double.MAX_VALUE);
        HBox.setHgrow(panel, Priority.ALWAYS);
        VBox.setVgrow(holder, Priority.ALWAYS);

        screen.getChildren().add(holder);
        gantiHalaman(screen);
    }

    private StackPane buatAreaUpload(Label namaFoto) {
        StackPane area = new StackPane();
        area.setPrefHeight(88);
        area.setMinHeight(88);
        area.setMaxHeight(88);
        area.setCursor(Cursor.HAND);

        area.setStyle(
                "-fx-background-color: #E8E8E8;" +
                "-fx-border-color: #777777;" +
                "-fx-border-radius: 6;" +
                "-fx-background-radius: 6;"
        );

        VBox petunjuk = new VBox(2);
        petunjuk.setAlignment(Pos.CENTER);

        Label ikon = new Label("☁ ⬆");
        ikon.setStyle(
                "-fx-font-size: 30px;" +
                "-fx-text-fill: #505050;"
        );

        Label teks = new Label("Klik untuk upload foto");
        teks.setStyle(
                "-fx-font-size: 11px;" +
                "-fx-text-fill: #555555;"
        );

        petunjuk.getChildren().addAll(ikon, teks);
        area.getChildren().add(petunjuk);

        area.setOnMouseClicked(event -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Pilih Foto Barang");
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(
                            "File gambar", "*.png", "*.jpg", "*.jpeg"));

            Window owner = pageRoot.getScene() == null
                    ? null : pageRoot.getScene().getWindow();

            File file = chooser.showOpenDialog(owner);

            if (file != null) {
                selectedFotoUri = file.toURI().toString();
                namaFoto.setText(file.getName());

                Image image = new Image(
                        selectedFotoUri, 240, 80, true, true);

                ImageView preview = new ImageView(image);
                preview.setFitWidth(240);
                preview.setFitHeight(80);
                preview.setPreserveRatio(true);

                area.getChildren().setAll(preview);
            }
        });

        return area;
    }

    private void konfirmasiTambah(
            TextField namaField,
            TextField hargaField,
            TextField ukuranField,
            TextField stokField) {

        String nama = namaField.getText().trim();
        String hargaText = hargaField.getText().trim()
                .replace(".", "")
                .replace(",", "");
        String ukuran = ukuranField.getText().trim();
        String stokText = stokField.getText().trim();

        if (nama.isEmpty() || hargaText.isEmpty()
                || stokText.isEmpty()) {
            tampilkanPeringatan(
                    "Nama barang, harga, dan total stok wajib diisi.");
            return;
        }

        try {
            long harga = Long.parseLong(hargaText);
            int stok = Integer.parseInt(stokText);

            if (harga <= 0) {
                tampilkanPeringatan(
                        "Harga barang harus lebih besar dari 0.");
                return;
            }

            if (stok < 0) {
                tampilkanPeringatan(
                        "Total stok tidak boleh negatif.");
                return;
            }

            if (ukuran.isEmpty()) {
                ukuran = "-";
            }

            daftarProduk.add(new Produk(
                    nama,
                    tentukanJenis(nama),
                    harga,
                    stok,
                    ukuran,
                    selectedFotoUri
            ));

            selectedFotoUri = null;
            tampilkanDaftar();

        } catch (NumberFormatException e) {
            tampilkanPeringatan(
                    "Harga dan total stok harus berupa angka.");
        }
    }

    // =========================================================
    // HALAMAN DETAIL PERLENGKAPAN
    // =========================================================

    private void tampilkanDetail(Produk produk) {
        VBox screen = new VBox(14);
        screen.setFillWidth(true);

        screen.getChildren().add(
                buatHeader("Detail perlengkapan", this::tampilkanDaftar));

        VBox panel = new VBox(17);
        panel.setPadding(new Insets(25, 32, 25, 32));
        panel.setPrefWidth(850);
        panel.setMaxWidth(900);
        panel.setMaxHeight(Double.MAX_VALUE);
        panel.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 14;"
        );

        StackPane gambar = new StackPane();
        gambar.setPrefHeight(120);
        gambar.setMinHeight(120);
        gambar.setMaxHeight(120);
        tampilkanFoto(gambar, produk, 140, 115);

        VBox bidang = new VBox(12);

        bidang.getChildren().addAll(
                buatField(
                        "Nama Barang",
                        fieldBacaSaja(produk.nama)),
                buatField(
                        "Harga Sewa (Per hari)",
                        fieldBacaSaja(
                                "Rp " + formatRupiah(produk.harga))),
                buatField(
                        "Status Barang",
                        fieldBacaSaja(
                                produk.stok > 0
                                        ? "Tersedia" : "Dipinjamkan")),
                buatField(
                        "Ukuran Barang",
                        fieldBacaSaja(produk.ukuran))
        );

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button konfirmasi = buatTombol(
                "Konfirmasi", true, this::tampilkanDaftar);

        HBox barisTombol = new HBox(konfirmasi);
        barisTombol.setAlignment(Pos.CENTER_RIGHT);

        panel.getChildren().addAll(
                gambar, bidang, spacer, barisTombol);

        HBox holder = new HBox(panel);
        holder.setAlignment(Pos.TOP_CENTER);
        holder.setMaxHeight(Double.MAX_VALUE);
        HBox.setHgrow(panel, Priority.ALWAYS);
        VBox.setVgrow(holder, Priority.ALWAYS);

        screen.getChildren().add(holder);
        gantiHalaman(screen);
    }

    // =========================================================
    // KOMPONEN UI BERSAMA
    // =========================================================

    private HBox buatHeader(String teks, Runnable aksiKembali) {
        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);

        Button kembali = new Button("‹");
        kembali.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: #00643D;" +
                "-fx-font-size: 26px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 0 3 0 0;" +
                "-fx-cursor: hand;"
        );
        kembali.setOnAction(event -> aksiKembali.run());

        Label judul = new Label(teks);
        judul.setStyle(
                "-fx-font-size: 20px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #00643D;"
        );

        header.getChildren().addAll(kembali, judul);
        return header;
    }

    private VBox buatField(String teks, javafx.scene.control.Control input) {
        VBox field = new VBox(5);
        field.setFillWidth(true);
        field.getChildren().addAll(labelForm(teks), input);
        return field;
    }

    private Label labelForm(String teks) {
        Label label = new Label(teks);
        label.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-text-fill: #333333;"
        );
        return label;
    }

    private TextField buatInput(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setPrefHeight(35);
        field.setMaxWidth(Double.MAX_VALUE);
        field.setStyle(gayaInput());
        return field;
    }

    private TextField fieldBacaSaja(String isi) {
        TextField field = new TextField(isi);
        field.setEditable(false);
        field.setPrefHeight(35);
        field.setMaxWidth(Double.MAX_VALUE);
        field.setStyle(gayaInput());
        return field;
    }

    private String gayaInput() {
        return "-fx-background-color: #E8E8E8;" +
               "-fx-border-color: #777777;" +
               "-fx-border-radius: 6;" +
               "-fx-background-radius: 6;" +
               "-fx-padding: 7 10 7 10;" +
               "-fx-font-size: 12px;";
    }

    private Button buatTombol(
            String teks, boolean utama, Runnable aksi) {

        Button tombol = new Button(teks);
        tombol.setPrefHeight(34);
        tombol.setMinWidth(98);
        tombol.setCursor(Cursor.HAND);
        tombol.setStyle(utama
                ? "-fx-background-color: #00643D;" +
                  "-fx-text-fill: white;" +
                  "-fx-font-weight: bold;" +
                  "-fx-background-radius: 6;" +
                  "-fx-padding: 8 20 8 20;"
                : "-fx-background-color: white;" +
                  "-fx-text-fill: #00643D;" +
                  "-fx-border-color: #00643D;" +
                  "-fx-border-radius: 6;" +
                  "-fx-background-radius: 6;" +
                  "-fx-padding: 8 20 8 20;"
        );

        tombol.setOnAction(event -> aksi.run());
        return tombol;
    }

    private void tampilkanFoto(
            StackPane area, Produk produk,
            double lebar, double tinggi) {

        area.getChildren().clear();

        if (produk.fotoUri != null
                && !produk.fotoUri.trim().isEmpty()) {

            try {
                Image image = new Image(
                        produk.fotoUri, lebar, tinggi, true, true);

                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(lebar);
                imageView.setFitHeight(tinggi);
                imageView.setPreserveRatio(true);

                area.getChildren().add(imageView);
                return;

            } catch (IllegalArgumentException e) {
                // Gunakan ilustrasi jika gambar tidak bisa dibaca.
            }
        }

        Label ikon = new Label(ikonProduk(produk.jenis));
        ikon.setStyle("-fx-font-size: 58px;");
        area.getChildren().add(ikon);
    }

    private void gantiHalaman(VBox halaman) {
        pageRoot.getChildren().setAll(halaman);
        VBox.setVgrow(halaman, Priority.ALWAYS);
        halaman.setMaxWidth(Double.MAX_VALUE);
        halaman.setMaxHeight(Double.MAX_VALUE);
    }

    private void tampilkanPeringatan(String pesan) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Periksa Data");
        alert.setHeaderText(null);
        alert.setContentText(pesan);
        alert.showAndWait();
    }

    private String tentukanJenis(String nama) {
        String teks = nama.toLowerCase(Locale.ROOT);

        if (teks.contains("kompor")) {
            return "Kompor";
        }

        if (teks.contains("sepatu")) {
            return "Sepatu";
        }

        return "Tas";
    }

    private String ikonProduk(String jenis) {
        if ("Kompor".equals(jenis)) {
            return "🔥";
        }

        if ("Sepatu".equals(jenis)) {
            return "🥾";
        }

        return "🎒";
    }

    private String formatRupiah(long nominal) {
        return String.format("%,d", nominal).replace(',', '.');
    }

    private static class Produk {
        private final String nama;
        private final String jenis;
        private final long harga;
        private final int stok;
        private final String ukuran;
        private final String fotoUri;

        private Produk(
                String nama,
                String jenis,
                long harga,
                int stok,
                String ukuran,
                String fotoUri) {

            this.nama = nama;
            this.jenis = jenis;
            this.harga = harga;
            this.stok = stok;
            this.ukuran = ukuran;
            this.fotoUri = fotoUri;
        }
    }
}
