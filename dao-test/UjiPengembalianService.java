import com.sistemmanajemenpenyewaanperlengkapanpendakian.DatabaseConnection;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.Service.PengembalianService;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.Service.TransaksiService;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.dao.PerlengkapanDAO;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.DetailTransaksi;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Pelanggan;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Perlengkapan;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Transaksi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UjiPengembalianService {

    private static final int ID_VENDOR = 1;
    private static final int ID_PELANGGAN = 1;
    private static final int KUANTITAS = 1;
    private static final int DURASI_SEWA = 2;

    private static final PerlengkapanDAO perlengkapanDAO =
            new PerlengkapanDAO();

    private static void periksa(boolean kondisi, String pesan) {
        if (!kondisi) {
            throw new AssertionError(pesan);
        }
    }

    private static Perlengkapan pilihPerlengkapanUji()
            throws Exception {

        List<Perlengkapan> daftar =
                perlengkapanDAO.ambilPerlengkapanVendor(ID_VENDOR);

        for (Perlengkapan perlengkapan : daftar) {
            if (perlengkapan != null && perlengkapan.getStok() >= 3) {
                return perlengkapan;
            }
        }

        throw new IllegalStateException(
                "Vendor ID " + ID_VENDOR
                + " tidak memiliki perlengkapan dengan stok minimal 3. "
                + "Pengujian dihentikan sebelum membuat transaksi."
        );
    }

    private static int ambilStok(int idPerlengkapan)
            throws Exception {

        Perlengkapan perlengkapan =
                perlengkapanDAO.ambilPerlengkapanBerdasarkanId(
                        idPerlengkapan
                );

        if (perlengkapan == null) {
            throw new IllegalStateException(
                    "Perlengkapan uji tidak ditemukan"
            );
        }

        return perlengkapan.getStok();
    }

    private static Transaksi buatTransaksiUji(
            int idPerlengkapan,
            String marker) throws Exception {

        Perlengkapan perlengkapan =
                perlengkapanDAO.ambilPerlengkapanBerdasarkanId(
                        idPerlengkapan
                );

        if (perlengkapan == null) {
            throw new IllegalStateException(
                    "Perlengkapan uji tidak ditemukan"
            );
        }

        Pelanggan pelanggan = new Pelanggan();

        pelanggan.setIdPelanggan(ID_PELANGGAN);
        pelanggan.setNamaPelanggan("Budi");
        pelanggan.setNoIdentitasPelanggan("510000000");

        DetailTransaksi detail = new DetailTransaksi();

        detail.setPerlengkapanYangDisewa(perlengkapan);
        detail.setKuantitasPerAlat(KUANTITAS);

        List<DetailTransaksi> daftarDetail = new ArrayList<>();

        daftarDetail.add(detail);

        LocalDate tanggalTransaksi = LocalDate.now();
        LocalDate tanggalSewa = tanggalTransaksi.plusDays(1);

        Transaksi transaksi = new Transaksi(
                pelanggan,
                daftarDetail,
                tanggalTransaksi,
                tanggalSewa,
                DURASI_SEWA,
                "Tunai"
        );

        transaksi.setFotoIdentitas(marker);

        TransaksiService.tambah(transaksi, ID_VENDOR);

        periksa(
                transaksi.getIdTransaksi() > 0,
                "Transaksi uji tidak mendapatkan ID"
        );

        periksa(
                detail.getIdDetailTransaksi() > 0,
                "Detail transaksi uji tidak mendapatkan ID"
        );

        periksa(
                detail.getHargaSewaHarianSaatTransaksi() != null,
                "Tarif historis tidak terisi setelah transaksi disimpan"
        );

        return transaksi;
    }

    private static DetailTransaksi cariDetail(
            Transaksi transaksi,
            int idDetail) {

        if (transaksi == null || transaksi.getDaftarDetail() == null) {
            return null;
        }

        for (DetailTransaksi detail : transaksi.getDaftarDetail()) {
            if (detail != null
                    && detail.getIdDetailTransaksi() == idDetail) {
                return detail;
            }
        }

        return null;
    }

    private static void ujiKasus(
            String namaKasus,
            String kondisi,
            int hariTerlambat,
            int idPerlengkapan,
            String marker) throws Exception {

        int stokSebelum = ambilStok(idPerlengkapan);

        periksa(
                stokSebelum > 0,
                "Stok tidak tersedia untuk " + namaKasus
        );

        Transaksi transaksi = buatTransaksiUji(
                idPerlengkapan,
                marker
        );

        DetailTransaksi detail =
                transaksi.getDaftarDetail().get(0);

        int stokSetelahSewa = ambilStok(idPerlengkapan);

        periksa(
                stokSetelahSewa == stokSebelum - KUANTITAS,
                "Stok tidak berkurang sesuai kuantitas untuk " + namaKasus
        );

        LocalDate tanggalJatuhTempo =
                transaksi.getTanggalKembali();

        LocalDate tanggalAktual =
                tanggalJatuhTempo.plusDays(hariTerlambat);

        long tarifHistoris =
                detail.getHargaSewaHarianSaatTransaksi();

        long hargaPerlengkapan =
                detail.getPerlengkapanYangDisewa()
                        .getHargaPerlengkapan();

        long dendaSeharusnya = Math.multiplyExact(
                Math.multiplyExact(
                        (long) hariTerlambat,
                        tarifHistoris
                ),
                KUANTITAS
        );

        long gantiRugiSeharusnya = 0;

        if ("rusak".equals(kondisi)
                || "hilang".equals(kondisi)) {

            gantiRugiSeharusnya = Math.multiplyExact(
                    hargaPerlengkapan,
                    KUANTITAS
            );
        }

        PengembalianService.HasilPengembalian hasil =
                PengembalianService.prosesPengembalian(
                        transaksi,
                        detail,
                        tanggalAktual,
                        kondisi,
                        ID_VENDOR
                );

        periksa(
                hasil.getDenda() == dendaSeharusnya,
                "Denda tidak sesuai pada " + namaKasus
                + ". Seharusnya Rp" + dendaSeharusnya
                + ", hasil Rp" + hasil.getDenda()
        );

        periksa(
                hasil.getGantiRugi() == gantiRugiSeharusnya,
                "Ganti rugi tidak sesuai pada " + namaKasus
                + ". Seharusnya Rp" + gantiRugiSeharusnya
                + ", hasil Rp" + hasil.getGantiRugi()
        );

        periksa(
                hasil.getTotalTagihan()
                        == dendaSeharusnya + gantiRugiSeharusnya,
                "Total tagihan tidak sesuai pada " + namaKasus
        );

        int stokSetelahPengembalian =
                ambilStok(idPerlengkapan);

        int stokSeharusnya =
                "utuh".equals(kondisi)
                        ? stokSebelum
                        : stokSetelahSewa;

        periksa(
                stokSetelahPengembalian == stokSeharusnya,
                "Stok tidak sesuai setelah kondisi " + kondisi
                + ". Seharusnya " + stokSeharusnya
                + ", hasil " + stokSetelahPengembalian
        );

        Transaksi tersimpan = TransaksiService.cariById(
                ID_VENDOR,
                transaksi.getIdTransaksi()
        );

        periksa(
                tersimpan != null,
                "Transaksi tidak ditemukan setelah pengembalian"
        );

        periksa(
                tersimpan.getDenda() == dendaSeharusnya,
                "Denda tidak tersimpan dengan benar pada tabel transaksi"
        );

        DetailTransaksi detailTersimpan = cariDetail(
                tersimpan,
                detail.getIdDetailTransaksi()
        );

        periksa(
                detailTersimpan != null,
                "Detail transaksi tidak ditemukan setelah dibaca ulang"
        );

        periksa(
                tanggalAktual.equals(
                        detailTersimpan.getTanggalPengembalianBarang()
                ),
                "Tanggal pengembalian tidak tersimpan dengan benar"
        );

        periksa(
                kondisi.equals(
                        detailTersimpan.getStatusPengembalianBarang()
                ),
                "Status pengembalian tidak tersimpan dengan benar"
        );

        periksa(
                detailTersimpan.getBiayaGantiRugi()
                        == gantiRugiSeharusnya,
                "Biaya ganti rugi tidak tersimpan dengan benar"
        );

        System.out.println("LULUS: " + namaKasus);
        System.out.println("  Denda: Rp" + hasil.getDenda());
        System.out.println("  Ganti rugi: Rp" + hasil.getGantiRugi());
        System.out.println("  Total tagihan: Rp" + hasil.getTotalTagihan());
        System.out.println("  Tanggal pengembalian dan status tersimpan");
        System.out.println("  Perubahan stok sesuai kondisi barang");
    }

    private static void bersihkanDataUji(
            String markerAwal,
            int idPerlengkapan,
            int stokAwal) throws Exception {

        String polaMarker = markerAwal + "-%";

        String hapusTransaksi =
                "DELETE FROM transaksi "
                + "WHERE id_vendor = ? AND foto_identitas LIKE ?";

        String hitungSisa =
                "SELECT COUNT(*) FROM transaksi "
                + "WHERE id_vendor = ? AND foto_identitas LIKE ?";

        String pulihkanStok =
                "UPDATE perlengkapan SET stok = ? "
                + "WHERE id_perlengkapan = ? AND id_vendor = ?";

        String bacaStok =
                "SELECT stok FROM perlengkapan "
                + "WHERE id_perlengkapan = ? AND id_vendor = ?";

        try (Connection connection = DatabaseConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {

                try (PreparedStatement statement =
                        connection.prepareStatement(hapusTransaksi)) {

                    statement.setInt(1, ID_VENDOR);
                    statement.setString(2, polaMarker);

                    statement.executeUpdate();
                }

                try (PreparedStatement statement =
                        connection.prepareStatement(hitungSisa)) {

                    statement.setInt(1, ID_VENDOR);
                    statement.setString(2, polaMarker);

                    try (ResultSet result = statement.executeQuery()) {

                        result.next();

                        periksa(
                                result.getInt(1) == 0,
                                "Masih ada transaksi uji yang belum dibersihkan"
                        );
                    }
                }

                try (PreparedStatement statement =
                        connection.prepareStatement(pulihkanStok)) {

                    statement.setInt(1, stokAwal);
                    statement.setInt(2, idPerlengkapan);
                    statement.setInt(3, ID_VENDOR);

                    periksa(
                            statement.executeUpdate() == 1,
                            "Stok perlengkapan uji gagal dipulihkan"
                    );
                }

                try (PreparedStatement statement =
                        connection.prepareStatement(bacaStok)) {

                    statement.setInt(1, idPerlengkapan);
                    statement.setInt(2, ID_VENDOR);

                    try (ResultSet result = statement.executeQuery()) {

                        periksa(
                                result.next(),
                                "Perlengkapan uji tidak ditemukan"
                        );

                        periksa(
                                result.getInt("stok") == stokAwal,
                                "Stok belum kembali ke nilai awal"
                        );
                    }
                }

                connection.commit();

                System.out.println(
                        "LULUS: Pembersihan transaksi dan pemulihan stok uji"
                );

            } catch (Exception | Error e) {

                try {
                    connection.rollback();
                } catch (Exception rollbackError) {
                    e.addSuppressed(rollbackError);
                }

                throw e;
            }
        }
    }

    public static void main(String[] args) throws Exception {

        Perlengkapan perlengkapanUji = pilihPerlengkapanUji();

        int idPerlengkapan =
                perlengkapanUji.getIdPerlengkapan();

        int stokAwal =
                perlengkapanUji.getStok();

        String markerAwal =
                "UJI-PENGEMBALIAN-" + System.currentTimeMillis();

        System.out.println(
                "=== PENGUJIAN PENGEMBALIAN SERVICE ==="
        );

        System.out.println(
                "Perlengkapan uji: "
                + perlengkapanUji.getNamaPerlengkapan()
        );

        System.out.println(
                "ID perlengkapan: " + idPerlengkapan
        );

        System.out.println(
                "Stok awal: " + stokAwal
        );

        System.out.println();

        Throwable kesalahanUtama = null;

        try {

            ujiKasus(
                    "Barang utuh terlambat 2 hari",
                    "utuh",
                    2,
                    idPerlengkapan,
                    markerAwal + "-UTUH"
            );

            ujiKasus(
                    "Barang rusak dikembalikan tepat waktu",
                    "rusak",
                    0,
                    idPerlengkapan,
                    markerAwal + "-RUSAK"
            );

            ujiKasus(
                    "Barang hilang terlambat 3 hari",
                    "hilang",
                    3,
                    idPerlengkapan,
                    markerAwal + "-HILANG"
            );

        } catch (Exception | Error e) {

            kesalahanUtama = e;
            throw e;

        } finally {

            try {

                bersihkanDataUji(
                        markerAwal,
                        idPerlengkapan,
                        stokAwal
                );

            } catch (Exception | Error errorPembersihan) {

                if (kesalahanUtama != null) {
                    kesalahanUtama.addSuppressed(errorPembersihan);
                } else {
                    throw errorPembersihan;
                }
            }
        }

        System.out.println();

        System.out.println(
                "SELURUH PENGUJIAN PENGEMBALIAN SERVICE BERHASIL"
        );
    }
}