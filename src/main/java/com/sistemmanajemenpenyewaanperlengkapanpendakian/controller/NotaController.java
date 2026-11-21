
package com.sistemmanajemenpenyewaanperlengkapanpendakian.controller;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.Service.NotaService;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.DetailTransaksi;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Transaksi;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class NotaController {

    private static final DateTimeFormatter FORMAT_TANGGAL =
            DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @FXML private VBox kertasNota;

    @FXML private Label labelTanggalCetak;
    @FXML private Label labelIdTransaksi;
    @FXML private Label labelTanggalTransaksi;
    @FXML private Label labelPenyewa;
    @FXML private Label labelTglSewa;
    @FXML private Label labelTglKembali;
    @FXML private Label labelMetodePembayaran;
    @FXML private Label labelStatus;

    @FXML private GridPane gridDetailBarang;

    @FXML private Label labelTotalKuantitas;
    @FXML private Label labelTotal;

    @FXML private VBox containerTagihanPengembalian;
    @FXML private Label labelDenda;
    @FXML private Label labelGantiRugi;
    @FXML private Label labelTotalTagihanPengembalian;

    @FXML private ImageView imageFotoIdentitas;
    @FXML private Label labelFotoKosong;

    @FXML private Button tombolKembali;
    @FXML private Button btnUnduhNota;
    @FXML private Button btnUnduhRingkasan;


    private Runnable aksiKembali;
    private Transaksi transaksiAktif;

    @FXML
    public void initialize() {

        labelTanggalCetak.setText(
                "Dicetak: " + LocalDate.now().format(FORMAT_TANGGAL)
        );

        containerTagihanPengembalian.setVisible(false);
        containerTagihanPengembalian.setManaged(false);

        tombolKembali.setCursor(Cursor.HAND);
        btnUnduhNota.setCursor(Cursor.HAND);
        btnUnduhRingkasan.setCursor(Cursor.HAND);
    }

    public void setTransaksi(Transaksi transaksi) {

        if (transaksi == null) {
            tampilkanPesan(
                    Alert.AlertType.WARNING,
                    "Data transaksi tidak tersedia."
            );
            return;
        }

        this.transaksiAktif = transaksi;

        LocalDate tanggalKembali =
                ambilTanggalKembali(transaksi);

        int lamaSewa =
                ambilLamaSewa(transaksi, tanggalKembali);

        labelIdTransaksi.setText(
                String.valueOf(transaksi.getIdTransaksi())
        );

        labelTanggalTransaksi.setText(
                formatTanggal(transaksi.getTanggalTransaksi())
        );

        labelPenyewa.setText(
                ambilNamaPenyewa(transaksi)
        );

        labelTglSewa.setText(
                formatTanggal(transaksi.getTanggalSewa())
        );

        labelTglKembali.setText(
                formatTanggal(tanggalKembali)
        );

        labelMetodePembayaran.setText(
                nilaiAtauStrip(transaksi.getMetodePembayaran())
        );

        labelStatus.setText("Berhasil");

        tampilkanDetailBarang(transaksi, lamaSewa);

        labelTotal.setText(
                NotaService.formatRupiah(transaksi.getTotalHarga())
        );

        tampilkanTagihanPengembalian(transaksi);

        tampilkanFoto(
                transaksi.getFotoIdentitas()
        );
    }

    private void tampilkanDetailBarang(
            Transaksi transaksi,
            int lamaSewa) {

        gridDetailBarang.getChildren().removeIf(node -> {
            Integer row = GridPane.getRowIndex(node);
            return row != null && row > 0;
        });

        long totalKuantitas = 0;
        int baris = 1;

        if (transaksi.getDaftarDetail() != null) {

            for (DetailTransaksi detail : transaksi.getDaftarDetail()) {

                if (detail == null
                        || detail.getPerlengkapanYangDisewa() == null
                        || detail.getKuantitasPerAlat() <= 0) {
                    continue;
                }

                String namaBarang = nilaiAtauStrip(
                        detail.getPerlengkapanYangDisewa()
                                .getNamaPerlengkapan()
                );

                long hargaPerHari =
                        detail.getHargaSewaHarianSaatTransaksi() != null
                                ? detail.getHargaSewaHarianSaatTransaksi()
                                : detail.getPerlengkapanYangDisewa()
                                        .getHargaSewaHarian();

                long subtotal = detail.getSubTotal(lamaSewa);

                totalKuantitas = Math.addExact(
                        totalKuantitas,
                        detail.getKuantitasPerAlat()
                );

                tambahBarisDetail(
                        baris++,
                        namaBarang,
                        String.valueOf(detail.getKuantitasPerAlat()),
                        lamaSewa + " hari",
                        NotaService.formatRupiah(hargaPerHari),
                        NotaService.formatRupiah(subtotal)
                );
            }
        }

        if (baris == 1) {
            tambahBarisDetail(
                    1,
                    "Tidak ada detail perlengkapan",
                    "-",
                    "-",
                    "-",
                    "-"
            );
        }

        labelTotalKuantitas.setText(
                String.valueOf(totalKuantitas)
        );
    }

    private void tambahBarisDetail(
            int baris,
            String namaBarang,
            String jumlah,
            String lamaSewa,
            String hargaPerHari,
            String subtotal) {

        String[] nilai = {
                namaBarang,
                jumlah,
                lamaSewa,
                hargaPerHari,
                subtotal
        };

        for (int kolom = 0; kolom < nilai.length; kolom++) {

            Label isi = new Label(nilai[kolom]);

            isi.setMaxWidth(Double.MAX_VALUE);
            isi.setMinHeight(38);
            isi.setWrapText(true);

            isi.setAlignment(
                    kolom == 0
                            ? Pos.CENTER_LEFT
                            : Pos.CENTER
            );

            isi.setPadding(
                    new Insets(8, 10, 8, 10)
            );

            isi.setStyle(
                    "-fx-background-color: #F5F5F5;" +
                    "-fx-font-size: 13px;" +
                    "-fx-text-fill: #222222;" +
                    "-fx-border-color: #E0E0E0;" +
                    "-fx-border-width: 0 0 1 0;"
            );

            GridPane.setHgrow(isi, Priority.ALWAYS);
            GridPane.setFillWidth(isi, true);

            gridDetailBarang.add(
                    isi,
                    kolom,
                    baris
            );
        }
    }

    private void tampilkanTagihanPengembalian(
            Transaksi transaksi) {

        boolean adaPengembalian = false;
        long totalGantiRugi = 0;

        if (transaksi.getDaftarDetail() != null) {

            for (DetailTransaksi detail : transaksi.getDaftarDetail()) {

                if (detail == null) {
                    continue;
                }

                if (detail.getTanggalPengembalianBarang() != null) {
                    adaPengembalian = true;
                }

                totalGantiRugi = Math.addExact(
                        totalGantiRugi,
                        detail.getBiayaGantiRugi()
                );
            }
        }

        if (!adaPengembalian) {
            containerTagihanPengembalian.setVisible(false);
            containerTagihanPengembalian.setManaged(false);
            return;
        }

        long totalDenda = transaksi.hitungDenda();

        long totalTagihan = Math.addExact(
                totalDenda,
                totalGantiRugi
        );

        labelDenda.setText(
                NotaService.formatRupiah(totalDenda)
        );

        labelGantiRugi.setText(
                NotaService.formatRupiah(totalGantiRugi)
        );

        labelTotalTagihanPengembalian.setText(
                NotaService.formatRupiah(totalTagihan)
        );

        containerTagihanPengembalian.setVisible(true);
        containerTagihanPengembalian.setManaged(true);
    }

    @FXML
    private void unduhNota() {

        if (transaksiAktif == null) {
            tampilkanPesan(
                    Alert.AlertType.WARNING,
                    "Data transaksi belum dimuat."
            );
            return;
        }

        transaksiAktif.cetakNota();

        simpanTeks(
                transaksiAktif.getIsiNota(),
                "Nota_Transaksi_" + transaksiAktif.getIdTransaksi() + ".txt",
                "Unduh Nota"
        );
    }

    @FXML
    private void unduhRingkasan() {

        if (transaksiAktif == null) {
            tampilkanPesan(
                    Alert.AlertType.WARNING,
                    "Data transaksi belum dimuat."
            );
            return;
        }

        transaksiAktif.ringkasanCetak();

        simpanTeks(
                transaksiAktif.getRingkasanTransaksi(),
                "Ringkasan_Transaksi_" + transaksiAktif.getIdTransaksi() + ".txt",
                "Unduh Ringkasan Transaksi"
        );
    }

    private void simpanTeks(
            String isi,
            String namaAwal,
            String judul) {

        if (isi == null || isi.trim().isEmpty()) {
            tampilkanPesan(
                    Alert.AlertType.ERROR,
                    "Isi dokumen tidak berhasil dibuat."
            );
            return;
        }

        FileChooser chooser = new FileChooser();

        chooser.setTitle(judul);
        chooser.setInitialFileName(namaAwal);

        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Text Files (*.txt)",
                        "*.txt"
                )
        );

        File tujuan = chooser.showSaveDialog(
                kertasNota.getScene().getWindow()
        );

        if (tujuan == null) {
            return;
        }

        try {

            Files.write(
                    tujuan.toPath(),
                    isi.getBytes(StandardCharsets.UTF_8)
            );

            tampilkanPesan(
                    Alert.AlertType.INFORMATION,
                    "Dokumen berhasil disimpan di:\n"
                            + tujuan.getAbsolutePath()
            );

        } catch (IOException e) {

            tampilkanPesan(
                    Alert.AlertType.ERROR,
                    "Dokumen gagal disimpan:\n" + e.getMessage()
            );
        }
    }

    public void setAksiKembali(Runnable aksiKembali) {
        this.aksiKembali = aksiKembali;
    }

    @FXML
    private void kembali() {

        if (aksiKembali != null) {
            aksiKembali.run();
        }
    }

    private String ambilNamaPenyewa(Transaksi transaksi) {

        if (transaksi.getPelanggan() == null) {
            return "-";
        }

        return nilaiAtauStrip(
                transaksi.getPelanggan().getNamaPelanggan()
        );
    }

    private LocalDate ambilTanggalKembali(Transaksi transaksi) {

        if (transaksi.getTanggalKembali() != null) {
            return transaksi.getTanggalKembali();
        }

        if (transaksi.getTanggalSewa() != null
                && transaksi.getDurasiSewa() > 0) {

            return transaksi.hitungTanggalKembali();
        }

        return null;
    }

    private int ambilLamaSewa(
            Transaksi transaksi,
            LocalDate tanggalKembali) {

        if (transaksi.getDurasiSewa() > 0) {
            return transaksi.getDurasiSewa();
        }

        long hari = NotaService.hitungLamaSewa(
                transaksi.getTanggalSewa(),
                tanggalKembali
        );

        if (hari > Integer.MAX_VALUE) {
            throw new IllegalArgumentException(
                    "Durasi sewa melebihi batas perhitungan."
            );
        }

        return (int) hari;
    }

    private String formatTanggal(LocalDate tanggal) {

        return tanggal == null
                ? "-"
                : tanggal.format(FORMAT_TANGGAL);
    }

    private String nilaiAtauStrip(String nilai) {

        return nilai == null || nilai.trim().isEmpty()
                ? "-"
                : nilai.trim();
    }

    private void tampilkanFoto(String lokasiFoto) {

        Image foto = null;

        if (lokasiFoto != null && !lokasiFoto.trim().isEmpty()) {

            String sumber = lokasiFoto.trim();

            try {

                if (sumber.regionMatches(true, 0, "file:", 0, 5)
                        || sumber.regionMatches(true, 0, "http://", 0, 7)
                        || sumber.regionMatches(true, 0, "https://", 0, 8)) {

                    foto = new Image(sumber, false);

                } else {

                    File file = new File(sumber);

                    if (file.isFile() && file.canRead()) {

                        foto = new Image(
                                file.toURI().toString(),
                                false
                        );

                    } else {

                        URL resource = getClass().getResource(
                                sumber.startsWith("/")
                                        ? sumber
                                        : "/images/" + sumber
                        );

                        if (resource != null) {
                            foto = new Image(
                                    resource.toExternalForm(),
                                    false
                            );
                        }
                    }
                }

                if (foto != null && foto.isError()) {
                    foto = null;
                }

            } catch (RuntimeException e) {
                foto = null;
            }
        }

        boolean tersedia = foto != null;

        imageFotoIdentitas.setImage(foto);
        imageFotoIdentitas.setVisible(tersedia);
        imageFotoIdentitas.setManaged(tersedia);

        labelFotoKosong.setVisible(!tersedia);
        labelFotoKosong.setManaged(!tersedia);
    }

    private void tampilkanPesan(
            Alert.AlertType tipe,
            String pesan) {

        Alert alert = new Alert(tipe);

        alert.setTitle("TAPAK - Nota Transaksi");
        alert.setHeaderText(null);
        alert.setContentText(pesan);

        alert.showAndWait();
    }
}
