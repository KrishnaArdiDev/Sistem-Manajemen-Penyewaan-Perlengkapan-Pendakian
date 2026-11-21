package com.sistemmanajemenpenyewaanperlengkapanpendakian.controller;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.App;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.Service.PelangganService;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.Service.PerlengkapanService;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.Service.TransaksiService;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.exception.DataTidakLengkapException;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.DetailTransaksi;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Pelanggan;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Perlengkapan;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Transaksi;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.controller.RingkasanTransaksiController;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class TambahTransaksiController {

    @FXML
    private TextField fieldNama;

    @FXML
    private TextField fieldNoIdentitas;

    @FXML
    private Label labelFoto;

    @FXML
    private DatePicker dateSewa;

    @FXML
    private DatePicker dateKembali;

    @FXML
    private ComboBox<String> comboMetodePembayaran;

    @FXML
    private Button btnBukaDaftarPerlengkapan;

    @FXML
    private ScrollPane scrollDaftarPerlengkapan;

    @FXML
    private VBox containerDaftarPerlengkapan;

    @FXML
    private Label labelTotal;

    @FXML
    private Button tombolBatal;

    @FXML
    private Button tombolSimpan;

    private File fotoIdentitas;

    private static final String PATH_TRANSAKSI =
            "/com/sistemmanajemenpenyewaanperlengkapanpendakian/fxml/pages/transaksi.fxml";

    private final PerlengkapanService perlengkapanService =
            new PerlengkapanService();

    private final List<PilihanPerlengkapan> daftarPilihan =
            new ArrayList<>();

    @FXML
    public void initialize() {

        comboMetodePembayaran.setItems(
                FXCollections.observableArrayList(
                        "Tunai",
                        "Transfer"
                )
        );

        dateSewa.setValue(LocalDate.now());
        dateKembali.setValue(LocalDate.now().plusDays(1));

        scrollDaftarPerlengkapan.setVisible(false);
        scrollDaftarPerlengkapan.setManaged(false);
        scrollDaftarPerlengkapan.setFitToWidth(true);
        containerDaftarPerlengkapan.setSpacing(8);

        dateSewa.valueProperty().addListener(
                (observable, lama, baru) -> hitungTotal()
        );

        dateKembali.valueProperty().addListener(
                (observable, lama, baru) -> hitungTotal()
        );

        styleForm();
        muatPerlengkapan();
        hitungTotal();
    }

    private void styleForm() {

        fieldNama.setStyle(
                "-fx-background-color: #E8E8E8;" +
                "-fx-border-color: #3A3A3A;" +
                "-fx-border-width: 1.5;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;"
        );

        fieldNoIdentitas.setStyle(
                "-fx-background-color: #E8E8E8;" +
                "-fx-border-color: #3A3A3A;" +
                "-fx-border-width: 1.5;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;"
        );

        comboMetodePembayaran.setMaxWidth(Double.MAX_VALUE);
        comboMetodePembayaran.setCursor(Cursor.HAND);

        btnBukaDaftarPerlengkapan.setMaxWidth(Double.MAX_VALUE);
        btnBukaDaftarPerlengkapan.setCursor(Cursor.HAND);

        tombolBatal.setCursor(Cursor.HAND);
        tombolSimpan.setCursor(Cursor.HAND);

        labelTotal.setStyle(
                "-fx-background-color: #E8E8E8;" +
                "-fx-border-color: #3A3A3A;" +
                "-fx-border-width: 1.5;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 0 10 0 10;" +
                "-fx-font-size: 18px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #00572B;"
        );
    }

    private void muatPerlengkapan() {

        daftarPilihan.clear();

        try {

            int idVendor = TransaksiService.getIdVendorAktif();

            List<Perlengkapan> inventaris =
                    perlengkapanService.ambilPerlengkapanVendor(idVendor);

            if (inventaris == null || inventaris.isEmpty()) {
                tampilkanStatusDaftar(
                        "Belum ada perlengkapan yang terdaftar."
                );
                return;
            }

            for (Perlengkapan perlengkapan : inventaris) {
                if (perlengkapan != null) {
                    daftarPilihan.add(
                            new PilihanPerlengkapan(perlengkapan)
                    );
                }
            }

            tampilkanDaftarPerlengkapan();

        } catch (IllegalArgumentException e) {

            tampilkanStatusDaftar(
                    "Login vendor diperlukan untuk memuat perlengkapan."
            );

        } catch (SQLException e) {

            tampilkanStatusDaftar(
                    "Data perlengkapan gagal dimuat dari database."
            );

            System.err.println(
                    "Gagal memuat perlengkapan: " + e.getMessage()
            );
        }
    }

    private void tampilkanStatusDaftar(String pesan) {

        containerDaftarPerlengkapan.getChildren().clear();

        Label status = new Label(pesan);
        status.setWrapText(true);
        status.setMaxWidth(Double.MAX_VALUE);
        status.setAlignment(Pos.CENTER);

        status.setStyle(
                "-fx-text-fill: #777777;" +
                "-fx-padding: 15;"
        );

        containerDaftarPerlengkapan.getChildren().add(status);

        perbaruiTeksTombolDaftar();
    }

    private void tampilkanDaftarPerlengkapan() {

        containerDaftarPerlengkapan.getChildren().clear();

        for (PilihanPerlengkapan pilihan : daftarPilihan) {

            containerDaftarPerlengkapan.getChildren().add(
                    buatBarisPerlengkapan(pilihan)
            );
        }

        perbaruiTeksTombolDaftar();
    }

    private HBox buatBarisPerlengkapan(
            PilihanPerlengkapan pilihan) {

        Perlengkapan barang = pilihan.perlengkapan;

        HBox baris = new HBox(12);
        baris.setAlignment(Pos.CENTER_LEFT);
        baris.setPadding(new Insets(8));
        baris.setMinHeight(78);
        baris.setMaxWidth(Double.MAX_VALUE);

        baris.setStyle(
                "-fx-background-color: #EEEEEE;" +
                "-fx-border-color: #CCCCCC;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;"
        );

        ImageView foto = new ImageView();
        foto.setFitWidth(58);
        foto.setFitHeight(58);
        foto.setPreserveRatio(true);
        foto.setSmooth(true);

        Image gambar = ambilGambarBarang(
                barang.getFotoBarang()
        );

        if (gambar != null) {
            foto.setImage(gambar);
        }

        Label namaBarang = new Label(
                barang.getNamaPerlengkapan()
        );

        namaBarang.setWrapText(true);
        namaBarang.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #222222;"
        );

        VBox informasi = new VBox(
                6,
                namaBarang
        );

        informasi.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(informasi, Priority.ALWAYS);

        Label statusStok = new Label(
                barang.getStok() > 0
                        ? "Tersedia: " + barang.getStok()
                        : "Stok Habis"
        );

        statusStok.setPadding(
                new Insets(3, 10, 3, 10)
        );

        statusStok.setStyle(
                barang.getStok() > 0
                        ? "-fx-background-color: #D8F5D4;" +
                          "-fx-text-fill: #005A32;" +
                          "-fx-background-radius: 12;" +
                          "-fx-border-color: #76C66D;" +
                          "-fx-border-radius: 12;"
                        : "-fx-background-color: #F8DEDE;" +
                          "-fx-text-fill: #861010;" +
                          "-fx-background-radius: 12;" +
                          "-fx-border-color: #861010;" +
                          "-fx-border-radius: 12;"
        );

        Button kurang = new Button("−");
        Button tambah = new Button("+");
        Label jumlah = new Label("00");

        kurang.setMinSize(28, 26);
        kurang.setPrefSize(28, 26);

        tambah.setMinSize(28, 26);
        tambah.setPrefSize(28, 26);

        jumlah.setMinWidth(28);
        jumlah.setAlignment(Pos.CENTER);

        jumlah.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;"
        );

        String styleTombolJumlah =
                "-fx-background-color: #00572B;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 5;" +
                "-fx-cursor: hand;";

        kurang.setStyle(styleTombolJumlah);
        tambah.setStyle(styleTombolJumlah);

        kurang.setCursor(Cursor.HAND);
        tambah.setCursor(Cursor.HAND);

        pilihan.labelJumlah = jumlah;
        pilihan.tombolKurang = kurang;
        pilihan.tombolTambah = tambah;

        kurang.setOnAction(
                event -> ubahJumlah(pilihan, -1)
        );

        tambah.setOnAction(
                event -> ubahJumlah(pilihan, 1)
        );

        HBox kontrolJumlah = new HBox(
                8,
                kurang,
                jumlah,
                tambah
        );

        kontrolJumlah.setAlignment(Pos.CENTER_RIGHT);

        VBox areaKontrol = new VBox(
                7,
                statusStok,
                kontrolJumlah
        );

        areaKontrol.setAlignment(Pos.CENTER_RIGHT);

        baris.getChildren().addAll(
                foto,
                informasi,
                areaKontrol
        );

        aturKontrolJumlah(pilihan);

        return baris;
    }

    private Image ambilGambarBarang(String lokasiFoto) {

        if (lokasiFoto == null || lokasiFoto.trim().isEmpty()) {
            return null;
        }

        try {

            String sumber = lokasiFoto.trim();

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
                    58,
                    58,
                    true,
                    true
            );

            return gambar.isError() ? null : gambar;

        } catch (IllegalArgumentException e) {

            System.err.println(
                    "Lokasi foto perlengkapan tidak valid: " + lokasiFoto
            );

            return null;
        }
    }

    private void ubahJumlah(
            PilihanPerlengkapan pilihan,
            int perubahan) {

        int stok = pilihan.perlengkapan.getStok();

        pilihan.jumlah = Math.max(
                0,
                Math.min(stok, pilihan.jumlah + perubahan)
        );

        aturKontrolJumlah(pilihan);
        hitungTotal();
        perbaruiTeksTombolDaftar();
    }

    private void aturKontrolJumlah(
            PilihanPerlengkapan pilihan) {

        if (pilihan.labelJumlah == null
                || pilihan.tombolKurang == null
                || pilihan.tombolTambah == null) {
            return;
        }

        pilihan.labelJumlah.setText(
                String.format("%02d", pilihan.jumlah)
        );

        pilihan.tombolKurang.setDisable(
                pilihan.jumlah <= 0
        );

        pilihan.tombolTambah.setDisable(
                pilihan.jumlah >= pilihan.perlengkapan.getStok()
        );
    }

    private void perbaruiTeksTombolDaftar() {

        int jumlahUnitTerpilih = 0;

        for (PilihanPerlengkapan pilihan : daftarPilihan) {
            jumlahUnitTerpilih = Math.addExact(
                    jumlahUnitTerpilih,
                    pilihan.jumlah
            );
        }

        btnBukaDaftarPerlengkapan.setText(
                "Pilih Barang Sewa (" + jumlahUnitTerpilih + ") - " +
                (scrollDaftarPerlengkapan.isVisible()
                        ? "Tutup" : "Buka")
        );
    }

    @FXML
    private void toggleDaftarPerlengkapan() {

        boolean tampil = !scrollDaftarPerlengkapan.isVisible();

        scrollDaftarPerlengkapan.setVisible(tampil);
        scrollDaftarPerlengkapan.setManaged(tampil);

        perbaruiTeksTombolDaftar();
    }

    @FXML
    private void pilihFoto() {

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Pilih Foto Identitas");

        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Gambar",
                        "*.png",
                        "*.jpg",
                        "*.jpeg"
                )
        );

        if (labelFoto.getScene() == null) {
            tampilkanPesan(
                    Alert.AlertType.WARNING,
                    "Formulir belum berada di dalam tampilan aplikasi."
            );
            return;
        }

        File file = chooser.showOpenDialog(
                labelFoto.getScene().getWindow()
        );

        if (file != null) {

            fotoIdentitas = file;

            labelFoto.setText(file.getName());

            labelFoto.setStyle(
                    "-fx-text-fill: #222222;"
            );
        }
    }

    private int ambilDurasiSewa() {

        if (dateSewa.getValue() == null
                || dateKembali.getValue() == null) {
            return 0;
        }

        long durasi = ChronoUnit.DAYS.between(
                dateSewa.getValue(),
                dateKembali.getValue()
        );

        if (durasi <= 0 || durasi > Integer.MAX_VALUE) {
            return 0;
        }

        return (int) durasi;
    }

    private void hitungTotal() {

        int durasi = ambilDurasiSewa();

        if (durasi <= 0) {
            labelTotal.setText(rupiah(0));
            return;
        }

        try {

            long total = 0;

            for (PilihanPerlengkapan pilihan : daftarPilihan) {

                if (pilihan.jumlah <= 0) {
                    continue;
                }

                long subtotalPerUnit =
                        pilihan.perlengkapan.hitungHargaSewa(durasi);

                long subtotal = Math.multiplyExact(
                        subtotalPerUnit,
                        pilihan.jumlah
                );

                total = Math.addExact(total, subtotal);
            }

            labelTotal.setText(rupiah(total));

        } catch (ArithmeticException e) {

            labelTotal.setText(
                    "Melebihi batas perhitungan"
            );
        }
    }

    @FXML
    private void simpan() {

        String nama = fieldNama.getText();
        String nomorIdentitas = fieldNoIdentitas.getText();

        if (nama == null || nama.trim().isEmpty()) {
            tampilkanPesan(
                    Alert.AlertType.WARNING,
                    "Nama penyewa wajib diisi."
            );
            fieldNama.requestFocus();
            return;
        }

        if (nama.trim().length() > 150) {
            tampilkanPesan(
                    Alert.AlertType.WARNING,
                    "Nama penyewa maksimal 150 karakter."
            );
            fieldNama.requestFocus();
            return;
        }

        if (nomorIdentitas == null
                || nomorIdentitas.trim().isEmpty()) {

            tampilkanPesan(
                    Alert.AlertType.WARNING,
                    "Nomor identitas wajib diisi."
            );
            fieldNoIdentitas.requestFocus();
            return;
        }

        if (nomorIdentitas.trim().length() > 50) {
            tampilkanPesan(
                    Alert.AlertType.WARNING,
                    "Nomor identitas maksimal 50 karakter."
            );
            fieldNoIdentitas.requestFocus();
            return;
        }

        if (fotoIdentitas == null
                || !fotoIdentitas.isFile()
                || !fotoIdentitas.canRead()) {

            tampilkanPesan(
                    Alert.AlertType.WARNING,
                    "Foto identitas wajib dipilih dan dapat dibaca."
            );
            return;
        }

        LocalDate tanggalSewa = dateSewa.getValue();
        LocalDate tanggalKembali = dateKembali.getValue();

        if (tanggalSewa == null || tanggalKembali == null) {
            tampilkanPesan(
                    Alert.AlertType.WARNING,
                    "Tanggal menyewa dan tanggal kembali wajib diisi."
            );
            return;
        }

        if (!tanggalKembali.isAfter(tanggalSewa)) {
            tampilkanPesan(
                    Alert.AlertType.WARNING,
                    "Tanggal kembali harus setelah tanggal menyewa."
            );
            return;
        }

        int durasiSewa = ambilDurasiSewa();

        if (durasiSewa <= 0) {
            tampilkanPesan(
                    Alert.AlertType.WARNING,
                    "Durasi sewa minimal satu hari."
            );
            return;
        }

        String metodePembayaran =
                comboMetodePembayaran.getValue();

        if (metodePembayaran == null
                || metodePembayaran.trim().isEmpty()) {

            tampilkanPesan(
                    Alert.AlertType.WARNING,
                    "Metode pembayaran wajib dipilih."
            );
            comboMetodePembayaran.requestFocus();
            return;
        }

        List<DetailTransaksi> daftarDetail =
                new ArrayList<>();

        for (PilihanPerlengkapan pilihan : daftarPilihan) {

            if (pilihan.jumlah <= 0) {
                continue;
            }

            DetailTransaksi detail = new DetailTransaksi();

            detail.setPerlengkapanYangDisewa(
                    pilihan.perlengkapan
            );

            detail.setKuantitasPerAlat(
                    pilihan.jumlah
            );

            daftarDetail.add(detail);
        }

        if (daftarDetail.isEmpty()) {
            tampilkanPesan(
                    Alert.AlertType.WARNING,
                    "Pilih setidaknya satu perlengkapan."
            );
            return;
        }

        try {

            long totalSebelumSimpan = 0;

            for (PilihanPerlengkapan pilihan : daftarPilihan) {

                if (pilihan.jumlah <= 0) {
                    continue;
                }

                long subtotal = Math.multiplyExact(
                        pilihan.perlengkapan.hitungHargaSewa(durasiSewa),
                        pilihan.jumlah
                );

                totalSebelumSimpan = Math.addExact(
                        totalSebelumSimpan,
                        subtotal
                );
            }

            labelTotal.setText(
                    rupiah(totalSebelumSimpan)
            );

        } catch (ArithmeticException e) {

            tampilkanPesan(
                    Alert.AlertType.ERROR,
                    "Total harga terlalu besar untuk dihitung."
            );
            return;
        }

        tombolSimpan.setDisable(true);

        try {

            int idVendor = TransaksiService.getIdVendorAktif();

            Pelanggan pelanggan =
                    PelangganService.prosesDataPelanggan(
                            nama.trim(),
                            nomorIdentitas.trim()
                    );

            Transaksi transaksi = new Transaksi(
                    pelanggan,
                    daftarDetail,
                    LocalDate.now(),
                    tanggalSewa,
                    durasiSewa,
                    metodePembayaran
            );

            transaksi.setFotoIdentitas(
                    fotoIdentitas.toURI().toString()
            );

            TransaksiService.tambah(
                    transaksi,
                    idVendor
            );

            tampilkanRingkasan(transaksi);

        } catch (DataTidakLengkapException | SQLException e) {

            tampilkanPesan(
                    Alert.AlertType.ERROR,
                    e.getMessage()
            );

        } catch (RuntimeException e) {

            tampilkanPesan(
                    Alert.AlertType.ERROR,
                    e.getMessage() == null
                            ? "Terjadi kesalahan saat menyimpan transaksi."
                            : e.getMessage()
            );

        } finally {
            tombolSimpan.setDisable(false);
        }
    }


private void tampilkanRingkasan(Transaksi transaksi) {

    try {

        FXMLLoader loader = new FXMLLoader(
                App.class.getResource(
                        "/com/sistemmanajemenpenyewaanperlengkapanpendakian/fxml/pages/ringkasan-transaksi.fxml"
                )
        );

        Parent halamanRingkasan = loader.load();

        RingkasanTransaksiController controller =
                loader.getController();

        controller.setDataTransaksi(transaksi);

        BorderPane layout = (BorderPane)
                tombolSimpan.getScene().lookup("#layoutPage");

        if (layout == null) {
            throw new IllegalStateException(
                    "Layout utama tidak ditemukan."
            );
        }

        BorderPane.setMargin(
                halamanRingkasan,
                new Insets(16)
        );

        layout.setCenter(halamanRingkasan);

    } catch (Exception e) {

        e.printStackTrace();

        tampilkanPesan(
                Alert.AlertType.ERROR,
                "Transaksi berhasil disimpan, tetapi halaman ringkasan gagal dibuka.\n"
                        + e.getMessage()
        );
    }
}


    @FXML
    private void batal() {
        kembaliKeTransaksi();
    }

    private void kembaliKeTransaksi() {

        try {

            Scene scene = tombolBatal.getScene();

            if (scene == null) {
                throw new IllegalStateException(
                        "Scene form transaksi tidak ditemukan."
                );
            }

            BorderPane layout = null;

            javafx.scene.Node node =
                    scene.lookup("#layoutPage");

            if (node instanceof BorderPane) {
                layout = (BorderPane) node;
            }

            if (layout == null) {
                throw new IllegalStateException(
                        "BorderPane layoutPage tidak ditemukan."
                );
            }

            URL lokasiTransaksi =
                    App.class.getResource(PATH_TRANSAKSI);

            if (lokasiTransaksi == null) {
                throw new IOException(
                        "FXML halaman Transaksi tidak ditemukan: "
                                + PATH_TRANSAKSI
                );
            }

            Parent halamanTransaksi =
                    FXMLLoader.load(lokasiTransaksi);

            BorderPane.setMargin(
                    halamanTransaksi,
                    new Insets(16)
            );

            layout.setCenter(halamanTransaksi);

        } catch (Exception e) {

            Throwable akar = e;

            while (akar.getCause() != null) {
                akar = akar.getCause();
            }

            tampilkanPesan(
                    Alert.AlertType.ERROR,
                    "Gagal kembali ke halaman Transaksi:\n"
                            + akar.getClass().getSimpleName()
                            + ": " + akar.getMessage()
            );
        }
    }

    private static String rupiah(long nilai) {

        return "Rp " + NumberFormat.getIntegerInstance(
                Locale.forLanguageTag("id-ID")
        ).format(nilai);
    }

    private void tampilkanPesan(
            Alert.AlertType tipe,
            String pesan) {

        Alert alert = new Alert(tipe);
        alert.setTitle("TAPAK - Transaksi");
        alert.setHeaderText(null);
        alert.setContentText(pesan);
        alert.showAndWait();
    }

    private static class PilihanPerlengkapan {

        private final Perlengkapan perlengkapan;
        private int jumlah;

        private Label labelJumlah;
        private Button tombolKurang;
        private Button tombolTambah;

        private PilihanPerlengkapan(
                Perlengkapan perlengkapan) {

            this.perlengkapan = perlengkapan;
            this.jumlah = 0;
        }
    }
}
