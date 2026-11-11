package com.sistemmanajemenpenyewaanperlengkapanpendakian.dao;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.DatabaseConnection;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Pelanggan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class PelangganDAO {
    // Penambahan CREATE, nambahin data pelanggan
    public void tambahDataPelanggan(Pelanggan pelanggan) throws SQLException{
        if (pelanggan == null){
            throw new IllegalArgumentException("Data pelanggan tidak boleh null");
        }

        String sql = "INSERT INTO pelanggan (nama_pelanggan, no_identitas) " + "VALUES (?, ?)";

        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
                statement.setString(1, pelanggan.getNamaPelanggan());
                statement.setString(1, pelanggan.getNoIdentitasPelanggan());

                statement.executeUpdate();

                // Penambahan mengambil ID yang dibuat otomatis oleh MySQL
                try(ResultSet generatedKeys = statement.getGeneratedKeys()){
                    if (generatedKeys.next()){
                        pelanggan.setIdPelanggan(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("Gagal mendapat ID pelanggan");
                    }
                }
            }
    }
    // Penambahan read, ngambil seluruh data pelanggan
    public List<Pelanggan> ambilSemuaPelanggan() throws SQLException{
        String sql = "SELECT id_pelanggan, nama_pelanggan, no_identitas " + "FROM pelanggan " + "ORDER BY id_pelanggan";

        List<Pelanggan> daftarPelanggan = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet result = statement.executeQuery()){
                while (result.next()) {
                    Pelanggan pelanggan = new Pelanggan();

                    pelanggan.setIdPelanggan(result.getInt("id_pelanggan"));
                    pelanggan.setNamaPelanggan(result.getString("nama_pelanggan"));
                    pelanggan.setNoIdentitasPelanggan(result.getString("no_identitas"));
                    
                    daftarPelanggan.add(pelanggan);
                }
            }
            return daftarPelanggan;
    }
}
