
package com.sistemmanajemenpenyewaanperlengkapanpendakian.controller;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.App;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
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

    private static final String PATH_ITEM =
            "/com/sistemmanajemenpenyewaanperlengkapanpendakian/fxml/pages/item-transaksi.fxml";

    private BorderPane layoutUtama;
    private Node halamanTransaksi;

    @FXML
    public void initialize() {
        muatTransaksi();
    }

    private void muatTransaksi() {
        containerTransaksi.getChildren().clear();

        for (int i = 0; i < 6; i++) {
            tambahKartu(
                    "Tas Carrier Puma",
                    "Andy Tinggi",
                    "19 - 10 - 2026",
                    "20 - 10 - 2026",
                    "Berhasil",
                    "/images/tas-carrier.png"
            );
        }
    }

    private void tambahKartu(
            String barang,
            String penyewa,
            String tglSewa,
            String tglKembali,
            String status,
            String gambar
    ) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource(PATH_ITEM));
            Node kartu = loader.load();

            ItemTransaksiController item = loader.getController();

            item.setData(
                    barang,
                    penyewa,
                    tglSewa,
                    tglKembali,
                    status,
                    gambar,
                    () -> tampilkanDetail(
                            barang,
                            penyewa,
                            tglSewa,
                            tglKembali,
                            status,
                            gambar
                    )
            );

            containerTransaksi.getChildren().add(kartu);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void tampilkanDetail(
            String barang,
            String penyewa,
            String tglSewa,
            String tglKembali,
            String status,
            String gambar
    ) {
        if (containerTransaksi.getScene() == null) {
            return;
        }

        if (layoutUtama == null) {
            Node node = containerTransaksi;

            while (node.getParent() != null) {
                Parent parent = node.getParent();

                if (parent instanceof BorderPane) {
                    BorderPane border = (BorderPane) parent;

                    if (border.getCenter() == node) {
                        layoutUtama = border;
                        break;
                    }
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
        detail.setPadding(new Insets(0));
        detail.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        detail.setStyle("-fx-background-color: #F0F0F0;");

        HBox judul = new HBox(8);
        judul.setAlignment(Pos.CENTER_LEFT);
        judul.setPadding(new Insets(0, 0, 0, 8));

        Label tombolKembali = new Label("‹");
        tombolKembali.setStyle(
                "-fx-font-size: 26px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #00572B;" +
                "-fx-cursor: hand;"
        );
        tombolKembali.setOnMouseClicked(e -> kembaliKeTransaksi());

        Label labelJudul = new Label("Detail Transaksi");
        labelJudul.setStyle(
                "-fx-font-size: 22px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #00572B;"
        );

        judul.getChildren().addAll(tombolKembali, labelJudul);

        VBox panel = new VBox(12);
        panel.setPadding(new Insets(18, 44, 18, 44));
        panel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        panel.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 16;"
        );
        VBox.setVgrow(panel, Priority.ALWAYS);

        ImageView foto = new ImageView();
        foto.setFitWidth(100);
        foto.setFitHeight(100);
        foto.setPreserveRatio(true);
        foto.setSmooth(true);

        try {
            var url = App.class.getResource(gambar);

            if (url != null) {
                foto.setImage(new Image(url.toExternalForm()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        HBox wadahFoto = new HBox(foto);
        wadahFoto.setAlignment(Pos.CENTER);

        panel.getChildren().add(wadahFoto);

        panel.getChildren().addAll(
                buatFieldDetail("Nama Barang", barang),
                buatFieldDetail("Status Transaksi", status),
                buatFieldDetail("Nama Peminjam", penyewa),
                buatFieldDetail("Kartu Identitas Peminjam", "Kartuidentitas.png"),
                buatFieldDetail("Tanggal Meminjam", tglSewa),
                buatFieldDetail("Tanggal Kembali", tglKembali)
        );

        Region ruang = new Region();
        VBox.setVgrow(ruang, Priority.ALWAYS);
        panel.getChildren().add(ruang);

        HBox areaTombol = new HBox();
        areaTombol.setAlignment(Pos.CENTER_RIGHT);

        Button konfirmasi = new Button("Konfirmasi");
        konfirmasi.setMinWidth(95);
        konfirmasi.setPrefHeight(30);
        konfirmasi.setFocusTraversable(false);
        konfirmasi.setStyle(
                "-fx-background-color: #00572B;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 7;" +
                "-fx-padding: 7 18;" +
                "-fx-font-size: 11px;" +
                "-fx-cursor: hand;"
        );

        konfirmasi.setOnAction(e -> kembaliKeTransaksi());

        areaTombol.getChildren().add(konfirmasi);
        panel.getChildren().add(areaTombol);

        detail.getChildren().addAll(judul, panel);
        VBox.setVgrow(detail, Priority.ALWAYS);

        BorderPane.setMargin(detail, new Insets(16));
        layoutUtama.setCenter(detail);
    }

    private VBox buatFieldDetail(String nama, String nilai) {
        VBox grup = new VBox(5);
        grup.setMaxWidth(Double.MAX_VALUE);

        Label label = new Label(nama);
        label.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-text-fill: black;"
        );

        TextField field = new TextField(nilai);
        field.setEditable(false);
        field.setFocusTraversable(false);
        field.setPrefHeight(30);
        field.setMaxWidth(Double.MAX_VALUE);
        field.setStyle(
                "-fx-background-color: #E8E8E8;" +
                "-fx-background-radius: 6;" +
                "-fx-border-color: #555555;" +
                "-fx-border-radius: 6;" +
                "-fx-font-size: 13px;"
        );

        grup.getChildren().addAll(label, field);
        return grup;
    }

    private void kembaliKeTransaksi() {
        if (layoutUtama != null && halamanTransaksi != null) {
            BorderPane.setMargin(halamanTransaksi, new Insets(16));
            layoutUtama.setCenter(halamanTransaksi);
        }
    }

    @FXML
    private void tambahTransaksi() {
        System.out.println("Tambah transaksi diklik");
    }
}
