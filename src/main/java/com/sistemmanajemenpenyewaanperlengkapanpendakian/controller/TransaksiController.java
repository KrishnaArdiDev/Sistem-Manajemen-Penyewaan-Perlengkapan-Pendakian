    package com.sistemmanajemenpenyewaanperlengkapanpendakian.controller;
 
import com.sistemmanajemenpenyewaanperlengkapanpendakian.App;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.Service.TransaksiService;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.Service.TransaksiService.DataTransaksi;
 
import java.io.IOException;
 
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
 
public class TransaksiController {
 
    @FXML private VBox containerTransaksi;
 
    private static final String PATH_ITEM =
            "/com/sistemmanajemenpenyewaanperlengkapanpendakian/fxml/item-transaksi.fxml";
 
    @FXML
    public void initialize() {
        muatTransaksi();
    }
 
    private void muatTransaksi() {
        containerTransaksi.getChildren().clear();
 
        if (TransaksiService.semua().isEmpty()) {
            Label kosong = new Label("Belum ada transaksi. Klik \"Tambah Transaksi\" untuk membuat yang pertama.");
            kosong.setStyle("-fx-font-size: 14px; -fx-text-fill: #7A7A7A;");
            containerTransaksi.getChildren().add(kosong);
            return;
        }
 
        for (DataTransaksi t : TransaksiService.semua()) {
            tambahKartu(t.namaBarang, t.penyewa, t.tglSewa, t.tglKembali,
                        t.status, "/images/tas-carrier.png");
        }
    }
 
    private void tambahKartu(String barang, String penyewa, String tglSewa,
                             String tglKembali, String status, String gambar) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource(PATH_ITEM));
            Node kartu = loader.load();
 
            ItemTransaksiController item = loader.getController();
            item.setData(barang, penyewa, tglSewa, tglKembali, status, gambar,
                    () -> System.out.println("Detail: " + barang + " - " + penyewa));
 
            containerTransaksi.getChildren().add(kartu);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 
    @FXML
    private void tambahTransaksi() {
        try {
            java.net.URL lokasi = App.class.getResource(
                "/com/sistemmanajemenpenyewaanperlengkapanpendakian/fxml/tambah-transaksi.fxml");
            if (lokasi == null) {
                throw new IOException("File tambah-transaksi.fxml tidak ditemukan di folder fxml");
            }
 
            Parent root = new FXMLLoader(lokasi).load();
 
            Stage dialog = new Stage();
            dialog.initOwner(containerTransaksi.getScene().getWindow());
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Tambah Transaksi");
            dialog.setScene(new Scene(root));
            dialog.showAndWait();
 
            muatTransaksi();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Gagal membuka form:\n" + e).showAndWait();
        }
    }
}