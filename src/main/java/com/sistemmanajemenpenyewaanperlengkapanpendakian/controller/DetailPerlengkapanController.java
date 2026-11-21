
package com.sistemmanajemenpenyewaanperlengkapanpendakian.controller;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Kompor;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Perlengkapan;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Sepatu;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Tas;

import java.io.File;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class DetailPerlengkapanController {

    // =========================
    // KOMPONEN FXML
    // =========================

    @FXML
    private ImageView gambarPerlengkapan;

    @FXML
    private Label labelPlaceholderFoto;

    @FXML
    private Label labelNamaBarang;

    @FXML
    private Label labelHargaSewa;

    @FXML
    private Label labelStatusBarang;

    @FXML
    private Label labelJenisBarang;

    @FXML
    private Label labelNilaiJenisBarang;

    @FXML
    private Button btnKonfirmasi;


    // =========================
    // DATA DETAIL
    // =========================

    private Perlengkapan perlengkapanAktif;

    private Runnable aksiKembali;


    // =========================
    // MENERIMA DATA DARI DAFTAR
    // =========================

    public void setData(
            Perlengkapan perlengkapan,
            Runnable aksiKembali) {

        this.perlengkapanAktif = perlengkapan;
        this.aksiKembali = aksiKembali;

        tampilkanData();
    }


    // =========================
    // MENAMPILKAN DATA PERLENGKAPAN
    // =========================

    private void tampilkanData() {

        if (perlengkapanAktif == null) {

            btnKonfirmasi.setDisable(true);

            tampilkanPeringatan(
                    Alert.AlertType.ERROR,
                    "Data Tidak Tersedia",
                    "Data perlengkapan yang dipilih tidak ditemukan."
            );

            return;
        }


        // NAMA BARANG

        String nama =
                perlengkapanAktif.getNamaPerlengkapan();

        labelNamaBarang.setText(
                nama == null || nama.trim().isEmpty()
                        ? "-"
                        : nama
        );


        // HARGA SEWA PER HARI

        labelHargaSewa.setText(
                "Rp " + formatRupiah(
                        perlengkapanAktif.getHargaSewaHarian()
                )
        );


        // STATUS BARANG BERDASARKAN STOK

        if (perlengkapanAktif.getStok() > 0) {

            labelStatusBarang.setText("Tersedia");

            labelStatusBarang.setStyle(
                    "-fx-font-size: 18px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-text-fill: #00572B;"
            );

        } else {

            labelStatusBarang.setText("Stok Habis");

            labelStatusBarang.setStyle(
                    "-fx-font-size: 18px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-text-fill: #A3212B;"
            );
        }


        // DETAIL KHUSUS SUBCLASS

        tampilkanDetailJenis();


        // FOTO PERLENGKAPAN

        tampilkanFoto(
                perlengkapanAktif.getFotoBarang()
        );
    }


    // =========================
    // DETAIL JENIS PERLENGKAPAN
    // =========================

    private void tampilkanDetailJenis() {

        if (perlengkapanAktif instanceof Tas) {

            Tas tas = (Tas) perlengkapanAktif;

            labelJenisBarang.setText("Tipe Tas");

            labelNilaiJenisBarang.setText(
                    nilaiTeks(tas.getTipeTas())
            );

        } else if (perlengkapanAktif instanceof Kompor) {

            Kompor kompor = (Kompor) perlengkapanAktif;

            labelJenisBarang.setText("Tipe Kompor");

            labelNilaiJenisBarang.setText(
                    nilaiTeks(kompor.getTipeKompor())
            );

        } else if (perlengkapanAktif instanceof Sepatu) {

            Sepatu sepatu = (Sepatu) perlengkapanAktif;

            labelJenisBarang.setText("Ukuran Sepatu");

            labelNilaiJenisBarang.setText(
                    sepatu.getUkuranSepatu() > 0
                            ? String.valueOf(sepatu.getUkuranSepatu())
                            : "-"
            );

        } else {

            labelJenisBarang.setText("Detail Jenis Barang");

            labelNilaiJenisBarang.setText("-");
        }
    }


    // =========================
    // MENAMPILKAN FOTO
    // =========================

    private void tampilkanFoto(String lokasiFoto) {

        Image foto = muatFoto(lokasiFoto);

        if (foto != null) {

            gambarPerlengkapan.setImage(foto);

            gambarPerlengkapan.setVisible(true);
            gambarPerlengkapan.setManaged(true);

            labelPlaceholderFoto.setVisible(false);
            labelPlaceholderFoto.setManaged(false);

        } else {

            gambarPerlengkapan.setImage(null);

            gambarPerlengkapan.setVisible(false);
            gambarPerlengkapan.setManaged(false);

            labelPlaceholderFoto.setText(
                    ikonProduk(perlengkapanAktif)
            );

            labelPlaceholderFoto.setVisible(true);
            labelPlaceholderFoto.setManaged(true);
        }
    }


    private Image muatFoto(String lokasiFoto) {

        if (lokasiFoto == null || lokasiFoto.trim().isEmpty()) {
            return null;
        }

        try {

            String sumber = lokasiFoto.trim();

            if (!sumber.regionMatches(
                    true,
                    0,
                    "file:",
                    0,
                    5
            )) {

                File file = new File(sumber);

                if (file.isFile() && file.canRead()) {

                    sumber = file.toURI().toString();

                } else {

                    URL resource = getClass().getResource(
                            sumber.startsWith("/")
                                    ? sumber
                                    : "/images/" + sumber
                    );

                    if (resource == null) {
                        return null;
                    }

                    sumber = resource.toExternalForm();
                }
            }

            Image foto = new Image(sumber, false);

            return foto.isError() ? null : foto;

        } catch (RuntimeException e) {

            return null;
        }
    }


    private String ikonProduk(Perlengkapan perlengkapan) {

        if (perlengkapan instanceof Kompor) {
            return "🔥";
        }

        if (perlengkapan instanceof Sepatu) {
            return "🥾";
        }

        return "🎒";
    }


    // =========================
    // KONFIRMASI KEMBALI
    // =========================

    @FXML
    private void handleKonfirmasi() {

        if (aksiKembali != null) {

            aksiKembali.run();

            return;
        }

        tampilkanPeringatan(
                Alert.AlertType.ERROR,
                "Navigasi Tidak Tersedia",
                "Halaman daftar perlengkapan tidak terhubung."
        );
    }


    // =========================
    // HELPER
    // =========================

    private String nilaiTeks(String nilai) {

        return nilai == null || nilai.trim().isEmpty()
                ? "-"
                : nilai;
    }


    private String formatRupiah(long nominal) {

        NumberFormat format =
                NumberFormat.getIntegerInstance(
                        Locale.forLanguageTag("id-ID")
                );

        return format.format(nominal);
    }


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
