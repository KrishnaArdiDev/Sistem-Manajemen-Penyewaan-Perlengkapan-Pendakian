
package com.sistemmanajemenpenyewaanperlengkapanpendakian.controller;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.App;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Transaksi;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.DetailTransaksi;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.print.PrinterJob;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class RingkasanTransaksiController {

    @FXML
    private BorderPane rootRingkasan;

    @FXML
    private Label lblIdTransaksi;

    @FXML
    private Label lblNamaPenyewa;

    @FXML
    private Label lblTanggalTransaksi;

    @FXML
    private Label lblPerlengkapan;

    @FXML
    private Label lblJumlah;

    @FXML
    private Label lblTotalBiaya;

    public void setDataTransaksi(Transaksi transaksi) {

        if (transaksi == null) {
            throw new IllegalArgumentException(
                    "Data transaksi tidak boleh kosong."
            );
        }

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(
                        "dd MMMM yyyy",
                        Locale.forLanguageTag("id-ID")
                );

        lblIdTransaksi.setText(
                "TRX-" + transaksi.getIdTransaksi()
        );

        lblNamaPenyewa.setText(
                transaksi.getPelanggan().getNamaPelanggan()
        );

        lblTanggalTransaksi.setText(
                transaksi.getTanggalTransaksi().format(formatter)
        );

        StringBuilder namaBarang = new StringBuilder();
        StringBuilder jumlahBarang = new StringBuilder();

        for (DetailTransaksi detail : transaksi.getDaftarDetail()) {

            if (detail == null ||
                    detail.getPerlengkapanYangDisewa() == null) {
                continue;
            }

            if (namaBarang.length() > 0) {
                namaBarang.append("\n");
                jumlahBarang.append("\n");
            }

            namaBarang.append(
                    detail.getPerlengkapanYangDisewa()
                            .getNamaPerlengkapan()
            );

            jumlahBarang.append(
                    detail.getKuantitasPerAlat()
            ).append(" Unit");
        }

        lblPerlengkapan.setText(namaBarang.toString());
        lblJumlah.setText(jumlahBarang.toString());

        lblTotalBiaya.setText(
                "Rp " + String.format(
                        Locale.forLanguageTag("id-ID"),
                        "%,d",
                        transaksi.getTotalHarga()
                )
        );
    }

    @FXML
    private void Cetak() {

        PrinterJob job = PrinterJob.createPrinterJob();

        if (job == null) {
            tampilkanPesan(
                    Alert.AlertType.WARNING,
                    "Printer tidak tersedia."
            );
            return;
        }

        boolean siapCetak = job.showPrintDialog(
                rootRingkasan.getScene().getWindow()
        );

        if (siapCetak) {

            boolean berhasil = job.printPage(rootRingkasan);

            if (berhasil) {
                job.endJob();

                tampilkanPesan(
                        Alert.AlertType.INFORMATION,
                        "Ringkasan transaksi berhasil dicetak."
                );

            } else {
                job.cancelJob();

                tampilkanPesan(
                        Alert.AlertType.ERROR,
                        "Gagal mencetak ringkasan transaksi."
                );
            }
        }
    }

    @FXML
    private void Tutup() {

        try {

            Node induk = rootRingkasan.getParent();
            BorderPane layout = null;

            // Mencari layout utama aplikasi
            while (induk != null) {

                if (induk instanceof BorderPane
                        && "layoutPage".equals(induk.getId())) {

                    layout = (BorderPane) induk;
                    break;
                }

                induk = induk.getParent();
            }

            if (layout == null) {
                throw new IllegalStateException(
                        "Layout utama tidak ditemukan."
                );
            }

            URL lokasiTransaksi = App.class.getResource(
                    "/com/sistemmanajemenpenyewaanperlengkapanpendakian/fxml/pages/transaksi.fxml"
            );

            if (lokasiTransaksi == null) {
                throw new IOException(
                        "Halaman transaksi tidak ditemukan."
                );
            }

            Parent halamanTransaksi =
                    FXMLLoader.load(lokasiTransaksi);

            BorderPane.setMargin(
                    halamanTransaksi,
                    new javafx.geometry.Insets(16)
            );

            layout.setCenter(halamanTransaksi);

        } catch (Exception e) {

            e.printStackTrace();

            tampilkanPesan(
                    Alert.AlertType.ERROR,
                    "Gagal kembali ke halaman transaksi:\n"
                            + e.getMessage()
            );
        }
    }

    private void tampilkanPesan(
            Alert.AlertType tipe,
            String pesan) {

        Alert alert = new Alert(tipe);
        alert.setTitle("TAPAK - Ringkasan Transaksi");
        alert.setHeaderText(null);
        alert.setContentText(pesan);
        alert.showAndWait();
    }
}
