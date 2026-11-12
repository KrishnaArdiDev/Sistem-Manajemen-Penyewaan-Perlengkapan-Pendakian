package com.sistemmanajemenpenyewaanperlengkapanpendakian.controller;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.App;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.Service.PengembalianService;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.Service.TransaksiService;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Transaksi;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.DetailTransaksi;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

public class PengembalianController {

    @FXML private Label labelNamaBarang;
    @FXML private Label labelStatusPeminjaman;
    @FXML private Label labelNamaPeminjam;
    @FXML private Label labelKartuIdentitas;
    @FXML private Label labelTanggalMeminjam;
    @FXML private Label labelTanggalKembali;
    @FXML private Button btnKonfirmasi;
    @FXML private Button btnKembali;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd - MM - yyyy");
    private Transaksi transaksiAktif;

    @FXML
    public void initialize() {
        muatDataTransaksi();
    }

    private void muatDataTransaksi() {
        List<Transaksi> daftar = TransaksiService.semua();
        if (daftar != null && !daftar.isEmpty()) {
            transaksiAktif = daftar.get(0); // Ambil transaksi teratas
            tampilkanData(transaksiAktif);
        }
    }

    private void tampilkanData(Transaksi t) {
        if (t == null) return;

        if (labelNamaBarang != null) {
            labelNamaBarang.setText(t.getNamaBarang());
        }
        if (labelNamaPeminjam != null) {
            labelNamaPeminjam.setText(t.getNamaPenyewa());
        }
        if (labelTanggalMeminjam != null && t.getTanggalSewa() != null) {
            labelTanggalMeminjam.setText(t.getTanggalSewa().format(FMT));
        }
        if (labelTanggalKembali != null && t.getTanggalKembali() != null) {
            labelTanggalKembali.setText(t.getTanggalKembali().format(FMT));
        }
        if (labelStatusPeminjaman != null) {
            if (t.getTanggalKembali() != null && LocalDate.now().isAfter(t.getTanggalKembali())) {
                labelStatusPeminjaman.setText("Terlambat");
                labelStatusPeminjaman.setStyle("-fx-text-fill: #A3212B; -fx-font-size: 18px; -fx-font-weight: bold;");
            } else {
                labelStatusPeminjaman.setText("Sedang Dipinjam");
                labelStatusPeminjaman.setStyle("-fx-text-fill: #17652B; -fx-font-size: 18px; -fx-font-weight: bold;");
            }
        }
    }

    @FXML
    private void handleKonfirmasi() {
        if (transaksiAktif == null) {
            tampilkanPesan(Alert.AlertType.INFORMATION, "Informasi", "Belum ada transaksi aktif untuk diproses pengembaliannya.");
            return;
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>("utuh", "utuh", "rusak", "hilang");
        dialog.setTitle("Konfirmasi Pengembalian");
        dialog.setHeaderText("Pilih Kondisi Perlengkapan yang Dikembalikan");
        dialog.setContentText("Kondisi:");

        dialog.showAndWait().ifPresent(kondisi -> {
            try {
                DetailTransaksi detail = null;
                if (transaksiAktif.getDaftarDetail() != null && !transaksiAktif.getDaftarDetail().isEmpty()) {
                    detail = transaksiAktif.getDaftarDetail().get(0);
                }

                if (detail != null) {
                    PengembalianService.HasilPengembalian hasil =
                            PengembalianService.prosesPengembalian(transaksiAktif, detail, LocalDate.now(), kondisi);

                    String ringkasan = "Pengembalian berhasil dikonfirmasi!\n\n"
                            + "Kondisi: " + kondisi.toUpperCase() + "\n"
                            + "Denda Keterlambatan: " + formatRupiah(hasil.getDenda()) + "\n"
                            + "Biaya Ganti Rugi: " + formatRupiah(hasil.getGantiRugi()) + "\n"
                            + "------------------------------------\n"
                            + "Total Tagihan Pengembalian: " + formatRupiah(hasil.getTotalTagihan());

                    tampilkanPesan(Alert.AlertType.INFORMATION, "Sukses", ringkasan);
                } else {
                    tampilkanPesan(Alert.AlertType.INFORMATION, "Sukses", "Pengembalian berhasil diproses dengan kondisi: " + kondisi);
                }

                if (labelStatusPeminjaman != null) {
                    labelStatusPeminjaman.setText("Sudah Dikembalikan (" + kondisi + ")");
                    labelStatusPeminjaman.setStyle("-fx-text-fill: #00572B; -fx-font-size: 18px; -fx-font-weight: bold;");
                }

            } catch (Exception e) {
                tampilkanPesan(Alert.AlertType.ERROR, "Gagal", "Gagal memproses pengembalian: " + e.getMessage());
            }
        });
    }

    @FXML
    private void handleKembali() {
        try {
            BorderPane layout = (BorderPane) btnKembali.getScene().lookup("#layoutPage");
            if (layout != null) {
                Parent dashboard = FXMLLoader.load(App.class.getResource("/com/sistemmanajemenpenyewaanperlengkapanpendakian/fxml/pages/dashboard.fxml"));
                BorderPane.setMargin(dashboard, new Insets(16));
                layout.setCenter(dashboard);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String formatRupiah(long nilai) {
        return String.format(Locale.forLanguageTag("id-ID"), "Rp %,d", nilai);
    }

    private void tampilkanPesan(Alert.AlertType tipe, String judul, String pesan) {
        Alert alert = new Alert(tipe);
        alert.setTitle(judul);
        alert.setHeaderText(null);
        alert.setContentText(pesan);
        alert.showAndWait();
    }
}