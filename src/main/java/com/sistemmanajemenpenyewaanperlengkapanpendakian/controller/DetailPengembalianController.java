package com.sistemmanajemenpenyewaanperlengkapanpendakian.controller;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.App;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.Service.PengembalianService;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.Service.TransaksiService;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.exception.DataTidakLengkapException;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.DetailTransaksi;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Perlengkapan;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Transaksi;



import java.io.File;
import java.net.URI;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

public class DetailPengembalianController {

    // >>> KOMPONEN FXML DETAIL

    @FXML
    private ImageView gambarPerlengkapan;

    @FXML
    private Label labelNamaBarang;

    @FXML
    private Label labelStatusPeminjaman;

    @FXML
    private Label labelNamaPeminjam;

    @FXML
    private Label labelKartuIdentitas;

    @FXML
    private Label labelTanggalMeminjam;

    @FXML
    private Label labelTanggalKembali;

    @FXML
    private Button btnKonfirmasi;

    @FXML
    private Button btnKembali;

    // <<< AKHIR KOMPONEN FXML


    // >>> KONFIGURASI DAN DATA DETAIL
    

    private static final String PATH_DAFTAR =
            "/com/sistemmanajemenpenyewaanperlengkapanpendakian/fxml/pages/pengembalian.fxml";

    private static final DateTimeFormatter FORMAT_TANGGAL =
            DateTimeFormatter.ofPattern("dd - MM - yyyy");

    private Transaksi transaksiAktif;

    private DetailTransaksi detailAktif;

    // Callback untuk kembali ke halaman daftar sebelumnya.
    private Runnable aksiKembali;

    // <<< AKHIR KONFIGURASI


    // =========================
    // INITIALIZE
    // =========================

    @FXML
    public void initialize() {

        styleDetail();

        btnKonfirmasi.setDisable(true);
    }


    // =========================
    // MENERIMA DATA DARI DAFTAR
    // =========================

    // >>> TAMBAHAN: MENERIMA TRANSAKSI DAN DETAIL TERPILIH

    public void setData(
            Transaksi transaksi,
            DetailTransaksi detail,
            Runnable aksiKembali) {

        this.transaksiAktif = transaksi;
        this.detailAktif = detail;
        this.aksiKembali = aksiKembali;

        tampilkanData();
    }

    private void tampilkanData() {

        if (transaksiAktif == null
                || detailAktif == null
                || detailAktif.getPerlengkapanYangDisewa() == null) {

            btnKonfirmasi.setDisable(true);

            tampilkanPesan(
                    Alert.AlertType.ERROR,
                    "Data Tidak Tersedia",
                    "Data transaksi atau detail perlengkapan tidak ditemukan."
            );

            return;
        }

        Perlengkapan barang =
                detailAktif.getPerlengkapanYangDisewa();

        // NAMA BARANG
        labelNamaBarang.setText(
                barang.getNamaPerlengkapan()
        );

        // NAMA PEMINJAM
        labelNamaPeminjam.setText(
                ambilNamaPeminjam(transaksiAktif)
        );

        // FOTO IDENTITAS
        labelKartuIdentitas.setText(
                namaFile(transaksiAktif.getFotoIdentitas())
        );

        // TANGGAL
        labelTanggalMeminjam.setText(
                formatTanggal(transaksiAktif.getTanggalSewa())
        );

        labelTanggalKembali.setText(
                formatTanggal(transaksiAktif.getTanggalKembali())
        );

        // FOTO PERLENGKAPAN
        gambarPerlengkapan.setImage(
                ambilGambar(barang.getFotoBarang())
        );

        // SUDAH DIKEMBALIKAN
        if (detailAktif.getTanggalPengembalianBarang() != null) {

            String kondisi =
                    detailAktif.getStatusPengembalianBarang();

            labelStatusPeminjaman.setText(
                    "Sudah Dikembalikan"
                    + (kondisi == null || kondisi.trim().isEmpty()
                    ? "" : " (" + kondisi + ")")
            );

            labelStatusPeminjaman.setStyle(
                    "-fx-text-fill: #00572B;" +
                    "-fx-font-size: 18px;" +
                    "-fx-font-weight: bold;"
            );

            btnKonfirmasi.setDisable(true);

            return;
        }

        // STATUS KETERLAMBATAN
        LocalDate jatuhTempo =
                transaksiAktif.getTanggalKembali();

        boolean terlambat =
                jatuhTempo != null
                && LocalDate.now().isAfter(jatuhTempo);

        if (terlambat) {

            labelStatusPeminjaman.setText("Terlambat");

            labelStatusPeminjaman.setStyle(
                    "-fx-text-fill: #A3212B;" +
                    "-fx-font-size: 18px;" +
                    "-fx-font-weight: bold;"
            );

        } else {

            labelStatusPeminjaman.setText("Sedang Dipinjam");

            labelStatusPeminjaman.setStyle(
                    "-fx-text-fill: #17652B;" +
                    "-fx-font-size: 18px;" +
                    "-fx-font-weight: bold;"
            );
        }

        btnKonfirmasi.setDisable(false);
    }

    // <<< AKHIR TAMBAHAN: DATA DETAIL


    // =========================
    // KONFIRMASI PENGEMBALIAN
    // =========================

    // >>> PERUBAHAN: KONFIRMASI DAN PEMILIHAN KONDISI

    @FXML
    private void handleKonfirmasi() {

        mintaKonfirmasi();
    }

    // Dipanggil juga oleh tombol Konfirmasi pada kartu daftar.
    public void mintaKonfirmasi() {

        if (transaksiAktif == null || detailAktif == null) {

            tampilkanPesan(
                    Alert.AlertType.INFORMATION,
                    "Informasi",
                    "Belum ada detail pengembalian yang dipilih."
            );

            return;
        }

        if (detailAktif.getTanggalPengembalianBarang() != null) {

            tampilkanPesan(
                    Alert.AlertType.INFORMATION,
                    "Informasi",
                    "Barang ini sudah dikembalikan."
            );

            btnKonfirmasi.setDisable(true);

            return;
        }

        // KONFIRMASI AWAL
        ButtonType batal = new ButtonType(
                "Batal",
                ButtonBar.ButtonData.CANCEL_CLOSE
        );

        ButtonType konfirmasi = new ButtonType(
                "Konfirmasi",
                ButtonBar.ButtonData.OK_DONE
        );

        Alert dialog = new Alert(
                Alert.AlertType.CONFIRMATION
        );

        dialog.setTitle("Konfirmasi Pengembalian");

        dialog.setHeaderText(null);

        dialog.setContentText(
                "Apakah Anda yakin ingin konfirmasi pengembalian ini?"
        );

        dialog.getButtonTypes().setAll(
                batal,
                konfirmasi
        );

        Optional<ButtonType> jawaban =
                dialog.showAndWait();

        if (!jawaban.isPresent()
                || !jawaban.get().equals(konfirmasi)) {
            return;
        }

        // PILIH KONDISI BARANG
        ChoiceDialog<String> dialogKondisi =
                new ChoiceDialog<>(
                        "utuh",
                        "utuh",
                        "rusak",
                        "hilang"
                );

        dialogKondisi.setTitle(
                "Kondisi Perlengkapan"
        );

        dialogKondisi.setHeaderText(
                "Pilih kondisi perlengkapan yang dikembalikan"
        );

        dialogKondisi.setContentText(
                "Kondisi:"
        );

        Optional<String> kondisi =
                dialogKondisi.showAndWait();

        if (!kondisi.isPresent()) {
            return;
        }

        prosesPengembalian(kondisi.get());
    }

    // <<< AKHIR PERUBAHAN: KONFIRMASI


    // =========================
    // PROSES PENGEMBALIAN
    // =========================

    // >>> TAMBAHAN: MENGHUBUNGKAN PENGEMBALIAN SERVICE

    private void prosesPengembalian(String kondisi) {

        btnKonfirmasi.setDisable(true);

        try {

            int idVendor =
                    TransaksiService.getIdVendorAktif();

            PengembalianService.HasilPengembalian hasil =
                    PengembalianService.prosesPengembalian(
                            transaksiAktif,
                            detailAktif,
                            LocalDate.now(),
                            kondisi,
                            idVendor
                    );

            // Service menyelaraskan objek detail setelah commit.
            tampilkanData();

            String ringkasan =
                    "Pengembalian berhasil dikonfirmasi!\n\n"
                    + "Barang: "
                    + detailAktif.getPerlengkapanYangDisewa()
                            .getNamaPerlengkapan()
                    + "\n"
                    + "Kondisi: "
                    + kondisi.toUpperCase(Locale.ROOT)
                    + "\n"
                    + "Denda Keterlambatan: "
                    + formatRupiah(hasil.getDenda())
                    + "\n"
                    + "Biaya Ganti Rugi: "
                    + formatRupiah(hasil.getGantiRugi())
                    + "\n"
                    + "------------------------------------\n"
                    + "Total Tagihan Pengembalian: "
                    + formatRupiah(hasil.getTotalTagihan());

            tampilkanPesan(
                    Alert.AlertType.INFORMATION,
                    "Pengembalian Berhasil",
                    ringkasan
            );

        } catch (DataTidakLengkapException | SQLException e) {

            btnKonfirmasi.setDisable(false);

            tampilkanPesan(
                    Alert.AlertType.ERROR,
                    "Gagal Memproses Pengembalian",
                    e.getMessage()
            );

        } catch (RuntimeException e) {

            btnKonfirmasi.setDisable(false);

            tampilkanPesan(
                    Alert.AlertType.ERROR,
                    "Kesalahan Pengembalian",
                    e.getMessage() == null
                            ? "Terjadi kesalahan saat memproses pengembalian."
                            : e.getMessage()
            );
        }
    }

    // <<< AKHIR TAMBAHAN: PROSES PENGEMBALIAN


    // =========================
    // KEMBALI KE DAFTAR
    // =========================

    // >>> PERUBAHAN: KEMBALI KE HALAMAN DAFTAR

    @FXML
    private void handleKembali() {

        if (aksiKembali != null) {

            // Mengembalikan halaman daftar yang sebelumnya dibuka.
            aksiKembali.run();

            return;
        }

        // Fallback jika detail dibuka di luar alur daftar.
        try {

            if (btnKembali.getScene() == null) {
                throw new IllegalStateException(
                        "Scene halaman detail tidak ditemukan."
                );
            }

            Node nodeLayout =
                    btnKembali.getScene().lookup("#layoutPage");

            if (!(nodeLayout instanceof BorderPane)) {
                throw new IllegalStateException(
                        "BorderPane #layoutPage tidak ditemukan."
                );
            }

            URL lokasiDaftar =
                    App.class.getResource(PATH_DAFTAR);

            if (lokasiDaftar == null) {
                throw new IllegalStateException(
                        "pengembalian.fxml tidak ditemukan."
                );
            }

            Parent halamanDaftar =
                    FXMLLoader.load(lokasiDaftar);

            BorderPane layout =
                    (BorderPane) nodeLayout;

            BorderPane.setMargin(
                    halamanDaftar,
                    new Insets(16)
            );

            layout.setCenter(halamanDaftar);

        } catch (Exception e) {

            tampilkanPesan(
                    Alert.AlertType.ERROR,
                    "Gagal Kembali",
                    e.getMessage() == null ? e.toString() : e.getMessage()
            );
        }
    }

    // <<< AKHIR PERUBAHAN: KEMBALI


    // =========================
    // INFORMASI PELANGGAN
    // =========================

    private String ambilNamaPeminjam(Transaksi transaksi) {

        if (transaksi.getPelanggan() == null
                || transaksi.getPelanggan().getNamaPelanggan() == null
                || transaksi.getPelanggan()
                        .getNamaPelanggan().trim().isEmpty()) {

            return "Nama pelanggan tidak tersedia";
        }

        return transaksi.getPelanggan().getNamaPelanggan();
    }

    private String namaFile(String lokasi) {

        if (lokasi == null || lokasi.trim().isEmpty()) {
            return "Tidak tersedia";
        }

        try {

            if (lokasi.startsWith("file:")) {
                return new File(
                        URI.create(lokasi)
                ).getName();
            }

            return new File(lokasi).getName();

        } catch (RuntimeException e) {
            return lokasi;
        }
    }


    // =========================
    // GAMBAR PERLENGKAPAN
    // =========================

    private Image ambilGambar(String lokasi) {

        if (lokasi == null || lokasi.trim().isEmpty()) {
            return null;
        }

        try {

            String sumber = lokasi.trim();

            URL resource = getClass().getResource(
                    sumber.startsWith("/")
                            ? sumber
                            : "/" + sumber
            );

            if (resource != null) {

                sumber = resource.toExternalForm();

            } else if (!sumber.startsWith("file:")
                    && !sumber.startsWith("http://")
                    && !sumber.startsWith("https://")) {

                File file = new File(sumber);

                if (!file.exists()) {
                    return null;
                }

                sumber = file.toURI().toString();
            }

            Image gambar = new Image(
                    sumber,
                    144,
                    140,
                    true,
                    true
            );

            return gambar.isError() ? null : gambar;

        } catch (IllegalArgumentException e) {

            System.err.println(
                    "Gagal memuat foto perlengkapan: " + lokasi
            );

            return null;
        }
    }


    // =========================
    // FORMAT TANGGAL DAN RUPIAH
    // =========================

    private String formatTanggal(LocalDate tanggal) {

        return tanggal == null
                ? "-"
                : tanggal.format(FORMAT_TANGGAL);
    }

    private String formatRupiah(long nilai) {

        return "Rp " + NumberFormat.getIntegerInstance(
                Locale.forLanguageTag("id-ID")
        ).format(nilai);
    }


    // =========================
    // STYLING
    // =========================

    private void styleDetail() {

        btnKembali.setCursor(Cursor.HAND);
        btnKonfirmasi.setCursor(Cursor.HAND);
    }


    // =========================
    // ALERT
    // =========================

    private void tampilkanPesan(
            Alert.AlertType tipe,
            String judul,
            String pesan) {

        Alert alert = new Alert(tipe);

        alert.setTitle(judul);
        alert.setHeaderText(null);
        alert.setContentText(
                pesan == null ? "Tidak ada keterangan tambahan." : pesan
        );

        alert.showAndWait();
    }
}
