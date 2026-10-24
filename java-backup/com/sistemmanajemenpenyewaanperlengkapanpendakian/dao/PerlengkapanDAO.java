
package com.sistemmanajemenpenyewaanperlengkapanpendakian.dao;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.DatabaseConnection;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Kompor;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Perlengkapan;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Sepatu;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Tas;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PerlengkapanDAO {

    public List<Perlengkapan> getAllPerlengkapan() throws SQLException {
        List<Perlengkapan> daftar = new ArrayList<>();

        String sql = "SELECT id_perlengkapan, id_vendor, nama_perlengkapan, "
                + "jenis_perlengkapan, stok, harga_perlengkapan, "
                + "harga_sewa_harian, tipe_kompor, tipe_tas, ukuran_sepatu "
                + "FROM perlengkapan";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String jenis = rs.getString("jenis_perlengkapan");

                int idPerlengkapan = rs.getInt("id_perlengkapan");
                int idVendor = rs.getInt("id_vendor");
                String nama = rs.getString("nama_perlengkapan");
                int stok = rs.getInt("stok");
                long hargaPerlengkapan =
                        rs.getLong("harga_perlengkapan");
                long hargaSewaHarian =
                        rs.getLong("harga_sewa_harian");

                Perlengkapan p;
                String jenisNormal = jenis == null
                        ? ""
                        : jenis.trim().toLowerCase(Locale.ROOT);

                if (jenisNormal.contains("kompor")) {
                    Kompor kompor = new Kompor(
                            idPerlengkapan, idVendor, nama, jenis, stok,
                            hargaPerlengkapan, hargaSewaHarian);
                    kompor.setTipeKompor(rs.getString("tipe_kompor"));
                    p = kompor;
                } else if (jenisNormal.contains("tas")) {
                    Tas tas = new Tas(
                            idPerlengkapan, idVendor, nama, jenis, stok,
                            hargaPerlengkapan, hargaSewaHarian);
                    tas.setTipeTas(rs.getString("tipe_tas"));
                    p = tas;
                } else if (jenisNormal.contains("sepatu")) {
                    Sepatu sepatu = new Sepatu(
                            idPerlengkapan, idVendor, nama, jenis, stok,
                            hargaPerlengkapan, hargaSewaHarian);
                    sepatu.setUkuranSepatu(rs.getInt("ukuran_sepatu"));
                    p = sepatu;
                } else {
                    throw new SQLException(
                            "Jenis perlengkapan tidak dikenal: " + jenis);
                }

                daftar.add(p);
            }
        }

        return daftar;
    }
}
