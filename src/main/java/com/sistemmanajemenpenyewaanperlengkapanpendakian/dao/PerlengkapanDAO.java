package com.sistemmanajemenpenyewaanperlengkapanpendakian.dao;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.DatabaseConnection;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.*;
import java.sql.*;
import java.util.*;

public class PerlengkapanDAO {

    private static final String SELECT_DASAR =
            "SELECT p.id_perlengkapan, p.id_vendor, p.nama_perlengkapan, p.jenis_perlengkapan, "
            + "p.stok, p.harga_perlengkapan, p.harga_sewa_harian, "
            + "p.tipe_kompor, p.tipe_tas, p.ukuran_sepatu, v.nama_vendor "
            + "FROM perlengkapan p JOIN vendor v ON v.id_vendor = p.id_vendor ";

    // ===== Create =====
    // Dipanggil oleh Vendor.tambahDataPerlengkapan(); id hasil auto-increment diisi ke objek.
    public void tambahDataPerlengkapan(Perlengkapan perlengkapan, int idVendor) throws SQLException {
        periksa(perlengkapan);
        String sql = "INSERT INTO perlengkapan (id_vendor, nama_perlengkapan, jenis_perlengkapan, stok, "
                + "harga_perlengkapan, harga_sewa_harian, tipe_kompor, tipe_tas, ukuran_sepatu) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idVendor);
            isiKolomPerlengkapan(ps, perlengkapan, 2);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    perlengkapan.setIdPerlengkapan(keys.getInt(1));
                }
            }
        }
    }

    // ===== Read =====
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
        String sql = SELECT_DASAR + "WHERE p.id_vendor = ? ORDER BY p.nama_perlengkapan";
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

    // Mengembalikan null bila id tidak ditemukan.
    public Perlengkapan ambilPerlengkapanBerdasarkanId(int idPerlengkapan) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return ambilPerlengkapanBerdasarkanId(conn, idPerlengkapan);
        }
    }

    // Versi yang memakai koneksi dari luar, supaya bisa dipakai dalam satu transaksi database.
    public Perlengkapan ambilPerlengkapanBerdasarkanId(Connection conn, int idPerlengkapan) throws SQLException {
        String sql = SELECT_DASAR + "WHERE p.id_perlengkapan = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPerlengkapan);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? petakan(rs) : null;
            }
        }
    }

    // ===== Update =====
    // Dipanggil oleh Vendor.updateDataPerlengkapan(); hanya boleh mengubah milik vendor yang bersangkutan.
    public void updateDataPerlengkapan(Perlengkapan perlengkapan, int idVendor) throws SQLException {
        periksa(perlengkapan);
        String sql = "UPDATE perlengkapan SET nama_perlengkapan = ?, jenis_perlengkapan = ?, stok = ?, "
                + "harga_perlengkapan = ?, harga_sewa_harian = ?, tipe_kompor = ?, tipe_tas = ?, "
                + "ukuran_sepatu = ? WHERE id_perlengkapan = ? AND id_vendor = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            isiKolomPerlengkapan(ps, perlengkapan, 1);
            ps.setInt(9, perlengkapan.getIdPerlengkapan());
            ps.setInt(10, idVendor);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Perlengkapan tidak ditemukan atau bukan milik vendor ini.");
            }
        }
    }

    // Hanya mengubah stok (dipakai saat penyewaan/pengembalian).
    public void updateStokPerlengkapan(int idPerlengkapan, int stokBaru) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            updateStokPerlengkapan(conn, idPerlengkapan, stokBaru);
        }
    }

    // Versi yang memakai koneksi dari luar, supaya bisa dipakai dalam satu transaksi database.
    public void updateStokPerlengkapan(Connection conn, int idPerlengkapan, int stokBaru) throws SQLException {
        String sql = "UPDATE perlengkapan SET stok = ? WHERE id_perlengkapan = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, stokBaru);
            ps.setInt(2, idPerlengkapan);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Perlengkapan dengan id " + idPerlengkapan + " tidak ditemukan.");
            }
        }
    }

    // ===== Delete =====
    // Dipanggil oleh Vendor.hapusDataPerlengkapan(). Perlengkapan yang sudah tercatat pada
    // detail_transaksi tidak bisa dihapus (FK RESTRICT) dan akan melempar SQLException.
    public void hapusDataPerlengkapan(Perlengkapan perlengkapan, int idVendor) throws SQLException {
        if (perlengkapan == null) {
            throw new IllegalArgumentException("Data perlengkapan tidak boleh kosong");
        }
        String sql = "DELETE FROM perlengkapan WHERE id_perlengkapan = ? AND id_vendor = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, perlengkapan.getIdPerlengkapan());
            ps.setInt(2, idVendor);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Perlengkapan tidak ditemukan atau bukan milik vendor ini.");
            }
        }
    }

    // ===== Helper =====
    private void periksa(Perlengkapan perlengkapan) {
        if (perlengkapan == null) {
            throw new IllegalArgumentException("Data perlengkapan tidak boleh kosong");
        }
    }

    // Mengisi 8 kolom data perlengkapan mulai dari indeks awal (nama .. ukuran_sepatu).
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
    }

    // Mengubah satu baris hasil query menjadi objek Kompor/Tas/Sepatu.
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
        return p;
    }
}