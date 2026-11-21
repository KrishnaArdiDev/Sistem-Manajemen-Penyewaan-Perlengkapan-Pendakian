
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


// CREATE
// CREATE dengan koneksi sendiri
public void tambahDetailTransaksi(
        DetailTransaksi detail, int idTransaksi) throws SQLException {

    try (Connection connection = DatabaseConnection.getConnection()) {
        tambahDetailTransaksi(connection, detail, idTransaksi);
    }
}

// CREATE dengan koneksi dari luar
public void tambahDetailTransaksi(Connection connection, DetailTransaksi detail,int idTransaksi) throws SQLException {
    if (connection == null) {
        throw new IllegalArgumentException("Koneksi database tidak boleh null");
    }

    validasiDetail(detail, idTransaksi);

    String sql = "INSERT INTO detail_transaksi "
        + "(id_transaksi, id_perlengkapan, kuantitas_per_alat, "
        + "tanggal_pengembalian_barang, status_pengembalian, "
        + "biaya_ganti_rugi, harga_sewa_harian_saat_transaksi) "
        + "VALUES (?, ?, ?, ?, ?, ?, ?)";

    try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

        statement.setInt(1, idTransaksi);

        statement.setInt(2,detail.getPerlengkapanYangDisewa().getIdPerlengkapan());

        statement.setInt(3, detail.getKuantitasPerAlat());

        if (detail.getTanggalPengembalianBarang() != null) {statement.setDate(4, Date.valueOf(detail.getTanggalPengembalianBarang()));
        } else {
            statement.setNull(4, Types.DATE);
        }

        if (detail.getStatusPengembalianBarang() != null) {statement.setString(5, detail.getStatusPengembalianBarang());
        } else {
            statement.setNull(5, Types.VARCHAR);
        }

        statement.setLong(6, detail.getBiayaGantiRugi());
        statement.setLong(7, detail.getHargaSewaHarianSaatTransaksi());

        statement.executeUpdate();

        try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
            if (generatedKeys.next()) {detail.setIdDetailTransaksi(generatedKeys.getInt(1));
            } else {
                throw new SQLException("Gagal mendapatkan ID detail transaksi");
            }
        }
    }
}
// READ

// Membaca detail transaksi dengan koneksi sendiri
public List<DetailTransaksi> ambilDetailBerdasarkanTransaksi(int idTransaksi) throws SQLException{
    try (Connection connection = DatabaseConnection.getConnection()){
        return ambilDetailBerdasarkanTransaksi(connection, idTransaksi, false);
    }
}

// Membaca detail transaksi menggunakan koneksi dari luar.
// kunciBaris = true digunakan saat proses pengembalian dalam transaksi database.

    public List<DetailTransaksi> ambilDetailBerdasarkanTransaksi(Connection connection, int idTransaksi, boolean kunciBaris) throws SQLException {

        if (connection == null){
            throw new IllegalArgumentException("Koneksi database tidak boleh null");
        }

        if (idTransaksi <= 0) {
            throw new IllegalArgumentException("ID transaksi tidak valid");
        }

        if (kunciBaris && connection.getAutoCommit()){
            throw new SQLException("Koneksi harus menggunakan autoCommit false untuk SELECT FOR UPDATE");
        }

        String sql = "SELECT d.id_detail_transaksi, "
                + "d.kuantitas_per_alat, d.tanggal_pengembalian_barang, "
                + "d.status_pengembalian, d.biaya_ganti_rugi, "
                + "d.harga_sewa_harian_saat_transaksi, "
                + "p.id_perlengkapan, p.nama_perlengkapan, "
                + "p.jenis_perlengkapan, p.stok, "
                + "p.harga_perlengkapan, p.harga_sewa_harian, "
                + "p.tipe_kompor, p.tipe_tas, p.ukuran_sepatu, "
                + "p.foto_barang "
                + "FROM detail_transaksi d "
                + "JOIN perlengkapan p "
                + "ON d.id_perlengkapan = p.id_perlengkapan "
                + "WHERE d.id_transaksi = ? "
                + "ORDER BY d.id_detail_transaksi";

        if (kunciBaris){
            sql += " FOR UPDATE";
        }

        List<DetailTransaksi> daftarDetail = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, idTransaksi);

            try (ResultSet result = statement.executeQuery()) {

                while (result.next()) {

                    Perlengkapan perlengkapan;
                    String jenis = result.getString("jenis_perlengkapan");

                // Membentuk objek sesuai dengan jenis perlengkapan
                    if ("kompor".equalsIgnoreCase(jenis)) {
                        Kompor kompor = new Kompor();
                        kompor.setTipeKompor(result.getString("tipe_kompor"));
                        perlengkapan = kompor;

                    } else if ("tas".equalsIgnoreCase(jenis)) {
                        Tas tas = new Tas();
                        tas.setTipeTas(result.getString("tipe_tas"));
                        perlengkapan = tas;

                    } else if ("sepatu".equalsIgnoreCase(jenis)) {
                        Sepatu sepatu = new Sepatu();
                        sepatu.setUkuranSepatu(result.getInt("ukuran_sepatu"));
                        perlengkapan = sepatu;

                    } else {
                        throw new SQLException("Jenis perlengkapan tidak dikenal: " + jenis);
                    }

                // Mengisi atribut dasar dari perlengkapanr
                    perlengkapan.setIdPerlengkapan(result.getInt("id_perlengkapan"));
                    perlengkapan.setNamaPerlengkapan(result.getString("nama_perlengkapan"));
                    perlengkapan.setStok(result.getInt("stok"));
                    perlengkapan.setHargaPerlengkapan(result.getLong("harga_perlengkapan"));
                    perlengkapan.setHargaSewaHarian(result.getLong("harga_sewa_harian"));
                    perlengkapan.setFotoBarang(result.getString("foto_barang"));
                        
                // Membentuk objek detail transaksis
                    DetailTransaksi detail = new DetailTransaksi();

                    detail.setIdDetailTransaksi(result.getInt("id_detail_transaksi"));
                    detail.setKuantitasPerAlat(result.getInt("kuantitas_per_alat"));
                    detail.setPerlengkapanYangDisewa(perlengkapan);
                    detail.setStatusPengembalianBarang(result.getString("status_pengembalian"));
                    detail.setBiayaGantiRugi(result.getLong("biaya_ganti_rugi"));

                    Date tanggalPengembalian = result.getDate("tanggal_pengembalian_barang");
                    long hargaHistoris = result.getLong("harga_sewa_harian_saat_transaksi");

                    if (!result.wasNull()){
                        detail.setHargaSewaHarianSaatTransaksi(hargaHistoris);
                    }

                    if (tanggalPengembalian != null) {
                        detail.setTanggalPengembalianBarang(tanggalPengembalian.toLocalDate());
                    }

                    detail.setPengembalianSudahDiproses(tanggalPengembalian != null);

                    daftarDetail.add(detail);
                }
            }
        }

        return daftarDetail;
    }

// UPDATE
// Memperbarui detail dengan koneksi sendiri
public void updateDetailTransaksi(DetailTransaksi detail) throws SQLException{
    try(Connection connection = DatabaseConnection.getConnection()){
        updateDetailTransaksi(connection, detail);
    }
}
// Memperbarui detail menggunakan koneksi dari luar
    public void updateDetailTransaksi(Connection connection, DetailTransaksi detail) throws SQLException {

        if (connection == null){
            throw new IllegalArgumentException("Koneksi database tidak boleh null");
        }

        if (detail == null) {
            throw new IllegalArgumentException("Detail transaksi tidak boleh null");
        }

        if (detail.getIdDetailTransaksi() <= 0) {
            throw new IllegalArgumentException("ID detail transaksi tidak valid");
        }


        String sql = "UPDATE detail_transaksi SET "
                + "tanggal_pengembalian_barang = ?, "
                + "status_pengembalian = ?, "
                + "biaya_ganti_rugi = ? "
                + "WHERE id_detail_transaksi = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
        // Tanggal pengembalian
                if (detail.getTanggalPengembalianBarang() != null){
                        statement.setDate(1, Date.valueOf(detail.getTanggalPengembalianBarang()));
        } else {
                statement.setNull(1, Types.DATE);
        }
        // Status pengembalian
        if (detail.getStatusPengembalianBarang() != null){
                statement.setString(2, detail.getStatusPengembalianBarang());
        } else {
                statement.setNull(2, Types.VARCHAR);
        }
        // Biaya ganti rugi
        statement.setLong(3, detail.getBiayaGantiRugi());

        // ID detail transaksi
        statement.setInt(4, detail.getIdDetailTransaksi());

        int affectedRows = statement.executeUpdate();
        /*
        * affectedRows bisa bernilai 0 apabila data sebenarnya ada,
        * tetapi nilainya tidak mengalami perubahan.
        * Karena itu, periksa keberadaan ID terlebih dahulu.
        */

        if (affectedRows == 0){
                String sqlPeriksa = "SELECT 1 FROM detail_transaksi " + "WHERE id_detail_transaksi = ?";
                try (PreparedStatement pemeriksaan = connection.prepareStatement(sqlPeriksa)){
                        pemeriksaan.setInt(1, detail.getIdDetailTransaksi());
                
                try (ResultSet result = pemeriksaan.executeQuery()){
                        if (!result.next()){
                                throw new SQLException("Detail transaksi tidak ditemukan");
                        }
                }
        }
}
}
}

// DELETE
    public void hapusDetailTransaksi(int idDetailTransaksi) throws SQLException {

        if (idDetailTransaksi <= 0) {
            throw new IllegalArgumentException("ID detail transaksi tidak valid");
        }

        throw new SQLException("Penghapusan detail transaksi langsung tidak diizinkan " + "karena dapat merusak stok dan riwayat pengembalian.");
    }

// VALIDASI
    private void validasiDetail(DetailTransaksi detail, int idTransaksi) {

        if (detail == null) {
            throw new IllegalArgumentException("Detail transaksi tidak boleh null");
        }

        if (idTransaksi <= 0) {
            throw new IllegalArgumentException("ID transaksi tidak valid");
        }

        if (detail.getPerlengkapanYangDisewa() == null || detail.getPerlengkapanYangDisewa().getIdPerlengkapan() <= 0) {
            throw new IllegalArgumentException("Perlengkapan tidak valid");
        }

        if (detail.getKuantitasPerAlat() <= 0) {
            throw new IllegalArgumentException("Kuantitas harus lebih dari 0");
        }
        if (detail.getHargaSewaHarianSaatTransaksi() == null || detail.getHargaSewaHarianSaatTransaksi() < 0){ throw new IllegalArgumentException("Harga sewa historis wajib diisi dan tidak boleh negatif");
        }
    }
}
