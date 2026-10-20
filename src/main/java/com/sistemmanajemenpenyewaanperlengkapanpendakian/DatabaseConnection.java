package com.sistemmanajemenpenyewaanperlengkapanpendakian;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/TAPAK-SistemManagement";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // sesuaikan jika pakai password

    public static void testConnection() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            if (conn != null) {
                System.out.println("BERHASIL: Terhubung ke database MySQL!");
            }
        } catch (SQLException e) {
            System.out.println("GAGAL terhubung: " + e.getMessage());
        }
    }
}