
package com.sistemmanajemenpenyewaanperlengkapanpendakian.controller;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.Service.PerlengkapanService;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.Service.TransaksiService;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Kompor;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Perlengkapan;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Sepatu;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Tas;

import java.io.File;
import java.sql.SQLException;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;

public class ControllerFormulirDataPerlengkapan {

    @FXML
    private TextField fieldNamaBarang;

    @FXML
    private ComboBox<String> comboJenisPerlengkapan;

    @FXML
    private Label labelDetailJenis;

    @FXML
    private TextField fieldDetailJenis;

    @FXML
    private TextField fieldHargaPerlengkapan;

    @FXML
    private TextField fieldHargaSewaHarian;

    @FXML
    private TextField fieldStok;

    @FXML
    private Button btnPilihFoto;

    @FXML
    private Label labelFoto;

    @FXML
    private javafx.scene.image.ImageView imagePreviewFoto;

    private final PerlengkapanService perlengkapanService =
            new PerlengkapanService();

    private String fotoTerpilih;
    private Runnable aksiKembali;

    @FXML
    private void initialize() {

        comboJenisPerlengkapan.getItems().setAll(
                "Tas",
                "Kompor",
                "Sepatu"
        );

        comboJenisPerlengkapan.setValue("Tas");

        comboJenisPerlengkapan.valueProperty().addListener(
                (observable, jenisLama, jenisBaru) ->
                        aturDetailJenis(jenisBaru)
        );

        aturDetailJenis(comboJenisPerlengkapan.getValue());

        imagePreviewFoto.setVisible(false);
        imagePreviewFoto.setManaged(false);
    }

    // =========================
    // CALLBACK KEMBALI KE DAFTAR
    // =========================

    public void setAksiKembali(Runnable aksiKembali) {
        this.aksiKembali = aksiKembali;
    }

    @FXML
    private void batal() {
        kembaliKeDaftar();
    }

    private void kembaliKeDaftar() {

        if (aksiKembali != null) {
            aksiKembali.run();
            return;
        }

        tampilkanPeringatan(
                Alert.AlertType.INFORMATION,
                "Navigasi Tidak Tersedia",
                "Halaman daftar perlengkapan tidak terhubung. Buka formulir melalui menu Data Perlengkapan."
        );
    }

    // =========================
    // JENIS PERLENGKAPAN
    // =========================

    private void aturDetailJenis(String jenis) {

        if (jenis == null) {
            return;
        }

        if ("Kompor".equals(jenis)) {

            labelDetailJenis.setText("Tipe Kompor");
            fieldDetailJenis.setPromptText("Contoh: Kompor 1 tungku");

        } else if ("Sepatu".equals(jenis)) {

            labelDetailJenis.setText("Ukuran Sepatu");
            fieldDetailJenis.setPromptText("Contoh: 40");

        } else {

            labelDetailJenis.setText("Tipe Tas");
            fieldDetailJenis.setPromptText("Contoh: Carrier 45 L");
        }

        fieldDetailJenis.clear();
    }

    // =========================
    // MEMILIH FOTO PERLENGKAPAN
    // =========================

    @FXML
    private void pilihFoto() {

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Pilih Foto Perlengkapan");

        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "File gambar",
                        "*.png",
                        "*.jpg",
                        "*.jpeg"
                )
        );

        File file = chooser.showOpenDialog(
                btnPilihFoto.getScene() == null
                        ? null
                        : btnPilihFoto.getScene().getWindow()
        );

        if (file == null) {
            return;
        }

        fotoTerpilih = file.toURI().toString();
        labelFoto.setText(file.getName());

        Image gambar = new Image(
                fotoTerpilih,
                180,
                100,
                true,
                true
        );

        if (!gambar.isError()) {
            imagePreviewFoto.setImage(gambar);
            imagePreviewFoto.setVisible(true);
            imagePreviewFoto.setManaged(true);
            btnPilihFoto.setText("Ganti Foto");
        }
    }

    // =========================
    // KONFIRMASI TAMBAH DATA
    // =========================

    @FXML
    private void konfirmasiTambah() {

        if (!validasiFormulir()) {
            return;
        }

        ButtonType batal = new ButtonType(
                "Batal",
                ButtonBar.ButtonData.CANCEL_CLOSE
        );

        ButtonType konfirmasi = new ButtonType(
                "Konfirmasi",
                ButtonBar.ButtonData.OK_DONE
        );

        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Konfirmasi Perubahan");
        dialog.setHeaderText(null);
        dialog.setContentText(
                "Apakah Anda yakin ingin konfirmasi perubahan ini?"
        );
        dialog.getButtonTypes().setAll(batal, konfirmasi);

        Optional<ButtonType> jawaban = dialog.showAndWait();

        if (!jawaban.isPresent() || jawaban.get() != konfirmasi) {
            return;
        }

        simpanPerlengkapan();
    }

    // =========================
    // VALIDASI FORMULIR
    // =========================

    private boolean validasiFormulir() {

        String nama = fieldNamaBarang.getText();
        String jenis = comboJenisPerlengkapan.getValue();
        String detail = fieldDetailJenis.getText();

        if (nama == null || nama.trim().isEmpty()) {
            tampilkanPeringatan(
                    Alert.AlertType.WARNING,
                    "Data Belum Lengkap",
                    "Nama barang wajib diisi."
            );
            fieldNamaBarang.requestFocus();
            return false;
        }

        if (nama.trim().length() > 150) {
            tampilkanPeringatan(
                    Alert.AlertType.WARNING,
                    "Nama Barang Terlalu Panjang",
                    "Nama perlengkapan maksimal 150 karakter."
            );
            fieldNamaBarang.requestFocus();
            return false;
        }

        if (jenis == null) {
            tampilkanPeringatan(
                    Alert.AlertType.WARNING,
                    "Jenis Belum Dipilih",
                    "Pilih jenis perlengkapan terlebih dahulu."
            );
            comboJenisPerlengkapan.requestFocus();
            return false;
        }

        if (detail == null || detail.trim().isEmpty()) {
            tampilkanPeringatan(
                    Alert.AlertType.WARNING,
                    "Data Jenis Belum Lengkap",
                    labelDetailJenis.getText() + " wajib diisi."
            );
            fieldDetailJenis.requestFocus();
            return false;
        }

        if ("Sepatu".equals(jenis)) {
            try {
                if (Integer.parseInt(detail.trim()) <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                tampilkanPeringatan(
                        Alert.AlertType.WARNING,
                        "Ukuran Sepatu Tidak Valid",
                        "Ukuran sepatu harus berupa bilangan bulat lebih dari 0."
                );
                fieldDetailJenis.requestFocus();
                return false;
            }
        }

        try {

            int stok = Integer.parseInt(fieldStok.getText().trim());

            long hargaPerlengkapan = parsingNominal(
                    fieldHargaPerlengkapan.getText()
            );

            long hargaSewa = parsingNominal(
                    fieldHargaSewaHarian.getText()
            );

            if (stok < 0) {
                throw new IllegalArgumentException(
                        "Stok tidak boleh negatif."
                );
            }

            if (hargaPerlengkapan <= 0) {
                throw new IllegalArgumentException(
                        "Harga perlengkapan harus lebih dari 0."
                );
            }

            if (hargaSewa <= 0) {
                throw new IllegalArgumentException(
                        "Harga sewa harian harus lebih dari 0."
                );
            }

            return true;

        } catch (NumberFormatException e) {

            tampilkanPeringatan(
                    Alert.AlertType.WARNING,
                    "Format Angka Tidak Valid",
                    "Stok dan kedua harga harus diisi dengan angka yang valid."
            );

            return false;

        } catch (IllegalArgumentException e) {

            tampilkanPeringatan(
                    Alert.AlertType.WARNING,
                    "Data Tidak Valid",
                    e.getMessage()
            );

            return false;
        }
    }

    // =========================
    // MENYIMPAN KE DATABASE
    // =========================

    private void simpanPerlengkapan() {

        try {

            int idVendor = TransaksiService.getIdVendorAktif();

            Perlengkapan perlengkapan = buatObjekPerlengkapan();

            perlengkapanService.tambahDataPerlengkapan(
                    perlengkapan,
                    idVendor
            );

            tampilkanPeringatan(
                    Alert.AlertType.INFORMATION,
                    "Berhasil",
                    "Data perlengkapan berhasil ditambahkan."
            );

            kembaliKeDaftar();

        } catch (SQLException | IllegalArgumentException e) {

            tampilkanPeringatan(
                    Alert.AlertType.ERROR,
                    "Gagal Menambahkan Perlengkapan",
                    e.getMessage()
            );
        }
    }

    private Perlengkapan buatObjekPerlengkapan() {

        String jenis = comboJenisPerlengkapan.getValue();
        String detail = fieldDetailJenis.getText().trim();

        Perlengkapan perlengkapan;

        if ("Kompor".equals(jenis)) {

            Kompor kompor = new Kompor();
            kompor.setTipeKompor(detail);
            perlengkapan = kompor;

        } else if ("Sepatu".equals(jenis)) {

            Sepatu sepatu = new Sepatu();
            sepatu.setUkuranSepatu(Integer.parseInt(detail));
            perlengkapan = sepatu;

        } else {

            Tas tas = new Tas();
            tas.setTipeTas(detail);
            perlengkapan = tas;
        }

        perlengkapan.setNamaPerlengkapan(
                fieldNamaBarang.getText().trim()
        );

        perlengkapan.setStok(
                Integer.parseInt(fieldStok.getText().trim())
        );

        perlengkapan.setHargaPerlengkapan(
                parsingNominal(fieldHargaPerlengkapan.getText())
        );

        perlengkapan.setHargaSewaHarian(
                parsingNominal(fieldHargaSewaHarian.getText())
        );

        perlengkapan.setFotoBarang(fotoTerpilih);

        return perlengkapan;
    }

    // =========================
    // PARSING HARGA
    // =========================

    private long parsingNominal(String nilai) {

        if (nilai == null) {
            throw new NumberFormatException("Harga belum diisi.");
        }

        String angka = nilai.trim()
                .replaceAll("(?i)rp", "")
                .replace(".", "")
                .replace(",", "")
                .replace(" ", "");

        if (!angka.matches("[0-9]+")) {
            throw new NumberFormatException("Format harga tidak valid.");
        }

        return Long.parseLong(angka);
    }

    // =========================
    // ALERT
    // =========================

    private void tampilkanPeringatan(
            Alert.AlertType tipe,
            String judul,
            String pesan) {

        Alert alert = new Alert(tipe);
        alert.setTitle(judul);
        alert.setHeaderText(null);
        alert.setContentText(
                pesan == null || pesan.trim().isEmpty()
                        ? "Terjadi kesalahan yang tidak diketahui."
                        : pesan
        );
        alert.showAndWait();
    }
}
