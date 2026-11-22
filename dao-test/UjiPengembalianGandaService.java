import com.sistemmanajemenpenyewaanperlengkapanpendakian.DatabaseConnection;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.Service.PengembalianService;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.Service.TransaksiService;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.dao.PerlengkapanDAO;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.exception.DataTidakLengkapException;
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

public class UjiPengembalianGandaService {

    private static final int ID_VENDOR = 1;
    private static final int ID_PELANGGAN = 1;

    private static final PerlengkapanDAO perlengkapanDAO =
            new PerlengkapanDAO();

    private static void periksa(boolean kondisi, String pesan) {
        if (!kondisi) {
            throw new AssertionError(pesan);
        }
    }


    private static Perlengkapan pilihPerlengkapan()
            throws Exception {

        List<Perlengkapan> daftar =
                perlengkapanDAO.ambilPerlengkapanVendor(ID_VENDOR);

        for (Perlengkapan p : daftar) {
            if (p != null && p.getStok() > 0) {
                return p;
            }
        }

        throw new IllegalStateException(
                "Vendor 1 tidak memiliki perlengkapan dengan stok tersedia"
        );
    }

    private static int ambilStok(int idPerlengkapan)
            throws Exception {

        Perlengkapan p =
                perlengkapanDAO.ambilPerlengkapanBerdasarkanId(
                        idPerlengkapan
                );

        if (p == null) {
            throw new IllegalStateException(
                    "Perlengkapan uji tidak ditemukan"
            );
        }

        return p.getStok();
    }

    private static DetailTransaksi cariDetail(
            Transaksi transaksi,
            int idDetail) {

        if (transaksi == null
                || transaksi.getDaftarDetail() == null) {
            return null;
        }

        for (DetailTransaksi d : transaksi.getDaftarDetail()) {
            if (d != null
                    && d.getIdDetailTransaksi() == idDetail) {
                return d;
            }
        }

        return null;
    }

    private static void bersihkan(
            int idTransaksi,
            String marker,
            int idPerlengkapan,
            int stokAwal) throws Exception {

        if (idTransaksi <= 0) {
            return;
        }

        Transaksi tersimpan =
                TransaksiService.cariById(
                        ID_VENDOR,
                        idTransaksi
                );

        if (tersimpan != null) {

            for (DetailTransaksi d : tersimpan.getDaftarDetail()) {

                if (d.getTanggalPengembalianBarang() == null) {

                    PengembalianService.prosesPengembalian(
                            tersimpan,
                            d,
                            tersimpan.getTanggalKembali(),
                            "utuh",
                            ID_VENDOR
                    );
                }
            }
        }

        periksa(
                ambilStok(idPerlengkapan) == stokAwal,
                "Stok belum kembali ke jumlah awal. "
                + "Transaksi uji dipertahankan."
        );

        String hapus =
                "DELETE FROM transaksi "
                + "WHERE id_transaksi = ? "
                + "AND id_vendor = ? "
                + "AND foto_identitas = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(hapus)) {

            statement.setInt(1, idTransaksi);
            statement.setInt(2, ID_VENDOR);
            statement.setString(3, marker);

            int terhapus = statement.executeUpdate();

            periksa(
                    terhapus == 1,
                    "Transaksi uji tidak berhasil dibersihkan"
            );
        }

        String hitung =
                "SELECT COUNT(*) FROM transaksi "
                + "WHERE id_transaksi = ? "
                + "AND id_vendor = ? "
                + "AND foto_identitas = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(hitung)) {

            statement.setInt(1, idTransaksi);
            statement.setInt(2, ID_VENDOR);
            statement.setString(3, marker);

            try (ResultSet result = statement.executeQuery()) {

                result.next();

                periksa(
                        result.getInt(1) == 0,
                        "Transaksi uji masih tersimpan"
                );
            }
        }

        System.out.println(
                "LULUS: Pembersihan transaksi uji dan pemulihan stok"
        );
    }

    public static void main(String[] args) throws Exception {

        Perlengkapan perlengkapan = pilihPerlengkapan();

        int idPerlengkapan =
                perlengkapan.getIdPerlengkapan();

        int stokAwal =
                ambilStok(idPerlengkapan);

        String marker =
                "UJI-PENGEMBALIAN-GANDA-"
                + System.currentTimeMillis();

        int idTransaksi = 0;
        int idDetail = 0;

        Throwable kesalahanUtama = null;

        System.out.println(
                "=== PENGUJIAN PENGEMBALIAN GANDA ==="
        );

        System.out.println(
                "Perlengkapan: "
                + perlengkapan.getNamaPerlengkapan()
        );

        System.out.println(
                "ID perlengkapan: " + idPerlengkapan
        );

        System.out.println(
                "Stok awal: " + stokAwal
        );

        try {

            Pelanggan pelanggan = new Pelanggan();

            pelanggan.setIdPelanggan(ID_PELANGGAN);
            pelanggan.setNamaPelanggan("Budi");
            pelanggan.setNoIdentitasPelanggan("510000000");

            DetailTransaksi detail = new DetailTransaksi();

            detail.setPerlengkapanYangDisewa(perlengkapan);
            detail.setKuantitasPerAlat(1);

            List<DetailTransaksi> daftarDetail =
                    new ArrayList<>();

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

            TransaksiService.tambah(
                    transaksi,
                    ID_VENDOR
            );

            idTransaksi = transaksi.getIdTransaksi();
            idDetail = detail.getIdDetailTransaksi();

            periksa(
                    idTransaksi > 0,
                    "Transaksi uji gagal dibuat"
            );

            periksa(
                    idDetail > 0,
                    "Detail transaksi uji gagal dibuat"
            );

            periksa(
                    ambilStok(idPerlengkapan) == stokAwal - 1,
                    "Stok tidak berkurang setelah penyewaan"
            );

            System.out.println(
                    "LULUS: Transaksi uji berhasil dibuat dan stok berkurang"
            );

            LocalDate tanggalJatuhTempo =
                    transaksi.getTanggalKembali();

            // Terlambat satu hari agar denda dapat diperiksa.
            LocalDate tanggalPertama =
                    tanggalJatuhTempo.plusDays(1);

            long tarifHistoris =
                    detail.getHargaSewaHarianSaatTransaksi();

            long dendaPertama = tarifHistoris;

            PengembalianService.HasilPengembalian hasilPertama =
                    PengembalianService.prosesPengembalian(
                            transaksi,
                            detail,
                            tanggalPertama,
                            "utuh",
                            ID_VENDOR
                    );

            periksa(
                    hasilPertama.getDenda() == dendaPertama,
                    "Denda pengembalian pertama tidak sesuai"
            );

            periksa(
                    hasilPertama.getGantiRugi() == 0,
                    "Ganti rugi pengembalian pertama seharusnya nol"
            );

            periksa(
                    ambilStok(idPerlengkapan) == stokAwal,
                    "Stok tidak kembali setelah pengembalian pertama"
            );

            System.out.println(
                    "LULUS: Pengembalian pertama berhasil"
            );

            Transaksi setelahPertama =
                    TransaksiService.cariById(
                            ID_VENDOR,
                            idTransaksi
                    );

            periksa(
                    setelahPertama != null,
                    "Transaksi tidak ditemukan setelah pengembalian pertama"
            );

            DetailTransaksi detailTersimpan =
                    cariDetail(setelahPertama, idDetail);

            periksa(
                    detailTersimpan != null,
                    "Detail tidak ditemukan setelah dibaca ulang"
            );

            periksa(
                    tanggalPertama.equals(
                            detailTersimpan.getTanggalPengembalianBarang()
                    ),
                    "Tanggal pertama tidak tersimpan"
            );

            periksa(
                    "utuh".equals(
                            detailTersimpan.getStatusPengembalianBarang()
                    ),
                    "Status pertama tidak tersimpan"
            );

            periksa(
                    detailTersimpan.getBiayaGantiRugi() == 0,
                    "Ganti rugi pertama tidak sesuai"
            );

            periksa(
                    setelahPertama.getDenda() == dendaPertama,
                    "Denda pertama tidak tersimpan"
            );

            periksa(
                    PengembalianService.isSelesai(setelahPertama),
                    "Transaksi satu detail seharusnya selesai"
            );

            System.out.println(
                    "LULUS: Tanggal, status, ganti rugi, dan denda pertama tersimpan"
            );

            int stokSebelumDuplikat =
                    ambilStok(idPerlengkapan);

            long dendaSebelumDuplikat =
                    setelahPertama.getDenda();

            LocalDate tanggalSebelumDuplikat =
                    detailTersimpan.getTanggalPengembalianBarang();

            String statusSebelumDuplikat =
                    detailTersimpan.getStatusPengembalianBarang();

            long gantiRugiSebelumDuplikat =
                    detailTersimpan.getBiayaGantiRugi();


            boolean pengembalianGandaDitolak = false;
            String pesanPenolakan = null;

            try {

                PengembalianService.prosesPengembalian(
                        setelahPertama,
                        detailTersimpan,
                        tanggalJatuhTempo.plusDays(5),
                        "rusak",
                        ID_VENDOR
                );

            } catch (DataTidakLengkapException e) {

                pengembalianGandaDitolak = true;
                pesanPenolakan = e.getMessage();
            }

            periksa(
                    pengembalianGandaDitolak,
                    "Pengembalian ganda seharusnya ditolak"
            );

            periksa(
                    "Barang ini sudah dikembalikan.".equals(pesanPenolakan),
                    "Alasan penolakan tidak sesuai: " + pesanPenolakan
            );

            System.out.println(
                    "LULUS: Pengembalian kedua pada detail yang sama ditolak"
            );

            Transaksi setelahPercobaanGanda =
                    TransaksiService.cariById(
                            ID_VENDOR,
                            idTransaksi
                    );

            periksa(
                    setelahPercobaanGanda != null,
                    "Transaksi hilang setelah percobaan ganda"
            );

            DetailTransaksi detailSetelahGanda =
                    cariDetail(
                            setelahPercobaanGanda,
                            idDetail
                    );

            periksa(
                    detailSetelahGanda != null,
                    "Detail hilang setelah percobaan ganda"
            );

            periksa(
                    tanggalSebelumDuplikat.equals(
                            detailSetelahGanda.getTanggalPengembalianBarang()
                    ),
                    "Tanggal pengembalian berubah akibat percobaan ganda"
            );

            periksa(
                    statusSebelumDuplikat.equals(
                            detailSetelahGanda.getStatusPengembalianBarang()
                    ),
                    "Status pengembalian berubah akibat percobaan ganda"
            );

            periksa(
                    gantiRugiSebelumDuplikat
                            == detailSetelahGanda.getBiayaGantiRugi(),
                    "Ganti rugi berubah akibat percobaan ganda"
            );

            periksa(
                    dendaSebelumDuplikat
                            == setelahPercobaanGanda.getDenda(),
                    "Denda berubah akibat percobaan ganda"
            );

            periksa(
                    stokSebelumDuplikat == ambilStok(idPerlengkapan),
                    "Stok berubah akibat percobaan ganda"
            );

            System.out.println(
                    "LULUS: Data tanggal, status, ganti rugi, dan denda tidak berubah"
            );

            System.out.println(
                    "LULUS: Stok tidak bertambah untuk kedua kalinya"
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
                "SELURUH PENGUJIAN PENGEMBALIAN GANDA BERHASIL"
        );
    }
}