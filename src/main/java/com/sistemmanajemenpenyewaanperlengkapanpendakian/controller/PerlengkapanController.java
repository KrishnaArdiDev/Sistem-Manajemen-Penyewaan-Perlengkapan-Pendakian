
package com.sistemmanajemenpenyewaanperlengkapanpendakian.controller;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.dao.PerlengkapanDAO;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Perlengkapan;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.geometry.Pos;
import javafx.geometry.Insets;

public class PerlengkapanController {

    @FXML
    private TextField cariPerlengkapanField;

    @FXML
    private FlowPane kartuPerlengkapanPane;

    private final PerlengkapanDAO perlengkapanDAO = new PerlengkapanDAO();

    private final ObservableList<Perlengkapan> daftarPerlengkapan =
            FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        muatDataPerlengkapan();
    }

    private void muatDataPerlengkapan() {
        try {
            List<Perlengkapan> data = perlengkapanDAO.getAllPerlengkapan();

            System.out.println("Jumlah data perlengkapan: " + data.size());

            daftarPerlengkapan.setAll(data);
            tampilkanKartuPerlengkapan(data);

        } catch (SQLException e) {
            tampilkanPesan(
                    "Gagal memuat data",
                    "Tidak dapat mengambil data perlengkapan: "
                            + e.getMessage()
            );
        }
    }

    private void tampilkanKartuPerlengkapan(List<Perlengkapan> data) {
        kartuPerlengkapanPane.getChildren().clear();

        for (Perlengkapan p : data) {
            VBox kartu = new VBox(8);
            kartu.setPrefWidth(220);
            kartu.setMinWidth(200);
            kartu.setPadding(new Insets(10));
            kartu.setAlignment(Pos.TOP_LEFT);

            kartu.setStyle(
                    "-fx-background-color: white;"
                    + "-fx-background-radius: 12;"
                    + "-fx-border-color: #CCCCCC;"
                    + "-fx-border-radius: 12;"
            );

            // Area gambar sementara
            StackPane areaGambar = new StackPane();
            areaGambar.setPrefSize(200, 150);
            areaGambar.setMinHeight(150);
            areaGambar.setStyle(
                    "-fx-background-color: #E8E8E8;"
                    + "-fx-background-radius: 8;"
            );

            Label ikon = new Label(
                    p.getJenisPerlengkapan() == null
                    ? "Perlengkapan"
                    : p.getJenisPerlengkapan().equalsIgnoreCase("kompor")
                            ? "Kompor"
                            : p.getJenisPerlengkapan().equalsIgnoreCase("tas")
                                    ? "Tas Carrier"
                                    : p.getJenisPerlengkapan().equalsIgnoreCase("sepatu")
                                            ? "Sepatu Gunung"
                                            : "Perlengkapan"
            );

            ikon.setStyle(
                    "-fx-font-size: 18px;"
                    + "-fx-font-weight: bold;"
                    + "-fx-text-fill: #075D39;"
            );

            areaGambar.getChildren().add(ikon);

            Label nama = new Label(p.getNamaPerlengkapan());
            nama.setWrapText(true);
            nama.setStyle(
                    "-fx-font-size: 15px;"
                    + "-fx-font-weight: bold;"
            );

            Label stok = new Label(
                    p.getStok() > 0
                            ? "Tersedia • Stok " + p.getStok()
                            : "Stok habis"
            );

            stok.setStyle(
                    p.getStok() > 0
                            ? "-fx-background-color: #D6F5DD;"
                                    + "-fx-text-fill: #176B35;"
                                    + "-fx-padding: 4 8;"
                                    + "-fx-background-radius: 5;"
                            : "-fx-background-color: #FFDADA;"
                                    + "-fx-text-fill: #A02020;"
                                    + "-fx-padding: 4 8;"
                                    + "-fx-background-radius: 5;"
            );

            Label harga = new Label(
                    "Rp " + p.getHargaSewaHarian().toPlainString()
                            + " / Hari"
            );

            harga.setStyle(
                    "-fx-font-size: 16px;"
                    + "-fx-font-weight: bold;"
                    + "-fx-text-fill: #075D39;"
            );

            Button detail = new Button("Detail");
            detail.setMaxWidth(Double.MAX_VALUE);
            detail.setOnAction(event -> tampilkanDetail(p));

            detail.setStyle(
                    "-fx-background-color: #075D39;"
                    + "-fx-text-fill: white;"
                    + "-fx-background-radius: 6;"
            );

            kartu.getChildren().addAll(
                    areaGambar,
                    nama,
                    stok,
                    harga,
                    detail
            );

            kartuPerlengkapanPane.getChildren().add(kartu);
        }
    }

    @FXML
    private void cariPerlengkapan() {
        String kataKunci = cariPerlengkapanField.getText()
                .trim()
                .toLowerCase();

        if (kataKunci.isEmpty()) {
            tampilkanKartuPerlengkapan(daftarPerlengkapan);
            return;
        }

        List<Perlengkapan> hasil = daftarPerlengkapan.stream()
                .filter(p -> p.getNamaPerlengkapan() != null
                        && p.getNamaPerlengkapan()
                                .toLowerCase()
                                .contains(kataKunci))
                .collect(Collectors.toList());

        tampilkanKartuPerlengkapan(hasil);
    }

    private void tampilkanDetail(Perlengkapan p) {
        String pesan = "Nama: " + p.getNamaPerlengkapan()
                + "\nJenis: " + p.getJenisPerlengkapan()
                + "\nStok: " + p.getStok()
                + "\nHarga perlengkapan: Rp "
                + p.getHargaPerlengkapan().toPlainString()
                + "\nHarga sewa per hari: Rp "
                + p.getHargaSewaHarian().toPlainString();

        tampilkanPesan("Detail Perlengkapan", pesan);
    }

    @FXML
    private void tambahPerlengkapan() {
        tampilkanPesan(
                "Tambah perlengkapan",
                "Form tambah perlengkapan belum dibuat."
        );
    }

    private void tampilkanPesan(String judul, String pesan) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(judul);
        alert.setHeaderText(null);
        alert.setContentText(pesan);
        alert.showAndWait();
    }
}
