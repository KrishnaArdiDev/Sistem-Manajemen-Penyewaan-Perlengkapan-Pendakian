import com.sistemmanajemenpenyewaanperlengkapanpendakian.DatabaseConnection;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.Service.PerlengkapanService;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.Service.TransaksiService;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.dao.PerlengkapanDAO;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.exception.StokTidakCukupException;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Kompor;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Pelanggan;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.DetailTransaksi;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Transaksi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class UjiKonsistensiStokService {

    private static final int ID_VENDOR = 1;
    private static final int ID_PELANGGAN = 1;
    private static final int STOK_UJI = 1;

    private static final PerlengkapanDAO perlengkapanDAO =
            new PerlengkapanDAO();

    private static final PerlengkapanService perlengkapanService =
            new PerlengkapanService();

    private static void periksa(boolean kondisi, String pesan) {
        if (!kondisi) {
            throw new AssertionError(pesan);
        }
    }

    private static class HasilPemesanan {
        final String marker;
        final boolean berhasil;
        final int idTransaksi;
        final int idDetail;
        final String pesan;

        HasilPemesanan(
                String marker,
                boolean berhasil,
                int idTransaksi,
                int idDetail,
                String pesan) {

            this.marker = marker;
            this.berhasil = berhasil;
            this.idTransaksi = idTransaksi;
            this.idDetail = idDetail;
            this.pesan = pesan;
        }
    }

    private static Kompor buatKomporUji(String nama) {

        Kompor kompor = new Kompor();

        kompor.setNamaPerlengkapan(nama);
        kompor.setStok(STOK_UJI);
        kompor.setHargaPerlengkapan(100000);
        kompor.setHargaSewaHarian(25000);
        kompor.setTipeKompor("2 sumbu");

        return kompor;
    }

    private static Callable<HasilPemesanan> buatPermintaan(
            int idPerlengkapan,
            String marker,
            CountDownLatch siap,
            CountDownLatch mulai) {

        return () -> {

            Pelanggan pelanggan = new Pelanggan();

            pelanggan.setIdPelanggan(ID_PELANGGAN);
            pelanggan.setNamaPelanggan("Budi");
            pelanggan.setNoIdentitasPelanggan("510000000");

            Kompor komporInput = new Kompor();
            komporInput.setIdPerlengkapan(idPerlengkapan);

            DetailTransaksi detail = new DetailTransaksi();

            detail.setPerlengkapanYangDisewa(komporInput);
            detail.setKuantitasPerAlat(1);

            List<DetailTransaksi> daftarDetail = new ArrayList<>();
            daftarDetail.add(detail);

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

            siap.countDown();

            if (!mulai.await(20, TimeUnit.SECONDS)) {
                throw new IllegalStateException(
                        "Permintaan tidak menerima sinyal mulai"
                );
            }

            try {

                TransaksiService.tambah(transaksi, ID_VENDOR);

                return new HasilPemesanan(
                        marker,
                        true,
                        transaksi.getIdTransaksi(),
                        detail.getIdDetailTransaksi(),
                        "Transaksi berhasil"
                );

            } catch (StokTidakCukupException e) {

                return new HasilPemesanan(
                        marker,
                        false,
                        transaksi.getIdTransaksi(),
                        detail.getIdDetailTransaksi(),
                        e.getMessage()
                );
            }
        };
    }

    private static int hitungTransaksiUji(
            String markerA,
            String markerB) throws Exception {

        String sql =
                "SELECT COUNT(*) FROM transaksi "
                + "WHERE id_vendor = ? AND foto_identitas IN (?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, ID_VENDOR);
            statement.setString(2, markerA);
            statement.setString(3, markerB);

            try (ResultSet result = statement.executeQuery()) {
                result.next();
                return result.getInt(1);
            }
        }
    }

    private static int hitungDetail(int idTransaksi)
            throws Exception {

        String sql =
                "SELECT COUNT(*) FROM detail_transaksi "
                + "WHERE id_transaksi = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, idTransaksi);

            try (ResultSet result = statement.executeQuery()) {
                result.next();
                return result.getInt(1);
            }
        }
    }

    private static void bersihkan(
            int idPerlengkapan,
            String markerA,
            String markerB) throws Exception {


        try (Connection connection = DatabaseConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {

                String hapusTransaksi =
                        "DELETE FROM transaksi "
                        + "WHERE id_vendor = ? "
                        + "AND foto_identitas IN (?, ?)";

                try (PreparedStatement statement =
                             connection.prepareStatement(hapusTransaksi)) {

                    statement.setInt(1, ID_VENDOR);
                    statement.setString(2, markerA);
                    statement.setString(3, markerB);

                    statement.executeUpdate();
                }

                String pulihkanStok =
                        "UPDATE perlengkapan SET stok = ? "
                        + "WHERE id_perlengkapan = ? "
                        + "AND id_vendor = ?";

                try (PreparedStatement statement =
                             connection.prepareStatement(pulihkanStok)) {

                    statement.setInt(1, STOK_UJI);
                    statement.setInt(2, idPerlengkapan);
                    statement.setInt(3, ID_VENDOR);

                    periksa(
                            statement.executeUpdate() == 1,
                            "Gagal memulihkan stok perlengkapan uji"
                    );
                }

                connection.commit();

            } catch (Exception | Error e) {

                try {
                    connection.rollback();
                } catch (Exception rollbackError) {
                    e.addSuppressed(rollbackError);
                }

                throw e;
            }
        }

        periksa(
                hitungTransaksiUji(markerA, markerB) == 0,
                "Transaksi uji masih tersimpan setelah cleanup"
        );

        periksa(
                perlengkapanDAO
                        .ambilPerlengkapanBerdasarkanId(idPerlengkapan)
                        .getStok() == STOK_UJI,
                "Stok belum kembali ke nilai awal setelah cleanup"
        );

        Kompor sementara = new Kompor();
        sementara.setIdPerlengkapan(idPerlengkapan);

        perlengkapanService.hapusDataPerlengkapan(
                sementara,
                ID_VENDOR
        );

        periksa(
                perlengkapanDAO
                        .ambilPerlengkapanBerdasarkanId(idPerlengkapan)
                        == null,
                "Perlengkapan sementara belum berhasil dihapus"
        );

        System.out.println(
                "LULUS: Transaksi uji dibersihkan, stok dipulihkan, "
                + "dan perlengkapan sementara dihapus"
        );
    }

    public static void main(String[] args) throws Exception {

        String markerDasar =
                "UJI-KONKURENSI-" + System.currentTimeMillis();

        String markerA = markerDasar + "-A";
        String markerB = markerDasar + "-B";

        Kompor perlengkapanUji =
                buatKomporUji(markerDasar);

        boolean perlengkapanTersimpan = false;

        ExecutorService executor =
                Executors.newFixedThreadPool(2);

        CountDownLatch siap = new CountDownLatch(2);
        CountDownLatch mulai = new CountDownLatch(1);

        Throwable kesalahanUtama = null;

        System.out.println(
                "=== PENGUJIAN KONSISTENSI STOK KONKUREN ==="
        );

        try {
=

            perlengkapanService.tambahDataPerlengkapan(
                    perlengkapanUji,
                    ID_VENDOR
            );

            perlengkapanTersimpan = true;

            int idPerlengkapan =
                    perlengkapanUji.getIdPerlengkapan();

            periksa(
                    idPerlengkapan > 0,
                    "ID perlengkapan sementara tidak terbentuk"
            );

            periksa(
                    perlengkapanDAO
                            .ambilPerlengkapanBerdasarkanId(idPerlengkapan)
                            .getStok() == STOK_UJI,
                    "Stok awal perlengkapan sementara bukan satu"
            );

            System.out.println(
                    "LULUS: Perlengkapan sementara dibuat dengan stok satu"
            );

            Future<HasilPemesanan> permintaanA =
                    executor.submit(
                            buatPermintaan(
                                    idPerlengkapan,
                                    markerA,
                                    siap,
                                    mulai
                            )
                    );

            Future<HasilPemesanan> permintaanB =
                    executor.submit(
                            buatPermintaan(
                                    idPerlengkapan,
                                    markerB,
                                    siap,
                                    mulai
                            )
                    );

            periksa(
                    siap.await(20, TimeUnit.SECONDS),
                    "Kedua thread tidak siap dalam batas waktu"
            );

            System.out.println(
                    "KEDUA PERMINTAAN SIAP: Melepaskan keduanya bersamaan"
            );

            mulai.countDown();

            HasilPemesanan hasilA =
                    permintaanA.get(45, TimeUnit.SECONDS);

            HasilPemesanan hasilB =
                    permintaanB.get(45, TimeUnit.SECONDS);


            int jumlahBerhasil =
                    (hasilA.berhasil ? 1 : 0)
                    + (hasilB.berhasil ? 1 : 0);

            int jumlahDitolak =
                    (hasilA.berhasil ? 0 : 1)
                    + (hasilB.berhasil ? 0 : 1);

            periksa(
                    jumlahBerhasil == 1,
                    "Harus tepat satu transaksi yang berhasil. "
                    + "Hasil A=" + hasilA.pesan
                    + ", hasil B=" + hasilB.pesan
            );

            periksa(
                    jumlahDitolak == 1,
                    "Harus tepat satu transaksi ditolak karena stok habis"
            );

            HasilPemesanan hasilGagal =
                    hasilA.berhasil ? hasilB : hasilA;

            HasilPemesanan hasilBerhasil =
                    hasilA.berhasil ? hasilA : hasilB;

            periksa(
                    hasilBerhasil.idTransaksi > 0,
                    "Transaksi berhasil tidak memiliki ID"
            );

            periksa(
                    hasilBerhasil.idDetail > 0,
                    "Detail transaksi berhasil tidak memiliki ID"
            );

            periksa(
                    hasilGagal.idTransaksi == 0,
                    "ID transaksi pada permintaan yang ditolak tidak dipulihkan"
            );

            periksa(
                    hasilGagal.idDetail == 0,
                    "ID detail pada permintaan yang ditolak tidak dipulihkan"
            );

            System.out.println(
                    "LULUS: Tepat satu permintaan berhasil dan satu ditolak"
            );

            System.out.println(
                    "LULUS: ID transaksi dan detail pada permintaan gagal dipulihkan"
            );


            int stokAkhir =
                    perlengkapanDAO
                            .ambilPerlengkapanBerdasarkanId(idPerlengkapan)
                            .getStok();

            periksa(
                    stokAkhir == 0,
                    "Stok akhir seharusnya nol, tetapi hasilnya "
                    + stokAkhir
            );

            System.out.println(
                    "LULUS: Stok akhir nol, tidak terjadi stok negatif"
            );

            periksa(
                    hitungTransaksiUji(markerA, markerB) == 1,
                    "Database seharusnya hanya memiliki satu "
                    + "header transaksi uji"
            );

            periksa(
                    hitungDetail(hasilBerhasil.idTransaksi) == 1,
                    "Transaksi yang berhasil seharusnya memiliki satu detail"
            );

            System.out.println(
                    "LULUS: Hanya satu header dan satu detail tersimpan"
            );

        } catch (Exception | Error e) {

            kesalahanUtama = e;
            throw e;

        } finally {

            mulai.countDown();

            executor.shutdown();

            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {

                executor.shutdownNow();

                if (!executor.awaitTermination(15, TimeUnit.SECONDS)) {

                    IllegalStateException e =
                            new IllegalStateException(
                                    "Thread pengujian belum berhenti. "
                                    + "Data uji tidak dibersihkan untuk "
                                    + "menghindari cleanup saat transaksi "
                                    + "masih berjalan."
                            );

                    if (kesalahanUtama != null) {
                        kesalahanUtama.addSuppressed(e);
                    } else {
                        throw e;
                    }

                    perlengkapanTersimpan = false;
                }
            }

            if (perlengkapanTersimpan && executor.isTerminated()) {

                try {

                    bersihkan(
                            perlengkapanUji.getIdPerlengkapan(),
                            markerA,
                            markerB
                    );

                } catch (Exception | Error e) {

                    if (kesalahanUtama != null) {
                        kesalahanUtama.addSuppressed(e);
                    } else {
                        throw e;
                    }
                }
            }
        }

        System.out.println();

        System.out.println(
                "SELURUH PENGUJIAN KONSISTENSI STOK KONKUREN BERHASIL"
        );
    }
}