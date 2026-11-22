import com.sistemmanajemenpenyewaanperlengkapanpendakian.DatabaseConnection;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.Service.PengembalianService;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.Service.TransaksiService;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.dao.PerlengkapanDAO;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UjiRollbackPengembalianService {

    private static final int ID_VENDOR = 1;
    private static final int ID_PELANGGAN = 1;

    private static final long NILAI_INJEKSI = Long.MAX_VALUE;

    private static final PerlengkapanDAO perlengkapanDAO =
            new PerlengkapanDAO();

    private static void periksa(boolean kondisi, String pesan) {
        if (!kondisi) {
            throw new AssertionError(pesan);
        }
    }

    private static Perlengkapan pilihPerlengkapan(
            List<Perlengkapan> daftar,
            String jenis) {

        for (Perlengkapan p : daftar) {

            boolean sesuai = false;

            if ("kompor".equals(jenis)) {
                sesuai = p instanceof Kompor;
            } else if ("tas".equals(jenis)) {
                sesuai = p instanceof Tas;
            } else if ("sepatu".equals(jenis)) {
                sesuai = p instanceof Sepatu;
            }

            if (sesuai && p.getStok() > 0) {
                return p;
            }
        }

        return null;
    }

    private static int ambilStok(int idPerlengkapan)
            throws Exception {

        Perlengkapan p =
                perlengkapanDAO.ambilPerlengkapanBerdasarkanId(
                        idPerlengkapan
                );

        if (p == null) {
            throw new IllegalStateException(
                    "Perlengkapan tidak ditemukan: " + idPerlengkapan
            );
        }

        return p.getStok();
    }

    private static DetailState bacaDetail(
            int idTransaksi,
            int idDetail) throws Exception {

        String sql =
                "SELECT tanggal_pengembalian_barang, "
                + "status_pengembalian, biaya_ganti_rugi "
                + "FROM detail_transaksi "
                + "WHERE id_transaksi = ? "
                + "AND id_detail_transaksi = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, idTransaksi);
            statement.setInt(2, idDetail);

            try (ResultSet result = statement.executeQuery()) {

                periksa(
                        result.next(),
                        "Detail tidak ditemukan saat membaca database"
                );

                DetailState state = new DetailState();

                Date tanggal =
                        result.getDate("tanggal_pengembalian_barang");

                state.tanggal =
                        tanggal == null ? null : tanggal.toLocalDate();

                state.status =
                        result.getString("status_pengembalian");

                state.gantiRugi =
                        result.getLong("biaya_ganti_rugi");

                return state;
            }
        }
    }

    private static long bacaDenda(int idTransaksi)
            throws Exception {

        String sql =
                "SELECT denda FROM transaksi "
                + "WHERE id_transaksi = ? AND id_vendor = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, idTransaksi);
            statement.setInt(2, ID_VENDOR);

            try (ResultSet result = statement.executeQuery()) {

                periksa(
                        result.next(),
                        "Header transaksi tidak ditemukan"
                );

                return result.getLong("denda");
            }
        }
    }

    private static void injeksiNilaiGantiRugi(
            int idTransaksi,
            int idDetail) throws Exception {

        String sql =
                "UPDATE detail_transaksi "
                + "SET biaya_ganti_rugi = ? "
                + "WHERE id_transaksi = ? "
                + "AND id_detail_transaksi = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, NILAI_INJEKSI);
            statement.setInt(2, idTransaksi);
            statement.setInt(3, idDetail);

            periksa(
                    statement.executeUpdate() == 1,
                    "Gagal memasang nilai pemicu overflow pada detail uji"
            );
        }

        DetailState hasil = bacaDetail(idTransaksi, idDetail);

        periksa(
                hasil.gantiRugi == NILAI_INJEKSI,
                "Nilai pemicu overflow tidak tersimpan"
        );
    }

    private static void bersihkan(
            int idTransaksi,
            String marker,
            int[] idPerlengkapan,
            int[] stokAwal) throws Exception {

        if (idTransaksi <= 0) {
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {

                String hapus =
                        "DELETE FROM transaksi "
                        + "WHERE id_transaksi = ? "
                        + "AND id_vendor = ? "
                        + "AND foto_identitas = ?";

                try (PreparedStatement statement =
                             connection.prepareStatement(hapus)) {

                    statement.setInt(1, idTransaksi);
                    statement.setInt(2, ID_VENDOR);
                    statement.setString(3, marker);

                    periksa(
                            statement.executeUpdate() == 1,
                            "Transaksi uji tidak berhasil dihapus"
                    );
                }

                String pulihkan =
                        "UPDATE perlengkapan SET stok = ? "
                        + "WHERE id_perlengkapan = ? "
                        + "AND id_vendor = ?";

                for (int i = 0; i < idPerlengkapan.length; i++) {

                    try (PreparedStatement statement =
                                 connection.prepareStatement(pulihkan)) {

                        statement.setInt(1, stokAwal[i]);
                        statement.setInt(2, idPerlengkapan[i]);
                        statement.setInt(3, ID_VENDOR);

                        periksa(
                                statement.executeUpdate() == 1,
                                "Gagal memulihkan stok perlengkapan "
                                + idPerlengkapan[i]
                        );
                    }
                }

                String hitung =
                        "SELECT COUNT(*) FROM transaksi "
                        + "WHERE id_transaksi = ? "
                        + "AND id_vendor = ? "
                        + "AND foto_identitas = ?";

                try (PreparedStatement statement =
                             connection.prepareStatement(hitung)) {

                    statement.setInt(1, idTransaksi);
                    statement.setInt(2, ID_VENDOR);
                    statement.setString(3, marker);

                    try (ResultSet result = statement.executeQuery()) {

                        result.next();

                        periksa(
                                result.getInt(1) == 0,
                                "Transaksi sementara masih ditemukan"
                        );
                    }
                }

                for (int i = 0; i < idPerlengkapan.length; i++) {

                    String periksaStok =
                            "SELECT stok FROM perlengkapan "
                            + "WHERE id_perlengkapan = ? "
                            + "AND id_vendor = ?";

                    try (PreparedStatement statement =
                                 connection.prepareStatement(periksaStok)) {

                        statement.setInt(1, idPerlengkapan[i]);
                        statement.setInt(2, ID_VENDOR);

                        try (ResultSet result = statement.executeQuery()) {

                            periksa(
                                    result.next(),
                                    "Perlengkapan tidak ditemukan setelah pemulihan"
                            );

                            periksa(
                                    result.getInt("stok") == stokAwal[i],
                                    "Stok belum kembali ke jumlah semula"
                            );
                        }
                    }
                }

                connection.commit();

                System.out.println(
                        "LULUS: Pembersihan transaksi dan pemulihan ketiga stok"
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

    private static class DetailState {
        LocalDate tanggal;
        String status;
        long gantiRugi;
    }

    public static void main(String[] args) throws Exception {

        List<Perlengkapan> daftar =
                perlengkapanDAO.ambilPerlengkapanVendor(ID_VENDOR);

        Perlengkapan kompor =
                pilihPerlengkapan(daftar, "kompor");

        Perlengkapan tas =
                pilihPerlengkapan(daftar, "tas");

        Perlengkapan sepatu =
                pilihPerlengkapan(daftar, "sepatu");

        periksa(
                kompor != null && tas != null && sepatu != null,
                "Vendor 1 harus memiliki Kompor, Tas, dan Sepatu dengan stok tersedia"
        );

        int[] idPerlengkapan = {
            kompor.getIdPerlengkapan(),
            tas.getIdPerlengkapan(),
            sepatu.getIdPerlengkapan()
        };

        int[] stokAwal = {
            ambilStok(idPerlengkapan[0]),
            ambilStok(idPerlengkapan[1]),
            ambilStok(idPerlengkapan[2])
        };

        String marker =
                "UJI-ROLLBACK-" + System.currentTimeMillis();

        int idTransaksi = 0;

        Throwable kesalahanUtama = null;

        System.out.println(
                "=== PENGUJIAN ROLLBACK PENGEMBALIAN SERVICE ==="
        );

        System.out.println(
                "Kompor: " + kompor.getNamaPerlengkapan()
                + " | Stok awal: " + stokAwal[0]
        );

        System.out.println(
                "Tas: " + tas.getNamaPerlengkapan()
                + " | Stok awal: " + stokAwal[1]
        );

        System.out.println(
                "Sepatu: " + sepatu.getNamaPerlengkapan()
                + " | Stok awal: " + stokAwal[2]
        );

        try {

            Pelanggan pelanggan = new Pelanggan();

            pelanggan.setIdPelanggan(ID_PELANGGAN);
            pelanggan.setNamaPelanggan("Budi");
            pelanggan.setNoIdentitasPelanggan("510000000");

            DetailTransaksi detailRusak = new DetailTransaksi();

            detailRusak.setPerlengkapanYangDisewa(kompor);
            detailRusak.setKuantitasPerAlat(1);

            DetailTransaksi detailInjeksi = new DetailTransaksi();

            detailInjeksi.setPerlengkapanYangDisewa(tas);
            detailInjeksi.setKuantitasPerAlat(1);

            DetailTransaksi detailTarget = new DetailTransaksi();

            detailTarget.setPerlengkapanYangDisewa(sepatu);
            detailTarget.setKuantitasPerAlat(1);

            List<DetailTransaksi> daftarDetail =
                    new ArrayList<>();

            daftarDetail.add(detailRusak);
            daftarDetail.add(detailInjeksi);
            daftarDetail.add(detailTarget);

            LocalDate tanggalTransaksi = LocalDate.now();
            LocalDate tanggalSewa = tanggalTransaksi.plusDays(1);

            Transaksi transaksi = new Transaksi(
                    pelanggan,
                    daftarDetail,
                    tanggalTransaksi,
                    tanggalSewa,
                    2,
                    "Tunai"
            );

            transaksi.setFotoIdentitas(marker);

            TransaksiService.tambah(
                    transaksi,
                    ID_VENDOR
            );

            idTransaksi = transaksi.getIdTransaksi();

            periksa(
                    idTransaksi > 0,
                    "Transaksi uji gagal dibuat"
            );

            periksa(
                    ambilStok(idPerlengkapan[0]) == stokAwal[0] - 1,
                    "Stok kompor tidak berkurang"
            );

            periksa(
                    ambilStok(idPerlengkapan[1]) == stokAwal[1] - 1,
                    "Stok tas tidak berkurang"
            );

            periksa(
                    ambilStok(idPerlengkapan[2]) == stokAwal[2] - 1,
                    "Stok sepatu tidak berkurang"
            );

            System.out.println(
                    "LULUS: Transaksi tiga detail berhasil dibuat"
            );

            System.out.println(
                    "LULUS: Stok ketiga perlengkapan berkurang"
            );

            LocalDate tanggalJatuhTempo =
                    transaksi.getTanggalKembali();

            PengembalianService.HasilPengembalian hasilRusak =
                    PengembalianService.prosesPengembalian(
                            transaksi,
                            detailRusak,
                            tanggalJatuhTempo,
                            "rusak",
                            ID_VENDOR
                    );

            periksa(
                    hasilRusak.getDenda() == 0,
                    "Pengembalian tepat waktu seharusnya tanpa denda"
            );

            periksa(
                    hasilRusak.getGantiRugi() > 0,
                    "Ganti rugi barang rusak seharusnya lebih dari nol"
            );

            System.out.println(
                    "LULUS: Detail pertama berhasil dikembalikan sebagai rusak"
            );

            injeksiNilaiGantiRugi(
                    idTransaksi,
                    detailInjeksi.getIdDetailTransaksi()
            );

            System.out.println(
                    "LULUS: Nilai pemicu overflow tersimpan pada detail uji"
            );

            long tarifHistoris =
                    detailTarget.getHargaSewaHarianSaatTransaksi();

            long dendaSeharusnya = tarifHistoris;

            periksa(
                    dendaSeharusnya > 0,
                    "Tarif historis target harus lebih dari nol"
            );

            // Pengembalian satu hari terlambat.
            LocalDate tanggalAktual =
                    tanggalJatuhTempo.plusDays(1);

            boolean overflowTerjadi = false;

            try {

                PengembalianService.prosesPengembalian(
                        transaksi,
                        detailTarget,
                        tanggalAktual,
                        "utuh",
                        ID_VENDOR
                );

            } catch (ArithmeticException e) {

                overflowTerjadi = true;

                System.out.println(
                        "TERDETEKSI: ArithmeticException - "
                        + e.getMessage()
                );
            }

            periksa(
                    overflowTerjadi,
                    "Pengujian seharusnya menghasilkan ArithmeticException"
            );

            System.out.println(
                    "LULUS: Kegagalan terjadi setelah operasi UPDATE dijalankan"
            );

            DetailState targetSetelahRollback =
                    bacaDetail(
                            idTransaksi,
                            detailTarget.getIdDetailTransaksi()
                    );

            periksa(
                    targetSetelahRollback.tanggal == null,
                    "Tanggal pengembalian target tidak ter-rollback"
            );

            periksa(
                    targetSetelahRollback.status == null,
                    "Status pengembalian target tidak ter-rollback"
            );

            periksa(
                    targetSetelahRollback.gantiRugi == 0,
                    "Ganti rugi target tidak ter-rollback"
            );

            System.out.println(
                    "LULUS: UPDATE tanggal, status, dan ganti rugi dibatalkan"
            );

            int stokTargetSesudah =
                    ambilStok(idPerlengkapan[2]);

            periksa(
                    stokTargetSesudah == stokAwal[2] - 1,
                    "UPDATE stok target tidak ter-rollback"
            );

            System.out.println(
                    "LULUS: UPDATE stok target dibatalkan"
            );

            long dendaSetelahRollback =
                    bacaDenda(idTransaksi);

            periksa(
                    dendaSetelahRollback == 0,
                    "UPDATE denda tidak ter-rollback"
            );

            periksa(
                    dendaSeharusnya > dendaSetelahRollback,
                    "Denda target seharusnya bernilai positif sebelum rollback"
            );

            System.out.println(
                    "LULUS: UPDATE denda transaksi dibatalkan"
            );

            DetailState rusakTersimpan =
                    bacaDetail(
                            idTransaksi,
                            detailRusak.getIdDetailTransaksi()
                    );

            periksa(
                    tanggalJatuhTempo.equals(rusakTersimpan.tanggal),
                    "Pengembalian rusak yang sudah commit ikut terhapus"
            );

            periksa(
                    "rusak".equals(rusakTersimpan.status),
                    "Status rusak sebelumnya berubah"
            );

            periksa(
                    rusakTersimpan.gantiRugi > 0,
                    "Ganti rugi sebelumnya berubah"
            );

            DetailState injeksiTersimpan =
                    bacaDetail(
                            idTransaksi,
                            detailInjeksi.getIdDetailTransaksi()
                    );

            periksa(
                    injeksiTersimpan.gantiRugi == NILAI_INJEKSI,
                    "Data injeksi di luar transaksi pengembalian ikut berubah"
            );

            System.out.println(
                    "LULUS: Data yang sudah commit tetap dipertahankan"
            );

            System.out.println(
                    "LULUS: Data injeksi di luar transaksi rollback tetap utuh"
            );

        } catch (Exception | Error e) {

            kesalahanUtama = e;
            throw e;

        } finally {

            try {

                bersihkan(
                        idTransaksi,
                        marker,
                        idPerlengkapan,
                        stokAwal
                );

            } catch (Exception | Error e) {

                if (kesalahanUtama != null) {
                    kesalahanUtama.addSuppressed(e);
                } else {
                    throw e;
                }
            }
        }

        System.out.println();

        System.out.println(
                "SELURUH PENGUJIAN ROLLBACK PENGEMBALIAN SERVICE BERHASIL"
        );
    }
}