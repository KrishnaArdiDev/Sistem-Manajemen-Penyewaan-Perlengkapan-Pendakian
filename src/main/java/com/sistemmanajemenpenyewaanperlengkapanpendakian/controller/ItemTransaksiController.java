package com.sistemmanajemenpenyewaanperlengkapanpendakian.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ItemTransaksiController {

    @FXML private ImageView gambarBarang;
    @FXML private Label labelNamaBarang;
    @FXML private Label labelPenyewa;
    @FXML private Label labelTanggal;
    @FXML private Label labelStatus;
    @FXML private Button tombolDetail;

    private Runnable aksiDetail;

    public void setData(String namaBarang, String penyewa,
                        String tglSewa, String tglKembali,
                        String status, String pathGambar,
                        Runnable aksiDetail) {

        labelNamaBarang.setText(namaBarang);
        labelPenyewa.setText(penyewa);
        labelTanggal.setText(tglSewa + " | " + tglKembali);
        this.aksiDetail = aksiDetail;

        atur(status);
        setGambar(pathGambar);
    }

    private void atur(String status) {
        labelStatus.setText(status);

        String warnaBg, warnaBorder, warnaTeks;
        String s = status.toLowerCase();
        if (s.equals("berhasil")) {
            warnaBg = "#C8FFB8"; warnaBorder = "#00572B"; warnaTeks = "#00572B";
        } else if (s.equals("terlambat")) {
            warnaBg = "#FFD6D6"; warnaBorder = "#B00020"; warnaTeks = "#B00020";
        } else { // dipinjam / proses
            warnaBg = "#FFF3C4"; warnaBorder = "#B38600"; warnaTeks = "#B38600";
        }
        labelStatus.setStyle(
            "-fx-background-color: " + warnaBg + ";" +
            "-fx-border-color: " + warnaBorder + ";" +
            "-fx-border-width: 2;" +
            "-fx-background-radius: 14; -fx-border-radius: 14;" +
            "-fx-text-fill: " + warnaTeks + ";" +
            "-fx-font-size: 16px; -fx-padding: 2 0 2 0;");
    }

    private void setGambar(String path) {
        try {
            java.io.InputStream stream = getClass().getResourceAsStream(path);
            if (stream != null) gambarBarang.setImage(new Image(stream));
        } catch (Exception e) {
            System.err.println("Gambar tidak ditemukan: " + path);
        }
    }

    @FXML
    private void bukaDetail() {
        if (aksiDetail != null) aksiDetail.run();
    }
}
