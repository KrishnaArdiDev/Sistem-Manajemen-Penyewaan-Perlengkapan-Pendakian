package com.sistemmanajemenpenyewaanperlengkapanpendakian.dao;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.DatabaseConnection;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Transaksi;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class TransaksiDAO {

    public void tambahDataTransaksi(Transaksi transaksi, int idVendor) throws SQLException {
        if (transaksi == null) {
            throw new IllegalArgumentException("Data transaksi tidak boleh null");
        }

        String sql = "INSERT INTO transaksi (id_vendor, id_pelanggan, tanggal_transaksi, "
                   + "tanggal_sewa, durasi_sewa, tanggal_kembali, total_harga, denda, metode_pembayaran) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            long durasi = ChronoUnit.DAYS.between(transaksi.getTglSewa(), transaksi.getTglKembali());
            int durasiHari = (int) Math.max(durasi, 1);

            statement.setInt(1, idVendor);
            statement.setInt(2, 1); 
            statement.setDate(3, Date.valueOf(LocalDate.now())); 
            statement.setDate(4, Date.valueOf(transaksi.getTglSewa()));
            statement.setInt(5, durasiHari);
            statement.setDate(6, Date.valueOf(transaksi.getTglKembali()));
            statement.setLong(7, transaksi.getTotalHarga());
            statement.setLong(8, 0); 
            statement.setString(9, "dibayar"); 

            statement.executeUpdate();

           try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
             int idTransaksiBaru = generatedKeys.getInt(1);
            transaksi.setIdTransaksi(idTransaksiBaru); 
             }
            }
        }
    }

    public void updateDataTransaksi(Transaksi transaksi, int idVendor) throws SQLException {
        if (transaksi == null) {
            throw new IllegalArgumentException("Data transaksi tidak boleh null");
        }

        String sql = "UPDATE transaksi SET tanggal_sewa = ?, tanggal_kembali = ?, "
                   + "total_harga = ? WHERE id_vendor = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setDate(1, Date.valueOf(transaksi.getTglSewa()));
            statement.setDate(2, Date.valueOf(transaksi.getTglKembali()));
            statement.setLong(3, transaksi.getTotalHarga());
            statement.setInt(4, idVendor);

            statement.executeUpdate();
        }
    }

    public List<Transaksi> ambilSemuaTransaksiByVendor(int idVendor) throws SQLException {
        String sql = "SELECT t.id_transaksi, p.nama_pelanggan, t.tanggal_sewa, t.tanggal_kembali, "
                   + "t.total_harga, t.denda "
                   + "FROM transaksi t "
                   + "JOIN pelanggan p ON t.id_pelanggan = p.id_pelanggan "
                   + "WHERE t.id_vendor = ? ORDER BY t.id_transaksi DESC";

        List<Transaksi> daftar = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, idVendor);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    Transaksi t = new Transaksi(
                        result.getString("nama_pelanggan"),
                        "Perlengkapan",
                        1,
                        result.getDate("tanggal_sewa").toLocalDate(),
                        result.getDate("tanggal_kembali").toLocalDate(),
                        result.getLong("total_harga")
                    );
                    daftar.add(t);
                }
            }
        }
        return daftar;
    }

    public void hapusTransaksi(int idTransaksi) throws SQLException {
        String sql = "DELETE FROM transaksi WHERE id_transaksi = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, idTransaksi);
            statement.executeUpdate();
        }
    }
}
