package com.sistemmanajemenpenyewaanperlengkapanpendakian.controller;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.App;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.Service.TransaksiService;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Transaksi;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.DetailTransaksi;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Perlengkapan;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import javafx.scene.control.ScrollPane;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Arrays;
import java.util.Optional;
import javafx.scene.control.ChoiceDialog;

public class TransaksiController {

    @FXML
    private VBox containerTransaksi;

    private static final String PATH_ITEM = "/com/sistemmanajemenpenyewaanperlengkapanpendakian/fxml/pages/item-transaksi.fxml";
    private static final String PATH_NOTA = "/com/sistemmanajemenpenyewaanperlengkapanpendakian/fxml/pages/nota.fxml";
    private static final String PATH_RINGKASAN = "/com/sistemmanajemenpenyewaanperlengkapanpendakian/fxml/pages/ringkasan-transaksi.fxml";
    private static final String PATH_TAMBAH = "/com/sistemmanajemenpenyewaanperlengkapanpendakian/fxml/pages/tambah-transaksi.fxml";
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private BorderPane layoutUtama;
    private Node halamanTransaksi;

    @FXML
    public void initialize() {
        muatTransaksi();
    }

    private void muatTransaksi() {
    containerTransaksi.getChildren().clear();

    List<Transaksi> daftarTransaksi;

    try {
        daftarTransaksi = TransaksiService.semua();
    } catch (IllegalArgumentException e) {
        Label kosong = new Label(
                "Login vendor diperlukan untuk melihat transaksi."
        );
        kosong.setStyle(
                "-fx-font-size: 14px; -fx-text-fill: #7A7A7A;"
        );
        containerTransaksi.getChildren().add(kosong);
        return;
    }

    if (daftarTransaksi.isEmpty()) {
        Label kosong = new Label(
                "Belum ada transaksi. Klik \"Tambah Transaksi\" untuk membuat yang pertama."
        );
        kosong.setStyle(
                "-fx-font-size: 14px; -fx-text-fill: #7A7A7A;"
        );
        containerTransaksi.getChildren().add(kosong);
        return;
    }

    for (Transaksi t : daftarTransaksi) {
        tambahKartu(t, "/images/tas.png");
    }
}

    private void tambahKartu(Transaksi t, String gambar) {
        try {
            var res = App.class.getResource(PATH_ITEM);
            if (res == null) {
                throw new IllegalStateException( "FXML item transaksi tidak ditemukan.");
            }

            FXMLLoader loader = new FXMLLoader(res);
            Region kartu = loader.load();
            ItemTransaksiController item = loader.getController();

            String barang = t.getDaftarDetail() == null ? "" : t.getDaftarDetail().stream().filter(detail -> detail != null && detail.getPerlengkapanYangDisewa() != null).map(detail -> detail.getPerlengkapanYangDisewa().getNamaPerlengkapan()).filter(nama -> nama != null && !nama.isBlank()).distinct().collect(Collectors.joining(", "));

            if (barang.isBlank()){
                barang = "Perlengkapan tidak diketahui";
            }

            String penyewa = t.getPelanggan() == null || t.getPelanggan().getNamaPelanggan() == null ? "-" : t.getPelanggan().getNamaPelanggan();
            String tglSewa = t.getTanggalSewa() == null ? "-" : t.getTanggalSewa().format(FMT);
            String tglKembali = t.getTanggalKembali() == null ? "-" : t.getTanggalKembali().format(FMT);
            String status = buatRingkasanStatusPengembalian(t);

            item.setData(barang, penyewa, tglSewa, tglKembali, status, gambar, () -> tampilkanDetail(t, gambar));
            item.setAksiCetak(() -> tampilkanNota(t));

            kartu.setMaxWidth(Double.MAX_VALUE);
            containerTransaksi.getChildren().add(kartu);
        } catch (Exception e) {
            System.err.println("Gagal memuat kartu transaksi:");
            e.printStackTrace();

            new Alert(Alert.AlertType.ERROR, "Kartu transaksi gagal dimuat:\n" + e.getMessage()).showAndWait();
        }
    }

    private String buatRingkasanStatusPengembalian(Transaksi transaksi){
        if (transaksi == null || transaksi.getDaftarDetail() == null || transaksi.getDaftarDetail().isEmpty()){
            return "Berhasil";
        }

        StringBuilder hasil = new StringBuilder();
        for (DetailTransaksi detail : transaksi.getDaftarDetail()){
            if (detail == null || detail.getPerlengkapanYangDisewa() == null || detail.getTanggalPengembalianBarang() == null){
                continue;
            }
            if (hasil.length() > 0){
                hasil.append("\n");
            }
            hasil.append(statusPengembalian(transaksi, detail));
        }
        return hasil.length() == 0 ? "Berhasil" : hasil.toString();
    }

    private String statusPengembalian(Transaksi transaksi, DetailTransaksi detail){
        LocalDate tanggalJatuhTempo = transaksi.getTanggalKembali();
        LocalDate tanggalAktual = detail.getTanggalPengembalianBarang();

        if (tanggalAktual == null){
            boolean terlambat = tanggalJatuhTempo != null && LocalDate.now().isAfter(tanggalJatuhTempo);

            return terlambat ? "Masih Disewa • Terlambat" : "Sedang Disewa";
        }

        String kondisi = detail.getStatusPengembalianBarang();

        if (kondisi == null || kondisi.trim().isEmpty()){
            kondisi = "Tidak diketahui";    
        }

        boolean terlambat = tanggalJatuhTempo != null && tanggalAktual.isAfter(tanggalJatuhTempo);

        return "Sudah Dikembalikan | " + (terlambat ? "Terlambat" : "Tepat Waktu") + " " + kondisi.toUpperCase(Locale.ROOT);
    }

    @FXML
    private void tambahTransaksi() {
        try {
            BorderPane layout = (BorderPane) containerTransaksi.getScene().lookup("#layoutPage");
            Parent form = FXMLLoader.load(App.class.getResource(PATH_TAMBAH));
            BorderPane.setMargin(form, new Insets(16));
            layout.setCenter(form);
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Gagal membuka form:\n" + e).showAndWait();
        }
    }

    private boolean siapkanLayoutUtama() {
        if (containerTransaksi.getScene() == null)
            return false;
        if (layoutUtama == null) {
            Node node = containerTransaksi;
            while (node.getParent() != null) {
                Parent parent = node.getParent();
                if (parent instanceof BorderPane && ((BorderPane) parent).getCenter() == node) {
                    layoutUtama = (BorderPane) parent;
                    break;
                }
                node = parent;
            }
        }
        if (layoutUtama == null) {
            System.err.println("Layout konten utama tidak ditemukan.");
            return false;
        }
        return true;
    }

    
private void tampilkanNota(Transaksi t) {

    if (!siapkanLayoutUtama()) {
        return;
    }

    ChoiceDialog<String> dialog = new ChoiceDialog<>(
            "Nota",
            Arrays.asList("Nota", "Ringkasan")
    );

    dialog.setTitle("Cetak Transaksi");
    dialog.setHeaderText("Pilih jenis dokumen yang akan dicetak");
    dialog.setContentText("Jenis dokumen:");

    Optional<String> pilihan = dialog.showAndWait();

    if (!pilihan.isPresent()) {
        return;
    }

    try {

        // Simpan halaman transaksi sebelumnya
        halamanTransaksi = layoutUtama.getCenter();

        // Jika pengguna memilih Ringkasan
        if ("Ringkasan".equals(pilihan.get())) {

            FXMLLoader loader = new FXMLLoader(
                    App.class.getResource(PATH_RINGKASAN)
            );

            Parent ringkasan = loader.load();

            RingkasanTransaksiController controller =
                    loader.getController();

            // Masukkan data transaksi yang dipilih
            controller.setDataTransaksi(t);

            BorderPane.setMargin(ringkasan, new Insets(16));
            layoutUtama.setCenter(ringkasan);

            return;
        }

        // Jika pengguna memilih Nota
        FXMLLoader loader = new FXMLLoader(
                App.class.getResource(PATH_NOTA)
        );

        Parent nota = loader.load();

        NotaController controller = loader.getController();

        controller.setTransaksi(t, pilihan.get());
        controller.setAksiKembali(this::kembaliKeTransaksi);

        BorderPane.setMargin(nota, new Insets(16));
        layoutUtama.setCenter(nota);

    } catch (IOException e) {

        e.printStackTrace();

        new Alert(
                Alert.AlertType.ERROR,
                "Gagal membuka preview dokumen:\n" + e.getMessage()
        ).showAndWait();
    }
}


    private void tampilkanDetail(Transaksi transaksi, String gambar) {
        if (containerTransaksi.getScene() == null || transaksi == null)
            return;

        if (!siapkanLayoutUtama()){
            return;
        }

        halamanTransaksi = layoutUtama.getCenter();

        VBox detail = new VBox(14);
        detail.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        detail.setStyle("-fx-background-color: #F0F0F0;");

        HBox judul = new HBox(8);
        judul.setAlignment(Pos.CENTER_LEFT);
        judul.setPadding(new Insets(0, 0, 0, 8));

        Label tombolKembali = new Label("<");
        tombolKembali.setStyle(
                "-fx-font-size: 26px;" + 
                "-fx-font-weight: bold;" + 
                "-fx-text-fill: #00572B;" + 
                "-fx-cursor: hand;"
        );

        tombolKembali.setOnMouseClicked(event -> kembaliKeTransaksi());

        Label labelJudul = new Label("Detail Transaksi");
        labelJudul.setStyle(
                "-fx-font-size: 22px;"+ 
                "-fx-font-weight: bold;"+ 
                "-fx-text-fill: #00572B;"
        );
        judul.getChildren().addAll(tombolKembali, labelJudul);

        VBox panel = new VBox(12);

        panel.setPadding(new Insets(18, 44, 18, 44));
        panel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        panel.setStyle(
                "-fx-background-color: white;"+ 
                "-fx-background-radius: 16;"
        );

        ImageView foto = new ImageView();

        foto.setFitWidth(100);
        foto.setFitHeight(100);
        foto.setPreserveRatio(true);
        foto.setSmooth(true);
        
        java.net.URL url = App.class.getResource(gambar);
        if (url != null){
            foto.setImage(new Image(url.toExternalForm()));
        }

        HBox wadahFoto = new HBox(foto);
        wadahFoto.setAlignment(Pos.CENTER);

        panel.getChildren().add(wadahFoto);
        String namaBarang = transaksi.getDaftarDetail() == null ? "Perlengkapan tidak diketahui" : transaksi.getDaftarDetail().stream().filter(d -> d != null && d.getPerlengkapanYangDisewa() != null).map(d -> d.getPerlengkapanYangDisewa().getNamaPerlengkapan()).distinct().collect(Collectors.joining(", "));
        String penyewa = transaksi.getPelanggan() == null || transaksi.getPelanggan().getNamaPelanggan() == null ? "-" : transaksi.getPelanggan().getNamaPelanggan();
        String fotoIdentitas = transaksi.getFotoIdentitas() == null ? "-": transaksi.getFotoIdentitas();
        String tanggalSewa = transaksi.getTanggalSewa() == null ? "-" : transaksi.getTanggalSewa().format(FMT);
        String tanggalKembali = transaksi.getTanggalKembali() == null ? "-" : transaksi.getTanggalKembali().format(FMT);
        
        panel.getChildren().addAll(
            buatFieldDetail("Nama Barang", namaBarang),
            buatFieldDetail("Status Transaksi", "Berhasil"),
            buatFieldDetail("Nama Peminjam", penyewa),
            buatFieldDetail("Kartu Identitas Peminjam", fotoIdentitas),
            buatFieldDetail("Tanggal Meminjam", tanggalSewa),
            buatFieldDetail("Tanggal Kembali", tanggalKembali),
            buatFieldDetail("Total Harga",formatRupiah(transaksi.getTotalHarga())), buatFieldDetail("Total Denda", formatRupiah(transaksi.getDenda())));

    if (transaksi.getDaftarDetail() != null) {

        for (DetailTransaksi item : transaksi.getDaftarDetail()) {

            if (item == null|| item.getPerlengkapanYangDisewa() == null) {
                continue;
            }

            String nama = item.getPerlengkapanYangDisewa().getNamaPerlengkapan();

            panel.getChildren().add(buatFieldDetail("Pengembalian - " + nama, statusPengembalian(transaksi, item)));

            if (item.getTanggalPengembalianBarang() != null) {

                panel.getChildren().add(
                        buatFieldDetail("Tanggal Pengembalian Aktual - " + nama, item.getTanggalPengembalianBarang().format(FMT)));
            }

            if (item.getBiayaGantiRugi() > 0) {

                panel.getChildren().add(buatFieldDetail("Biaya Ganti Rugi - " + nama, formatRupiah(item.getBiayaGantiRugi())));
            }
        }
    }

        Region ruang = new Region();
    VBox.setVgrow(ruang, Priority.ALWAYS);

    panel.getChildren().add(ruang);

    Button konfirmasi = new Button("Konfirmasi");

    konfirmasi.setMinWidth(95);
    konfirmasi.setPrefHeight(30);
    konfirmasi.setFocusTraversable(false);

    konfirmasi.setStyle(
            "-fx-background-color: #00572B;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 7;" +
            "-fx-padding: 7 18;" +
            "-fx-font-size: 11px;" +
            "-fx-cursor: hand;"
    );

    konfirmasi.setOnAction(event -> kembaliKeTransaksi());

    HBox areaTombol = new HBox(konfirmasi);
    areaTombol.setAlignment(Pos.CENTER_RIGHT);
    panel.getChildren().add(areaTombol);

    ScrollPane scrollPanel = new ScrollPane(panel);
    scrollPanel.setFitToWidth(true);
    scrollPanel.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    scrollPanel.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    scrollPanel.setPannable(true);
    scrollPanel.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

    VBox.setVgrow(scrollPanel, Priority.ALWAYS);

    detail.getChildren().addAll(judul, scrollPanel);
    BorderPane.setMargin(detail, new Insets(16));
    layoutUtama.setCenter(detail);
    }

    private String formatRupiah(long nilai){
        NumberFormat format = NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID"));

        return "Rp " + format.format(nilai);
    }
    private VBox buatFieldDetail(String nama, String nilai) {
        Label label = new Label(nama);
        label.setStyle("-fx-font-size: 12px; -fx-text-fill: black;");

        TextField field = new TextField(nilai);
        field.setEditable(false);
        field.setFocusTraversable(false);
        field.setPrefHeight(30);
        field.setMaxWidth(Double.MAX_VALUE);
        field.setStyle(
                "-fx-background-color: #E8E8E8; -fx-background-radius: 6; -fx-border-color: #555555; -fx-border-radius: 6; -fx-font-size: 13px;");

        VBox grup = new VBox(5, label, field);
        grup.setMaxWidth(Double.MAX_VALUE);
        return grup;
    }

    private void kembaliKeTransaksi() {
        if (layoutUtama != null && halamanTransaksi != null) {
            BorderPane.setMargin(halamanTransaksi, new Insets(16));
            layoutUtama.setCenter(halamanTransaksi);
        }
    }
}
