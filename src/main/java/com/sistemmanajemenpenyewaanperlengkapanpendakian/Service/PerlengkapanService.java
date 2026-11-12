
package com.sistemmanajemenpenyewaanperlengkapanpendakian.Service;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.dao.PerlengkapanDAO;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Perlengkapan;

import java.sql.SQLException;

public class PerlengkapanService {

    private final PerlengkapanDAO perlengkapanDAO;

    public PerlengkapanService() {
        this.perlengkapanDAO = new PerlengkapanDAO();
    }

    // CREATE: Validasi dan menambahkan perlengkapan
    public void tambahDataPerlengkapan(
            Perlengkapan perlengkapan, int idVendor) throws SQLException {

        validasiData(perlengkapan, idVendor);

        perlengkapanDAO.tambahDataPerlengkapan(
                perlengkapan, idVendor);
    }

    // UPDATE: Validasi dan memperbarui perlengkapan
    public void updateDataPerlengkapan(
            Perlengkapan perlengkapan, int idVendor) throws SQLException {

        validasiData(perlengkapan, idVendor);

        if (perlengkapan.getIdPerlengkapan() <= 0) {
            throw new IllegalArgumentException(
                    "ID perlengkapan tidak valid");
        }

        perlengkapanDAO.updateDataPerlengkapan(
                perlengkapan, idVendor);
    }

    // DELETE: Menghapus perlengkapan
    public void hapusDataPerlengkapan(
            Perlengkapan perlengkapan, int idVendor) throws SQLException {

        if (perlengkapan == null) {
            throw new IllegalArgumentException(
                    "Data perlengkapan tidak boleh null");
        }

        if (idVendor <= 0) {
            throw new IllegalArgumentException(
                    "ID vendor tidak valid");
        }

        if (perlengkapan.getIdPerlengkapan() <= 0) {
            throw new IllegalArgumentException(
                    "ID perlengkapan tidak valid");
        }

        perlengkapanDAO.hapusDataPerlengkapan(
                perlengkapan, idVendor);
    }

    // Validasi data sebelum diteruskan ke DAO
    private void validasiData(
            Perlengkapan perlengkapan, int idVendor) {

        if (perlengkapan == null) {
            throw new IllegalArgumentException(
                    "Data perlengkapan tidak boleh null");
        }

        if (idVendor <= 0) {
            throw new IllegalArgumentException(
                    "ID vendor tidak valid");
        }

        if (perlengkapan.getNamaPerlengkapan() == null
                || perlengkapan.getNamaPerlengkapan().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Nama perlengkapan tidak boleh kosong");
        }

        if (perlengkapan.getNamaPerlengkapan().length() > 150) {
            throw new IllegalArgumentException(
                    "Nama perlengkapan maksimal 150 karakter");
        }

        if (perlengkapan.getStok() < 0) {
            throw new IllegalArgumentException(
                    "Stok tidak boleh negatif");
        }

        if (perlengkapan.getHargaPerlengkapan() < 0) {
            throw new IllegalArgumentException(
                    "Harga perlengkapan tidak boleh negatif");
        }

        if (perlengkapan.getHargaSewaHarian() < 0) {
            throw new IllegalArgumentException(
                    "Harga sewa harian tidak boleh negatif");
        }
    }
}
