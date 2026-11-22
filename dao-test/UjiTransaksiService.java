import com.sistemmanajemenpenyewaanperlengkapanpendakian.DatabaseConnection;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.Service.TransaksiService;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.Service.PengembalianService;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.dao.PerlengkapanDAO;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Pelanggan;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Kompor;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.DetailTransaksi;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Transaksi;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.exception.StokTidakCukupException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UjiTransaksiService {

    private static void periksa(boolean kondisi, String pesan) {
        if (!kondisi) {
            throw new AssertionError(pesan);
        }
    }

    private static int ambilStok(PerlengkapanDAO dao, int id)
            throws Exception {

        return dao.ambilPerlengkapanBerdasarkanId(id).getStok();
    }

    private static int hitungTransaksi(String marker)
            throws Exception {

        String sql =
                "SELECT COUNT(*) FROM transaksi WHERE foto_identitas = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, marker);

            try (ResultSet result = statement.executeQuery()) {
                result.next();
                return result.getInt(1);
            }
        }
    }

    private static void hapusTransaksiUji(
            int idTransaksi, int idVendor, String marker) throws Exception {

        String sql =
                "DELETE FROM transaksi "
                + "WHERE id_transaksi = ? "
                + "AND id_vendor = ? "
                + "AND foto_identitas = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, idTransaksi);
            statement.setInt(2, idVendor);
            statement.setString(3, marker);

            int affectedRows = statement.executeUpdate();

            periksa(affectedRows == 1,
                    "Transaksi uji tidak ditemukan saat pembersihan");
        }
    }

    public static void main(String[] args) throws Exception {

        int idVendor = 1;
        int idPerlengkapan = 1;

        PerlengkapanDAO perlengkapanDAO = new PerlengkapanDAO();

        int stokAwal = ambilStok(perlengkapanDAO, idPerlengkapan);

        String marker = "UJI-TRANSAKSI-" + System.currentTimeMillis();
        String markerRollback = marker + "-ROLLBACK";

        int idTransaksiUji = 0;
        boolean pengembalianBerhasil = false;

        DetailTransaksi detail = null;
        Transaksi transaksi = null;

        LocalDate tanggalTransaksi = LocalDate.now();
        LocalDate tanggalSewa = tanggalTransaksi.plusDays(1);
        LocalDate tanggalJatuhTempo = tanggalSewa.plusDays(2);

        System.out.println("=== PENGUJIAN TRANSAKSISERVICE ===");

        try {

            Pelanggan pelanggan = new Pelanggan();

            pelanggan.setIdPelanggan(1);
            pelanggan.setNamaPelanggan("Budi");
            pelanggan.setNoIdentitasPelanggan("510000000");

            Kompor kompor = new Kompor();

            kompor.setIdPerlengkapan(idPerlengkapan);

            detail = new DetailTransaksi();

            detail.setPerlengkapanYangDisewa(kompor);
            detail.setKuantitasPerAlat(1);

            List<DetailTransaksi> daftarDetail = new ArrayList<>();

            daftarDetail.add(detail);

            transaksi = new Transaksi(
                    pelanggan,
                    daftarDetail,
                    tanggalTransaksi,
                    tanggalSewa,
                    2,
                    "Tunai"
            );

            transaksi.setFotoIdentitas(marker);

            TransaksiService.tambah(transaksi, idVendor);

            idTransaksiUji = transaksi.getIdTransaksi();

            periksa(idTransaksiUji > 0,
                    "Transaksi gagal disimpan");

            periksa(detail.getIdDetailTransaksi() > 0,
                    "Detail transaksi gagal disimpan");

            periksa(transaksi.getTotalHarga() == 60000,
                    "Total harga transaksi tidak sesuai");

            periksa(tanggalJatuhTempo.equals(transaksi.getTanggalKembali()),
                    "Tanggal kembali tidak sesuai");

            periksa(hitungTransaksi(marker) == 1,
                    "Header transaksi tidak tersimpan");

            System.out.println("LULUS: Membuat transaksi");
            System.out.println("LULUS: Menyimpan detail transaksi");
            System.out.println("LULUS: Perhitungan total harga Rp60.000");
            System.out.println("LULUS: Perhitungan tanggal kembali");

            int stokSetelahSewa =
                    ambilStok(perlengkapanDAO, idPerlengkapan);

            periksa(stokSetelahSewa == stokAwal - 1,
                    "Stok tidak berkurang sesuai kuantitas");

            System.out.println("LULUS: Pengurangan stok setelah transaksi");

            Kompor komporTidakCukup = new Kompor();

            komporTidakCukup.setIdPerlengkapan(idPerlengkapan);

            DetailTransaksi detailTidakCukup = new DetailTransaksi();

            detailTidakCukup.setPerlengkapanYangDisewa(komporTidakCukup);
            detailTidakCukup.setKuantitasPerAlat(stokSetelahSewa + 1);

            List<DetailTransaksi> daftarTidakCukup = new ArrayList<>();

            daftarTidakCukup.add(detailTidakCukup);

            Transaksi transaksiTidakCukup = new Transaksi(
                    pelanggan,
                    daftarTidakCukup,
                    tanggalTransaksi,
                    tanggalSewa,
                    2,
                    "Tunai"
            );

            transaksiTidakCukup.setFotoIdentitas(markerRollback);

            boolean stokDitolak = false;

            try {

                TransaksiService.tambah(transaksiTidakCukup, idVendor);

            } catch (StokTidakCukupException e) {

                stokDitolak = true;
            }

            periksa(stokDitolak,
                    "Transaksi dengan stok tidak cukup seharusnya ditolak");

            periksa(transaksiTidakCukup.getIdTransaksi() == 0,
                    "ID transaksi gagal dipulihkan setelah penolakan");

            periksa(detailTidakCukup.getIdDetailTransaksi() == 0,
                    "ID detail gagal dipulihkan setelah penolakan");

            periksa(hitungTransaksi(markerRollback) == 0,
                    "Transaksi yang ditolak ternyata tersimpan");

            periksa(
                    ambilStok(perlengkapanDAO, idPerlengkapan) == stokSetelahSewa,
                    "Stok berubah padahal transaksi ditolak"
            );

            System.out.println("LULUS: Penolakan transaksi ketika stok tidak cukup");
            System.out.println("LULUS: Rollback dan pemulihan ID transaksi");
            System.out.println("LULUS: Stok tetap ketika transaksi ditolak");

            PengembalianService.HasilPengembalian hasil =
                    PengembalianService.prosesPengembalian(
                            transaksi,
                            detail,
                            tanggalJatuhTempo,
                            "utuh",
                            idVendor
                    );

            pengembalianBerhasil = true;

            periksa(hasil.getDenda() == 0,
                    "Denda seharusnya nol karena barang dikembalikan tepat waktu");

            periksa(hasil.getGantiRugi() == 0,
                    "Ganti rugi seharusnya nol untuk barang utuh");

            periksa(hasil.getTotalTagihan() == 0,
                    "Total tagihan pengembalian seharusnya nol");

            System.out.println("LULUS: Pengembalian barang dalam kondisi utuh");
            System.out.println("LULUS: Denda tepat waktu sebesar Rp0");
            System.out.println("LULUS: Biaya ganti rugi sebesar Rp0");

            int stokSetelahPengembalian =
                    ambilStok(perlengkapanDAO, idPerlengkapan);

            periksa(stokSetelahPengembalian == stokAwal,
                    "Stok tidak kembali ke jumlah semula");

            System.out.println("LULUS: Pengembalian stok barang utuh");

        } finally {

            if (idTransaksiUji > 0) {

                if (!pengembalianBerhasil
                        && detail != null
                        && transaksi != null
                        && detail.getTanggalPengembalianBarang() == null) {

                    try {

                        PengembalianService.prosesPengembalian(
                                transaksi,
                                detail,
                                tanggalJatuhTempo,
                                "utuh",
                                idVendor
                        );

                        pengembalianBerhasil = true;

                        System.out.println(
                                "PEMULIHAN: Pengembalian barang uji berhasil dilakukan");

                    } catch (Exception e) {

                        System.out.println(
                                "PERINGATAN: Pengembalian otomatis gagal. "
                                + "Transaksi uji ID " + idTransaksiUji
                                + " dipertahankan untuk pemeriksaan. "
                                + e.getMessage()
                        );
                    }
                }

                if (detail != null
                        && (detail.getTanggalPengembalianBarang() != null
                        || pengembalianBerhasil)) {

                    int stokAkhir =
                            ambilStok(perlengkapanDAO, idPerlengkapan);

                    periksa(stokAkhir == stokAwal,
                            "Stok belum kembali seperti semula. "
                            + "Transaksi uji tidak dihapus.");

                    hapusTransaksiUji(
                            idTransaksiUji,
                            idVendor,
                            marker
                    );

                    periksa(hitungTransaksi(marker) == 0,
                            "Transaksi uji belum berhasil dibersihkan");

                    System.out.println("LULUS: Pembersihan transaksi pengujian");

                } else {

                    System.out.println(
                            "PERHATIAN: Transaksi uji ID " + idTransaksiUji
                            + " dipertahankan agar stok dan riwayat tidak rusak. "
                            + "Periksa data tersebut sebelum melanjutkan."
                    );
                }
            }
        }

        System.out.println("\nSELURUH PENGUJIAN TRANSAKSISERVICE BERHASIL");
    }
}