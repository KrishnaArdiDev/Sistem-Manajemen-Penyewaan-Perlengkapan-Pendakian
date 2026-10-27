package com.sistemmanajemenpenyewaanperlengkapanpendakian.controller;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.App;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.Service.TransaksiService;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Transaksi;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class TransaksiController {

    @FXML private VBox containerTransaksi;

    private static final String PATH_ITEM =
            "/com/sistemmanajemenpenyewaanperlengkapanpendakian/fxml/item-transaksi.fxml";

    private static final String PATH_TAMBAH =
            "/com/sistemmanajemenpenyewaanperlengkapanpendakian/fxml/tambah-transaksi.fxml";

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

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
                        t.getStatus(), "/images/tas-carrier.png");
        }
    }

    private void tambahKartu(String barang, String penyewa, String tglSewa,
                             String tglKembali, String status, String gambar) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource(PATH_ITEM));
            Node kartu = loader.load();

            ItemTransaksiController item = loader.getController();
            item.setData(barang, penyewa, tglSewa, tglKembali, status, gambar,
                    () -> System.out.println("Detail: " + barang + " - " + penyewa));

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
}
