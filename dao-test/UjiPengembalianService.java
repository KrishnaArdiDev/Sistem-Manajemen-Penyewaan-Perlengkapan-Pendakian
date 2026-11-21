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

    // Memilih perlengkapan milik Vendor 1 dengan stok minimal 3.
    // Stok 3 diperlukan untuk menjalankan tiga transaksi pengujian.
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

    // Mengambil stok terbaru dari database.
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

    // Membuat satu transaksi sementara melalui TransaksiService.
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

    // Mencari detail berdasarkan ID dari transaksi yang dibaca ulang.
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

    // Menguji satu kondisi pengembalian.
    private static void ujiKasus(
            String namaKasus,
            String kondisi,
            int hariTerlambat,
            int idPerlengkapan,
            String marker) throws Exception {

        // Stok sebelum transaksi dibuat.
        int stokSebelum = ambilStok(idPerlengkapan);

        periksa(
                stokSebelum > 0,
                "Stok tidak tersedia untuk " + namaKasus
        );

        // Membuat transaksi melalui Service.
        Transaksi transaksi = buatTransaksiUji(
                idPerlengkapan,
                marker
        );

        DetailTransaksi detail =
                transaksi.getDaftarDetail().get(0);

        // Memastikan stok sudah berkurang setelah penyewaan.
        int stokSetelahSewa = ambilStok(idPerlengkapan);

        periksa(
                stokSetelahSewa == stokSebelum - KUANTITAS,
                "Stok tidak berkurang sesuai kuantitas untuk " + namaKasus
        );

        LocalDate tanggalJatuhTempo =
                transaksi.getTanggalKembali();

        LocalDate tanggalAktual =
                tanggalJatuhTempo.plusDays(hariTerlambat);

        // Mengambil tarif historis dan harga perlengkapan.
        long tarifHistoris =
                detail.getHargaSewaHarianSaatTransaksi();

        long hargaPerlengkapan =
                detail.getPerlengkapanYangDisewa()
                        .getHargaPerlengkapan();

        // Menghitung denda yang seharusnya.
        long dendaSeharusnya = Math.multiplyExact(
                Math.multiplyExact(
                        (long) hariTerlambat,
                        tarifHistoris
                ),
                KUANTITAS
        );

        // Menghitung ganti rugi yang seharusnya.
        long gantiRugiSeharusnya = 0;

        if ("rusak".equals(kondisi)
                || "hilang".equals(kondisi)) {

            gantiRugiSeharusnya = Math.multiplyExact(
                    hargaPerlengkapan,
                    KUANTITAS
            );
        }

        // Memproses pengembalian melalui PengembalianService.
        PengembalianService.HasilPengembalian hasil =
                PengembalianService.prosesPengembalian(
                        transaksi,
                        detail,
                        tanggalAktual,
                        kondisi,
                        ID_VENDOR
                );

        // Memeriksa denda.
        periksa(
                hasil.getDenda() == dendaSeharusnya,
                "Denda tidak sesuai pada " + namaKasus
                + ". Seharusnya Rp" + dendaSeharusnya
                + ", hasil Rp" + hasil.getDenda()
        );

        // Memeriksa ganti rugi.
        periksa(
                hasil.getGantiRugi() == gantiRugiSeharusnya,
                "Ganti rugi tidak sesuai pada " + namaKasus
                + ". Seharusnya Rp" + gantiRugiSeharusnya
                + ", hasil Rp" + hasil.getGantiRugi()
        );

        // Memeriksa total tagihan.
        periksa(
                hasil.getTotalTagihan()
                        == dendaSeharusnya + gantiRugiSeharusnya,
                "Total tagihan tidak sesuai pada " + namaKasus
        );

        // Memeriksa stok setelah pengembalian.
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

        // Membaca ulang transaksi dari database.
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

        // Membaca ulang detail transaksi.
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

    // Membersihkan transaksi sementara dan mengembalikan stok.
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

                // Menghapus transaksi uji berdasarkan marker khusus.
                // Detail transaksi akan terhapus melalui ON DELETE CASCADE.
                try (PreparedStatement statement =
                        connection.prepareStatement(hapusTransaksi)) {

                    statement.setInt(1, ID_VENDOR);
                    statement.setString(2, polaMarker);

                    statement.executeUpdate();
                }

                // Memastikan seluruh transaksi uji sudah terhapus.
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

                // Memulihkan stok ke nilai sebelum pengujian.
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

                // Memeriksa stok setelah pemulihan.
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

        // Memilih perlengkapan milik Vendor 1 dengan stok minimal 3.
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

            // KASUS 1
            // Barang utuh terlambat 2 hari.
            ujiKasus(
                    "Barang utuh terlambat 2 hari",
                    "utuh",
                    2,
                    idPerlengkapan,
                    markerAwal + "-UTUH"
            );

            // KASUS 2
            // Barang rusak dikembalikan tepat waktu.
            ujiKasus(
                    "Barang rusak dikembalikan tepat waktu",
                    "rusak",
                    0,
                    idPerlengkapan,
                    markerAwal + "-RUSAK"
            );

            // KASUS 3
            // Barang hilang terlambat 3 hari.
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