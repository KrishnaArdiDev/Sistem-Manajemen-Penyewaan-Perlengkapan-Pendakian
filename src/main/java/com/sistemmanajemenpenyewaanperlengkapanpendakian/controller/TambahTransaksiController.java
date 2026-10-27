package com.sistemmanajemenpenyewaanperlengkapanpendakian.controller;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.App;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.Service.TransaksiService;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Transaksi;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

public class TambahTransaksiController {

    @FXML private TextField fieldNama;
    @FXML private Label labelFoto;
    @FXML private ComboBox<Barang> comboPerlengkapan;
    @FXML private Spinner<Integer> spinnerJumlah;
    @FXML private DatePicker dateSewa;
    @FXML private DatePicker dateKembali;
    @FXML private Label labelTotal;
    @FXML private Button tombolBatal;
    @FXML private Button tombolSimpan;

    private File fotoIdentitas;

    private static final String PATH_TRANSAKSI =
            "/com/sistemmanajemenpenyewaanperlengkapanpendakian/fxml/pages/transaksi.fxml";

    public static class Barang {
        final String nama;
        final int hargaPerHari;

        Barang(String nama, int hargaPerHari) {
            this.nama = nama;
            this.hargaPerHari = hargaPerHari;
        }

        @Override
        public String toString() {
            return nama + "  (" + rupiah(hargaPerHari) + "/hari)";
        }
    }

    @FXML
    public void initialize() {
        comboPerlengkapan.setItems(FXCollections.observableArrayList(
                new Barang("Tas Carrier (40)", 25000),
                new Barang("Tenda Dome 4P", 50000),
                new Barang("Sepatu Gunung", 20000),
                new Barang("Kompor Portable", 10000)));

        spinnerJumlah.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 1));

        dateSewa.setValue(LocalDate.now());
        dateKembali.setValue(LocalDate.now().plusDays(1));

        // total harga dihitung ulang setiap ada perubahan
        comboPerlengkapan.valueProperty().addListener((o, a, b) -> hitungTotal());
        spinnerJumlah.valueProperty().addListener((o, a, b) -> hitungTotal());
        dateSewa.valueProperty().addListener((o, a, b) -> hitungTotal());
        dateKembali.valueProperty().addListener((o, a, b) -> hitungTotal());

        hitungTotal();
    }

    @FXML
    private void pilihFoto() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Pilih Foto Identitas");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Gambar", "*.png", "*.jpg", "*.jpeg"));

        File f = fc.showOpenDialog(labelFoto.getScene().getWindow());
        if (f != null) {
            fotoIdentitas = f;
            labelFoto.setText(f.getName());
            labelFoto.setStyle("-fx-text-fill: black;");
        }
    }

    @FXML
    private void simpan() {
        String nama = fieldNama.getText();

        if (nama == null || nama.isBlank()) {
            info(Alert.AlertType.WARNING, "Nama peminjam wajib diisi.");
            return;
        }
        if (fotoIdentitas == null) {
            info(Alert.AlertType.WARNING, "Foto identitas wajib dipilih.");
            return;
        }
        if (comboPerlengkapan.getValue() == null) {
            info(Alert.AlertType.WARNING, "Pilih barang pinjaman terlebih dahulu.");
            return;
        }
        if (dateSewa.getValue() == null || dateKembali.getValue() == null) {
            info(Alert.AlertType.WARNING, "Tanggal meminjam dan tanggal kembali wajib diisi.");
            return;
        }
        if (dateKembali.getValue().isBefore(dateSewa.getValue())) {
            info(Alert.AlertType.WARNING, "Tanggal kembali tidak boleh sebelum tanggal meminjam.");
            return;
        }

        Barang barang = comboPerlengkapan.getValue();
        int jumlah = spinnerJumlah.getValue();
        long total = (long) barang.hargaPerHari * jumlah * jumlahHari();

        TransaksiService.tambah(new Transaksi(
                nama.trim(),                    
                barang.nama,
                jumlah,
                dateSewa.getValue(),
                dateKembali.getValue(),
                total));

        info(Alert.AlertType.INFORMATION,
                "Transaksi untuk " + nama.trim() + " tersimpan.\nTotal: " + labelTotal.getText());
        kembaliKeTransaksi();
    }

    @FXML
    private void batal() {
        kembaliKeTransaksi();
    }

    private long jumlahHari() {
        if (dateSewa.getValue() == null || dateKembali.getValue() == null) return 1;
        long hari = ChronoUnit.DAYS.between(dateSewa.getValue(), dateKembali.getValue());
        return Math.max(hari, 1);
    }

    private void hitungTotal() {
        Barang barang = comboPerlengkapan.getValue();
        if (barang == null || spinnerJumlah.getValue() == null) {
            labelTotal.setText(rupiah(0));
            return;
        }
        long total = (long) barang.hargaPerHari * spinnerJumlah.getValue() * jumlahHari();
        labelTotal.setText(rupiah(total));
    }

    // Ganti isi area tengah main-layout kembali ke halaman Transaksi
    private void kembaliKeTransaksi() {
        try {
            BorderPane layout = (BorderPane) tombolBatal.getScene().lookup("#layoutPage");
            Parent halaman = FXMLLoader.load(App.class.getResource(PATH_TRANSAKSI));
            BorderPane.setMargin(halaman, new Insets(16));
            layout.setCenter(halaman);
        } catch (IOException e) {
            e.printStackTrace();
            info(Alert.AlertType.ERROR, "Gagal kembali ke halaman Transaksi:\n" + e.getMessage());
        }
    }

    private static String rupiah(long nilai) {
        return "Rp " + String.format("%,d", nilai).replace(',', '.');
    }

    private void info(Alert.AlertType tipe, String pesan) {
        Alert a = new Alert(tipe, pesan);
        a.setHeaderText(null);
        a.showAndWait();
    }
}
