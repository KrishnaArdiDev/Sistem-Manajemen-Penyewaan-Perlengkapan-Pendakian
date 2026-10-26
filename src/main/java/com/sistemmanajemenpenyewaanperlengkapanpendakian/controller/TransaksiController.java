package com.sistemmanajemenpenyewaanperlengkapanpendakian.controller;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.App;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class TransaksiController {

    @FXML private VBox containerTransaksi;

    private static final String PATH_ITEM =
<<<<<<< HEAD
            "/com/sistemmanajemenpenyewaanperlengkapanpendakian/fxml/item-transaksi.fxml";

=======
            "/com/sistemmanajemenpenyewaanperlengkapanpendakian/fxml/pages/item-transaksi.fxml";
 
>>>>>>> staging
    @FXML
    public void initialize() {
        muatTransaksi();
    }

    private void muatTransaksi() {
        containerTransaksi.getChildren().clear();

        for (int i = 0; i < 6; i++) {
            tambahKartu("Tas Carrier Puma", "Andy Tinggi",
                        "18-10-2026", "19-10-2026",
                        "Berhasil", "/images/tas-carrier.png");
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
        System.out.println("Tambah transaksi diklik");

    }
}
