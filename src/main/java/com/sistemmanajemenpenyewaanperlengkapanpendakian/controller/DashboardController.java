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
import java.net.URL;
import javafx.scene.Node;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.layout.*;




public class DashboardController {
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

    private final PerlengkapanService perlengkapanService = new PerlengkapanService();
    private final List<ItemDashboard> daftarProdukDipinjam = new ArrayList<>();
    private static final int ITEM_PER_HALAMAN = 3;
    private static final DateTimeFormatter FORMAT_TANGGAL =DateTimeFormatter.ofPattern("dd - MM - yyyy");
    private int halamanAktif = 0;
    private static final String PATH_KARTU =  "/com/sistemmanajemenpenyewaanperlengkapanpendakian/fxml/components/card-produk-disewa.fxml";
    private static final String PATH_DETAIL_PENGEMBALIAN = "/com/sistemmanajemenpenyewaanperlengkapanpendakian/fxml/pages/detail-pengembalian.fxml";
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

        try{
            containerDaftarProdukSewa.getChildren().add(buatKartuDashboard(item));
        } catch (Exception e){
            tampilkanPesan(Alert.AlertType.ERROR, "Gagal memuat card", "Card penyewaan gagal untuk dimuat" + e.getMessage());
        }
    }

    aturPagination();
}

private HBox buatKartuDashboard(ItemDashboard item) throws Exception{
    URL lokasiKartu = App.class.getResource(PATH_KARTU);

    if (lokasiKartu == null){
        throw new IllegalArgumentException("card-produk-disewa.fxml tidak ditemukan.");
    }

    FXMLLoader loader = new FXMLLoader(lokasiKartu);

    HBox kartu = loader.load();

    Label namaBarang = komponenKartu(loader, "labelNamaPerlengkapan", Label.class);
    Label namaPenyewa = komponenKartu(loader, "labelNamaPenyewa", Label.class);
    Label tanggalSewa = komponenKartu(loader, "labelTanggalSewa", Label.class);
    Label tanggalKembali  = komponenKartu(loader, "labelTanggalSelesaiSewa", Label.class);
    Label status = komponenKartu(loader, "labelStatusProduk", Label.class);
    ImageView foto = komponenKartu(loader, "gambarProduk", ImageView.class);
    Region indikator = komponenKartu(loader, "indikatorStatusProduk", Region.class);
    Button detail = komponenKartu(loader, "btnDetail", Button.class);
    Button konfirmasi = komponenKartu(loader, "btnKonfirmasi", Button.class);
    Perlengkapan barang = item.detail.getPerlengkapanYangDisewa();
    aturGambar(foto, barang.getFotoBarang());

    namaBarang.setText(barang.getNamaPerlengkapan());
    namaPenyewa.setText(namaPelanggan(item.transaksi));
    tanggalSewa.setText(formatTanggal(item.transaksi.getTanggalSewa()));
    tanggalKembali.setText(formatTanggal(item.transaksi.getTanggalKembali()));


    status.setText(item.labelStatus());

    boolean bermasalah = item.terlambat || item.terlambatDikembalikan || item.kondisiBermasalah();

    String warna;
    String latar;

    if (bermasalah){
        warna = "#A3212B";
        latar = "#FFD6D6";
    } else if (item.sudahDikembalikan){
        warna = "#005A32";
        latar = "#DDF5E3";
    } else {
        warna = "#B38600";;
        latar = "#FFF3C4";
    }

    status.setStyle(
         "-fx-background-color: " + latar + ";" +
            "-fx-border-color: " + warna + ";" +
            "-fx-border-radius: 14;" +
            "-fx-background-radius: 14;" +
            "-fx-text-fill: " + warna + ";" +
            "-fx-font-size: 12px;"
    );
    
    indikator.setStyle(
        "-fx-background-color: " + warna + ";" +
        "-fx-background-radius: 8 0 0 8;"
    );

    kartu.setStyle(
        "-fx-background-color: #F5F5F5;" +
        "-fx-background-radius: 12;"
    );

    detail.setStyle(
         "-fx-background-color: #FFFFFF;" +
        "-fx-border-color: #005A32;" +
        "-fx-border-radius: 14;" +
        "-fx-background-radius: 14;" +
        "-fx-text-fill: #005A32;"
    );

    konfirmasi.setStyle(
        "-fx-background-color: #005A32;" +
        "-fx-border-color: #005A32;" +
        "-fx-border-radius: 14;" +
        "-fx-background-radius: 14;" +
        "-fx-text-fill: #FFFFFF;"
    );

    detail.setCursor(Cursor.HAND);
    detail.setOnAction(event -> bukaDetailPengembalian(item, false));

    konfirmasi.setCursor(Cursor.HAND);
    konfirmasi.setOnAction(event -> bukaDetailPengembalian(item, true));

    konfirmasi.setVisible(!item.sudahDikembalikan);
    konfirmasi.setManaged(!item.sudahDikembalikan);

    kartu.setUserData(item);
    kartu.setMaxWidth(Double.MAX_VALUE);

    return kartu;
}

private <T> T komponenKartu (FXMLLoader loader, String fxId, Class<T> tipe){
    Object komponen = loader.getNamespace().get(fxId);

    if (!tipe.isInstance(komponen)){
        throw new IllegalStateException("Komponen " + fxId + " tidak ditemukan atau memilih tipe yang salah");
    }
    return tipe.cast(komponen);
}

private void bukaDetailPengembalian(ItemDashboard item, boolean langsungKonfirmasi){
    try{
        if (containerDashboard.getScene() == null){
            throw new IllegalStateException( "Scene Dashboard tidak ditemukan.");
        }
        
        Node nodeLayout = containerDashboard.getScene().lookup("#layoutPage");
        if (!(nodeLayout instanceof BorderPane)){
            throw new IllegalStateException("BorderPane #layoutPage tidak ditemukan.");
        }

        BorderPane layoutPage = (BorderPane) nodeLayout;
        Node halamanDashboard = layoutPage.getCenter();

        if (halamanDashboard == null){
            throw new IllegalStateException("Halaman Dashboard tidak ditemukan.");
        }

        Insets marginSebelumnya = BorderPane.getMargin(halamanDashboard);
        URL lokasiDetail = App.class.getResource(PATH_DETAIL_PENGEMBALIAN);
        
        if (lokasiDetail == null){
            throw new IllegalStateException("detail-pengembalian.fxml tidak ditemukan.");
        }

        FXMLLoader loader = new FXMLLoader(lokasiDetail);
        Parent halamanDetail = loader.load();

        DetailPengembalianController controller = loader.getController();

        controller.setData(item.transaksi, item.detail, () ->{
            if (marginSebelumnya != null){
                BorderPane.setMargin(halamanDashboard, marginSebelumnya);
            }

            layoutPage.setCenter(halamanDashboard);
            muatDataDashboard();
        });
        BorderPane.setMargin(halamanDetail, new Insets(16));
        layoutPage.setCenter(halamanDetail);

        if (langsungKonfirmasi && !item.sudahDikembalikan){
            controller.mintaKonfirmasi();
        }
    } catch(Exception e){
        tampilkanPesan(Alert.AlertType.ERROR, "Gagal Membuka Detail", e.getMessage() == null ? e.toString() : e.getMessage());
    }
}
private void isiTemplateProduk(ItemDashboard item) {
    Perlengkapan barang = item.detail.getPerlengkapanYangDisewa();

    labelNamaPerlengkapan.setText(barang.getNamaPerlengkapan());
    labelNamaPenyewa.setText(namaPelanggan(item.transaksi));
    labelTanggalSewa.setText(formatTanggal(item.transaksi.getTanggalSewa()));
    labelTanggalSelesaiSewa.setText(
            formatTanggal(item.transaksi.getTanggalKembali())
    );

    aturGambar(gambarProduk, barang.getFotoBarang());
    aturStatusProduk(
            labelStatusProduk,
            indikatorStatusProduk,
            item.terlambat
    );
    labelStatusProduk.setText(item.labelStatus());
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

    Label kosong = new Label("Belum ada riwayat penyewaan.");
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
    long jumlahSedangDisewa = 0;
    
    if (transaksi == null){
        return jumlahSedangDisewa;
    }

    for (Transaksi t : transaksi){
        if (t == null || t.getDaftarDetail() == null){
            continue;
        }

        for (DetailTransaksi detail : t.getDaftarDetail()){
            if (detail == null || detail.getPerlengkapanYangDisewa() == null){
                continue;
            }

            if (detail.getTanggalPengembalianBarang() == null){
                jumlahSedangDisewa = Math.addExact(jumlahSedangDisewa, detail.getKuantitasPerAlat());
            }

            daftarProdukDipinjam.add(new ItemDashboard(t, detail));
        }
    }

    daftarProdukDipinjam.sort((a, b) -> {
        if (a.prioritas() != b.prioritas()){
            return Integer.compare(a.prioritas(), b.prioritas());
        }
        LocalDate tanggalA = a.sudahDikembalikan ? a.detail.getTanggalPengembalianBarang() : a.transaksi.getTanggalKembali();
        LocalDate tanggalB = b.sudahDikembalikan ? b.detail.getTanggalPengembalianBarang() : b.transaksi.getTanggalKembali();

        if (tanggalA == null && tanggalB == null){
            return 0;
        }

        if (tanggalA == null){
            return 1;
        }

        if (tanggalB == null){
            return -1;
        }

        if (a.sudahDikembalikan){
            return tanggalB.compareTo(tanggalA);
        }

        return tanggalA.compareTo(tanggalB);
    });
    return jumlahSedangDisewa;
}

    private String formatJumlah(long jumlah){
        return NumberFormat.getIntegerInstance(Locale.forLanguageTag("id-ID")).format(jumlah) + " Barang";
    }
    private static class ItemDashboard {
    private final Transaksi transaksi;
    private final DetailTransaksi detail;

    private final boolean sudahDikembalikan;
    private final boolean terlambat;
    private final boolean terlambatDikembalikan;

    private ItemDashboard(Transaksi transaksi, DetailTransaksi detail) {
        this.transaksi = transaksi;
        this.detail = detail;

        LocalDate tanggalJatuhTempo = transaksi.getTanggalKembali();
        LocalDate tanggalAktual = detail.getTanggalPengembalianBarang();
        this.sudahDikembalikan = tanggalAktual != null;

        this.terlambat = !sudahDikembalikan && tanggalJatuhTempo != null && LocalDate.now().isAfter(tanggalJatuhTempo);
        this.terlambatDikembalikan = sudahDikembalikan && tanggalJatuhTempo != null && tanggalAktual.isAfter(tanggalJatuhTempo);
    }
    private String ambilKondisi(){
        String kondisi = detail.getStatusPengembalianBarang();

        if (kondisi == null || kondisi.trim().isEmpty()){
            return "Tidak Diketahui";
        }
        return kondisi.trim();
    }

    private boolean kondisiBermasalah(){
        String kondisi = ambilKondisi();

        return kondisi.equalsIgnoreCase("rusak") || kondisi.equalsIgnoreCase("hilang");
    }

    private String labelStatus(){
        if (sudahDikembalikan){
            return "Sudah Dikembalikan • " + (terlambatDikembalikan ? "Terlambat" : "Tepat Waktu") + " " + ambilKondisi().toUpperCase(Locale.ROOT);
        }
        return terlambat ? "Terlambat" : "Sedang disewa";
    }

    private int prioritas(){
        if (!sudahDikembalikan){
            return terlambat ? 0 : 1;
        }
        return 2;
    }
}

}