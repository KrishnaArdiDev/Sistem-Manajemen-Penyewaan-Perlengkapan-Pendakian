package com.sistemmanajemenpenyewaanperlengkapanpendakian.controller;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.App;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.Service.TransaksiService;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Transaksi;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class TransaksiController {

    @FXML
    private VBox containerTransaksi;

    private static final String PATH_ITEM = "/com/sistemmanajemenpenyewaanperlengkapanpendakian/fxml/pages/item-transaksi.fxml";
    private static final String PATH_NOTA = "/com/sistemmanajemenpenyewaanperlengkapanpendakian/fxml/pages/nota.fxml";
    private static final String PATH_TAMBAH = "/com/sistemmanajemenpenyewaanperlengkapanpendakian/fxml/tambah-transaksi.fxml";
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private BorderPane layoutUtama;
    private Node halamanTransaksi;

    @FXML
    public void initialize() {
        muatTransaksi();
    }

    private void muatTransaksi() {
        containerTransaksi.getChildren().clear();
        if (TransaksiService.semua().isEmpty()) {
            Label kosong = new Label("Belum ada transaksi. Klik \"Tambah Transaksi\" untuk membuat yang pertama.");
            kosong.setStyle("-fx-font-size: 14px; -fx-text-fill: #7A7A7A;");
            containerTransaksi.getChildren().add(kosong);
            return;
        }
        for (Transaksi t : TransaksiService.semua()) {
            tambahKartu(t.getNamaBarang(), t.getNamaPenyewa(),
                    t.getTglSewa().format(FMT), t.getTglKembali().format(FMT),
                    t.getStatus(), "/images/tas.png");
        }
    }

    private void tambahKartu(String barang, String penyewa, String tglSewa,
            String tglKembali, String status, String gambar) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource(PATH_ITEM));
            Node kartu = loader.load();
            ItemTransaksiController item = loader.getController();
            item.setData(barang, penyewa, tglSewa, tglKembali, status, gambar,
                    () -> tampilkanDetail(barang, penyewa, tglSewa, tglKembali, status, gambar));
            containerTransaksi.getChildren().add(kartu);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void tambahTransaksi() {
        try {
            BorderPane layout = (BorderPane) containerTransaksi.getScene().lookup("#layoutPage");
            Parent form = FXMLLoader.load(App.class.getResource(PATH_TAMBAH));
            BorderPane.setMargin(form, new Insets(16));
            layout.setCenter(form);
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Gagal membuka form:\n" + e).showAndWait();
        }
    }

    private boolean siapkanLayoutUtama() {
        if (containerTransaksi.getScene() == null)
            return false;
        if (layoutUtama == null) {
            Node node = containerTransaksi;
            while (node.getParent() != null) {
                Parent parent = node.getParent();
                if (parent instanceof BorderPane && ((BorderPane) parent).getCenter() == node) {
                    layoutUtama = (BorderPane) parent;
                    break;
                }
                node = parent;
            }
        }
        if (layoutUtama == null) {
            System.err.println("Layout konten utama tidak ditemukan.");
            return false;
        }
        return true;
    }

    private void tampilkanNota(Transaksi t) {
        if (!siapkanLayoutUtama())
            return;
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource(PATH_NOTA));
            Parent nota = loader.load();
            NotaController controller = loader.getController();
            controller.setTransaksi(t);
            controller.setAksiKembali(this::kembaliKeTransaksi);
            halamanTransaksi = layoutUtama.getCenter();
            BorderPane.setMargin(nota, new Insets(16));
            layoutUtama.setCenter(nota);
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Gagal membuka nota:\n" + e).showAndWait();
        }
    }

    private void tampilkanDetail(String barang, String penyewa, String tglSewa,
            String tglKembali, String status, String gambar) {
        if (containerTransaksi.getScene() == null)
            return;

        if (layoutUtama == null) {
            Node node = containerTransaksi;
            while (node.getParent() != null) {
                Parent parent = node.getParent();
                if (parent instanceof BorderPane && ((BorderPane) parent).getCenter() == node) {
                    layoutUtama = (BorderPane) parent;
                    break;
                }
                node = parent;
            }
        }
        if (layoutUtama == null) {
            System.err.println("Layout konten utama tidak ditemukan.");
            return;
        }

        halamanTransaksi = layoutUtama.getCenter();

        VBox detail = new VBox(14);
        detail.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        detail.setStyle("-fx-background-color: #F0F0F0;");

        HBox judul = new HBox(8);
        judul.setAlignment(Pos.CENTER_LEFT);
        judul.setPadding(new Insets(0, 0, 0, 8));

        Label tombolKembali = new Label("<");
        tombolKembali.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #00572B; -fx-cursor: hand;");
        tombolKembali.setOnMouseClicked(e -> kembaliKeTransaksi());

        Label labelJudul = new Label("Detail Transaksi");
        labelJudul.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #00572B;");
        judul.getChildren().addAll(tombolKembali, labelJudul);

        VBox panel = new VBox(12);
        panel.setPadding(new Insets(18, 44, 18, 44));
        panel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        panel.setStyle("-fx-background-color: white; -fx-background-radius: 16;");
        VBox.setVgrow(panel, Priority.ALWAYS);

        ImageView foto = new ImageView();
        foto.setFitWidth(100);
        foto.setFitHeight(100);
        foto.setPreserveRatio(true);
        foto.setSmooth(true);
        var url = App.class.getResource(gambar);
        if (url != null)
            foto.setImage(new Image(url.toExternalForm()));

        HBox wadahFoto = new HBox(foto);
        wadahFoto.setAlignment(Pos.CENTER);
        panel.getChildren().add(wadahFoto);

        panel.getChildren().addAll(
                buatFieldDetail("Nama Barang", barang),
                buatFieldDetail("Status Transaksi", status),
                buatFieldDetail("Nama Peminjam", penyewa),
                buatFieldDetail("Kartu Identitas Peminjam", "Kartuidentitas.png"),
                buatFieldDetail("Tanggal Meminjam", tglSewa),
                buatFieldDetail("Tanggal Kembali", tglKembali));

        Region ruang = new Region();
        VBox.setVgrow(ruang, Priority.ALWAYS);
        panel.getChildren().add(ruang);

        Button konfirmasi = new Button("Konfirmasi");
        konfirmasi.setMinWidth(95);
        konfirmasi.setPrefHeight(30);
        konfirmasi.setFocusTraversable(false);
        konfirmasi.setStyle(
                "-fx-background-color: #00572B; -fx-text-fill: white; -fx-background-radius: 7; -fx-padding: 7 18; -fx-font-size: 11px; -fx-cursor: hand;");
        konfirmasi.setOnAction(e -> kembaliKeTransaksi());

        HBox areaTombol = new HBox(konfirmasi);
        areaTombol.setAlignment(Pos.CENTER_RIGHT);
        panel.getChildren().add(areaTombol);

        detail.getChildren().addAll(judul, panel);
        BorderPane.setMargin(detail, new Insets(16));
        layoutUtama.setCenter(detail);
    }

    private VBox buatFieldDetail(String nama, String nilai) {
        Label label = new Label(nama);
        label.setStyle("-fx-font-size: 12px; -fx-text-fill: black;");

        TextField field = new TextField(nilai);
        field.setEditable(false);
        field.setFocusTraversable(false);
        field.setPrefHeight(30);
        field.setMaxWidth(Double.MAX_VALUE);
        field.setStyle(
                "-fx-background-color: #E8E8E8; -fx-background-radius: 6; -fx-border-color: #555555; -fx-border-radius: 6; -fx-font-size: 13px;");

        VBox grup = new VBox(5, label, field);
        grup.setMaxWidth(Double.MAX_VALUE);
        return grup;
    }

    private void kembaliKeTransaksi() {
        if (layoutUtama != null && halamanTransaksi != null) {
            BorderPane.setMargin(halamanTransaksi, new Insets(16));
            layoutUtama.setCenter(halamanTransaksi);
        }
    }
}
