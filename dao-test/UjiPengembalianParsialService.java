import com.sistemmanajemenpenyewaanperlengkapanpendakian.DatabaseConnection;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.Service.PengembalianService;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.Service.TransaksiService;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.dao.PerlengkapanDAO;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UjiPengembalianParsialService {

    private static final int ID_VENDOR = 1;
    private static final int ID_PELANGGAN = 1;

    private static final PerlengkapanDAO perlengkapanDAO =
            new PerlengkapanDAO();

    private static void periksa(boolean kondisi, String pesan) {
        if (!kondisi) {
            throw new AssertionError(pesan);
        }
    }

    private static Perlengkapan pilih(
            List<Perlengkapan> daftar,
            boolean pilihKompor) {

        for (Perlengkapan perlengkapan : daftar) {

            if (perlengkapan.getStok() > 0
                    && (pilihKompor
                    ? perlengkapan instanceof Kompor
                    : perlengkapan instanceof Tas)) {

                return perlengkapan;
            }
        }

        return null;
    }

    private static int stok(int idPerlengkapan) throws Exception {

        return perlengkapanDAO
                .ambilPerlengkapanBerdasarkanId(idPerlengkapan)
                .getStok();
    }

    private static DetailTransaksi detailById(
            Transaksi transaksi,
            int idDetail) {

        for (DetailTransaksi detail : transaksi.getDaftarDetail()) {

            if (detail.getIdDetailTransaksi() == idDetail) {
                return detail;
            }
        }

        return null;
    }

    // Membersihkan transaksi sementara setelah semua barang
    // berhasil dikembalikan dan stok sudah kembali ke kondisi awal.
    private static void bersihkan(
            int idTransaksi,
            String marker,
            int idKompor,
            int stokKomporAwal,
            int idTas,
            int stokTasAwal) throws Exception {

        if (idTransaksi <= 0) {
            return;
        }

        Transaksi tersimpan =
                TransaksiService.cariById(ID_VENDOR, idTransaksi);

        if (tersimpan != null) {

            // Jika pengujian terhenti di tengah proses,
            // kembalikan detail yang belum selesai terlebih dahulu.
            for (DetailTransaksi detail : tersimpan.getDaftarDetail()) {

                if (detail.getTanggalPengembalianBarang() == null) {

                    PengembalianService.prosesPengembalian(
                            tersimpan,
                            detail,
                            tersimpan.getTanggalKembali(),
                            "utuh",
                            ID_VENDOR
                    );
                }
            }

            tersimpan =
                    TransaksiService.cariById(ID_VENDOR, idTransaksi);

            periksa(
                    PengembalianService.isSelesai(tersimpan),
                    "Semua detail harus dikembalikan sebelum pembersihan"
            );
        }

        periksa(
                stok(idKompor) == stokKomporAwal,
                "Stok kompor belum kembali ke nilai awal. "
                + "Transaksi sementara dipertahankan."
        );

        periksa(
                stok(idTas) == stokTasAwal,
                "Stok tas belum kembali ke nilai awal. "
                + "Transaksi sementara dipertahankan."
        );

        String sql =
                "DELETE FROM transaksi "
                + "WHERE id_transaksi = ? "
                + "AND id_vendor = ? "
                + "AND foto_identitas = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, idTransaksi);
            statement.setInt(2, ID_VENDOR);
            statement.setString(3, marker);

            statement.executeUpdate();
        }

        String sqlPeriksa =
                "SELECT COUNT(*) FROM transaksi "
                + "WHERE id_transaksi = ? "
                + "AND id_vendor = ? "
                + "AND foto_identitas = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sqlPeriksa)) {

            statement.setInt(1, idTransaksi);
            statement.setInt(2, ID_VENDOR);
            statement.setString(3, marker);

            try (ResultSet result = statement.executeQuery()) {

                result.next();

                periksa(
                        result.getInt(1) == 0,
                        "Transaksi uji belum berhasil dihapus"
                );
            }
        }

        System.out.println(
                "LULUS: Pembersihan transaksi dan pemulihan stok"
        );
    }

    public static void main(String[] args) throws Exception {

        List<Perlengkapan> daftar =
                perlengkapanDAO.ambilPerlengkapanVendor(ID_VENDOR);

        Perlengkapan kompor = pilih(daftar, true);
        Perlengkapan tas = pilih(daftar, false);

        periksa(
                kompor != null,
                "Tidak ditemukan Kompor Vendor 1 dengan stok tersedia"
        );

        periksa(
                tas != null,
                "Tidak ditemukan Tas Vendor 1 dengan stok tersedia"
        );

        periksa(
                kompor.getIdPerlengkapan() != tas.getIdPerlengkapan(),
                "Dua detail harus menggunakan perlengkapan berbeda"
        );

        int stokKomporAwal = stok(kompor.getIdPerlengkapan());
        int stokTasAwal = stok(tas.getIdPerlengkapan());

        String marker =
                "UJI-PARSIAL-" + System.currentTimeMillis();

        int idTransaksi = 0;
        Throwable kesalahanUtama = null;

        System.out.println(
                "=== PENGUJIAN PENGEMBALIAN PARSIAL ==="
        );

        System.out.println(
                "Kompor: " + kompor.getNamaPerlengkapan()
                + " | Stok awal: " + stokKomporAwal
        );

        System.out.println(
                "Tas: " + tas.getNamaPerlengkapan()
                + " | Stok awal: " + stokTasAwal
        );

        try {

            // ==========================================
            // 1. MEMBUAT TRANSAKSI DENGAN DUA DETAIL
            // ==========================================

            Pelanggan pelanggan = new Pelanggan();

            pelanggan.setIdPelanggan(ID_PELANGGAN);
            pelanggan.setNamaPelanggan("Budi");
            pelanggan.setNoIdentitasPelanggan("510000000");

            DetailTransaksi detailKompor = new DetailTransaksi();

            detailKompor.setPerlengkapanYangDisewa(kompor);
            detailKompor.setKuantitasPerAlat(1);

            DetailTransaksi detailTas = new DetailTransaksi();

            detailTas.setPerlengkapanYangDisewa(tas);
            detailTas.setKuantitasPerAlat(1);

            List<DetailTransaksi> daftarDetail =
                    new ArrayList<>();

            daftarDetail.add(detailKompor);
            daftarDetail.add(detailTas);

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

            TransaksiService.tambah(transaksi, ID_VENDOR);

            idTransaksi = transaksi.getIdTransaksi();

            periksa(
                    idTransaksi > 0,
                    "Transaksi uji gagal dibuat"
            );

            periksa(
                    detailKompor.getIdDetailTransaksi() > 0,
                    "Detail kompor gagal disimpan"
            );

            periksa(
                    detailTas.getIdDetailTransaksi() > 0,
                    "Detail tas gagal disimpan"
            );

            System.out.println(
                    "LULUS: Transaksi dengan dua detail berhasil dibuat"
            );

            // ==========================================
            // 2. MEMERIKSA PENGURANGAN KEDUA STOK
            // ==========================================

            periksa(
                    stok(kompor.getIdPerlengkapan())
                            == stokKomporAwal - 1,
                    "Stok kompor tidak berkurang"
            );

            periksa(
                    stok(tas.getIdPerlengkapan())
                            == stokTasAwal - 1,
                    "Stok tas tidak berkurang"
            );

            System.out.println(
                    "LULUS: Stok kedua perlengkapan berkurang"
            );

            // ==========================================
            // 3. MENGEMBALIKAN DETAIL PERTAMA
            // ==========================================

            LocalDate tanggalJatuhTempo =
                    transaksi.getTanggalKembali();

            LocalDate tanggalKembaliKompor =
                    tanggalJatuhTempo.plusDays(2);

            long tarifKompor =
                    detailKompor.getHargaSewaHarianSaatTransaksi();

            long tarifTas =
                    detailTas.getHargaSewaHarianSaatTransaksi();

            long dendaKompor =
                    2L * tarifKompor;

            PengembalianService.HasilPengembalian hasilPertama =
                    PengembalianService.prosesPengembalian(
                            transaksi,
                            detailKompor,
                            tanggalKembaliKompor,
                            "utuh",
                            ID_VENDOR
                    );

            periksa(
                    hasilPertama.getDenda() == dendaKompor,
                    "Denda pengembalian kompor tidak sesuai"
            );

            periksa(
                    hasilPertama.getGantiRugi() == 0,
                    "Ganti rugi kompor seharusnya nol"
            );

            periksa(
                    stok(kompor.getIdPerlengkapan()) == stokKomporAwal,
                    "Stok kompor tidak kembali setelah pengembalian"
            );

            periksa(
                    stok(tas.getIdPerlengkapan()) == stokTasAwal - 1,
                    "Stok tas seharusnya masih berkurang"
            );

            System.out.println(
                    "LULUS: Pengembalian detail pertama"
            );

            // ==========================================
            // 4. MEMERIKSA STATUS PENGEMBALIAN PARSIAL
            // ==========================================

            Transaksi setelahParsial =
                    TransaksiService.cariById(
                            ID_VENDOR,
                            idTransaksi
                    );

            periksa(
                    setelahParsial != null,
                    "Transaksi tidak ditemukan setelah pengembalian parsial"
            );

            DetailTransaksi komporTersimpan = detailById(
                    setelahParsial,
                    detailKompor.getIdDetailTransaksi()
            );

            DetailTransaksi tasTersimpan = detailById(
                    setelahParsial,
                    detailTas.getIdDetailTransaksi()
            );

            periksa(
                    komporTersimpan != null
                    && komporTersimpan.getTanggalPengembalianBarang() != null,
                    "Detail kompor seharusnya sudah dikembalikan"
            );

            periksa(
                    tasTersimpan != null
                    && tasTersimpan.getTanggalPengembalianBarang() == null,
                    "Detail tas seharusnya belum dikembalikan"
            );

            periksa(
                    !PengembalianService.isSelesai(setelahParsial),
                    "Transaksi seharusnya belum selesai"
            );

            periksa(
                    setelahParsial.getDenda() == dendaKompor,
                    "Denda header seharusnya hanya menghitung kompor"
            );

            System.out.println(
                    "LULUS: Satu detail sudah kembali dan satu detail masih aktif"
            );

            System.out.println(
                    "LULUS: Transaksi masih berstatus belum selesai"
            );

            System.out.println(
                    "LULUS: Denda sementara hanya menghitung detail yang kembali"
            );

            // ==========================================
            // 5. MENGEMBALIKAN DETAIL KEDUA
            // ==========================================

            LocalDate tanggalKembaliTas =
                    tanggalJatuhTempo.plusDays(3);

            long dendaTas =
                    3L * tarifTas;

            long dendaKumulatif =
                    dendaKompor + dendaTas;

            PengembalianService.HasilPengembalian hasilKedua =
                    PengembalianService.prosesPengembalian(
                            setelahParsial,
                            tasTersimpan,
                            tanggalKembaliTas,
                            "utuh",
                            ID_VENDOR
                    );

            periksa(
                    hasilKedua.getDenda() == dendaKumulatif,
                    "Denda kumulatif kedua detail tidak sesuai"
            );

            periksa(
                    hasilKedua.getGantiRugi() == 0,
                    "Ganti rugi total seharusnya nol"
            );

            // ==========================================
            // 6. MEMERIKSA TRANSAKSI SELESAI
            // ==========================================

            Transaksi selesai =
                    TransaksiService.cariById(
                            ID_VENDOR,
                            idTransaksi
                    );

            periksa(
                    PengembalianService.isSelesai(selesai),
                    "Transaksi seharusnya sudah selesai"
            );

            periksa(
                    selesai.getDenda() == dendaKumulatif,
                    "Denda akhir transaksi tidak sesuai"
            );

            periksa(
                    stok(kompor.getIdPerlengkapan()) == stokKomporAwal,
                    "Stok kompor tidak kembali ke kondisi awal"
            );

            periksa(
                    stok(tas.getIdPerlengkapan()) == stokTasAwal,
                    "Stok tas tidak kembali ke kondisi awal"
            );

            System.out.println(
                    "LULUS: Pengembalian detail kedua"
            );

            System.out.println(
                    "LULUS: Denda kumulatif seluruh detail"
            );

            System.out.println(
                    "LULUS: Transaksi berubah menjadi selesai"
            );

            System.out.println(
                    "LULUS: Stok kedua perlengkapan kembali ke kondisi awal"
            );

        } catch (Exception | Error e) {

            kesalahanUtama = e;
            throw e;

        } finally {

            try {

                bersihkan(
                        idTransaksi,
                        marker,
                        kompor.getIdPerlengkapan(),
                        stokKomporAwal,
                        tas.getIdPerlengkapan(),
                        stokTasAwal
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
                "SELURUH PENGUJIAN PENGEMBALIAN PARSIAL BERHASIL"
        );
    }
}