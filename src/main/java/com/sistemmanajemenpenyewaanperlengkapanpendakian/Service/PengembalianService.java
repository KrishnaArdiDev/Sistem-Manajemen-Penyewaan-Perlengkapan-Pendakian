package com.sistemmanajemenpenyewaanperlengkapanpendakian.Service;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.exception.DataTidakLengkapException;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.DetailTransaksi;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Perlengkapan;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Transaksi;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.DatabaseConnection;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.dao.DetailTransaksiDAO;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.dao.PerlengkapanDAO;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.dao.TransaksiDAO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.sql.Connection;
import java.sql.SQLException;

public class PengembalianService {
    private static final DetailTransaksiDAO detailDAO = new DetailTransaksiDAO();
    private static final PerlengkapanDAO perlengkapanDAO = new PerlengkapanDAO();
    private static final TransaksiDAO transaksiDAO = new TransaksiDAO();

    public static List<Transaksi> getTransaksiAktif() {
        List<Transaksi> hasil = new ArrayList<>();
        for (Transaksi t : TransaksiService.semua()) {
            if (!isSelesai(t)) {
                hasil.add(t);
            }
        }
        return hasil;
    }

    public static boolean isSelesai(Transaksi t) {
        if (t == null || t.getDaftarDetail() == null || t.getDaftarDetail().isEmpty()) {
            return false;
        }
        for (DetailTransaksi d : t.getDaftarDetail()) {
            if (d == null || d.getTanggalPengembalianBarang() == null) {
                return false;
            }
        }
        return true;
    }

    public static HasilPengembalian prosesPengembalian(Transaksi t, DetailTransaksi detail, LocalDate tglKembaliAktual, String kondisi, int idVendor) throws DataTidakLengkapException, SQLException {

        // Validasi data awal
        if (t == null || t.getIdTransaksi() <=0) {
            throw new DataTidakLengkapException("Transaksi tidak ditemukan atau tidak valid");
        }
        if (detail == null || detail.getIdDetailTransaksi() <= 0) {
            throw new DataTidakLengkapException("Detail transaksi tidak valid.");
        }
        if (tglKembaliAktual == null) {
            throw new DataTidakLengkapException("Tanggal pengembalian aktual wajib diisi.");
        }
        if (kondisi == null || kondisi.trim().isEmpty()) {
            throw new DataTidakLengkapException("Kondisi perlengkapan wajib diisi.");
        }
        if (idVendor <=0){
            throw new DataTidakLengkapException("ID vendor tidak valid.");
        }

        String kondisiNormal = kondisi.trim().toLowerCase(Locale.ROOT);

        if (!kondisiNormal.equals("utuh")  && !kondisiNormal.equals("rusak") && !kondisiNormal.equals("hilang")){
            throw new DataTidakLengkapException("Kondisi harus utuh, rusak, atau hilang.");
        }

        long gantiRugi;
        long denda;

        try (Connection connection = DatabaseConnection.getConnection()){
            connection.setAutoCommit(false);
            try {
                // Mengambil dan mengunci header transaksi terbaru dari database
                Transaksi transaksiDatabase = transaksiDAO.ambilTransaksiBerdasarkanId(connection, t.getIdTransaksi(), idVendor);

                if (transaksiDatabase == null){
                    throw new DataTidakLengkapException("Transaksi tidak ditemukan atau bukan milik vendor ini.");
                }

                // Mengambil seluruh detail terbaru dan mengunci barisnya
                List<DetailTransaksi> daftarDetail = detailDAO.ambilDetailBerdasarkanTransaksi(connection, t.getIdTransaksi(), true);
                // Mencari detail yang akan dikembalikan
                DetailTransaksi detailDatabase = null;
                for (DetailTransaksi d :daftarDetail){
                    if (d.getIdDetailTransaksi() == detail.getIdDetailTransaksi()){
                        detailDatabase = d;
                        break;
                    }
                }
                
                if (detailDatabase == null) {
                throw new DataTidakLengkapException("Detail tidak ditemukan dalam transaksi tersebut.");
                }

                if (detailDatabase.getTanggalPengembalianBarang() != null){
                    throw new DataTidakLengkapException("Barang ini sudah dikembalikan.");
                }
                // Mengambil perlengkapan aktual dan mengunci stoknya
                int idPerlengkapan = detailDatabase.getPerlengkapanYangDisewa().getIdPerlengkapan();

                Perlengkapan perlengkapan = perlengkapanDAO.ambilPerlengkapanBerdasarkanId(connection, idPerlengkapan, idVendor);
                
                if (perlengkapan == null){
                    throw new DataTidakLengkapException("Perlengkapan tidak ditemukan atau bukan milik vendor ini.");
                }

                detailDatabase.setPerlengkapanYangDisewa(perlengkapan);
                // Memproses pengembalian menggunakan Model
                gantiRugi = detailDatabase.prosesPengembalianBarang(tglKembaliAktual, kondisiNormal);

                // Mengembalikan stok hanya jika kondisi barang utuh
                if (kondisiNormal.equals("utuh")){
                    perlengkapanDAO.updateStokPerlengkapan(connection, idPerlengkapan, perlengkapan.getStok());
                }

                // Menghitung denda seluruh detail menggunakan header terbaru dari database
                LocalDate tanggalJatuhTempo = transaksiDatabase.getTanggalKembali();

                if (tanggalJatuhTempo == null){
                    tanggalJatuhTempo = transaksiDatabase.hitungTanggalKembali();
                }

                Transaksi transaksiPerhitungan = new Transaksi(
                    transaksiDatabase.getIdTransaksi(),
                    transaksiDatabase.getPelanggan(),
                    daftarDetail,
                    transaksiDatabase.getTanggalTransaksi(),
                    transaksiDatabase.getTanggalSewa(),
                    transaksiDatabase.getDurasiSewa(),
                    transaksiDatabase.getMetodePembayaran(),
                    tanggalJatuhTempo,
                    transaksiDatabase.getTotalHarga(),
                    transaksiDatabase.getDenda()
                );

                denda = transaksiPerhitungan.hitungDenda();
                // Menyimpan perubahan detail
                detailDAO.updateDetailTransaksi(connection, detailDatabase);

                // Menyimpan perubahan denda transaksi
                transaksiDAO.updateDendaTransaksi(connection, t.getIdTransaksi(), idVendor, denda);

                // Menghitung total ganti rugi seluruh detail transaksi
                gantiRugi = 0;
                for (DetailTransaksi detailSelesai : daftarDetail){
                    if (detailSelesai != null){
                        gantiRugi = Math.addExact(gantiRugi, detailSelesai.getBiayaGantiRugi());
                    }
                }
                
                // Semua perubahan berhasil
                connection.commit();

                // Menyelaraskan objek detail yang dikirim pemanggil
                detail.setTanggalPengembalianBarang(detailDatabase.getTanggalPengembalianBarang());
                detail.setStatusPengembalianBarang(detailDatabase.getStatusPengembalianBarang());
                detail.setBiayaGantiRugi(detailDatabase.getBiayaGantiRugi());
                detail.setPengembalianSudahDiproses(true);

                if (detail.getPerlengkapanYangDisewa() != null && kondisiNormal.equals("utuh")){
                    detail.getPerlengkapanYangDisewa().setStok(perlengkapan.getStok());
                }
            } catch (SQLException | DataTidakLengkapException | RuntimeException e){
                try {
                    connection.rollback();
                } catch (SQLException rollbackError){
                    e.addSuppressed(rollbackError);
                }
                throw e;
            }
        }
        return new HasilPengembalian(denda, gantiRugi);
    }


    // Menampung hasil perhitungan pengembalian
    public static class HasilPengembalian {
        private final long denda;
        private final long gantiRugi;

        public HasilPengembalian(long denda, long gantiRugi) {
            this.denda = denda;
            this.gantiRugi = gantiRugi;
        }

        public long getDenda() {
            return denda;
        }

        public long getGantiRugi() {
            return gantiRugi;
        }

        public long getTotalTagihan() {
            return Math.addExact(denda, gantiRugi);
        }
    }
}