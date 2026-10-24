
package com.sistemmanajemenpenyewaanperlengkapanpendakian;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    private static final Properties CONFIG = new Properties();

    static {
        try (FileInputStream file = new FileInputStream("db.properties")) {
            CONFIG.load(file);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(
                "File db.properties tidak ditemukan atau tidak dapat dibaca."
            );
        }
    }

    public static Connection getConnection() throws SQLException {
        String url = CONFIG.getProperty("db.url");
        String user = CONFIG.getProperty("db.user");
        String password = CONFIG.getProperty("db.password");

        return DriverManager.getConnection(url, user, password);
    }

    public static void testConnection() {
        try (Connection conn = getConnection()) {
            System.out.println("BERHASIL: Terhubung ke database MySQL!");
        } catch (SQLException e) {
            System.out.println("GAGAL terhubung: " + e.getMessage());
        }
    }
}
