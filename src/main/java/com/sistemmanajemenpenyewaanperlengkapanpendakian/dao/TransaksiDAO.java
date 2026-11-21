package com.sistemmanajemenpenyewaanperlengkapanpendakian.dao;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.DatabaseConnection;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Transaksi;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Pelanggan;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.DetailTransaksi;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.sql.Types;

public class TransaksiDAO {

    public void tambahDataTransaksi(
            Transaksi transaksi, int idVendor) throws SQLException {

        try (Connection connection = DatabaseConnection.getConnection()) {
            tambahDataTransaksi(connection, transaksi, idVendor);
        }
    }

public void tambahDataTransaksi(
        Connection connection,
        Transaksi transaksi,
        int idVendor) throws SQLException {

    if (connection == null) {
        throw new IllegalArgumentException("Koneksi database tidak boleh null");
    }

    validasiTransaksi(transaksi, idVendor);

    String sql = "INSERT INTO transaksi "
            + "(id_vendor, id_pelanggan, tanggal_transaksi, "
            + "tanggal_sewa, durasi_sewa, tanggal_kembali, "
            + "total_harga, denda, metode_pembayaran, foto_identitas) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    try (PreparedStatement statement = connection.prepareStatement(
            sql, Statement.RETURN_GENERATED_KEYS)) {

        statement.setInt(1, idVendor);
        statement.setInt(2,transaksi.getPelanggan().getIdPelanggan());

        statement.setDate(3,Date.valueOf(transaksi.getTanggalTransaksi()));

        statement.setDate(4,Date.valueOf(transaksi.getTanggalSewa()));

        statement.setInt(5, transaksi.getDurasiSewa());

        statement.setDate(6,Date.valueOf(transaksi.hitungTanggalKembali()));

        statement.setLong(7, transaksi.hitungTotalHarga());
        statement.setLong(8, transaksi.getDenda());

        statement.setString(9,transaksi.getMetodePembayaran());

        if (transaksi.getFotoIdentitas() == null|| transaksi.getFotoIdentitas().trim().isEmpty()) {
            statement.setNull(10, Types.VARCHAR);
        } else {
            statement.setString(10,transaksi.getFotoIdentitas());
        }

        statement.executeUpdate();

        try (ResultSet generatedKeys = statement.getGeneratedKeys()) {

            if (generatedKeys.next()) {
                transaksi.setIdTransaksi(generatedKeys.getInt(1));
            } else {
                throw new SQLException("Gagal mendapatkan ID transaksi");
            }
        }
    }
}

    public List<Transaksi> ambilSemuaTransaksiByVendor(int idVendor) throws SQLException {

        if (idVendor <= 0){
            throw new IllegalArgumentException("ID Vendor tidak valid");
        }


        String sql = "SELECT "
                +"t.id_transaksi, "
                +"t.id_pelanggan, " + "p.nama_pelanggan, "+"p.no_identitas, "
                + "t.tanggal_transaksi, "+"t.tanggal_sewa, "+"t.durasi_sewa, "+"t.tanggal_kembali, "
                + "t.total_harga, "+"t.denda, "
                +"t.metode_pembayaran, " + "t.foto_identitas "
                + "FROM transaksi t "
                + "JOIN pelanggan p " + "ON t.id_pelanggan = p.id_pelanggan "
                + "WHERE t.id_vendor = ? " + "ORDER BY t.id_transaksi DESC";

        List<Transaksi> daftarTransaksi = new ArrayList<>();

        DetailTransaksiDAO detailDAO = new DetailTransaksiDAO();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, idVendor);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    Pelanggan pelanggan = new Pelanggan();

                    pelanggan.setIdPelanggan(result.getInt("id_pelanggan"));
                    pelanggan.setNamaPelanggan(result.getString("nama_pelanggan"));
                    pelanggan.setNoIdentitasPelanggan(result.getString("no_identitas"));
                    int idTransaksi = result.getInt("id_transaksi");
                    
                    List <DetailTransaksi> daftarDetail = detailDAO.ambilDetailBerdasarkanTransaksi(idTransaksi);

                    Transaksi transaksi = new Transaksi(idTransaksi, pelanggan, daftarDetail, result.getDate("tanggal_transaksi").toLocalDate(), result.getDate("tanggal_sewa").toLocalDate(), result.getInt("durasi_sewa"), result.getString("metode_pembayaran"), result.getDate("tanggal_kembali").toLocalDate(), result.getLong("total_harga"), result.getLong("denda"));

                    transaksi.setFotoIdentitas(result.getString("foto_identitas"));
                    daftarTransaksi.add(transaksi);    
                }
            }
        }
        return daftarTransaksi;
    }

    public Transaksi ambilTransaksiBerdasarkanId(Connection connection, int idTransaksi, int idVendor) throws SQLException{
        if (connection == null){
            throw new IllegalArgumentException("Koneksi database tidak boleh null");
        }
        
        if (idTransaksi <= 0||idVendor <=0){
            throw new IllegalArgumentException("ID transaksi atau vendor tidak valid");
        }

        if (connection.getAutoCommit()){
            throw new SQLException("Koneksi harus menggunakan autoCommit false untuk SELECT FOR UPDATE");
        }
        
        String sql = "SELECT "
            + "t.id_transaksi, t.id_pelanggan, "
            + "p.nama_pelanggan, p.no_identitas, "
            + "t.tanggal_transaksi, t.tanggal_sewa, "
            + "t.durasi_sewa, t.tanggal_kembali, "
            + "t.total_harga, t.denda, "
            + "t.metode_pembayaran, t.foto_identitas "
            + "FROM transaksi t "
            + "JOIN pelanggan p ON t.id_pelanggan = p.id_pelanggan "
            + "WHERE t.id_transaksi = ? AND t.id_vendor = ? "
            + "FOR UPDATE";

            try(PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setInt(1, idTransaksi);
                statement.setInt(2, idVendor);

                try (ResultSet result = statement.executeQuery()){
                    if (!result.next()){
                        return null;
                    }

                    Pelanggan pelanggan = new Pelanggan();
                    
                    pelanggan.setIdPelanggan(result.getInt("id_pelanggan"));
                    pelanggan.setNamaPelanggan(result.getString("nama_pelanggan"));
                    pelanggan.setNoIdentitasPelanggan(result.getString("no_identitas"));

                    Date tanggalTransaksi = result.getDate("tanggal_transaksi");
                    Date tanggalSewa = result.getDate("tanggal_sewa");
                    Date tanggalKembali = result.getDate("tanggal_kembali");

                    if (tanggalTransaksi == null || tanggalSewa == null || tanggalKembali == null){
                        throw new SQLException("Data tanggal pada transaksi tidak lengkap");
                    }

                    Transaksi transaksi = new Transaksi(
                    result.getInt("id_transaksi"),
                    pelanggan,
                    new ArrayList<>(),
                    tanggalTransaksi.toLocalDate(),
                    tanggalSewa.toLocalDate(),
                    result.getInt("durasi_sewa"),
                    result.getString("metode_pembayaran"),
                    tanggalKembali.toLocalDate(),
                    result.getLong("total_harga"),
                    result.getLong("denda"));

                    transaksi.setFotoIdentitas(result.getString("foto_identitas"));

                    return transaksi;
                }
            }
    }

    public void updateDataTransaksi(Transaksi transaksi, int idVendor) throws SQLException {
        validasiTransaksi(transaksi, idVendor);

        if (transaksi.getIdTransaksi() <=0) {
            throw new IllegalArgumentException("ID transaksi tidak valid");
        }

        String sql = "UPDATE transaksi SET "
            +"id_pelanggan = ?, " + "tanggal_transaksi = ?, "    
            +"tanggal_sewa = ?, "+ "durasi_sewa = ?, "+"tanggal_kembali = ?, "
            + "total_harga = ?, "+"denda = ?, " + "metode_pembayaran = ?, " + "foto_identitas = ? "
            +"WHERE id_transaksi = ? AND id_vendor = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, transaksi.getPelanggan().getIdPelanggan());
            statement.setDate(2, Date.valueOf(transaksi.getTanggalTransaksi()));
            statement.setDate(3, Date.valueOf(transaksi.getTanggalSewa()));
            statement.setInt(4, transaksi.getDurasiSewa());
            statement.setDate(5, Date.valueOf(transaksi.hitungTanggalKembali()));
            statement.setLong(6, transaksi.hitungTotalHarga());
            statement.setLong(7, transaksi.hitungDenda());
            statement.setString(8, transaksi.getMetodePembayaran());

            if (transaksi.getFotoIdentitas() == null || transaksi.getFotoIdentitas().trim().isEmpty()){
                statement.setNull(9, Types.VARCHAR);
            } else{
                statement.setString(9, transaksi.getFotoIdentitas());
            }

            statement.setInt(10, transaksi.getIdTransaksi());
            statement.setInt(11, idVendor);

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0){
                String sqlPeriksa = "SELECT 1 FROM transaksi " + "WHERE id_transaksi = ? AND id_vendor = ?";

                try (PreparedStatement pemeriksaan = connection.prepareStatement(sqlPeriksa)){
                    pemeriksaan.setInt(1, transaksi.getIdTransaksi());
                    pemeriksaan.setInt(2, idVendor);

                    try(ResultSet result = pemeriksaan.executeQuery()){
                        if (!result.next()){
                            throw new SQLException("Transaksi tidak ditemukan " + "Atau memang bukan milik vendor ini");
                        }
                    }
                }
            }
        }
    }

    public void updateDendaTransaksi(Connection connection, int idTransaksi, int idVendor, long denda) throws SQLException{
        if (connection == null){
            throw new IllegalArgumentException("Koneksi database tidak boleh null");
        }

        if (idTransaksi <=0 || idVendor<=0){
            throw new IllegalArgumentException("ID transaksi atau vendor tidak valid");
        }

        if (denda<0){
            throw new IllegalArgumentException("Denda tidak boleh negatif");
        }

        String sql = "UPDATE transaksi SET denda = ? " + "WHERE id_transaksi = ? AND id_vendor = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setLong(1, denda);
            statement.setInt(2, idTransaksi);
            statement.setInt(3, idVendor);

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0){
                String sqlPeriksa = "SELECT 1 FROM transaksi " + "WHERE id_transaksi = ? AND id_vendor = ?";
                try (PreparedStatement pemeriksaan = connection.prepareStatement(sqlPeriksa)){
                    pemeriksaan.setInt(1, idTransaksi);
                    pemeriksaan.setInt(2, idVendor);

                    try(ResultSet result = pemeriksaan.executeQuery()){
                        if (!result.next()){
                            throw new SQLException("Transaksi tidak ditemukan " + "atau bukan milik vendor ini");
                        }
                    }
                }
            }
        }
    }

    public void hapusTransaksi(int idTransaksi) throws SQLException {

        if (idTransaksi <=0){
            throw new IllegalArgumentException("ID Transaksi tidak valid");
        }

        throw new SQLException("Penghapusan transaksi langsung tidak diizinkan. "+ "Pembatalan harus melalui proses yang mengelola pengembalian stok "+ "dan riwayat transaksi.");            
    }

    private void validasiTransaksi(Transaksi transaksi, int idVendor){
        if (transaksi == null){
            throw new IllegalArgumentException("Data transaksi tidak boleh null");
        }

        if (idVendor <=0){
            throw new IllegalArgumentException("ID vendor tidak valid");
        }

        if (transaksi.getPelanggan() == null || transaksi.getPelanggan().getIdPelanggan() <=0){
            throw new IllegalArgumentException("Data pelanggan belum valid  atau belum tersimpan");
        }

        if (transaksi.getTanggalTransaksi() == null || transaksi.getTanggalSewa() == null){
            throw new IllegalArgumentException("Tanggal transaksi dan tanggal sewa wajib diisi");
        }

        if (transaksi.getDurasiSewa() <=0){
            throw new IllegalArgumentException("durasi sewa harus lebih dari 0");
        }

        if (transaksi.getMetodePembayaran() == null || transaksi.getMetodePembayaran().trim().isEmpty()){
            throw new IllegalArgumentException("Metode pembayaran wajib diisi");
        }

        if (transaksi.getDaftarDetail() == null || transaksi.getDaftarDetail().isEmpty()){
            throw new IllegalArgumentException("Detail perlengkapan transaksi belum tersedia");
        }

        for (DetailTransaksi detail :transaksi.getDaftarDetail()){
            if (detail == null || detail.getPerlengkapanYangDisewa() == null || detail.getPerlengkapanYangDisewa().getIdPerlengkapan() <= 0||detail.getKuantitasPerAlat() <=0){
                throw new IllegalArgumentException("Data detail perlengkapan transaksi tidak valid");
            }
        }
    }
}
