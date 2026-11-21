package com.sistemmanajemenpenyewaanperlengkapanpendakian.dao;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.DatabaseConnection;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Vendor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VendorDAO {
    // Penambahan
    public Vendor login(String emailAtauusername, String password) throws SQLException{
        String sql = "SELECT id_vendor, username_adm, email, nama_vendor, " + "lokasi_toko, foto_profile " +"FROM vendor " + "WHERE (username_adm = ? OR email = ?) " + "AND pw_adm = ? " + "LIMIT 1";

        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setString(1, emailAtauusername);
                statement.setString(2, emailAtauusername);
                statement.setString(3, password);

                try(ResultSet result = statement.executeQuery()){
                    if (result.next()){
                        Vendor vendor = new Vendor();

                        vendor.setIdVendor(result.getInt("id_vendor"));
                        vendor.setUsernameAdm(result.getString("username_adm"));
                        vendor.setEmail(result.getString("email"));
                        vendor.setNamaVendor(result.getString("nama_vendor"));
                        vendor.setLokasiToko(result.getString("lokasi_toko"));
                        vendor.setFotoProfile(result.getString("foto_profile"));

                        return vendor;
                    }
                    return null;
                }
            }
    }

    // Penambahan memperbarui profile vendor
    public void updateProfileVendor(Vendor vendor) throws SQLException{
        if (vendor == null){
            throw new IllegalArgumentException("Data vendor tidak boleh null");
        }

        if (vendor.getIdVendor() <=0){
            throw new IllegalArgumentException("ID vendor tidak valid");
        } 

        if (vendor.getUsernameAdm() == null || vendor.getUsernameAdm().trim().isEmpty()){
            throw new IllegalArgumentException("Username vendor tidak boleh kosong");
        }

        String sql = "UPDATE vendor SET " +"username_adm = ?, " + "nama_vendor = ?, "+ "email = ?, "+"lokasi_toko = ?, "+"foto_profile = ? "+ "WHERE id_vendor = ?";

        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1, vendor.getUsernameAdm());
            statement.setString(2, vendor.getNamaVendor());
            statement.setString(3, vendor.getEmail());
            statement.setString(4, vendor.getLokasiToko());
            statement.setString(5, vendor.getFotoProfile());
            statement.setInt(6, vendor.getIdVendor());

            int affectedRows;
            try{
                affectedRows = statement.executeUpdate();
            } catch(SQLException e){
                if (e.getErrorCode() == 1062){
                    throw new SQLException("Username: " + vendor.getUsernameAdm() + " sudah digunakan oleh vendor lain", e);
                }
                throw e;
            }

            if (affectedRows == 0){
                String sqlPeriksa =  "SELECT 1 FROM vendor WHERE id_vendor = ?";
                try(PreparedStatement pemeriksaan = connection.prepareStatement(sqlPeriksa)){
                    pemeriksaan.setInt(1, vendor.getIdVendor());
                    try (ResultSet result = pemeriksaan.executeQuery()){
                        if (!result.next()){
                            throw new SQLException("Profile vendor tidak ditemukan");
                        }
                    }
                }
            }
        }
    }
    // Memperbarui atau menghapus foto profile vendor.
    public void updateFotoProfile(int idVendor, String fotoProfile)
            throws SQLException{

        String sql = "UPDATE vendor SET foto_profile = ? WHERE id_vendor = ?";

        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)){

            if (fotoProfile == null){
                statement.setNull(1, java.sql.Types.VARCHAR);
            } else {
                statement.setString(1, fotoProfile);
            }

            statement.setInt(2, idVendor);

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0){
                String sqlPeriksa = "SELECT 1 FROM vendor WHERE id_vendor = ?";

                try(PreparedStatement pemeriksaan = connection.prepareStatement(sqlPeriksa)){
                    pemeriksaan.setInt(1, idVendor);

                    try(ResultSet result = pemeriksaan.executeQuery()){
                        if (!result.next()){
                            throw new SQLException("Vendor tidak ditemukan");
                        }
                    }
                }
            }
        }
    }
}