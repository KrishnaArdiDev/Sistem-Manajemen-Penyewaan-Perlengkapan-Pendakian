package com.sistemmanajemenpenyewaanperlengkapanpendakian.dao;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.DatabaseConnection;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PerlengkapanDAO {

    private static final String SELECT_DASAR =
            "SELECT p.id_perlengkapan, p.id_vendor, p.nama_perlengkapan, "
            + "p.jenis_perlengkapan, p.stok, p.harga_perlengkapan, "
            + "p.harga_sewa_harian, p.tipe_kompor, p.tipe_tas, "
            + "p.ukuran_sepatu, p.foto_barang, v.nama_vendor "
            + "FROM perlengkapan p "
            + "JOIN vendor v ON v.id_vendor = p.id_vendor ";

    public void tambahDataPerlengkapan(Perlengkapan perlengkapan, int idVendor) throws SQLException {
        periksa(perlengkapan);
        String sql = "INSERT INTO perlengkapan (id_vendor, nama_perlengkapan, jenis_perlengkapan, stok, "
                + "harga_perlengkapan, harga_sewa_harian, tipe_kompor, tipe_tas, ukuran_sepatu, foto_barang) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, idVendor);

            isiKolomPerlengkapan(ps, perlengkapan, 2);
            ps.executeUpdate();
            
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    perlengkapan.setIdPerlengkapan(keys.getInt(1));
                } else {
                    throw new SQLException("Gagal mendapat ID perlengkapan");
                }
            }
        }
    }

    public List<Perlengkapan> ambilSemuaPerlengkapan() throws SQLException {
        String sql = SELECT_DASAR + "ORDER BY p.nama_perlengkapan";
        List<Perlengkapan> hasil = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                hasil.add(petakan(rs));
            }
        }
        return hasil;
    }

    public List<Perlengkapan> ambilPerlengkapanVendor(int idVendor) throws SQLException {
        String sql = SELECT_DASAR + "WHERE p.id_vendor = ? " + "ORDER BY p.nama_perlengkapan";
        List<Perlengkapan> hasil = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idVendor);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    hasil.add(petakan(rs));
                }
            }
        }
        return hasil;
    }

    public Perlengkapan ambilPerlengkapanBerdasarkanId(int idPerlengkapan) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return ambilPerlengkapanBerdasarkanId(conn, idPerlengkapan);
        }
    }

    public Perlengkapan ambilPerlengkapanBerdasarkanId(Connection conn, int idPerlengkapan) throws SQLException {
        String sql = SELECT_DASAR + "WHERE p.id_perlengkapan = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPerlengkapan);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? petakan(rs) : null;
            }
        }
    }

public Perlengkapan ambilPerlengkapanBerdasarkanId(
        Connection conn, int idPerlengkapan, int idVendor) throws SQLException {

    if (conn == null) {
        throw new IllegalArgumentException("Koneksi database tidak boleh null");
    }

    if (idPerlengkapan <= 0 || idVendor <= 0) {
        throw new IllegalArgumentException("ID perlengkapan atau vendor tidak valid");
    }

    String sql = SELECT_DASAR
            + "WHERE p.id_perlengkapan = ? "
            + "AND p.id_vendor = ? "
            + "FOR UPDATE";

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, idPerlengkapan);
        ps.setInt(2, idVendor);

        try (ResultSet rs = ps.executeQuery()) {
            return rs.next() ? petakan(rs) : null;
        }
    }
}

    public void updateDataPerlengkapan(Perlengkapan perlengkapan, int idVendor) throws SQLException {
        periksa(perlengkapan);
        String sql = "UPDATE perlengkapan SET " +"nama_perlengkapan = ?, " +" jenis_perlengkapan = ?, " + "stok = ?, " 
                + "harga_perlengkapan = ?, " + "harga_sewa_harian = ?, "+ "tipe_kompor = ?, " + "tipe_tas = ?, "
                + "ukuran_sepatu = ?, " + "foto_barang = ? " + "WHERE id_perlengkapan = ? AND id_vendor = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            isiKolomPerlengkapan(ps, perlengkapan, 1);
            ps.setInt(10, perlengkapan.getIdPerlengkapan());
            ps.setInt(11, idVendor);
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                String sqlPeriksa = "SELECT 1 FROM perlengkapan "+ "WHERE id_perlengkapan = ? AND id_vendor = ?";
                try (PreparedStatement pemeriksaan = conn.prepareStatement(sqlPeriksa)){
                    pemeriksaan.setInt(1, perlengkapan.getIdPerlengkapan());
                    pemeriksaan.setInt(2, idVendor);

                    try (ResultSet rs = pemeriksaan.executeQuery()){
                        if (!rs.next()){
                            throw new SQLException("Perlengkapan tidak ditemukan atau bukan milik vendor ini.");
                        }
                    }
                }
            }
        }
    }

    public void updateStokPerlengkapan(int idPerlengkapan, int stokBaru) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            updateStokPerlengkapan(conn, idPerlengkapan, stokBaru);
        }
    }

    public void updateStokPerlengkapan(Connection conn, int idPerlengkapan, int stokBaru) throws SQLException {
        String sql = "UPDATE perlengkapan " + "SET stok = ? " + "WHERE id_perlengkapan = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, stokBaru);
            ps.setInt(2, idPerlengkapan);

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                String sqlPeriksa = "SELECT 1 FROM perlengkapan WHERE id_perlengkapan = ?";
                try (PreparedStatement pemeriksaan = conn.prepareStatement(sqlPeriksa)){
                    pemeriksaan.setInt(1, idPerlengkapan);

                    try (ResultSet rs = pemeriksaan.executeQuery()){
                        if(!rs.next()){
                            throw new SQLException("Perlengkapan dengan id "+ idPerlengkapan + " tidak ditemukan.");
                        }
                    }
                }
            }
        }
    }

    public void hapusDataPerlengkapan(Perlengkapan perlengkapan, int idVendor) throws SQLException {
        if (perlengkapan == null) {
            throw new IllegalArgumentException("Data perlengkapan tidak boleh kosong");
        }
        String sql = "DELETE FROM perlengkapan " + "WHERE id_perlengkapan = ? AND id_vendor = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, perlengkapan.getIdPerlengkapan());
            ps.setInt(2, idVendor);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Perlengkapan tidak ditemukan atau bukan milik vendor ini.");
            }
        }
    }

    private void periksa(Perlengkapan perlengkapan) {
        if (perlengkapan == null) {
            throw new IllegalArgumentException("Data perlengkapan tidak boleh kosong");
        }
    }

    private void isiKolomPerlengkapan(PreparedStatement ps, Perlengkapan p, int awal) throws SQLException {
        String jenis;
        String tipeKompor = null;
        String tipeTas = null;
        Integer ukuranSepatu = null;

        if (p instanceof Kompor) {
            jenis = "kompor";
            tipeKompor = ((Kompor) p).getTipeKompor();
        } else if (p instanceof Tas) {
            jenis = "tas";
            tipeTas = ((Tas) p).getTipeTas();
        } else if (p instanceof Sepatu) {
            jenis = "sepatu";
            ukuranSepatu = ((Sepatu) p).getUkuranSepatu();
        } else {
            throw new IllegalArgumentException("Jenis perlengkapan tidak dikenal");
        }

        ps.setString(awal, p.getNamaPerlengkapan());
        ps.setString(awal + 1, jenis);
        ps.setInt(awal + 2, p.getStok());
        ps.setLong(awal + 3, p.getHargaPerlengkapan());
        ps.setLong(awal + 4, p.getHargaSewaHarian());
        ps.setString(awal + 5, tipeKompor);
        ps.setString(awal + 6, tipeTas);
        if (ukuranSepatu == null) {
            ps.setNull(awal + 7, Types.INTEGER);
        } else {
            ps.setInt(awal + 7, ukuranSepatu);
        }
        if (p.getFotoBarang() == null ||p.getFotoBarang().trim().isEmpty()){
            ps.setNull(awal + 8, Types.VARCHAR);
        } else {
            ps.setString(awal + 8, p.getFotoBarang());
        }
    }

    private Perlengkapan petakan(ResultSet rs) throws SQLException {
        String jenis = rs.getString("jenis_perlengkapan");
        Perlengkapan p;

        if ("kompor".equals(jenis)) {
            Kompor kompor = new Kompor();
            kompor.setTipeKompor(rs.getString("tipe_kompor"));
            p = kompor;
        } else if ("tas".equals(jenis)) {
            Tas tas = new Tas();
            tas.setTipeTas(rs.getString("tipe_tas"));
            p = tas;
        } else if ("sepatu".equals(jenis)) {
            Sepatu sepatu = new Sepatu();
            sepatu.setUkuranSepatu(rs.getInt("ukuran_sepatu"));
            p = sepatu;
        } else {
            throw new SQLException("Jenis perlengkapan tidak dikenal: " + jenis);
        }

        p.setIdPerlengkapan(rs.getInt("id_perlengkapan"));
        p.setNamaPerlengkapan(rs.getString("nama_perlengkapan"));
        p.setStok(rs.getInt("stok"));
        p.setHargaPerlengkapan(rs.getLong("harga_perlengkapan"));
        p.setHargaSewaHarian(rs.getLong("harga_sewa_harian"));
        p.setNamaVendor(rs.getString("nama_vendor"));

        // penambahan untuk mengambil lokasi file foto dari db
        p.setFotoBarang(rs.getString("foto_barang"));
        return p;
    }
}
