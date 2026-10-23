
package com.sistemmanajemenpenyewaanperlengkapanpendakian.dao;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.DatabaseConnection;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Perlengkapan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PerlengkapanDAO {

    public List<Perlengkapan> getAllPerlengkapan() throws SQLException {
        List<Perlengkapan> daftar = new ArrayList<>();

        String sql = "SELECT id_perlengkapan, id_vendor, nama_perlengkapan, "
                + "jenis_perlengkapan, stok, harga_perlengkapan, harga_sewa_harian "
                + "FROM perlengkapan";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Perlengkapan p = new Perlengkapan(
                        rs.getInt("id_perlengkapan"),
                        rs.getInt("id_vendor"),
                        rs.getString("nama_perlengkapan"),
                        rs.getString("jenis_perlengkapan"),
                        rs.getInt("stok"),
                        rs.getBigDecimal("harga_perlengkapan"),
                        rs.getBigDecimal("harga_sewa_harian")
                );

                daftar.add(p);
            }
        }

        return daftar;
    }
}
