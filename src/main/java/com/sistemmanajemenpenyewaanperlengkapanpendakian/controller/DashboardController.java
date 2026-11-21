
package com.sistemmanajemenpenyewaanperlengkapanpendakian.controller;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.App;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.*;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.Service.PerlengkapanService;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.Service.TransaksiService;

import java.time.format.DateTimeFormatter;
import java.text.NumberFormat;
import java.io.IOException;
import java.sql.SQLException;
import java.time.*;
import java.util.*;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.layout.*;

public class DashboardController {
//#region manggil komponen
    @FXML 
    private VBox containerDashboard;

    @FXML 
    private VBox cardBarangTersedia;

    @FXML 
    private VBox cardBarangDisewakan;

    @FXML 
    private VBox cardSeluruhBarang;

    @FXML 
    private VBox productSectionContainer;

    @FXML 
    private HBox itemProduk1;

    @FXML 
    private Region indikatorStatusProduk;
    @FXML 
    private Button btnDetail;
    @FXML 
    private Button btnKonfirmasi;
    @FXML 
    private Label labelJudulDaftarProduk;

    @FXML 
    private Label labelNamaPenyewa;
    @FXML 
    private Label labelNamaPerlengkapan;
    @FXML 
    private Label labelTanggalSewa;
    @FXML 
    private Label labelPemisahTanggalSewa;
    @FXML 
    private Label labelTanggalSelesaiSewa;
    @FXML 
    private Label labelStatusProduk;
    @FXML private Label labelJumlahTersedia;
    @FXML private Label labelJumlahDisewa;
    @FXML private Label labelJumlahSeluruh;
    @FXML private VBox containerDaftarProdukSewa;
    @FXML private HBox containerPagination;
    @FXML private ImageView gambarProduk;
//#endregion

//#region Style
    private final PerlengkapanService perlengkapanService = new PerlengkapanService();
    private final List<ItemDashboard> daftarProdukDipinjam = new ArrayList<>();
    private static final int ITEM_PER_HALAMAN = 4;
    private static final DateTimeFormatter FORMAT_TANGGAL =DateTimeFormatter.ofPattern("dd - MM - yyyy");
    private int halamanAktif = 0;
    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("login");
    }

    @FXML void initialize(){
        styleContainerDashboard();
        styleCardTotalTersedia();
        styleCardDisewa();
        styleCardTotalSeluruhBarang();
        styleDaftarBarang();
        styleItemProduk();
        styleTombolProduk();
        styleLabelJudulDaftarProduk();
        styleTeksProduk();
        styleLabelStatusProduk("#FFFFFF", "#861010", "#861010");
        containerPagination.setVisible(false);
        containerPagination.setManaged(false);
        muatDataDashboard();
    }

    private void styleContainerDashboard(){
        containerDashboard.setStyle(
            "-fx-border-radius: 14;"+
            "-fx-background-color: transparent;"+
            "-fx-background-radius: 14;"
        );
    }

    private void styleCardTotalTersedia(){
        cardBarangTersedia.setStyle(
            "-fx-border-color: #005A32;"+
            "-fx-border-radius:14;"+
            "-fx-background-radius:14;"+
            "-fx-background-color: #E2FBE0;"
        );
    }
    private void styleCardDisewa(){
        cardBarangDisewakan.setStyle(
            "-fx-border-color: #861010;"+
            "-fx-border-radius:14;"+
            "-fx-background-radius:14;"+
            "-fx-background-color: #F8DEDE;"
        );
    }
    private void styleCardTotalSeluruhBarang(){
        cardSeluruhBarang.setStyle(
            "-fx-border-color: #122A7A;"+
            "-fx-border-radius:14;"+
            "-fx-background-radius:14;"+
            "-fx-background-color: #D1DDFE;"
        );
    }

    private void styleDaftarBarang(){
        productSectionContainer.setStyle(
            "-fx-background-color: #FFFFFF;"+
            "-fx-background-radius:14;"

        );
    }

    private  void styleItemProduk(){
        itemProduk1.setStyle(
            "-fx-background-color: #F5F5F5;"+
            "-fx-background-radius:12;"
        );
    }
    private void styleTombolProduk(){
        btnDetail.setStyle(
            "-fx-background-color: #FFFFFF;"+
            "-fx-border-color: #005A32;"+
            "-fx-border-radius: 14;"+
            "-fx-background-radius: 14;"+
            "-fx-text-fill: #005A32;"
            
        );
        btnKonfirmasi.setStyle(
            "-fx-background-color: #005A32;"+
            "-fx-border-color: #005A32;"+
            "-fx-background-radius: 14;"+
            "-fx-border-radius: 14;"+
            "-fx-text-fill: #FFFFFF;"
        );
    }
    
    private void styleLabelJudulDaftarProduk(){
        labelJudulDaftarProduk.setStyle(
            "-fx-text-fill: #005A32;"
        );
    }
    private void styleTeksProduk(){
        labelNamaPerlengkapan.setStyle(
            "-fx-font-size: 18px;"+
            "-fx-text-fill:#000000;"+
            "-fx-font-weight: Bold;"
        );
        labelNamaPenyewa.setStyle(
            "-fx-font-size: 14px;"+
            "-fx-text-fill:#868686;"
        );
        labelPemisahTanggalSewa.setStyle(
            "-fx-font-size: 14px;"+
            "-fx-text-fill:#868686;"
        );
        labelTanggalSelesaiSewa.setStyle(
            "-fx-font-size: 14px;"+
            "-fx-text-fill:#868686;"
        );
        labelTanggalSewa.setStyle(
            "-fx-font-size: 14px;"+
            "-fx-text-fill:#868686;"
        );
    }

    private void styleLabelStatusProduk(String warnaBg, String warnaBorder, String warnaTeks){
        labelStatusProduk.setStyle(
            "-fx-border-color: " +warnaBorder+";"+
            "-fx-background-color: "+warnaBg+";"+
            "-fx-border-radius: 14;"+
            "-fx-background-radius: 14;"+
            "-fx-text-fill: "+warnaTeks+";"
        );
    }
//#endregion

//#region Pagination

private void tampilkanHalamanProduk(int halaman) {
    halamanAktif = halaman;
    containerDaftarProdukSewa.getChildren().clear();

    int awal = halamanAktif * ITEM_PER_HALAMAN;
    int akhir = Math.min(
            awal + ITEM_PER_HALAMAN,
            daftarProdukDipinjam.size()
    );

    for (int i = awal; i < akhir; i++) {
        ItemDashboard item = daftarProdukDipinjam.get(i);

        if (i == awal) {
            isiTemplateProduk(item);
            containerDaftarProdukSewa.getChildren().add(itemProduk1);
        } else {
            containerDaftarProdukSewa.getChildren().add(
                    buatBarisProduk(item)
            );
        }
    }

    aturPagination();
}

private void isiTemplateProduk(ItemDashboard item) {
    Perlengkapan barang = item.detail.getPerlengkapanYangDisewa();

    labelNamaPerlengkapan.setText(barang.getNamaPerlengkapan());
    labelNamaPenyewa.setText(namaPelanggan(item.transaksi));
    labelTanggalSewa.setText(formatTanggal(item.transaksi.getTanggalSewa()));
    labelTanggalSelesaiSewa.setText(
            formatTanggal(item.transaksi.getTanggalKembali())
    );

    labelStatusProduk.setText(
            item.terlambat ? "Terlambat" : "Akan Terlambat"
    );

    aturGambar(gambarProduk, barang.getFotoBarang());
    aturStatusProduk(
            labelStatusProduk,
            indikatorStatusProduk,
            item.terlambat
    );

    // Menyimpan data item untuk integrasi tombol berikutnya.
    itemProduk1.setUserData(item);
    btnDetail.setUserData(item);
    btnKonfirmasi.setUserData(item);
}

private HBox buatBarisProduk(ItemDashboard item) {
    Perlengkapan barang = item.detail.getPerlengkapanYangDisewa();

    HBox baris = new HBox(12);
    baris.setAlignment(Pos.CENTER_LEFT);
    baris.setPrefHeight(120);
    baris.setMinHeight(120);
    baris.setPadding(new Insets(8));
    baris.setStyle(
            "-fx-background-color: #F5F5F5;" +
            "-fx-background-radius: 12;"
    );

    Region indikator = new Region();
    indikator.setMinWidth(10);
    indikator.setPrefWidth(10);
    indikator.setMaxWidth(10);

    ImageView foto = new ImageView();
    foto.setFitWidth(97);
    foto.setFitHeight(97);
    foto.setPreserveRatio(true);
    aturGambar(foto, barang.getFotoBarang());

    Label namaBarang = new Label(barang.getNamaPerlengkapan());
    namaBarang.setStyle(
            "-fx-font-size: 18px;" +
            "-fx-text-fill: #000000;" +
            "-fx-font-weight: bold;"
    );

    Label namaPenyewa = new Label(namaPelanggan(item.transaksi));
    namaPenyewa.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-text-fill: #868686;"
    );

    Label tanggalSewa = new Label(
            formatTanggal(item.transaksi.getTanggalSewa())
    );
    Label pemisah = new Label("|");
    Label tanggalKembali = new Label(
            formatTanggal(item.transaksi.getTanggalKembali())
    );

    String styleTanggal =
            "-fx-font-size: 14px; -fx-text-fill: #868686;";

    tanggalSewa.setStyle(styleTanggal);
    pemisah.setStyle(styleTanggal);
    tanggalKembali.setStyle(styleTanggal);

    VBox informasi = new VBox(
            6,
            namaBarang,
            namaPenyewa,
            new HBox(8, tanggalSewa, pemisah, tanggalKembali)
    );

    informasi.setAlignment(Pos.CENTER_LEFT);
    HBox.setHgrow(informasi, Priority.ALWAYS);

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    Label status = new Label();
    status.setAlignment(Pos.CENTER);
    status.setMinSize(240, 28);
    status.setPrefSize(240, 28);
    status.setMaxSize(240, 28);

    aturStatusProduk(status, indikator, item.terlambat);

    Button detail = buatTombolProduk("Detail", false);
    Button konfirmasi = buatTombolProduk("Konfirmasi", true);

    detail.setUserData(item);
    konfirmasi.setUserData(item);

    HBox tombol = new HBox(12, detail, konfirmasi);
    tombol.setAlignment(Pos.CENTER_RIGHT);

    Region ruang = new Region();
    VBox.setVgrow(ruang, Priority.ALWAYS);

    VBox aksi = new VBox(12, status, ruang, tombol);
    aksi.setAlignment(Pos.TOP_RIGHT);
    aksi.setMinSize(328, 100);
    aksi.setPrefSize(328, 100);
    aksi.setMaxWidth(328);

    baris.getChildren().addAll(
            indikator,
            foto,
            informasi,
            spacer,
            aksi
    );

    baris.setUserData(item);
    return baris;
}

private Button buatTombolProduk(String teks, boolean utama) {
    Button tombol = new Button(teks);

    tombol.setMinSize(158, 28);
    tombol.setPrefSize(158, 28);
    tombol.setMaxSize(158, 28);
    tombol.setCursor(Cursor.HAND);

    tombol.setStyle(utama
            ? "-fx-background-color: #005A32; -fx-border-color: #005A32;"
                + "-fx-background-radius: 14; -fx-border-radius: 14;"
                + "-fx-text-fill: #FFFFFF;"
            : "-fx-background-color: #FFFFFF; -fx-border-color: #005A32;"
                + "-fx-background-radius: 14; -fx-border-radius: 14;"
                + "-fx-text-fill: #005A32;"
    );

    return tombol;
}

private void aturStatusProduk(
        Label status, Region indikator, boolean terlambat) {

    String latar = terlambat ? "#FFD6D6" : "#FFF3C4";
    String warna = terlambat ? "#861010" : "#B38600";

    status.setText(terlambat ? "Terlambat" : "Akan Terlambat");
    status.setStyle(
            "-fx-background-color: " + latar + ";" +
            "-fx-border-color: " + warna + ";" +
            "-fx-border-radius: 14;" +
            "-fx-background-radius: 14;" +
            "-fx-text-fill: " + warna + ";"
    );

    indikator.setStyle(
            "-fx-background-color: " + warna + ";" +
            "-fx-background-radius: 8 0 0 8;"
    );
}

private void aturGambar(ImageView target, String uri) {
    target.setImage(null);

    if (uri == null || uri.trim().isEmpty()) {
        return;
    }

    try {
        Image image = new Image(uri, 97, 97, true, true);

        if (!image.isError()) {
            target.setImage(image);
        }
    } catch (IllegalArgumentException e) {
        System.err.println("Lokasi foto barang tidak valid: " + uri);
    }
}

private String namaPelanggan(Transaksi transaksi) {
    if (transaksi.getPelanggan() == null
            || transaksi.getPelanggan().getNamaPelanggan() == null
            || transaksi.getPelanggan().getNamaPelanggan().trim().isEmpty()) {
        return "Nama pelanggan tidak tersedia";
    }

    return transaksi.getPelanggan().getNamaPelanggan();
}

private String formatTanggal(LocalDate tanggal) {
    return tanggal == null ? "-" : tanggal.format(FORMAT_TANGGAL);
}

private void tampilkanProdukKosong() {
    containerDaftarProdukSewa.getChildren().clear();

    Label kosong = new Label(
            "Belum ada barang yang sedang dipinjam."
    );
    kosong.setMaxWidth(Double.MAX_VALUE);
    kosong.setAlignment(Pos.CENTER);
    kosong.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-text-fill: #868686;" +
            "-fx-padding: 28 12 28 12;"
    );

    containerDaftarProdukSewa.getChildren().add(kosong);
    containerPagination.getChildren().clear();
    containerPagination.setVisible(false);
    containerPagination.setManaged(false);
}

private void aturPagination() {
    containerPagination.getChildren().clear();

    int jumlahHalaman = (int) Math.ceil(
            (double) daftarProdukDipinjam.size() / ITEM_PER_HALAMAN
    );

    boolean tampil = jumlahHalaman > 1;
    containerPagination.setVisible(tampil);
    containerPagination.setManaged(tampil);

    if (!tampil) {
        return;
    }

    Button sebelumnya = buatTombolPagination("‹");
    sebelumnya.setDisable(halamanAktif == 0);
    sebelumnya.setOnAction(
            e -> tampilkanHalamanProduk(halamanAktif - 1)
    );
    containerPagination.getChildren().add(sebelumnya);

    for (int i = 0; i < jumlahHalaman; i++) {
        final int halaman = i;
        Button nomor = buatTombolPagination(String.valueOf(i + 1));

        if (i == halamanAktif) {
            nomor.setStyle(
                    "-fx-background-color: #005A32;" +
                    "-fx-text-fill: #FFFFFF;" +
                    "-fx-background-radius: 6;"
            );
        }

        nomor.setOnAction(e -> tampilkanHalamanProduk(halaman));
        containerPagination.getChildren().add(nomor);
    }

    Button berikutnya = buatTombolPagination("›");
    berikutnya.setDisable(halamanAktif >= jumlahHalaman - 1);
    berikutnya.setOnAction(
            e -> tampilkanHalamanProduk(halamanAktif + 1)
    );
    containerPagination.getChildren().add(berikutnya);
}

private Button buatTombolPagination(String teks) {
    Button tombol = new Button(teks);
    tombol.setMinSize(32, 30);
    tombol.setPrefSize(32, 30);
    tombol.setMaxSize(32, 30);
    tombol.setCursor(Cursor.HAND);
    tombol.setStyle(
            "-fx-background-color: #FFFFFF;" +
            "-fx-text-fill: #005A32;" +
            "-fx-border-color: #005A32;" +
            "-fx-border-radius: 6;" +
            "-fx-background-radius: 6;"
    );
    return tombol;
}

private void tampilkanPesan(
        Alert.AlertType tipe, String judul, String pesan) {

    Alert alert = new Alert(tipe);
    alert.setTitle(judul);
    alert.setHeaderText(null);
    alert.setContentText(pesan);
    alert.showAndWait();
}
//#endregion



//#region controller konteks MVC
    public void muatDataDashboard(){
        final int idVendor;


        try{
            idVendor = TransaksiService.getIdVendorAktif();
        } catch (IllegalArgumentException e){
            // Tanpa sesi, pertahankan data contoh FXML untuk preview UI.
            return;
        }

        try {
            List<Perlengkapan> perlengkapan = perlengkapanService.ambilPerlengkapanVendor(idVendor);
            List<Transaksi> transaksi = TransaksiService.semua(idVendor);

            long tersedia = hitungStokTersedia(perlengkapan);
            long disewa = kumpulkanProdukDipinjam(transaksi);

            labelJumlahTersedia.setText(formatJumlah(tersedia));
            labelJumlahDisewa.setText(formatJumlah(disewa));
            labelJumlahSeluruh.setText(formatJumlah(Math.addExact(tersedia, disewa)));

            if (daftarProdukDipinjam.isEmpty()){
                tampilkanProdukKosong();
            } else {
                tampilkanHalamanProduk(0);
            }
        } catch (SQLException | ArithmeticException e){
            tampilkanPesan(Alert.AlertType.ERROR, "Gagal Memuat Dashboard", "Data Dashboard tidak dapat dimuat.\n" + e.getMessage());
        }
    }

    private long hitungStokTersedia(List<Perlengkapan> perlengkapan) {
        long jumlah = 0;

        if (perlengkapan == null){
            return jumlah;
        }

        for (Perlengkapan item : perlengkapan){
            if (item != null){
                jumlah = Math.addExact(jumlah, item.getStok());
            }
        }
        return jumlah;
    }

    private long kumpulkanProdukDipinjam(List<Transaksi> transaksi){
        daftarProdukDipinjam.clear();
        long jumlah = 0;

        if (transaksi == null){
            return  jumlah;
        }

        for (Transaksi t : transaksi){
            if (t == null || t.getDaftarDetail() == null){
                continue;
            }

            for (DetailTransaksi detail : t.getDaftarDetail()){
                if (detail == null || detail.getTanggalPengembalianBarang() != null || detail.getPerlengkapanYangDisewa() == null){
                    continue;
                }
                jumlah = Math.addExact(jumlah, detail.getKuantitasPerAlat());

                daftarProdukDipinjam.add(new ItemDashboard(t, detail));
            }
        }

    // Barang terlambat ditempatkan lebih dahulu.
    daftarProdukDipinjam.sort((a, b) -> {
        if (a.terlambat != b.terlambat){
            return a.terlambat ? -1 : 1;
        }

        LocalDate tglA = a.transaksi.getTanggalKembali();
        LocalDate tglB = b.transaksi.getTanggalKembali();

        if (tglA == null &&tglB == null) return 0;
        if (tglA == null) return 1;
        if (tglB == null) return -1;

        return tglA.compareTo(tglB);
    });

        return jumlah;
    }

    private String formatJumlah(long jumlah){
        return NumberFormat.getIntegerInstance(Locale.forLanguageTag("id-ID")).format(jumlah) + " Barang";
    }
//#endregion

// >>> TAMBAHAN: DATA SATU PRODUK PADA DASHBOARD
private static class ItemDashboard {
    private final Transaksi transaksi;
    private final DetailTransaksi detail;
    private final boolean terlambat;

    private ItemDashboard(Transaksi transaksi, DetailTransaksi detail) {
        this.transaksi = transaksi;
        this.detail = detail;

        LocalDate kembali = transaksi.getTanggalKembali();

        this.terlambat = kembali != null
                && LocalDate.now().isAfter(kembali);
    }
}
// <<< AKHIR TAMBAHAN: DATA ITEM DASHBOARD

}