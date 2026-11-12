
package com.sistemmanajemenpenyewaanperlengkapanpendakian.dao;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.DatabaseConnection;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.DetailTransaksi;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Perlengkapan;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Kompor;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Tas;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Sepatu;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class DetailTransaksiDAO {

    public void tambahDetailTransaksi(
            DetailTransaksi detail, int idTransaksi) throws SQLException {

        validasiDetail(detail, idTransaksi);

        String sql = "INSERT INTO detail_transaksi "
                + "(id_transaksi, id_perlengkapan, kuantitas_per_alat, "
                + "tanggal_pengembalian_barang, status_pengembalian, "
                + "biaya_ganti_rugi) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, idTransaksi);
            statement.setInt(2,
                    detail.getPerlengkapanYangDisewa().getIdPerlengkapan());
            statement.setInt(3, detail.getKuantitasPerAlat());

            if (detail.getTanggalPengembalianBarang() != null) {
                statement.setDate(4, Date.valueOf(
                        detail.getTanggalPengembalianBarang()));
            } else {
                statement.setNull(4, Types.DATE);
            }

            statement.setString(5, detail.getStatusPengembalianBarang());
            statement.setLong(6, detail.getBiayaGantiRugi());

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    detail.setIdDetailTransaksi(generatedKeys.getInt(1));
                } else {
                    throw new SQLException(
                            "Gagal mendapatkan ID detail transaksi");
                }
            }
        }
    }

    public List<DetailTransaksi> ambilDetailBerdasarkanTransaksi(
            int idTransaksi) throws SQLException {

        if (idTransaksi <= 0) {
            throw new IllegalArgumentException("ID transaksi tidak valid");
        }

        String sql = "SELECT d.id_detail_transaksi, "
                + "d.kuantitas_per_alat, d.tanggal_pengembalian_barang, "
                + "d.status_pengembalian, d.biaya_ganti_rugi, "
                + "p.id_perlengkapan, p.nama_perlengkapan, "
                + "p.jenis_perlengkapan, p.stok, "
                + "p.harga_perlengkapan, p.harga_sewa_harian, "
                + "p.tipe_kompor, p.tipe_tas, p.ukuran_sepatu "
                + "FROM detail_transaksi d "
                + "JOIN perlengkapan p "
                + "ON d.id_perlengkapan = p.id_perlengkapan "
                + "WHERE d.id_transaksi = ? "
                + "ORDER BY d.id_detail_transaksi";

        List<DetailTransaksi> daftarDetail = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, idTransaksi);

            try (ResultSet result = statement.executeQuery()) {

                while (result.next()) {

                    Perlengkapan perlengkapan;
                    String jenis = result.getString("jenis_perlengkapan");

                    if ("kompor".equalsIgnoreCase(jenis)) {
                        Kompor kompor = new Kompor();
                        kompor.setTipeKompor(
                                result.getString("tipe_kompor"));
                        perlengkapan = kompor;

                    } else if ("tas".equalsIgnoreCase(jenis)) {
                        Tas tas = new Tas();
                        tas.setTipeTas(result.getString("tipe_tas"));
                        perlengkapan = tas;

                    } else if ("sepatu".equalsIgnoreCase(jenis)) {
                        Sepatu sepatu = new Sepatu();
                        sepatu.setUkuranSepatu(
                                result.getInt("ukuran_sepatu"));
                        perlengkapan = sepatu;

                    } else {
                        throw new SQLException(
                                "Jenis perlengkapan tidak dikenal: " + jenis);
                    }

                    perlengkapan.setIdPerlengkapan(
                            result.getInt("id_perlengkapan"));
                    perlengkapan.setNamaPerlengkapan(
                            result.getString("nama_perlengkapan"));
                    perlengkapan.setStok(result.getInt("stok"));
                    perlengkapan.setHargaPerlengkapan(
                            result.getLong("harga_perlengkapan"));
                    perlengkapan.setHargaSewaHarian(
                            result.getLong("harga_sewa_harian"));

                    DetailTransaksi detail = new DetailTransaksi();

                    detail.setIdDetailTransaksi(
                            result.getInt("id_detail_transaksi"));
                    detail.setKuantitasPerAlat(
                            result.getInt("kuantitas_per_alat"));
                    detail.setPerlengkapanYangDisewa(perlengkapan);
                    detail.setStatusPengembalianBarang(
                            result.getString("status_pengembalian"));
                    detail.setBiayaGantiRugi(
                            result.getLong("biaya_ganti_rugi"));

                    Date tanggalPengembalian = result.getDate(
                            "tanggal_pengembalian_barang");

                    if (tanggalPengembalian != null) {
                        detail.setTanggalPengembalianBarang(
                                tanggalPengembalian.toLocalDate());
                    }

                    detail.setPengembalianSudahDiproses(
                            tanggalPengembalian != null);

                    daftarDetail.add(detail);
                }
            }
        }

        return daftarDetail;
    }

    public void updateDetailTransaksi(
            DetailTransaksi detail) throws SQLException {

        if (detail == null) {
            throw new IllegalArgumentException(
                    "Detail transaksi tidak boleh null");
        }

        if (detail.getIdDetailTransaksi() <= 0) {
            throw new IllegalArgumentException(
                    "ID detail transaksi tidak valid");
        }

        if (detail.getPerlengkapanYangDisewa() == null
                || detail.getPerlengkapanYangDisewa()
                        .getIdPerlengkapan() <= 0) {
            throw new IllegalArgumentException(
                    "Perlengkapan tidak valid");
        }

        if (detail.getKuantitasPerAlat() <= 0) {
            throw new IllegalArgumentException(
                    "Kuantitas harus lebih dari 0");
        }

        String sql = "UPDATE detail_transaksi SET "
                + "id_perlengkapan = ?, "
                + "kuantitas_per_alat = ?, "
                + "tanggal_pengembalian_barang = ?, "
                + "status_pengembalian = ?, "
                + "biaya_ganti_rugi = ? "
                + "WHERE id_detail_transaksi = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1,
                    detail.getPerlengkapanYangDisewa().getIdPerlengkapan());
            statement.setInt(2, detail.getKuantitasPerAlat());

            if (detail.getTanggalPengembalianBarang() != null) {
                statement.setDate(3, Date.valueOf(
                        detail.getTanggalPengembalianBarang()));
            } else {
                statement.setNull(3, Types.DATE);
            }

            statement.setString(4, detail.getStatusPengembalianBarang());
            statement.setLong(5, detail.getBiayaGantiRugi());
            statement.setInt(6, detail.getIdDetailTransaksi());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException(
                        "Detail transaksi tidak ditemukan");
            }
        }
    }

    public void hapusDetailTransaksi(
            int idDetailTransaksi) throws SQLException {

        if (idDetailTransaksi <= 0) {
            throw new IllegalArgumentException(
                    "ID detail transaksi tidak valid");
        }

        String sql = "DELETE FROM detail_transaksi "
                + "WHERE id_detail_transaksi = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, idDetailTransaksi);

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException(
                        "Detail transaksi tidak ditemukan");
            }
        }
    }

    private void validasiDetail(
            DetailTransaksi detail, int idTransaksi) {

        if (detail == null) {
            throw new IllegalArgumentException(
                    "Detail transaksi tidak boleh null");
        }

        if (idTransaksi <= 0) {
            throw new IllegalArgumentException(
                    "ID transaksi tidak valid");
        }

        if (detail.getPerlengkapanYangDisewa() == null
                || detail.getPerlengkapanYangDisewa()
                        .getIdPerlengkapan() <= 0) {
            throw new IllegalArgumentException(
                    "Perlengkapan tidak valid");
        }

        if (detail.getKuantitasPerAlat() <= 0) {
            throw new IllegalArgumentException(
                    "Kuantitas harus lebih dari 0");
        }
    }
}
