package com.sistemmanajemenpenyewaanperlengkapanpendakian.controller;
 
import com.sistemmanajemenpenyewaanperlengkapanpendakian.Service.TransaksiService;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.Service.TransaksiService.DataTransaksi;
 
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
 
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
 
public class TambahTransaksiController {
 
    @FXML private TextField fieldNama;
    @FXML private TextField fieldTelepon;
    @FXML private DatePicker dateSewa;
    @FXML private DatePicker dateKembali;
 
    @FXML private ComboBox<Barang> comboPerlengkapan;
    @FXML private Spinner<Integer> spinnerJumlah;
 
    @FXML private Label labelTotal;
    @FXML private Button tombolBatal;
 
    public static class Barang {
        final String nama;
        final int hargaPerHari;
        Barang(String nama, int hargaPerHari) { this.nama = nama; this.hargaPerHari = hargaPerHari; }
        @Override public String toString() { return nama + "  (" + rupiah(hargaPerHari) + "/hari)"; }
    }
 
    @FXML
    public void initialize() {
        comboPerlengkapan.setItems(FXCollections.observableArrayList(
                new Barang("Tas Carrier Puma", 25000),
                new Barang("Tenda Dome 4P", 50000),
                new Barang("Sepatu Gunung", 20000),
                new Barang("Kompor Portable", 10000)));
 
        spinnerJumlah.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 1));
 
        dateSewa.setValue(LocalDate.now());
        dateKembali.setValue(LocalDate.now().plusDays(1));
 
        comboPerlengkapan.valueProperty().addListener((o, a, b) -> hitungTotal());
        spinnerJumlah.valueProperty().addListener((o, a, b) -> hitungTotal());
        dateSewa.valueProperty().addListener((o, a, b) -> hitungTotal());
        dateKembali.valueProperty().addListener((o, a, b) -> hitungTotal());
 
        hitungTotal();
    }
 
    @FXML
    private void simpan() {
        String nama = fieldNama.getText();
        if (nama == null || nama.isBlank()) {
            info(Alert.AlertType.WARNING, "Nama penyewa wajib diisi.");
            return;
        }
        if (dateSewa.getValue() == null || dateKembali.getValue() == null) {
            info(Alert.AlertType.WARNING, "Tanggal sewa dan tanggal kembali wajib diisi.");
            return;
        }
        if (dateKembali.getValue().isBefore(dateSewa.getValue())) {
            info(Alert.AlertType.WARNING, "Tanggal kembali tidak boleh sebelum tanggal sewa.");
            return;
        }
        if (comboPerlengkapan.getValue() == null) {
            info(Alert.AlertType.WARNING, "Pilih perlengkapan terlebih dahulu.");
            return;
        }
 
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        Barang barang = comboPerlengkapan.getValue();
        int jumlah = spinnerJumlah.getValue();
        long total = (long) barang.hargaPerHari * jumlah * jumlahHari();
 
        TransaksiService.tambah(new DataTransaksi(
                barang.nama,
                jumlah,
                nama.trim(),
                fieldTelepon.getText(),
                dateSewa.getValue().format(fmt),
                dateKembali.getValue().format(fmt),
                total,
                "Dipinjam"));
 
        info(Alert.AlertType.INFORMATION, "Transaksi untuk " + nama + " tersimpan.\nTotal: " + labelTotal.getText());
        tutup();
    }
 
    @FXML
    private void batal() {
        tutup();
    }
 
    private long jumlahHari() {
        if (dateSewa.getValue() == null || dateKembali.getValue() == null) return 1;
        long hari = ChronoUnit.DAYS.between(dateSewa.getValue(), dateKembali.getValue());
        return Math.max(hari, 1);
    }
 
    private void hitungTotal() {
        Barang barang = comboPerlengkapan.getValue();
        if (barang == null) {
            labelTotal.setText(rupiah(0));
            return;
        }
        long total = (long) barang.hargaPerHari * spinnerJumlah.getValue() * jumlahHari();
        labelTotal.setText(rupiah(total));
    }
 
    private static String rupiah(long nilai) {
        return "Rp " + String.format("%,d", nilai).replace(',', '.');
    }
 
    private void info(Alert.AlertType tipe, String pesan) {
        Alert a = new Alert(tipe, pesan);
        a.setHeaderText(null);
        a.showAndWait();
    }
 
    private void tutup() {
        ((Stage) tombolBatal.getScene().getWindow()).close();
    }
}