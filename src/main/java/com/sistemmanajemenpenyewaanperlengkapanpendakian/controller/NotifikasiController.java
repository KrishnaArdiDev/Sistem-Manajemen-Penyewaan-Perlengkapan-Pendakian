package com.sistemmanajemenpenyewaanperlengkapanpendakian.controller;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.App;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.Service.TransaksiService;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.DetailTransaksi;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Perlengkapan;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Transaksi;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class NotifikasiController {

    @FXML
    private VBox rootNotifikasi;

    @FXML
    private ScrollPane scrollDaftarNotifikasi;

    @FXML
    private VBox containerNotifikasi;

    @FXML
    private HBox containerPagination;

    private static final int ITEM_PER_HALAMAN = 5;

    private static final String PATH_KARTU =
            "/com/sistemmanajemenpenyewaanperlengkapanpendakian/fxml/components/card-produk-disewa.fxml";

    private static final String PATH_DETAIL =
            "/com/sistemmanajemenpenyewaanperlengkapanpendakian/fxml/pages/detail-pengembalian.fxml";

    private static final DateTimeFormatter FORMAT_TANGGAL =
            DateTimeFormatter.ofPattern("dd - MM - yyyy");

    private final List<ItemNotifikasi> daftarNotifikasi =
            new ArrayList<>();

    private int halamanAktif = 0;


    // =========================
    // INITIALIZE
    // =========================

    @FXML
    public void initialize() {
        styleHalaman();
        muatDataNotifikasi();
    }


    // =========================
    // MEMUAT DATA NOTIFIKASI
    // =========================

    private void muatDataNotifikasi() {

        daftarNotifikasi.clear();

        try {

            int idVendor = TransaksiService.getIdVendorAktif();

            List<Transaksi> daftarTransaksi =
                    TransaksiService.semua(idVendor);

            if (daftarTransaksi != null) {

                for (Transaksi transaksi : daftarTransaksi) {

                    if (transaksi == null
                            || transaksi.getDaftarDetail() == null) {
                        continue;
                    }

                    for (DetailTransaksi detail : transaksi.getDaftarDetail()) {

                        if (detail == null
                                || detail.getPerlengkapanYangDisewa() == null) {
                            continue;
                        }

                        // Barang yang sudah dikembalikan
                        // tidak ditampilkan di halaman Notifikasi.
                        if (detail.getTanggalPengembalianBarang() != null) {
                            continue;
                        }

                        daftarNotifikasi.add(
                                new ItemNotifikasi(transaksi, detail)
                        );
                    }
                }
            }

            // Terlambat selalu berada di atas Akan Terlambat.
            // Tanggal jatuh tempo yang lebih awal ditampilkan dahulu.
            daftarNotifikasi.sort(
                    Comparator
                            .comparing(
                                    (ItemNotifikasi item) -> !item.isTerlambat()
                            )
                            .thenComparing(
                                    item -> item.transaksi.getTanggalKembali(),
                                    Comparator.nullsLast(
                                            Comparator.naturalOrder()
                                    )
                            )
            );

            // Menyesuaikan halaman aktif apabila jumlah notifikasi berkurang.
            int jumlahHalaman = hitungJumlahHalaman();

            if (halamanAktif >= jumlahHalaman) {
                halamanAktif = jumlahHalaman - 1;
            }

            if (halamanAktif < 0) {
                halamanAktif = 0;
            }

            tampilkanHalamanAktif();

        } catch (IllegalArgumentException e) {

            daftarNotifikasi.clear();
            halamanAktif = 0;

            tampilkanStatusKosong(
                    "Login vendor diperlukan untuk melihat notifikasi."
            );

            aturPagination();

        } catch (SQLException e) {

            daftarNotifikasi.clear();
            halamanAktif = 0;

            tampilkanStatusKosong(
                    "Data notifikasi gagal dimuat dari database."
            );

            aturPagination();

            tampilkanPesan(
                    Alert.AlertType.ERROR,
                    "Gagal Memuat Notifikasi",
                    e.getMessage()
            );
        }
    }


    // =========================
    // PAGINATION
    // =========================

    private int hitungJumlahHalaman() {

        return Math.max(
                1,
                (int) Math.ceil(
                        (double) daftarNotifikasi.size() / ITEM_PER_HALAMAN
                )
        );
    }


    private void tampilkanHalamanAktif() {

        containerNotifikasi.getChildren().clear();

        if (daftarNotifikasi.isEmpty()) {

            tampilkanStatusKosong(
                    "Belum ada notifikasi."
            );

            aturPagination();
            return;
        }

        int indeksAwal = halamanAktif * ITEM_PER_HALAMAN;

        int indeksAkhir = Math.min(
                indeksAwal + ITEM_PER_HALAMAN,
                daftarNotifikasi.size()
        );

        for (int i = indeksAwal; i < indeksAkhir; i++) {

            ItemNotifikasi item = daftarNotifikasi.get(i);

            try {

                containerNotifikasi.getChildren().add(
                        buatKartuNotifikasi(item)
                );

            } catch (Exception e) {

                e.printStackTrace();

                tampilkanPesan(
                        Alert.AlertType.ERROR,
                        "Gagal Memuat Kartu Notifikasi",
                        e.getMessage()
                );
            }
        }

        aturPagination();

        scrollDaftarNotifikasi.setVvalue(0);
    }


    private void aturPagination() {

        containerPagination.getChildren().clear();

        int jumlahHalaman = hitungJumlahHalaman();

        Button sebelumnya = buatTombolPagination("‹");

        sebelumnya.setDisable(halamanAktif == 0);

        sebelumnya.setOnAction(event -> {

            if (halamanAktif > 0) {
                halamanAktif--;
                tampilkanHalamanAktif();
            }
        });

        containerPagination.getChildren().add(sebelumnya);

        for (int i = 0; i < jumlahHalaman; i++) {

            final int nomorHalaman = i;

            Button nomor = buatTombolPagination(
                    String.valueOf(i + 1)
            );

            if (i == halamanAktif) {

                nomor.setStyle(
                        "-fx-background-color: #005A32;" +
                        "-fx-text-fill: #FFFFFF;" +
                        "-fx-background-radius: 6;" +
                        "-fx-border-color: #005A32;" +
                        "-fx-border-radius: 6;"
                );
            }

            nomor.setOnAction(event -> {

                halamanAktif = nomorHalaman;
                tampilkanHalamanAktif();
            });

            containerPagination.getChildren().add(nomor);
        }

        Button berikutnya = buatTombolPagination("›");

        berikutnya.setDisable(
                halamanAktif >= jumlahHalaman - 1
        );

        berikutnya.setOnAction(event -> {

            if (halamanAktif < jumlahHalaman - 1) {
                halamanAktif++;
                tampilkanHalamanAktif();
            }
        });

        containerPagination.getChildren().add(berikutnya);

        containerPagination.setVisible(true);
        containerPagination.setManaged(true);
    }


    private Button buatTombolPagination(String teks) {

        Button tombol = new Button(teks);

        tombol.setMinSize(34, 30);
        tombol.setPrefSize(34, 30);
        tombol.setMaxSize(34, 30);
        tombol.setCursor(Cursor.HAND);

        tombol.setStyle(
                "-fx-background-color: #FFFFFF;" +
                "-fx-text-fill: #005A32;" +
                "-fx-border-color: #005A32;" +
                "-fx-border-radius: 6;" +
                "-fx-background-radius: 6;" +
                "-fx-font-weight: bold;"
        );

        return tombol;
    }


    // =========================
    // MEMBUAT KARTU NOTIFIKASI
    // =========================

    private HBox buatKartuNotifikasi(
            ItemNotifikasi item) throws Exception {

        URL lokasiKartu = App.class.getResource(PATH_KARTU);

        if (lokasiKartu == null) {
            throw new IllegalStateException(
                    "card-produk-disewa.fxml tidak ditemukan."
            );
        }

        FXMLLoader loader = new FXMLLoader(lokasiKartu);

        HBox kartu = loader.load();

        Label labelNamaBarang = komponen(
                loader, "labelNamaPerlengkapan", Label.class
        );

        Label labelNamaPenyewa = komponen(
                loader, "labelNamaPenyewa", Label.class
        );

        Label labelTanggalSewa = komponen(
                loader, "labelTanggalSewa", Label.class
        );

        Label labelTanggalKembali = komponen(
                loader, "labelTanggalSelesaiSewa", Label.class
        );

        Label labelStatus = komponen(
                loader, "labelStatusProduk", Label.class
        );

        ImageView gambarProduk = komponen(
                loader, "gambarProduk", ImageView.class
        );

        Region indikator = komponen(
                loader, "indikatorStatusProduk", Region.class
        );

        Button btnDetail = komponen(
                loader, "btnDetail", Button.class
        );

        Button btnKonfirmasi = komponen(
                loader, "btnKonfirmasi", Button.class
        );

        Perlengkapan barang =
                item.detail.getPerlengkapanYangDisewa();


        // =========================
        // INFORMASI BARANG
        // =========================

        labelNamaBarang.setText(
                barang.getNamaPerlengkapan()
        );

        labelNamaPenyewa.setText(
                ambilNamaPeminjam(item.transaksi)
        );

        labelTanggalSewa.setText(
                formatTanggal(item.transaksi.getTanggalSewa())
        );

        labelTanggalKembali.setText(
                formatTanggal(item.transaksi.getTanggalKembali())
        );

        gambarProduk.setImage(
                ambilGambar(barang.getFotoBarang(), 97, 97)
        );


        // =========================
        // STATUS DAN WARNA KARTU
        // =========================

        boolean terlambat = item.isTerlambat();

        String warna = terlambat ? "#A3212B" : "#C28A00";
        String latar = terlambat ? "#FFD6D6" : "#FFF3C4";

        labelStatus.setText(item.labelStatus());

        labelStatus.setStyle(
                "-fx-background-color: " + latar + ";" +
                "-fx-border-color: " + warna + ";" +
                "-fx-border-width: 1.5;" +
                "-fx-border-radius: 14;" +
                "-fx-background-radius: 14;" +
                "-fx-text-fill: " + warna + ";" +
                "-fx-font-size: 13px;"
        );

        indikator.setStyle(
                "-fx-background-color: " + warna + ";" +
                "-fx-background-radius: 8;"
        );

        kartu.setStyle(
                "-fx-background-color: #F5F5F5;" +
                "-fx-background-radius: 12;"
        );


        // =========================
        // STYLE INFORMASI
        // =========================

        labelNamaBarang.setStyle(
                "-fx-font-size: 18px;" +
                "-fx-text-fill: #000000;" +
                "-fx-font-weight: bold;"
        );

        labelNamaPenyewa.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-text-fill: #868686;"
        );

        labelTanggalSewa.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-text-fill: #868686;"
        );

        labelTanggalKembali.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-text-fill: #868686;"
        );


        // =========================
        // TOMBOL DETAIL
        // =========================

        btnDetail.setCursor(Cursor.HAND);

        btnDetail.setStyle(
                "-fx-background-color: #FFFFFF;" +
                "-fx-border-color: #005A32;" +
                "-fx-border-radius: 14;" +
                "-fx-background-radius: 14;" +
                "-fx-text-fill: #005A32;"
        );

        btnDetail.setOnAction(
                event -> bukaDetail(item, false)
        );


        // =========================
        // TOMBOL KONFIRMASI
        // =========================

        btnKonfirmasi.setCursor(Cursor.HAND);

        btnKonfirmasi.setStyle(
                "-fx-background-color: #005A32;" +
                "-fx-border-color: #005A32;" +
                "-fx-border-radius: 14;" +
                "-fx-background-radius: 14;" +
                "-fx-text-fill: #FFFFFF;"
        );

        btnKonfirmasi.setOnAction(
                event -> bukaDetail(item, true)
        );

        kartu.setMaxWidth(Double.MAX_VALUE);

        return kartu;
    }


    // =========================
    // MENGAMBIL KOMPONEN FXML
    // =========================

    private <T> T komponen(
            FXMLLoader loader,
            String fxId,
            Class<T> tipe) {

        Object value = loader.getNamespace().get(fxId);

        if (!tipe.isInstance(value)) {

            throw new IllegalStateException(
                    "Komponen fx:id '" + fxId
                    + "' tidak ditemukan atau bertipe salah."
            );
        }

        return tipe.cast(value);
    }


    // =========================
    // MEMBUKA DETAIL PENGEMBALIAN
    // =========================

    private void bukaDetail(
            ItemNotifikasi item,
            boolean langsungKonfirmasi) {

        try {

            Scene scene = rootNotifikasi.getScene();

            if (scene == null) {
                throw new IllegalStateException(
                        "Halaman Notifikasi belum berada di Scene."
                );
            }

            Node nodeLayout = scene.lookup("#layoutPage");

            if (!(nodeLayout instanceof BorderPane)) {

                throw new IllegalStateException(
                        "BorderPane #layoutPage tidak ditemukan."
                );
            }

            BorderPane layoutPage = (BorderPane) nodeLayout;

            Node halamanNotifikasi = layoutPage.getCenter();

            if (halamanNotifikasi == null) {

                throw new IllegalStateException(
                        "Halaman Notifikasi tidak ditemukan."
                );
            }

            Insets marginSebelumnya =
                    BorderPane.getMargin(halamanNotifikasi);

            URL lokasiDetail = App.class.getResource(PATH_DETAIL);

            if (lokasiDetail == null) {

                throw new IllegalStateException(
                        "detail-pengembalian.fxml tidak ditemukan."
                );
            }

            FXMLLoader loader = new FXMLLoader(lokasiDetail);

            Parent halamanDetail = loader.load();

            DetailPengembalianController controller =
                    loader.getController();

            controller.setData(
                    item.transaksi,
                    item.detail,
                    () -> {

                        if (marginSebelumnya != null) {

                            BorderPane.setMargin(
                                    halamanNotifikasi,
                                    marginSebelumnya
                            );
                        }

                        layoutPage.setCenter(halamanNotifikasi);

                        // Memuat ulang daftar Notifikasi.
                        // Barang yang sudah dikembalikan
                        // otomatis tidak muncul lagi.
                        muatDataNotifikasi();
                    }
            );

            BorderPane.setMargin(
                    halamanDetail,
                    new Insets(16)
            );

            layoutPage.setCenter(halamanDetail);

            if (langsungKonfirmasi) {
                controller.mintaKonfirmasi();
            }

        } catch (Exception e) {

            tampilkanPesan(
                    Alert.AlertType.ERROR,
                    "Gagal Membuka Detail",
                    e.getMessage() == null
                            ? e.toString()
                            : e.getMessage()
            );
        }
    }


    // =========================
    // MEMUAT GAMBAR
    // =========================

    private Image ambilGambar(
            String lokasi,
            double lebar,
            double tinggi) {

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
                    lebar,
                    tinggi,
                    true,
                    true
            );

            return gambar.isError() ? null : gambar;

        } catch (IllegalArgumentException e) {

            System.err.println(
                    "Gagal memuat gambar perlengkapan: " + lokasi
            );

            return null;
        }
    }


    // =========================
    // INFORMASI PEMINJAM
    // =========================

    private String ambilNamaPeminjam(Transaksi transaksi) {

        if (transaksi.getPelanggan() == null
                || transaksi.getPelanggan().getNamaPelanggan() == null
                || transaksi.getPelanggan().getNamaPelanggan()
                        .trim().isEmpty()) {

            return "Nama pelanggan tidak tersedia";
        }

        return transaksi.getPelanggan().getNamaPelanggan();
    }


    // =========================
    // FORMAT TANGGAL
    // =========================

    private String formatTanggal(LocalDate tanggal) {

        return tanggal == null
                ? "-"
                : tanggal.format(FORMAT_TANGGAL);
    }


    // =========================
    // STATUS KOSONG
    // =========================

    private void tampilkanStatusKosong(String pesan) {

        containerNotifikasi.getChildren().clear();

        Label kosong = new Label(pesan);

        kosong.setWrapText(true);
        kosong.setMaxWidth(Double.MAX_VALUE);
        kosong.setAlignment(Pos.CENTER);
        kosong.setPadding(new Insets(28));

        kosong.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-text-fill: #777777;"
        );

        containerNotifikasi.getChildren().add(kosong);
    }


    // =========================
    // STYLE HALAMAN
    // =========================

    private void styleHalaman() {

        rootNotifikasi.setStyle(
                "-fx-background-color: transparent;"
        );

        scrollDaftarNotifikasi.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-background: transparent;"
        );

        containerNotifikasi.setStyle(
                "-fx-background-color: #FFFFFF;" +
                "-fx-background-radius: 16;"
        );

        containerPagination.setAlignment(Pos.CENTER);
    }


    // =========================
    // PESAN
    // =========================

    private void tampilkanPesan(
            Alert.AlertType tipe,
            String judul,
            String pesan) {

        Alert alert = new Alert(tipe);

        alert.setTitle(judul);
        alert.setHeaderText(null);

        alert.setContentText(
                pesan == null
                        ? "Tidak ada keterangan tambahan."
                        : pesan
        );

        alert.showAndWait();
    }


    // =========================
    // DATA ITEM NOTIFIKASI
    // =========================

    private static class ItemNotifikasi {

        private final Transaksi transaksi;
        private final DetailTransaksi detail;

        private ItemNotifikasi(
                Transaksi transaksi,
                DetailTransaksi detail) {

            this.transaksi = transaksi;
            this.detail = detail;
        }

        private boolean isTerlambat() {

            LocalDate jatuhTempo =
                    transaksi.getTanggalKembali();

            return jatuhTempo != null
                    && LocalDate.now().isAfter(jatuhTempo);
        }

        private String labelStatus() {

            return isTerlambat()
                    ? "Terlambat"
                    : "Akan Terlambat";
        }
    }
}