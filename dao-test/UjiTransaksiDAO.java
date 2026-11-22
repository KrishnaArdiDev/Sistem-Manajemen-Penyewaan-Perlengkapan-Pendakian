import com.sistemmanajemenpenyewaanperlengkapanpendakian.DatabaseConnection;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.dao.TransaksiDAO;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Pelanggan;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Kompor;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.DetailTransaksi;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Transaksi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UjiTransaksiDAO {

    private static void periksa(boolean kondisi, String pesan) {
        if (!kondisi) {
            throw new AssertionError(pesan);
        }
    }

    public static void main(String[] args) throws Exception {

        TransaksiDAO dao = new TransaksiDAO();

        int idVendor = 1;
        int idTransaksiUji = 0;

        System.out.println("=== PENGUJIAN TRANSAKSIDao ===");

        Pelanggan pelanggan = new Pelanggan();
        pelanggan.setIdPelanggan(1);
        pelanggan.setNamaPelanggan("Budi");
        pelanggan.setNoIdentitasPelanggan("510000000");

        Kompor kompor = new Kompor();
        kompor.setIdPerlengkapan(1);
        kompor.setNamaPerlengkapan("Kompor Portable");
        kompor.setHargaSewaHarian(30000);
        kompor.setHargaPerlengkapan(50000);

        DetailTransaksi detail = new DetailTransaksi();
        detail.setPerlengkapanYangDisewa(kompor);
        detail.setKuantitasPerAlat(1);
        detail.setHargaSewaHarianSaatTransaksi(30000L);

        List<DetailTransaksi> daftarDetail = new ArrayList<>();
        daftarDetail.add(detail);

        LocalDate tanggalTransaksi = LocalDate.of(2026, 11, 20);
        LocalDate tanggalSewa = LocalDate.of(2026, 11, 21);

        Transaksi transaksi = new Transaksi(
                pelanggan,
                daftarDetail,
                tanggalTransaksi,
                tanggalSewa,
                2,
                "Tunai"
        );

        try {

            dao.tambahDataTransaksi(transaksi, idVendor);

            idTransaksiUji = transaksi.getIdTransaksi();

            periksa(idTransaksiUji > 0,
                    "ID transaksi gagal dibuat");

            System.out.println("LULUS: Menambahkan transaksi");

            periksa(transaksi.hitungTotalHarga() == 60000,
                    "Perhitungan total harga awal tidak sesuai");

            System.out.println("LULUS: Perhitungan total harga transaksi");

            List<Transaksi> daftarTransaksi =
                    dao.ambilSemuaTransaksiByVendor(idVendor);

            Transaksi hasilBaca = null;

            for (Transaksi t : daftarTransaksi) {
                if (t.getIdTransaksi() == idTransaksiUji) {
                    hasilBaca = t;
                    break;
                }
            }

            periksa(hasilBaca != null,
                    "Transaksi baru tidak ditemukan");

            periksa(
                    hasilBaca.getPelanggan().getIdPelanggan() == 1,
                    "Pelanggan transaksi tidak sesuai"
            );

            System.out.println("LULUS: Membaca transaksi berdasarkan vendor");

            List<Transaksi> daftarVendorLain =
                    dao.ambilSemuaTransaksiByVendor(2);

            boolean ditemukanVendorLain = false;

            for (Transaksi t : daftarVendorLain) {
                if (t.getIdTransaksi() == idTransaksiUji) {
                    ditemukanVendorLain = true;
                    break;
                }
            }

            periksa(!ditemukanVendorLain,
                    "Transaksi vendor pertama bocor ke vendor kedua");

            System.out.println("LULUS: Pembatasan pembacaan berdasarkan vendor");

            Transaksi perubahan = new Transaksi(
                    idTransaksiUji,
                    pelanggan,
                    daftarDetail,
                    tanggalTransaksi,
                    LocalDate.of(2026, 11, 22),
                    3,
                    "Transfer",
                    LocalDate.of(2026, 11, 25),
                    0,
                    0
            );

            dao.updateDataTransaksi(perubahan, idVendor);

            List<Transaksi> hasilDaftarUpdate =
                    dao.ambilSemuaTransaksiByVendor(idVendor);

            Transaksi hasilUpdate = null;

            for (Transaksi t : hasilDaftarUpdate) {
                if (t.getIdTransaksi() == idTransaksiUji) {
                    hasilUpdate = t;
                    break;
                }
            }

            periksa(hasilUpdate != null,
                    "Transaksi tidak ditemukan setelah update");

            periksa(
                    hasilUpdate.getDurasiSewa() == 3,
                    "Durasi sewa gagal diperbarui"
            );

            periksa(
                    hasilUpdate.getTanggalSewa().equals(
                            LocalDate.of(2026, 11, 22)),
                    "Tanggal sewa gagal diperbarui"
            );

            periksa(
                    hasilUpdate.getTanggalKembali().equals(
                            LocalDate.of(2026, 11, 25)),
                    "Tanggal kembali gagal diperbarui"
            );

            periksa(
                    hasilUpdate.getTotalHarga() == 90000,
                    "Total harga gagal diperbarui"
            );

            periksa(
                    hasilUpdate.getMetodePembayaran().equals("Transfer"),
                    "Metode pembayaran gagal diperbarui"
            );

            System.out.println("LULUS: Memperbarui transaksi");

            boolean ditolak = false;

            try {
                dao.updateDataTransaksi(perubahan, 2);
            } catch (java.sql.SQLException e) {
                ditolak = true;
            }

            periksa(ditolak,
                    "Vendor lain seharusnya tidak dapat memperbarui transaksi");

            System.out.println("LULUS: Penolakan update oleh vendor lain");

        } finally {

            if (idTransaksiUji > 0) {

                String sql =
                        "DELETE FROM transaksi "
                        + "WHERE id_transaksi = ? AND id_vendor = ?";

                try (Connection connection = DatabaseConnection.getConnection();
                     PreparedStatement statement =
                             connection.prepareStatement(sql)) {

                    statement.setInt(1, idTransaksiUji);
                    statement.setInt(2, idVendor);

                    statement.executeUpdate();
                }

                String sqlPeriksa =
                        "SELECT 1 FROM transaksi WHERE id_transaksi = ?";

                try (Connection connection = DatabaseConnection.getConnection();
                     PreparedStatement statement =
                             connection.prepareStatement(sqlPeriksa)) {

                    statement.setInt(1, idTransaksiUji);

                    try (ResultSet result = statement.executeQuery()) {
                        periksa(!result.next(),
                                "Transaksi pengujian belum berhasil dibersihkan");
                    }
                }

                System.out.println("LULUS: Pembersihan transaksi pengujian");
            }
        }

        System.out.println("\nSELURUH PENGUJIAN TRANSAKSIDao BERHASIL");
    }
}