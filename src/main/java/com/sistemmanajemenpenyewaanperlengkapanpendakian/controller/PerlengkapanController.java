
package com.sistemmanajemenpenyewaanperlengkapanpendakian.controller;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.App;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.Service.PerlengkapanService;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.Service.TransaksiService;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Kompor;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Perlengkapan;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Sepatu;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Tas;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;


public class PerlengkapanController {

    private static final int BATAS_PER_HALAMAN = 5;

    private static final String PATH_PERLENGKAPAN =
            "/com/sistemmanajemenpenyewaanperlengkapanpendakian/fxml/pages/perlengkapan.fxml";

    private static final String PATH_FORMULIR =
            "/com/sistemmanajemenpenyewaanperlengkapanpendakian/fxml/pages/formulir-data-perlengkapan.fxml";

    private static final String PATH_DETAIL =
            "/com/sistemmanajemenpenyewaanperlengkapanpendakian/fxml/pages/detail-perlengkapan.fxml";

    @FXML
    private GridPane gridProduk;

    @FXML
    private Button tambahProduk;

    @FXML
    private HBox containerPagination;

    private final PerlengkapanService perlengkapanService =
            new PerlengkapanService();

    private final List<Perlengkapan> daftarPerlengkapan =
            new ArrayList<>();

    private final List<Perlengkapan> daftarPerlengkapanAsli = new ArrayList<>();

    private int idVendorAktif;
    private int halamanAktif;

    private BorderPane layoutUtama;

    @FXML
    private void initialize() {

        muatPerlengkapan();
    }

    private void muatPerlengkapan() {

        daftarPerlengkapan.clear();
        halamanAktif = 0;

        try {

            idVendorAktif =
                    TransaksiService.getIdVendorAktif();

            daftarPerlengkapan.addAll(
                    perlengkapanService.ambilPerlengkapanVendor(
                            idVendorAktif
                    )
            );
            daftarPerlengkapanAsli.clear();
            daftarPerlengkapanAsli.addAll(daftarPerlengkapan);

            tampilkanDaftar();

        } catch (IllegalArgumentException e) {

            idVendorAktif = 0;

            tampilkanPesanKosong(
                    "Sesi vendor belum aktif. Login untuk melihat data perlengkapan."
            );

        } catch (SQLException e) {

            idVendorAktif = 0;

            tampilkanPesanKosong(
                    "Data perlengkapan gagal dimuat dari database."
            );

            tampilkanPeringatan(
                    Alert.AlertType.ERROR,
                    "Gagal Memuat Perlengkapan",
                    e.getMessage()
            );
        }
    }

    private void tampilkanDaftar() {

        gridProduk.getChildren().clear();

        if (daftarPerlengkapan.isEmpty()) {

            tampilkanPesanKosong(
                    "Belum ada perlengkapan yang terdaftar."
            );

            return;
        }

        int jumlahHalaman = hitungJumlahHalaman();

        if (halamanAktif >= jumlahHalaman) {
            halamanAktif = jumlahHalaman - 1;
        }

        if (halamanAktif < 0) {
            halamanAktif = 0;
        }

        int indeksAwal =
                halamanAktif * BATAS_PER_HALAMAN;

        int indeksAkhir = Math.min(
                indeksAwal + BATAS_PER_HALAMAN,
                daftarPerlengkapan.size()
        );

        for (int indeks = indeksAwal;
                indeks < indeksAkhir;
                indeks++) {

            Perlengkapan perlengkapan =
                    daftarPerlengkapan.get(indeks);

            int posisi = indeks - indeksAwal;

            VBox kartu = buatKartuProduk(perlengkapan);

            GridPane.setHgrow(
                    kartu,
                    Priority.ALWAYS
            );

            GridPane.setFillWidth(
                    kartu,
                    true
            );

            gridProduk.add(
                    kartu,
                    posisi % 4,
                    posisi / 4
            );
        }

        aturPagination();
    }

    private VBox buatKartuProduk(
            Perlengkapan perlengkapan) {

        VBox kartu = new VBox(9);

        kartu.setMinWidth(0);
        kartu.setMaxWidth(Double.MAX_VALUE);
        kartu.setPadding(new Insets(8));

        kartu.getStyleClass().add("kartu-produk");

        kartu.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: #D3D3D3;" +
                "-fx-border-radius: 10;"
        );

        StackPane areaGambar = new StackPane();

        areaGambar.setAlignment(Pos.CENTER);
        areaGambar.setMinHeight(136);
        areaGambar.setPrefHeight(136);
        areaGambar.setMaxHeight(136);

        areaGambar.getStyleClass().add(
                "bingkai-gambar"
        );

        areaGambar.setStyle(
                "-fx-background-color: #F1F1F1;" +
                "-fx-background-radius: 7;"
        );

        ImageView gambar = buatGambar(perlengkapan);

        if (gambar != null) {

            areaGambar.getChildren().add(gambar);

        } else {

            Label ikon = new Label(
                    ikonProduk(perlengkapan)
            );

            ikon.setStyle("-fx-font-size: 58px;");

            areaGambar.getChildren().add(ikon);
        }

        HBox barisNama = new HBox(8);

        barisNama.setAlignment(
                Pos.CENTER_LEFT
        );

        Label nama = new Label(
                perlengkapan.getNamaPerlengkapan()
        );

        nama.setMaxWidth(Double.MAX_VALUE);
        nama.setMinWidth(0);
        nama.setWrapText(true);

        nama.getStyleClass().add("nama-produk");

        nama.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #333333;"
        );

        HBox.setHgrow(
                nama,
                Priority.ALWAYS
        );

        Label status = new Label(
                perlengkapan.getStok() > 0
                        ? "Tersedia"
                        : "Stok Habis"
        );

        status.getStyleClass().add(
                perlengkapan.getStok() > 0
                        ? "badge-tersedia"
                        : "badge-dipinjam"
        );

        status.setStyle(
                perlengkapan.getStok() > 0
                        ? "-fx-background-color: #C9FFBC;" +
                          "-fx-text-fill: #00572B;" +
                          "-fx-background-radius: 5;" +
                          "-fx-padding: 4 7 4 7;" +
                          "-fx-font-size: 9px;"
                        : "-fx-background-color: #FEC5C5;" +
                          "-fx-text-fill: #861010;" +
                          "-fx-background-radius: 5;" +
                          "-fx-padding: 4 7 4 7;" +
                          "-fx-font-size: 9px;"
        );

        barisNama.getChildren().addAll(
                nama,
                status
        );


        HBox barisHarga = new HBox(4);

        barisHarga.setAlignment(
                Pos.BASELINE_LEFT
        );

        Label harga = new Label(
                "Rp " + formatRupiah(
                        perlengkapan.getHargaSewaHarian()
                )
        );

        harga.getStyleClass().add("harga");

        harga.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #005A32;"
        );

        Label satuan = new Label("/ Hari");

        satuan.getStyleClass().add("harga-satuan");

        satuan.setStyle(
                "-fx-font-size: 11px;" +
                "-fx-text-fill: #555555;"
        );

        barisHarga.getChildren().addAll(
                harga,
                satuan
        );

        Button detail = new Button("Detail");

        detail.setMaxWidth(Double.MAX_VALUE);
        detail.setMnemonicParsing(false);

        detail.getStyleClass().add("btn-detail");

        detail.setStyle(
                "-fx-background-color: #00572B;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 5;" +
                "-fx-cursor: hand;"
        );

        detail.setUserData(perlengkapan);

        detail.setOnAction(
                this::lihatDetail
        );


        kartu.getChildren().addAll(
                areaGambar,
                barisNama,
                barisHarga,
                detail
        );

        return kartu;
    }

    private int hitungJumlahHalaman() {

        return Math.max(
                1,
                (int) Math.ceil(
                        (double) daftarPerlengkapan.size()
                        / BATAS_PER_HALAMAN
                )
        );
    }


    private void aturPagination() {

        containerPagination.getChildren().clear();

        int jumlahHalaman = hitungJumlahHalaman();

        Button sebelumnya =buatTombolPagination("‹");

        sebelumnya.setDisable(
                halamanAktif == 0
        );

        sebelumnya.setOnAction(event -> {

            if (halamanAktif > 0) {

                halamanAktif--;

                tampilkanDaftar();
            }
        });

        containerPagination.getChildren().add(
                sebelumnya
        );

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

                tampilkanDaftar();
            });

            containerPagination.getChildren().add(
                    nomor
            );
        }

        Button berikutnya =
                buatTombolPagination("›");

        berikutnya.setDisable(
                halamanAktif >= jumlahHalaman - 1
        );

        berikutnya.setOnAction(event -> {

            if (halamanAktif < jumlahHalaman - 1) {

                halamanAktif++;

                tampilkanDaftar();
            }
        });

        containerPagination.getChildren().add(
                berikutnya
        );

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

    @FXML
    private void tambahProduk() {

        if (!siapkanLayoutUtama()) {

            tampilkanPeringatan(
                    Alert.AlertType.ERROR,
                    "Gagal Membuka Formulir",
                    "Layout utama aplikasi tidak ditemukan."
            );

            return;
        }

        try {

            URL lokasiFormulir =
                    App.class.getResource(PATH_FORMULIR);

            if (lokasiFormulir == null) {

                throw new IOException(
                        "formulir-data-perlengkapan.fxml tidak ditemukan."
                );
            }

            FXMLLoader loader =
                    new FXMLLoader(lokasiFormulir);

            Parent formulir = loader.load();

            ControllerFormulirDataPerlengkapan controller =
                    loader.getController();

            controller.setAksiKembali(
                    this::kembaliKeDaftar
            );

            BorderPane.setMargin(
                    formulir,
                    new Insets(16)
            );

            layoutUtama.setCenter(formulir);

        } catch (IOException | RuntimeException e) {

            tampilkanPeringatan(
                    Alert.AlertType.ERROR,
                    "Gagal Membuka Formulir",
                    e.getMessage()
            );
        }
    }

    private boolean siapkanLayoutUtama() {

        if (layoutUtama != null) {
            return true;
        }

        if (gridProduk.getScene() == null) {
            return false;
        }

        Node node = gridProduk;

        while (node.getParent() != null) {

            Parent parent = node.getParent();

            if (parent instanceof BorderPane
                    && ((BorderPane) parent).getCenter() == node) {

                layoutUtama = (BorderPane) parent;

                break;
            }

            node = parent;
        }

        return layoutUtama != null;
    }

    private void kembaliKeDaftar() {

        if (layoutUtama == null) {
            return;
        }

        try {

            URL lokasiDaftar =
                    App.class.getResource(PATH_PERLENGKAPAN);

            if (lokasiDaftar == null) {

                throw new IOException(
                        "perlengkapan.fxml tidak ditemukan."
                );
            }

            FXMLLoader loader =
                    new FXMLLoader(lokasiDaftar);

            Parent halamanPerlengkapan = loader.load();

            BorderPane.setMargin(
                    halamanPerlengkapan,
                    new Insets(16)
            );

            layoutUtama.setCenter(
                    halamanPerlengkapan
            );

        } catch (IOException | RuntimeException e) {

            tampilkanPeringatan(
                    Alert.AlertType.ERROR,
                    "Gagal Kembali",
                    e.getMessage()
            );
        }
    }

    @FXML
    private void lihatDetail(ActionEvent event) {

        if (!(event.getSource() instanceof Button)) {
            return;
        }

        Object data =
                ((Button) event.getSource()).getUserData();

        if (!(data instanceof Perlengkapan)) {
            return;
        }

        Perlengkapan perlengkapan =
                (Perlengkapan) data;

        if (!siapkanLayoutUtama()) {

            tampilkanPeringatan(
                    Alert.AlertType.ERROR,
                    "Gagal Membuka Detail",
                    "Layout utama aplikasi tidak ditemukan."
            );

            return;
        }

        try {

            URL lokasiDetail =
                    App.class.getResource(PATH_DETAIL);

            if (lokasiDetail == null) {

                throw new IOException(
                        "detail-perlengkapan.fxml tidak ditemukan."
                );
            }

            FXMLLoader loader =
                    new FXMLLoader(lokasiDetail);

            Parent halamanDetail = loader.load();

            DetailPerlengkapanController controller =
                    loader.getController();

            controller.setData(
                    perlengkapan,
                    this::kembaliKeDaftar
            );

            BorderPane.setMargin(
                    halamanDetail,
                    new Insets(16)
            );

            layoutUtama.setCenter(halamanDetail);

        } catch (IOException | RuntimeException e) {

            tampilkanPeringatan(
                    Alert.AlertType.ERROR,
                    "Gagal Membuka Detail",
                    e.getMessage()
            );
        }
    }

    private ImageView buatGambar(
            Perlengkapan perlengkapan) {

        String lokasi = perlengkapan.getFotoBarang();

        if (lokasi == null || lokasi.trim().isEmpty()) {
            return null;
        }

        try {

            String sumber = lokasi.trim();

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

            Image gambar = new Image(
                    sumber,
                    160,
                    136,
                    true,
                    true
            );

            if (gambar.isError()) {
                return null;
            }

            ImageView view = new ImageView(gambar);

            view.setFitWidth(160);
            view.setFitHeight(136);
            view.setPreserveRatio(true);
            view.setSmooth(true);

            return view;

        } catch (RuntimeException e) {

            return null;
        }
    }


    private String ikonProduk(
            Perlengkapan perlengkapan) {

        if (perlengkapan instanceof Kompor) {
            return "🔥";
        }

        if (perlengkapan instanceof Sepatu) {
            return "🥾";
        }

        return "🎒";
    }

    private void tampilkanPesanKosong(String pesan) {

        gridProduk.getChildren().clear();

        Label label = new Label(pesan);

        label.setWrapText(true);

        label.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-text-fill: #686868;" +
                "-fx-padding: 24px;"
        );

        GridPane.setColumnSpan(label, 4);

        gridProduk.add(
                label,
                0,
                0
        );

        aturPagination();
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

    public void terapkanPencarian(String kataKunci) {

    daftarPerlengkapan.clear();

    if (kataKunci == null || kataKunci.trim().isEmpty()) {
        daftarPerlengkapan.addAll(daftarPerlengkapanAsli);
    } else {
        String kataKunciBersih = kataKunci.trim().toLowerCase();

        List<Perlengkapan> hasilPencarian = daftarPerlengkapanAsli.stream()
                .filter(p -> p.getNamaPerlengkapan() != null
                        && p.getNamaPerlengkapan()
                        .toLowerCase()
                        .contains(kataKunciBersih))
                .collect(java.util.stream.Collectors.toList());

        daftarPerlengkapan.addAll(hasilPencarian);
    }

    halamanAktif = 0;
    tampilkanDaftar();
    }

}
