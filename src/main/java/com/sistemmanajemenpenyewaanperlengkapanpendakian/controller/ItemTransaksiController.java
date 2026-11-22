package com.sistemmanajemenpenyewaanperlengkapanpendakian.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.File;
import java.net.URL;
import java.util.Locale;
import javafx.scene.layout.VBox;

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
    private Button tombolCetak;

    private Runnable aksiDetail;
    private Runnable aksiCetak;

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

    labelStatus.setText( status == null || status.trim().isEmpty() ? "Berhasil": status);

    labelStatus.setWrapText(true);
    labelStatus.setMaxWidth(Double.MAX_VALUE);
    labelStatus.setMinHeight(32);
    labelStatus.setMaxHeight(Double.MAX_VALUE);

    if (labelStatus.getParent() instanceof VBox) {

        VBox wadahStatus = (VBox) labelStatus.getParent();

        wadahStatus.setMaxHeight(Double.MAX_VALUE);
        wadahStatus.setMaxWidth(Double.MAX_VALUE);

        wadahStatus.setSpacing(status != null && status.contains("\n")? 8: 40
        );
    }

    String s = status == null ? "" : status.toLowerCase(Locale.ROOT);

    boolean bermasalah = s.contains("terlambat") || s.contains("rusak") || s.contains("hilang");

    boolean masihDisewa = s.contains("sedang disewa");

    boolean sudahDikembalikan = s.contains("sudah dikembalikan");

    String warnaBg;
    String warnaBorder;
    String warnaTeks;

    if (bermasalah) {
        warnaBg = "#FFD6D6";
        warnaBorder = "#B00020";
        warnaTeks = "#B00020";

    } else if (s.equals("berhasil")
            || (sudahDikembalikan && !masihDisewa)) {
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

    gambarBarang.setImage(null);

    if (path == null || path.trim().isEmpty()) {
        return;
    }

    try {

        String sumber = path.trim();

        if (!sumber.startsWith("file:")
                && !sumber.startsWith("http://")
                && !sumber.startsWith("https://")) {

            File file = new File(sumber);

            if (file.isFile() && file.canRead()) {

                sumber = file.toURI().toString();

            } else {

                String pathResource = sumber.startsWith("/")
                        ? sumber : sumber.startsWith("images/") ? "/" + sumber : "/images/" + sumber;

                URL resource = getClass().getResource(pathResource);

                if (resource == null) { 
                    System.err.println( "Gambar tidak ditemukan: " + path );
                    return;
                }

                sumber = resource.toExternalForm();
            }
        }

        Image gambar = new Image( sumber, 92, 98, true, true);

        if (!gambar.isError()) {
            gambarBarang.setImage(gambar);
        } else {
            System.err.println("Gambar gagal dimuat: " + path);
        }

    } catch (RuntimeException e) {

        System.err.println("Gagal memuat gambar: " + path);

        e.printStackTrace();
    }
}

    @FXML
    private void bukaDetail() {
        if (aksiDetail != null)
            aksiDetail.run();
    }

    public void setAksiCetak(Runnable aksiCetak) {
        this.aksiCetak = aksiCetak;
    }

    @FXML
    private void Cetak() {
        if (aksiCetak != null)
            aksiCetak.run();
    }
}
