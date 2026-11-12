package com.sistemmanajemenpenyewaanperlengkapanpendakian;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/TAPAK-SistemManagement-PenyewaanPerlengkapanPendakian";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // sesuaikan jika pakai password

    // Penambahan ini buat menyediakan koneksi db untuk seluruh DAO
    public static Connection getConnection() throws SQLException{
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void testConnection(){
        try (Connection conn = getConnection()){
            if (conn != null){
                System.out.println("Berhasil terhubung ke database MySQL");
            } 
        }catch (SQLException e){
            System.out.println("Gagal terhubung: " + e.getMessage());
        }
    }
}