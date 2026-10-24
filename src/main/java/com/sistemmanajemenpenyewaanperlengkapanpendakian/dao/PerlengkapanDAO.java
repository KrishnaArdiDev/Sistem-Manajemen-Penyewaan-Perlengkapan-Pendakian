package com.sistemmanajemenpenyewaanperlengkapanpendakian.dao;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.DatabaseConnection;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Kompor;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Perlengkapan;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Sepatu;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Tas;

import java.sql.*;
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
                String normal = jenis == null ? "" :
                        jenis.trim().toLowerCase(Locale.ROOT);

                int id = rs.getInt("id_perlengkapan");
                int vendor = rs.getInt("id_vendor");
                String nama = rs.getString("nama_perlengkapan");
                int stok = rs.getInt("stok");
                long harga = rs.getLong("harga_perlengkapan");
                long sewa = rs.getLong("harga_sewa_harian");

                Perlengkapan p;

                if (normal.contains("kompor")) {
                    Kompor k = new Kompor(id, vendor, nama, jenis,
                            stok, harga, sewa);
                    k.setTipeKompor(rs.getString("tipe_kompor"));
                    p = k;
                } else if (normal.contains("tas")) {
                    Tas t = new Tas(id, vendor, nama, jenis,
                            stok, harga, sewa);
                    t.setTipeTas(rs.getString("tipe_tas"));
                    p = t;
                } else if (normal.contains("sepatu")) {
                    Sepatu s = new Sepatu(id, vendor, nama, jenis,
                            stok, harga, sewa);
                    s.setUkuranSepatu(rs.getInt("ukuran_sepatu"));
                    p = s;
                } else {
                    throw new SQLException("Jenis perlengkapan tidak dikenal: " + jenis);
                }

                daftar.add(p);
            }
        }

        return daftar;
    }

    public int tambahPerlengkapan(
            int idVendor,
            String nama,
            String jenis,
            int stok,
            long hargaBarang,
            long hargaSewa,
            String tipeKompor,
            String tipeTas,
            Integer ukuranSepatu) throws SQLException {

        String sql = "INSERT INTO perlengkapan "
                + "(id_vendor, nama_perlengkapan, jenis_perlengkapan, stok, "
                + "harga_perlengkapan, harga_sewa_harian, tipe_kompor, "
                + "tipe_tas, ukuran_sepatu) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, idVendor);
            ps.setString(2, nama);
            ps.setString(3, jenis);
            ps.setInt(4, stok);
            ps.setLong(5, hargaBarang);
            ps.setLong(6, hargaSewa);

            if ("kompor".equalsIgnoreCase(jenis)) {
                ps.setString(7, tipeKompor);
            } else {
                ps.setNull(7, Types.VARCHAR);
            }

            if ("tas".equalsIgnoreCase(jenis)) {
                ps.setString(8, tipeTas);
            } else {
                ps.setNull(8, Types.VARCHAR);
            }

            if ("sepatu".equalsIgnoreCase(jenis) && ukuranSepatu != null) {
                ps.setInt(9, ukuranSepatu);
            } else {
                ps.setNull(9, Types.INTEGER);
            }

            if (ps.executeUpdate() != 1) {
                throw new SQLException("Data perlengkapan gagal disimpan.");
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }

        throw new SQLException("ID perlengkapan baru tidak berhasil diperoleh.");
    }
}
