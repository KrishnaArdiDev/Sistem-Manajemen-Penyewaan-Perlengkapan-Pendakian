package com.sistemmanajemenpenyewaanperlengkapanpendakian.controller;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.DatabaseConnection;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.dao.PerlengkapanDAO;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Perlengkapan;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Kompor;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Tas;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Sepatu;

import javafx.fxml.FXML;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class PerlengkapanController {

    @FXML
    private BorderPane perlengkapanRoot;

    private final PerlengkapanDAO dao = new PerlengkapanDAO();

    private final ObservableList<Perlengkapan> daftar =
            FXCollections.observableArrayList();

    private static final String HIJAU = "#075D39";
    private static final String LATAR = "#F0F0F0";

    private File fotoTerpilih;
    private boolean hapusFotoLama = false;

    @FXML
    public void initialize() {
        tampilkanDaftar();
    }

    private void tampilkanDaftar() {
        if (perlengkapanRoot == null) {
            System.err.println("perlengkapanRoot belum terhubung ke FXML.");
            return;
        }

        try {
            daftar.setAll(dao.getAllPerlengkapan());
        } catch (SQLException e) {
            tampilkanPesan("Gagal memuat data", e.getMessage());
        }

        VBox halaman = new VBox(18);
        halaman.setPadding(new Insets(24, 28, 24, 28));
        halaman.setStyle("-fx-background-color:" + LATAR + ";");

        HBox judulBaris = new HBox(12);
        judulBaris.setAlignment(Pos.CENTER_LEFT);

        VBox judulKolom = new VBox(5);

        Label judul = new Label("Data Perlengkapan");
        judul.setStyle(
                "-fx-font-size:26px;"
                + "-fx-font-weight:bold;"
                + "-fx-text-fill:" + HIJAU + ";"
        );

        Label deskripsi = new Label(
                "Kelola inventaris perlengkapan penyewaan pendakian."
        );

        judulKolom.getChildren().addAll(judul, deskripsi);

        Region ruang = new Region();
        HBox.setHgrow(ruang, Priority.ALWAYS);

        Button tambah = tombolHijau("+ Tambah Produk");
        tambah.setOnAction(e -> tampilkanFormTambah());

        judulBaris.getChildren().addAll(judulKolom, ruang, tambah);

        TextField pencarian = new TextField();
        pencarian.setPromptText("Masukkan nama barang yang ingin dicari...");
        pencarian.setPrefHeight(42);
        pencarian.setStyle(
                "-fx-background-color:white;"
                + "-fx-border-color:" + HIJAU + ";"
                + "-fx-border-radius:22;"
                + "-fx-background-radius:22;"
                + "-fx-padding:0 15;"
        );

        Button cari = tombolOutline("Cari");
        cari.setOnAction(e -> cariBarang(pencarian.getText()));
        pencarian.setOnAction(e -> cariBarang(pencarian.getText()));

        HBox cariBaris = new HBox(10, pencarian, cari);
        HBox.setHgrow(pencarian, Priority.ALWAYS);
        cariBaris.setAlignment(Pos.CENTER_LEFT);

        FlowPane kartuPane = new FlowPane();
        kartuPane.setHgap(18);
        kartuPane.setVgap(18);
        kartuPane.setPadding(new Insets(5));

        for (Perlengkapan p : daftar) {
            kartuPane.getChildren().add(buatKartu(p));
        }

        ScrollPane scroll = new ScrollPane(kartuPane);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle(
                "-fx-background-color:transparent;"
                + "-fx-background:transparent;"
                + "-fx-border-color:transparent;"
        );

        VBox.setVgrow(scroll, Priority.ALWAYS);
        halaman.getChildren().addAll(judulBaris, cariBaris, scroll);
        perlengkapanRoot.setCenter(halaman);
    }

    private VBox buatKartu(Perlengkapan p) {
        VBox kartu = new VBox(10);
        kartu.setPrefWidth(260);
        kartu.setMinWidth(220);
        kartu.setPadding(new Insets(14));
        kartu.setStyle(
                "-fx-background-color:white;"
                + "-fx-background-radius:16;"
                + "-fx-border-color:#DFE8E2;"
                + "-fx-border-radius:16;"
        );

        StackPane areaFoto = new StackPane();
        areaFoto.setPrefSize(235, 180);
        areaFoto.setMinHeight(180);
        areaFoto.setMaxWidth(Double.MAX_VALUE);
        areaFoto.setStyle(
                "-fx-background-color:#E8F0EB;"
                + "-fx-background-radius:10;"
        );

        ImageView foto = buatFotoBarang(
                p.getIdPerlengkapan(), 225, 170
        );

        if (foto != null) {
            areaFoto.getChildren().add(foto);
        } else {
            Label tanpaFoto = new Label("Tidak ada foto");
            tanpaFoto.setWrapText(true);
            tanpaFoto.setStyle(
                    "-fx-font-size:14px;"
                    + "-fx-text-fill:#777777;"
            );
            areaFoto.getChildren().add(tanpaFoto);
        }

        String namaProduk = p.getNamaPerlengkapan();

        if (namaProduk == null || namaProduk.trim().isEmpty()) {
            namaProduk = "Nama produk belum diisi";
        }

        Label nama = new Label(namaProduk);
        nama.setWrapText(true);
        nama.setMaxWidth(Double.MAX_VALUE);
        nama.setStyle(
                "-fx-font-size:16px;"
                + "-fx-font-weight:bold;"
                + "-fx-text-fill:#222222;"
        );

        Label status = new Label(
                p.getStok() > 0
                        ? "Tersedia • Stok " + p.getStok()
                        : "Stok habis"
        );

        status.setStyle(
                p.getStok() > 0
                        ? "-fx-background-color:#D6F5DD;"
                          + "-fx-text-fill:#176B35;"
                          + "-fx-padding:5 9;"
                          + "-fx-background-radius:5;"
                        : "-fx-background-color:#FFDADA;"
                          + "-fx-text-fill:#A02020;"
                          + "-fx-padding:5 9;"
                          + "-fx-background-radius:5;"
        );

        Label harga = new Label(
                formatRupiah(p.getHargaSewaHarian()) + " / hari"
        );
        harga.setWrapText(true);
        harga.setStyle(
                "-fx-font-size:17px;"
                + "-fx-font-weight:bold;"
                + "-fx-text-fill:" + HIJAU + ";"
        );

        Button detail = tombolHijau("Lihat Detail");
        detail.setMaxWidth(Double.MAX_VALUE);
        detail.setOnAction(e -> tampilkanDetail(p));

        Button edit = tombolOutline("Edit Produk");
        edit.setMaxWidth(Double.MAX_VALUE);
        edit.setOnAction(e -> tampilkanFormEdit(p));

        kartu.getChildren().addAll(
                areaFoto, nama, status, harga, detail, edit
        );

        return kartu;
    }

    private void cariBarang(String kata) {
        String cari = kata == null
                ? ""
                : kata.trim().toLowerCase(Locale.ROOT);

        if (cari.isEmpty()) {
            tampilkanDaftar();
            return;
        }

        List<Perlengkapan> hasil = daftar.stream()
                .filter(p -> p.getNamaPerlengkapan() != null
                        && p.getNamaPerlengkapan()
                                .toLowerCase(Locale.ROOT)
                                .contains(cari))
                .collect(Collectors.toList());

        FlowPane pane = new FlowPane();
        pane.setHgap(18);
        pane.setVgap(18);
        pane.setPadding(new Insets(5));

        if (hasil.isEmpty()) {
            pane.getChildren().add(new Label("Produk tidak ditemukan."));
        } else {
            for (Perlengkapan p : hasil) {
                pane.getChildren().add(buatKartu(p));
            }
        }

        Node pusat = perlengkapanRoot.getCenter();

        if (pusat instanceof VBox) {
            VBox halaman = (VBox) pusat;

            if (halaman.getChildren().size() > 2
                    && halaman.getChildren().get(2) instanceof ScrollPane) {
                ((ScrollPane) halaman.getChildren().get(2)).setContent(pane);
            }
        }
    }

    private void tampilkanFormTambah() {
        fotoTerpilih = null;
        hapusFotoLama = false;

        VBox halaman = halamanForm("FORMULIR TAMBAH PERLENGKAPAN");

        TextField nama = field();
        nama.setPromptText("Masukkan nama barang");

        ComboBox<VendorPilihan> vendor = new ComboBox<>();
        vendor.setPromptText("Pilih vendor");
        vendor.setMaxWidth(Double.MAX_VALUE);
        muatVendor(vendor);

        ComboBox<String> jenis = new ComboBox<>();
        jenis.getItems().addAll("kompor", "tas", "sepatu");
        jenis.setValue("tas");
        jenis.setMaxWidth(Double.MAX_VALUE);

        TextField tipeKompor = field();
        tipeKompor.setPromptText("Tipe kompor");

        TextField tipeTas = field();
        tipeTas.setPromptText("Tipe tas");

        TextField ukuranSepatu = field();
        ukuranSepatu.setPromptText("Ukuran sepatu");

        TextField hargaBarang = field();
        hargaBarang.setPromptText("Harga barang");

        TextField hargaSewa = field();
        hargaSewa.setPromptText("Harga sewa per hari");

        TextField stok = field();
        stok.setPromptText("Total stok");

        VBox bagianKhusus = new VBox(8);

        Runnable perbaruiJenis = () -> {
            bagianKhusus.getChildren().clear();

            if ("kompor".equals(jenis.getValue())) {
                bagianKhusus.getChildren().addAll(
                        labelForm("Tipe Kompor"), tipeKompor
                );
            } else if ("tas".equals(jenis.getValue())) {
                bagianKhusus.getChildren().addAll(
                        labelForm("Tipe Tas"), tipeTas
                );
            } else if ("sepatu".equals(jenis.getValue())) {
                bagianKhusus.getChildren().addAll(
                        labelForm("Ukuran Sepatu"), ukuranSepatu
                );
            }
        };

        jenis.setOnAction(e -> perbaruiJenis.run());
        perbaruiJenis.run();

        Label petunjukFoto = new Label(
                "Pilih foto untuk menampilkan gambar produk"
        );

        StackPane areaUnggah = buatAreaUnggah(petunjukFoto, null);

        Button pilih = tombolOutline("Pilih Foto");
        pilih.setOnAction(e -> pilihFoto(petunjukFoto, areaUnggah));

        VBox kolom = new VBox(10);
        kolom.setMaxWidth(Double.MAX_VALUE);
        kolom.getChildren().addAll(
                labelForm("Nama Barang"), nama,
                labelForm("Vendor"), vendor,
                labelForm("Jenis Perlengkapan"), jenis,
                bagianKhusus,
                labelForm("Harga Barang"), hargaBarang,
                labelForm("Harga Sewa / Hari"), hargaSewa,
                labelForm("Total Stok"), stok,
                labelForm("Foto Barang"), areaUnggah, pilih
        );

        Button batal = tombolOutline("Batalkan");
        batal.setOnAction(e -> tampilkanDaftar());

        Button simpan = tombolHijau("Konfirmasi");
        simpan.setOnAction(e -> simpanBarang(
                nama, vendor, jenis, stok, hargaBarang, hargaSewa,
                tipeKompor, tipeTas, ukuranSepatu
        ));

        HBox aksi = new HBox(12, batal, simpan);
        aksi.setAlignment(Pos.CENTER_RIGHT);
        kolom.getChildren().add(aksi);

        tampilkanPanelForm(halaman, kolom);
    }

    private void tampilkanFormEdit(Perlengkapan p) {
        fotoTerpilih = null;
        hapusFotoLama = false;

        VBox halaman = halamanForm("EDIT PERLENGKAPAN");

        TextField nama = field();
        nama.setText(aman(p.getNamaPerlengkapan()));

        ComboBox<VendorPilihan> vendor = new ComboBox<>();
        vendor.setPromptText("Pilih vendor");
        vendor.setMaxWidth(Double.MAX_VALUE);
        muatVendor(vendor);

        for (VendorPilihan v : vendor.getItems()) {
            if (v.id == p.getIdVendor()) {
                vendor.setValue(v);
                break;
            }
        }

        ComboBox<String> jenis = new ComboBox<>();
        jenis.getItems().addAll("kompor", "tas", "sepatu");
        jenis.setValue(
                p.getJenisPerlengkapan() == null
                        ? "tas"
                        : p.getJenisPerlengkapan().toLowerCase(Locale.ROOT)
        );
        jenis.setMaxWidth(Double.MAX_VALUE);

        TextField tipeKompor = field();
        tipeKompor.setPromptText("Tipe kompor");

        TextField tipeTas = field();
        tipeTas.setPromptText("Tipe tas");

        TextField ukuranSepatu = field();
        ukuranSepatu.setPromptText("Ukuran sepatu");

        if (p instanceof Kompor) {
            tipeKompor.setText(aman(((Kompor) p).getTipeKompor()));
        } else if (p instanceof Tas) {
            tipeTas.setText(aman(((Tas) p).getTipeTas()));
        } else if (p instanceof Sepatu) {
            ukuranSepatu.setText(
                    String.valueOf(((Sepatu) p).getUkuranSepatu())
            );
        }

        TextField hargaBarang = field();
        hargaBarang.setText(String.valueOf(p.getHargaPerlengkapan()));

        TextField hargaSewa = field();
        hargaSewa.setText(String.valueOf(p.getHargaSewaHarian()));

        TextField stok = field();
        stok.setText(String.valueOf(p.getStok()));

        VBox bagianKhusus = new VBox(8);

        Runnable perbaruiJenis = () -> {
            bagianKhusus.getChildren().clear();

            if ("kompor".equals(jenis.getValue())) {
                bagianKhusus.getChildren().addAll(
                        labelForm("Tipe Kompor"), tipeKompor
                );
            } else if ("tas".equals(jenis.getValue())) {
                bagianKhusus.getChildren().addAll(
                        labelForm("Tipe Tas"), tipeTas
                );
            } else if ("sepatu".equals(jenis.getValue())) {
                bagianKhusus.getChildren().addAll(
                        labelForm("Ukuran Sepatu"), ukuranSepatu
                );
            }
        };

        jenis.setOnAction(e -> perbaruiJenis.run());
        perbaruiJenis.run();

        Label petunjukFoto = new Label(
                "Foto produk akan diperbarui setelah disimpan"
        );

        ImageView fotoLama = buatFotoBarang(
                p.getIdPerlengkapan(), 280, 125
        );

        StackPane areaUnggah = buatAreaUnggah(petunjukFoto, fotoLama);

        Button gantiFoto = tombolOutline("Pilih Foto Baru");
        gantiFoto.setOnAction(e -> pilihFoto(petunjukFoto, areaUnggah));

        VBox kolom = new VBox(10);
        kolom.setMaxWidth(Double.MAX_VALUE);
        kolom.getChildren().addAll(
                labelForm("Nama Barang"), nama,
                labelForm("Vendor"), vendor,
                labelForm("Jenis Perlengkapan"), jenis,
                bagianKhusus,
                labelForm("Harga Barang"), hargaBarang,
                labelForm("Harga Sewa / Hari"), hargaSewa,
                labelForm("Total Stok"), stok,
                labelForm("Foto Barang"), areaUnggah, gantiFoto
        );

        Button batal = tombolOutline("Batalkan");
        batal.setOnAction(e -> tampilkanDaftar());

        Button simpan = tombolHijau("Simpan Perubahan");
        simpan.setOnAction(e -> perbaruiBarang(
                p, nama, vendor, jenis, stok, hargaBarang, hargaSewa,
                tipeKompor, tipeTas, ukuranSepatu
        ));

        HBox aksi = new HBox(12, batal, simpan);
        aksi.setAlignment(Pos.CENTER_RIGHT);
        kolom.getChildren().add(aksi);

        tampilkanPanelForm(halaman, kolom);
    }

    private void tampilkanPanelForm(VBox halaman, VBox kolom) {
        kolom.setMaxWidth(Double.MAX_VALUE);

        VBox pembungkus = new VBox(kolom);
        pembungkus.setAlignment(Pos.TOP_CENTER);
        pembungkus.setFillWidth(true);
        pembungkus.setMaxWidth(Double.MAX_VALUE);
        pembungkus.setPadding(new Insets(24, 26, 26, 26));

        ScrollPane scroll = new ScrollPane(pembungkus);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle(
                "-fx-background-color:transparent;"
                + "-fx-background:transparent;"
                + "-fx-border-color:transparent;"
        );

        VBox panelPutih = new VBox(scroll);
        panelPutih.setMaxWidth(Double.MAX_VALUE);
        panelPutih.setStyle(
                "-fx-background-color:white;"
                + "-fx-background-radius:16;"
        );

        VBox.setVgrow(scroll, Priority.ALWAYS);
        VBox.setVgrow(panelPutih, Priority.ALWAYS);
        halaman.getChildren().add(panelPutih);
        perlengkapanRoot.setCenter(halaman);
    }

    private VBox halamanForm(String judul) {
        VBox halaman = new VBox(18);
        halaman.setPadding(new Insets(24, 28, 24, 28));
        halaman.setStyle("-fx-background-color:" + LATAR + ";");

        Label label = new Label(judul);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setAlignment(Pos.CENTER);
        label.setStyle(
                "-fx-font-size:22px;"
                + "-fx-font-weight:bold;"
                + "-fx-text-fill:" + HIJAU + ";"
        );

        halaman.getChildren().add(label);
        return halaman;
    }

    private void simpanBarang(
            TextField nama,
            ComboBox<VendorPilihan> vendor,
            ComboBox<String> jenis,
            TextField stok,
            TextField hargaBarang,
            TextField hargaSewa,
            TextField tipeKompor,
            TextField tipeTas,
            TextField ukuranSepatu) {

        try {
            if (nama.getText().trim().isEmpty()
                    || vendor.getValue() == null) {
                tampilkanPesan(
                        "Input belum lengkap",
                        "Nama barang dan vendor wajib diisi."
                );
                return;
            }

            int jumlahStok = Integer.parseInt(stok.getText().trim());
            long nilaiBarang = Long.parseLong(hargaBarang.getText().trim());
            long nilaiSewa = Long.parseLong(hargaSewa.getText().trim());

            if (jumlahStok < 0 || nilaiBarang <= 0 || nilaiSewa <= 0) {
                tampilkanPesan(
                        "Input tidak valid",
                        "Stok tidak boleh negatif dan harga harus lebih dari nol."
                );
                return;
            }

            Integer ukuran = validasiUkuran(jenis, ukuranSepatu);
            if ("sepatu".equals(jenis.getValue()) && ukuran == null) {
                return;
            }

            if ("kompor".equals(jenis.getValue())
                    && teksAtauNull(tipeKompor.getText()) == null) {
                tampilkanPesan(
                        "Input belum lengkap",
                        "Tipe kompor wajib diisi."
                );
                return;
            }

            if ("tas".equals(jenis.getValue())
                    && teksAtauNull(tipeTas.getText()) == null) {
                tampilkanPesan(
                        "Input belum lengkap",
                        "Tipe tas wajib diisi."
                );
                return;
            }

            Alert konfirmasi = new Alert(Alert.AlertType.CONFIRMATION);
            konfirmasi.setTitle("Konfirmasi");
            konfirmasi.setHeaderText("Simpan perlengkapan?");
            konfirmasi.setContentText(
                    "Barang \"" + nama.getText().trim() + "\" akan disimpan."
            );

            if (konfirmasi.showAndWait().orElse(ButtonType.CANCEL)
                    != ButtonType.OK) {
                return;
            }

            int id = dao.tambahPerlengkapan(
                    vendor.getValue().id,
                    nama.getText().trim(),
                    jenis.getValue(),
                    jumlahStok,
                    nilaiBarang,
                    nilaiSewa,
                    "kompor".equals(jenis.getValue())
                            ? teksAtauNull(tipeKompor.getText()) : null,
                    "tas".equals(jenis.getValue())
                            ? teksAtauNull(tipeTas.getText()) : null,
                    ukuran
            );

            if (fotoTerpilih != null) {
                simpanFotoBarang(id, fotoTerpilih);
            }

            fotoTerpilih = null;
            hapusFotoLama = false;

            tampilkanPesan("Berhasil", "Perlengkapan berhasil disimpan.");
            tampilkanDaftar();

        } catch (NumberFormatException e) {
            tampilkanPesan(
                    "Input tidak valid",
                    "Stok dan harga harus berupa angka."
            );
        } catch (SQLException e) {
            tampilkanPesan("Gagal menyimpan", e.getMessage());
        } catch (IOException e) {
            tampilkanPesan("Foto gagal disimpan", e.getMessage());
        }
    }

    private void perbaruiBarang(
            Perlengkapan p,
            TextField nama,
            ComboBox<VendorPilihan> vendor,
            ComboBox<String> jenis,
            TextField stok,
            TextField hargaBarang,
            TextField hargaSewa,
            TextField tipeKompor,
            TextField tipeTas,
            TextField ukuranSepatu) {

        try {
            if (nama.getText().trim().isEmpty()
                    || vendor.getValue() == null) {
                tampilkanPesan(
                        "Input belum lengkap",
                        "Nama barang dan vendor wajib diisi."
                );
                return;
            }

            int jumlahStok = Integer.parseInt(stok.getText().trim());
            long nilaiBarang = Long.parseLong(hargaBarang.getText().trim());
            long nilaiSewa = Long.parseLong(hargaSewa.getText().trim());

            if (jumlahStok < 0 || nilaiBarang <= 0 || nilaiSewa <= 0) {
                tampilkanPesan(
                        "Input tidak valid",
                        "Stok tidak boleh negatif dan harga harus lebih dari nol."
                );
                return;
            }

            Integer ukuran = validasiUkuran(jenis, ukuranSepatu);
            if ("sepatu".equals(jenis.getValue()) && ukuran == null) {
                return;
            }

            if ("kompor".equals(jenis.getValue())
                    && teksAtauNull(tipeKompor.getText()) == null) {
                tampilkanPesan(
                        "Input belum lengkap",
                        "Tipe kompor wajib diisi."
                );
                return;
            }

            if ("tas".equals(jenis.getValue())
                    && teksAtauNull(tipeTas.getText()) == null) {
                tampilkanPesan(
                        "Input belum lengkap",
                        "Tipe tas wajib diisi."
                );
                return;
            }

            Alert konfirmasi = new Alert(Alert.AlertType.CONFIRMATION);
            konfirmasi.setTitle("Konfirmasi Perubahan");
            konfirmasi.setHeaderText("Simpan perubahan produk?");
            konfirmasi.setContentText(
                    "Perubahan pada \"" + nama.getText().trim()
                    + "\" akan disimpan."
            );

            if (konfirmasi.showAndWait().orElse(ButtonType.CANCEL)
                    != ButtonType.OK) {
                return;
            }

            String sql =
                    "UPDATE perlengkapan SET id_vendor=?, "
                    + "nama_perlengkapan=?, jenis_perlengkapan=?, "
                    + "stok=?, harga_perlengkapan=?, "
                    + "harga_sewa_harian=?, tipe_kompor=?, "
                    + "tipe_tas=?, ukuran_sepatu=? "
                    + "WHERE id_perlengkapan=?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, vendor.getValue().id);
                ps.setString(2, nama.getText().trim());
                ps.setString(3, jenis.getValue());
                ps.setInt(4, jumlahStok);
                ps.setLong(5, nilaiBarang);
                ps.setLong(6, nilaiSewa);
                ps.setString(
                        7,
                        "kompor".equals(jenis.getValue())
                                ? teksAtauNull(tipeKompor.getText()) : null
                );
                ps.setString(
                        8,
                        "tas".equals(jenis.getValue())
                                ? teksAtauNull(tipeTas.getText()) : null
                );

                if (ukuran == null) {
                    ps.setNull(9, Types.INTEGER);
                } else {
                    ps.setInt(9, ukuran);
                }

                ps.setInt(10, p.getIdPerlengkapan());
                ps.executeUpdate();
            }

            if (fotoTerpilih != null) {
                simpanFotoBarang(p.getIdPerlengkapan(), fotoTerpilih);
            } else if (hapusFotoLama) {
                hapusFileFotoBarang(p.getIdPerlengkapan());
            }

            fotoTerpilih = null;
            hapusFotoLama = false;

            tampilkanPesan(
                    "Berhasil",
                    "Perlengkapan berhasil diperbarui."
            );
            tampilkanDaftar();

        } catch (NumberFormatException e) {
            tampilkanPesan(
                    "Input tidak valid",
                    "Stok dan harga harus berupa angka."
            );
        } catch (SQLException e) {
            tampilkanPesan("Gagal memperbarui", e.getMessage());
        } catch (IOException e) {
            tampilkanPesan("Foto gagal disimpan", e.getMessage());
        }
    }

    private Integer validasiUkuran(
            ComboBox<String> jenis,
            TextField ukuranSepatu) {

        if (!"sepatu".equals(jenis.getValue())) {
            return null;
        }

        try {
            int ukuran = Integer.parseInt(ukuranSepatu.getText().trim());

            if (ukuran < 1 || ukuran > 60) {
                tampilkanPesan(
                        "Ukuran tidak valid",
                        "Ukuran sepatu harus antara 1 sampai 60."
                );
                return null;
            }

            return ukuran;

        } catch (NumberFormatException e) {
            tampilkanPesan(
                    "Ukuran tidak valid",
                    "Masukkan ukuran sepatu berupa angka."
            );
            return null;
        }
    }

    private void tampilkanDetail(Perlengkapan p) {
        VBox halaman = halamanForm("Detail Perlengkapan");

        VBox panel = new VBox(16);
        panel.setPadding(new Insets(24));
        panel.setMaxWidth(Double.MAX_VALUE);
        panel.setStyle(
                "-fx-background-color:white;"
                + "-fx-background-radius:16;"
        );

        StackPane areaFoto = new StackPane();
        areaFoto.setMinHeight(210);
        areaFoto.setMaxWidth(Double.MAX_VALUE);
        areaFoto.setStyle(
                "-fx-background-color:#E8F0EB;"
                + "-fx-background-radius:10;"
        );

        ImageView foto = buatFotoBarang(
                p.getIdPerlengkapan(), 600, 190
        );

        if (foto != null) {
            areaFoto.getChildren().add(foto);
        } else {
            areaFoto.getChildren().add(new Label("Tidak ada foto"));
        }

        panel.getChildren().addAll(
                areaFoto,
                labelForm("Nama Barang"),
                fieldReadOnly(p.getNamaPerlengkapan()),
                labelForm("Harga Barang"),
                fieldReadOnly(formatRupiah(p.getHargaPerlengkapan())),
                labelForm("Harga Sewa (Per Hari)"),
                fieldReadOnly(formatRupiah(p.getHargaSewaHarian())),
                labelForm("Status Barang"),
                fieldReadOnly(p.getStok() > 0 ? "Tersedia" : "Stok habis"),
                labelForm("Total Stok"),
                fieldReadOnly(String.valueOf(p.getStok()))
        );

        if (p instanceof Tas) {
            panel.getChildren().addAll(
                    labelForm("Tipe Tas"),
                    fieldReadOnly(aman(((Tas) p).getTipeTas()))
            );
        } else if (p instanceof Kompor) {
            panel.getChildren().addAll(
                    labelForm("Tipe Kompor"),
                    fieldReadOnly(aman(((Kompor) p).getTipeKompor()))
            );
        } else if (p instanceof Sepatu) {
            panel.getChildren().addAll(
                    labelForm("Ukuran Sepatu"),
                    fieldReadOnly(
                            String.valueOf(((Sepatu) p).getUkuranSepatu())
                    )
            );
        }

        Button kembali = tombolOutline("Kembali");
        kembali.setOnAction(e -> tampilkanDaftar());

        Button edit = tombolHijau("Edit Produk");
        edit.setOnAction(e -> tampilkanFormEdit(p));

        HBox aksi = new HBox(10, kembali, edit);
        aksi.setAlignment(Pos.CENTER_RIGHT);
        panel.getChildren().add(aksi);

        VBox pembungkus = new VBox(panel);
        pembungkus.setMaxWidth(Double.MAX_VALUE);
        pembungkus.setPadding(new Insets(24, 26, 26, 26));

        ScrollPane scroll = new ScrollPane(pembungkus);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle(
                "-fx-background-color:transparent;"
                + "-fx-background:transparent;"
                + "-fx-border-color:transparent;"
        );

        VBox panelPutih = new VBox(scroll);
        panelPutih.setMaxWidth(Double.MAX_VALUE);
        panelPutih.setStyle(
                "-fx-background-color:white;"
                + "-fx-background-radius:16;"
        );

        VBox.setVgrow(scroll, Priority.ALWAYS);
        VBox.setVgrow(panelPutih, Priority.ALWAYS);
        halaman.getChildren().add(panelPutih);
        perlengkapanRoot.setCenter(halaman);
    }

    private StackPane buatAreaUnggah(
            Label petunjuk,
            ImageView fotoAwal) {

        StackPane area = new StackPane();
        area.setMinHeight(230);
        area.setPrefHeight(230);
        area.setMaxWidth(Double.MAX_VALUE);
        area.setPadding(new Insets(12));
        area.setStyle(
                "-fx-background-color:#F5F8F6;"
                + "-fx-border-color:#D5DED8;"
                + "-fx-border-width:1.2;"
                + "-fx-border-radius:12;"
                + "-fx-background-radius:12;"
        );

        VBox isi = new VBox(10);
        isi.setAlignment(Pos.CENTER);
        isi.setMaxWidth(340);

        StackPane bingkaiFoto = new StackPane();
        bingkaiFoto.setPrefSize(300, 135);
        bingkaiFoto.setMinSize(0, 135);
        bingkaiFoto.setMaxWidth(300);
        bingkaiFoto.setStyle(
                "-fx-background-color:#E7EFEA;"
                + "-fx-background-radius:8;"
        );

        if (fotoAwal != null) {
            fotoAwal.setFitWidth(280);
            fotoAwal.setFitHeight(125);
            fotoAwal.setPreserveRatio(true);
            fotoAwal.setSmooth(true);
            bingkaiFoto.getChildren().add(fotoAwal);
        } else {
            StackPane ikonUpload = buatIkonUpload();
            ikonUpload.setOnMouseClicked(e -> pilihFoto(petunjuk, area));
            bingkaiFoto.getChildren().add(ikonUpload);
        }

        Label namaFile = new Label(
                fotoAwal == null
                        ? "Klik ikon untuk mengunggah foto"
                        : "Foto produk saat ini"
        );
        namaFile.setMaxWidth(300);
        namaFile.setWrapText(true);
        namaFile.setAlignment(Pos.CENTER);
        namaFile.setStyle(
                "-fx-font-size:12px;"
                + "-fx-text-fill:#66736B;"
        );

        isi.getChildren().addAll(bingkaiFoto, namaFile);

        if (fotoAwal != null) {
            Button hapus = tombolOutline("Hapus Foto");
            hapus.setPrefWidth(140);
            hapus.setOnAction(e -> {
                fotoTerpilih = null;
                hapusFotoLama = true;
                tampilkanPlaceholderFoto(area, petunjuk);
            });
            isi.getChildren().add(hapus);
        }

        area.getChildren().setAll(isi);
        return area;
    }

    private StackPane buatIkonUpload() {
        StackPane ikonUpload = new StackPane();
        ikonUpload.setPrefSize(90, 90);
        ikonUpload.setMaxSize(90, 90);
        ikonUpload.setStyle("-fx-cursor:hand;");

        Label awan = new Label("☁");
        awan.setStyle(
                "-fx-font-size:55px;"
                + "-fx-text-fill:#444444;"
                + "-fx-font-weight:bold;"
        );

        Label panah = new Label("↑");
        panah.setStyle(
                "-fx-font-size:27px;"
                + "-fx-font-weight:bold;"
                + "-fx-text-fill:#444444;"
        );
        panah.setTranslateY(-4);

        ikonUpload.getChildren().addAll(awan, panah);
        return ikonUpload;
    }

    private void pilihFoto(Label label, StackPane areaUnggah) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Pilih Foto Barang");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "File Gambar", "*.png", "*.jpg", "*.jpeg"
                )
        );

        File file = chooser.showOpenDialog(
                perlengkapanRoot.getScene().getWindow()
        );

        if (file == null) {
            return;
        }

        Image image = new Image(file.toURI().toString());

        if (image.isError()) {
            tampilkanPesan(
                    "Gagal membuka foto",
                    "File gambar tidak dapat dibuka."
            );
            return;
        }

        fotoTerpilih = file;
        hapusFotoLama = false;

        ImageView preview = new ImageView(image);
        preview.setFitWidth(280);
        preview.setFitHeight(125);
        preview.setPreserveRatio(true);
        preview.setSmooth(true);

        StackPane bingkaiFoto = new StackPane(preview);
        bingkaiFoto.setPrefSize(300, 135);
        bingkaiFoto.setMinSize(0, 135);
        bingkaiFoto.setMaxWidth(300);
        bingkaiFoto.setStyle(
                "-fx-background-color:#E7EFEA;"
                + "-fx-background-radius:8;"
        );

        Label namaFile = new Label(file.getName());
        namaFile.setMaxWidth(300);
        namaFile.setWrapText(true);
        namaFile.setAlignment(Pos.CENTER);
        namaFile.setStyle(
                "-fx-font-size:12px;"
                + "-fx-text-fill:#66736B;"
        );

        Button hapus = tombolOutline("Hapus Foto");
        hapus.setPrefWidth(140);
        hapus.setOnAction(e -> {
            fotoTerpilih = null;
            hapusFotoLama = true;
            tampilkanPlaceholderFoto(areaUnggah, label);
        });

        VBox isi = new VBox(10, bingkaiFoto, namaFile, hapus);
        isi.setAlignment(Pos.CENTER);
        isi.setMaxWidth(340);

        areaUnggah.getChildren().setAll(isi);
    }

    private void tampilkanPlaceholderFoto(
            StackPane areaUnggah,
            Label label) {

        StackPane bingkaiFoto = new StackPane();
        bingkaiFoto.setPrefSize(300, 135);
        bingkaiFoto.setMinSize(0, 135);
        bingkaiFoto.setMaxWidth(300);
        bingkaiFoto.setStyle(
                "-fx-background-color:#E7EFEA;"
                + "-fx-background-radius:8;"
        );

        StackPane ikonUpload = buatIkonUpload();
        ikonUpload.setOnMouseClicked(e -> pilihFoto(label, areaUnggah));
        bingkaiFoto.getChildren().add(ikonUpload);

        Label statusFoto = new Label("Klik ikon untuk mengunggah foto");
        statusFoto.setMaxWidth(300);
        statusFoto.setWrapText(true);
        statusFoto.setAlignment(Pos.CENTER);
        statusFoto.setStyle(
                "-fx-font-size:12px;"
                + "-fx-text-fill:#66736B;"
        );

        VBox isi = new VBox(10, bingkaiFoto, statusFoto);
        isi.setAlignment(Pos.CENTER);
        isi.setMaxWidth(340);

        areaUnggah.getChildren().setAll(isi);
    }

    private void muatVendor(ComboBox<VendorPilihan> combo) {
        String sql =
                "SELECT id_vendor, nama_vendor "
                + "FROM vendor ORDER BY nama_vendor";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            combo.getItems().clear();

            while (rs.next()) {
                combo.getItems().add(
                        new VendorPilihan(
                                rs.getInt("id_vendor"),
                                rs.getString("nama_vendor")
                        )
                );
            }

        } catch (SQLException e) {
            tampilkanPesan("Gagal memuat vendor", e.getMessage());
        }
    }

    private void simpanFotoBarang(int id, File file) throws IOException {
        String namaFile = file.getName().toLowerCase(Locale.ROOT);

        String ekstensi = namaFile.endsWith(".png")
                ? ".png"
                : namaFile.endsWith(".jpeg") ? ".jpeg" : ".jpg";

        Path folder = Path.of("data", "perlengkapan");
        Files.createDirectories(folder);

        hapusFileFotoBarang(id);

        Files.copy(
                file.toPath(),
                folder.resolve("barang_" + id + ekstensi),
                StandardCopyOption.REPLACE_EXISTING
        );
    }

    private void hapusFileFotoBarang(int id) throws IOException {
        Path folder = Path.of("data", "perlengkapan");

        if (!Files.isDirectory(folder)) {
            return;
        }

        try (java.util.stream.Stream<Path> files = Files.list(folder)) {
            List<Path> fotoLama = files
                    .filter(Files::isRegularFile)
                    .filter(f -> f.getFileName().toString()
                            .matches("barang_" + id + "\\.(png|jpg|jpeg)"))
                    .collect(Collectors.toList());

            for (Path foto : fotoLama) {
                Files.deleteIfExists(foto);
            }
        }
    }

    private ImageView buatFotoBarang(
            int id,
            double lebar,
            double tinggi) {

        Path folder = Path.of("data", "perlengkapan");

        if (!Files.isDirectory(folder)) {
            return null;
        }

        try (java.util.stream.Stream<Path> files = Files.list(folder)) {
            Path foto = files
                    .filter(Files::isRegularFile)
                    .filter(f -> f.getFileName().toString()
                            .matches("barang_" + id + "\\.(png|jpg|jpeg)"))
                    .findFirst()
                    .orElse(null);

            if (foto == null) {
                return null;
            }

            Image image = new Image(foto.toUri().toString());

            if (image.isError()) {
                return null;
            }

            ImageView view = new ImageView(image);
            view.setFitWidth(lebar);
            view.setFitHeight(tinggi);
            view.setPreserveRatio(true);
            view.setSmooth(true);
            return view;

        } catch (IOException e) {
            System.err.println("Foto barang gagal dibaca: " + e.getMessage());
            return null;
        }
    }

    private Label labelForm(String teks) {
        Label label = new Label(teks);
        label.setStyle(
                "-fx-font-size:13px;"
                + "-fx-text-fill:#333333;"
                + "-fx-font-weight:bold;"
        );
        return label;
    }

    private TextField field() {
        TextField field = new TextField();
        field.setPrefHeight(42);
        field.setMaxWidth(Double.MAX_VALUE);
        field.setStyle(
                "-fx-background-color:#E9E9E9;"
                + "-fx-border-color:#999999;"
                + "-fx-border-radius:7;"
                + "-fx-background-radius:7;"
                + "-fx-padding:0 12;"
        );
        return field;
    }

    private TextField fieldReadOnly(String teks) {
        TextField field = field();
        field.setText(teks == null ? "" : teks);
        field.setEditable(false);
        return field;
    }

    private Button tombolHijau(String teks) {
        Button button = new Button(teks);
        button.setMinHeight(40);
        button.setStyle(
                "-fx-background-color:" + HIJAU + ";"
                + "-fx-text-fill:white;"
                + "-fx-font-weight:bold;"
                + "-fx-background-radius:8;"
                + "-fx-padding:9 20;"
                + "-fx-cursor:hand;"
        );
        return button;
    }

    private Button tombolOutline(String teks) {
        Button button = new Button(teks);
        button.setMinHeight(40);
        button.setStyle(
                "-fx-background-color:white;"
                + "-fx-text-fill:" + HIJAU + ";"
                + "-fx-border-color:" + HIJAU + ";"
                + "-fx-border-radius:8;"
                + "-fx-background-radius:8;"
                + "-fx-padding:9 20;"
                + "-fx-cursor:hand;"
        );
        return button;
    }

    private String formatRupiah(long angka) {
        NumberFormat nf = NumberFormat.getNumberInstance(
                new Locale("id", "ID")
        );
        return "Rp " + nf.format(angka);
    }

    private String teksAtauNull(String teks) {
        return teks == null || teks.trim().isEmpty()
                ? null : teks.trim();
    }

    private String aman(String teks) {
        return teks == null || teks.trim().isEmpty() ? "-" : teks;
    }

    private void tampilkanPesan(String judul, String pesan) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(judul);
        alert.setHeaderText(null);
        alert.setContentText(pesan);
        alert.showAndWait();
    }

    private static class VendorPilihan {
        private final int id;
        private final String nama;

        private VendorPilihan(int id, String nama) {
            this.id = id;
            this.nama = nama;
        }

        @Override
        public String toString() {
            return nama;
        }
    }
}