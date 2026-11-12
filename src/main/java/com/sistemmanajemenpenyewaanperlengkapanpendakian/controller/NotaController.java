package com.sistemmanajemenpenyewaanperlengkapanpendakian.controller;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.App;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.*;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.dao.*;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.exception.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import javafx.fxml.FXML;
import javafx.collections.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.File;

public class NotaController {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    @FXML
    private Label labelTanggalCetak;
    @FXML
    private ImageView imageFotoIdentitas;
    @FXML
    private Label labelFotoKosong;
    @FXML
    private Label labelPenyewa;
    @FXML
    private Label labelTglSewa;
    @FXML
    private Label labelTglKembali;
    @FXML
    private Label labelStatus;
    @FXML
    private Label labelBarang;
    @FXML
    private Label labelJumlah;
    @FXML
    private Label labelLamaSewa;
    @FXML
    private Label labelHargaHari;
    @FXML
    private Label labelSubtotal;
    @FXML
    private Label labelTotal;
    private Runnable aksiKembali;

    @FXML
    public void initialize() {
        labelTanggalCetak.setText("Dicetak: " + LocalDate.now().format(FMT));
    }

    public void setTransaksi(Transaksi t) {
        if (t == null)
            return;
        long hari = Math.max(ChronoUnit.DAYS.between(t.getTglSewa(), t.getTglKembali()), 1);
        long hargaHari = t.getJumlah() > 0 ? t.getTotalHarga() / (t.getJumlah() * hari) : 0;
        labelPenyewa.setText(t.getNamaPenyewa());
        labelTglSewa.setText(t.getTglSewa().format(FMT));
        labelTglKembali.setText(t.getTglKembali().format(FMT));
        labelStatus.setText(t.getStatus());
        labelBarang.setText(t.getNamaBarang());
        labelJumlah.setText(String.valueOf(t.getJumlah()));
        labelLamaSewa.setText(hari + " hari");
        labelHargaHari.setText(rupiah(hargaHari));
        labelSubtotal.setText(rupiah(t.getTotalHarga()));
        labelTotal.setText(rupiah(t.getTotalHarga()));
        tampilkanFoto(t.getFotoIdentitas());

    }

    public void setAksiKembali(Runnable aksiKembali) {
        this.aksiKembali = aksiKembali;
    }

    @FXML
    private void kembali() {
        if (aksiKembali != null)
            aksiKembali.run();
    }

    private static String rupiah(long nilai) {
        return "Rp " + String.format("%,d", nilai).replace(',', '.');
    }

    private void tampilkanFoto(String path) {
        File f = (path == null) ? null : new File(path);
        boolean ada = f != null && f.exists();
        if (ada) {
            imageFotoIdentitas.setImage(new Image(f.toURI().toString()));
        }
        imageFotoIdentitas.setVisible(ada);
        imageFotoIdentitas.setManaged(ada);
        labelFotoKosong.setVisible(!ada);
        labelFotoKosong.setManaged(!ada);
    }

}
