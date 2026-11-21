package com.sistemmanajemenpenyewaanperlengkapanpendakian.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ItemTransaksiController {

    @FXML
    private ImageView gambarBarang;
    @FXML
    private Label labelNamaBarang;
    @FXML
    private Label labelPenyewa;
    @FXML
    private Label labelTanggal;
    @FXML
    private Label labelStatus;
    @FXML
    private Button tombolDetail;
    @FXML
    private Button tombolUnduh;

    private Runnable aksiDetail;
    private Runnable aksiUnduh;

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

    labelStatus.setWrapText(true);
    if (labelStatus.getParent() instanceof javafx.scene.layout.VBox){
        javafx.scene.layout.VBox wadahStatus = (javafx.scene.layout.VBox) labelStatus.getParent();
        wadahStatus.setMaxHeight(Double.MAX_VALUE);
    }
    labelStatus.setMinHeight(32);
    labelStatus.setMaxHeight(Double.MAX_VALUE);

    String s = status == null ? "" : status.toLowerCase(java.util.Locale.ROOT);

    String warnaBg;
    String warnaBorder;
    String warnaTeks;

    boolean bermasalah = s.contains("terlambat") || s.contains("rusak") || s.contains("hilang");

    boolean masihDisewa = s.contains("sedang disewa");

    boolean sudahDikembalikan = s.contains("sudah dikembalikan");

    if (bermasalah) {
        warnaBg = "#FFD6D6";
        warnaBorder = "#B00020";
        warnaTeks = "#B00020";
    } else if (sudahDikembalikan && !masihDisewa) {
        warnaBg = "#C8FFB8";
        warnaBorder = "#00572B";
        warnaTeks = "#00572B";
    } else {
        warnaBg = "#FFF3C4";
        warnaBorder = "#B38600";
        warnaTeks = "#B38600";
    }

    labelStatus.setStyle(
            "-fx-background-color: " + warnaBg + ";" +
            "-fx-border-color: " + warnaBorder + ";" +
            "-fx-border-width: 2;" +
            "-fx-background-radius: 14;" +
            "-fx-border-radius: 14;" +
            "-fx-text-fill: " + warnaTeks + ";" +
            "-fx-font-size: 12px;" +
            "-fx-padding: 5 8 5 8;"
    );
    }

    private void setGambar(String path) {
        try {
            java.io.InputStream stream = getClass().getResourceAsStream(path);
            if (stream != null)
                gambarBarang.setImage(new Image(stream));
        } catch (Exception e) {
            System.err.println("Gambar tidak ditemukan: " + path);
        }
    }

    @FXML
    private void bukaDetail() {
        if (aksiDetail != null)
            aksiDetail.run();
    }

    public void setAksiUnduh(Runnable aksiUnduh) {
        this.aksiUnduh = aksiUnduh;
    }

    @FXML
    private void unduh() {
        if (aksiUnduh != null)
            aksiUnduh.run();
    }
}
