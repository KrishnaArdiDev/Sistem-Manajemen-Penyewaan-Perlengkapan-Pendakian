import com.sistemmanajemenpenyewaanperlengkapanpendakian.DatabaseConnection;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.dao.DetailTransaksiDAO;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.dao.TransaksiDAO;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Pelanggan;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Perlengkapan;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Kompor;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Tas;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Sepatu;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.DetailTransaksi;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Transaksi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UjiDetailTransaksiDAO {

    private static void periksa(boolean kondisi, String pesan) {
        if (!kondisi) {
            throw new AssertionError(pesan);
        }
    }

    private static DetailTransaksi buatDetail(
            Perlengkapan perlengkapan, long hargaHistoris) {

        DetailTransaksi detail = new DetailTransaksi();

        detail.setPerlengkapanYangDisewa(perlengkapan);
        detail.setKuantitasPerAlat(1);
        detail.setHargaSewaHarianSaatTransaksi(hargaHistoris);

        return detail;
    }

    public static void main(String[] args) throws Exception {

        DetailTransaksiDAO detailDAO = new DetailTransaksiDAO();
        TransaksiDAO transaksiDAO = new TransaksiDAO();

        int idVendor = 1;
        int idTransaksiUji = 0;

        System.out.println("=== PENGUJIAN DETAILTRANSAKSIDAO ===");

        Pelanggan pelanggan = new Pelanggan();

        pelanggan.setIdPelanggan(1);
        pelanggan.setNamaPelanggan("Budi");
        pelanggan.setNoIdentitasPelanggan("510000000");

        Kompor kompor = new Kompor();

        kompor.setIdPerlengkapan(1);
        kompor.setNamaPerlengkapan("Kompor Portable");
        kompor.setStok(5);
        kompor.setHargaPerlengkapan(50000);
        kompor.setHargaSewaHarian(30000);
        kompor.setTipeKompor("2 sumbu");

        Tas tas = new Tas();

        tas.setIdPerlengkapan(2);
        tas.setNamaPerlengkapan("Carrier 60L");
        tas.setStok(3);
        tas.setHargaPerlengkapan(90000);
        tas.setHargaSewaHarian(50000);
        tas.setTipeTas("Carrier");

        Sepatu sepatu = new Sepatu();

        sepatu.setIdPerlengkapan(3);
        sepatu.setNamaPerlengkapan("Sepatu Gunung 42");
        sepatu.setStok(4);
        sepatu.setHargaPerlengkapan(70000);
        sepatu.setHargaSewaHarian(40000);
        sepatu.setUkuranSepatu(42);

        DetailTransaksi detailKompor = buatDetail(kompor, 30000L);
        DetailTransaksi detailTas = buatDetail(tas, 50000L);
        DetailTransaksi detailSepatu = buatDetail(sepatu, 40000L);

        List<DetailTransaksi> daftarDetail = new ArrayList<>();

        daftarDetail.add(detailKompor);
        daftarDetail.add(detailTas);
        daftarDetail.add(detailSepatu);

        Transaksi transaksi = new Transaksi(
                pelanggan,
                daftarDetail,
                LocalDate.of(2026, 11, 20),
                LocalDate.of(2026, 11, 21),
                2,
                "Tunai"
        );

        try {

            transaksiDAO.tambahDataTransaksi(transaksi, idVendor);

            idTransaksiUji = transaksi.getIdTransaksi();

            periksa(idTransaksiUji > 0,
                    "Header transaksi gagal dibuat");

            detailDAO.tambahDetailTransaksi(
                    detailKompor, idTransaksiUji);

            periksa(detailKompor.getIdDetailTransaksi() > 0,
                    "ID detail Kompor gagal dibuat");

            detailDAO.tambahDetailTransaksi(
                    detailTas, idTransaksiUji);

            periksa(detailTas.getIdDetailTransaksi() > 0,
                    "ID detail Tas gagal dibuat");

            detailDAO.tambahDetailTransaksi(
                    detailSepatu, idTransaksiUji);

            periksa(detailSepatu.getIdDetailTransaksi() > 0,
                    "ID detail Sepatu gagal dibuat");

            System.out.println("LULUS: Menambahkan tiga detail transaksi");

            List<DetailTransaksi> hasilBaca =
                    detailDAO.ambilDetailBerdasarkanTransaksi(idTransaksiUji);

            periksa(hasilBaca.size() == 3,
                    "Jumlah detail transaksi tidak sesuai");

            System.out.println("LULUS: Membaca detail berdasarkan transaksi");

            boolean komporDitemukan = false;
            boolean tasDitemukan = false;
            boolean sepatuDitemukan = false;

            for (DetailTransaksi detail : hasilBaca) {

                Perlengkapan perlengkapan =
                        detail.getPerlengkapanYangDisewa();

                if (perlengkapan instanceof Kompor) {
                    komporDitemukan = true;

                    periksa(
                            ((Kompor) perlengkapan).getTipeKompor()
                                    .equals("2 sumbu"),
                            "Tipe Kompor tidak sesuai"
                    );

                    periksa(
                            Long.valueOf(30000L).equals(
                                    detail.getHargaSewaHarianSaatTransaksi()),
                            "Harga historis Kompor tidak sesuai"
                    );

                } else if (perlengkapan instanceof Tas) {
                    tasDitemukan = true;

                    periksa(
                            ((Tas) perlengkapan).getTipeTas()
                                    .equals("Carrier"),
                            "Tipe Tas tidak sesuai"
                    );

                    periksa(
                            Long.valueOf(50000L).equals(
                                    detail.getHargaSewaHarianSaatTransaksi()),
                            "Harga historis Tas tidak sesuai"
                    );

                } else if (perlengkapan instanceof Sepatu) {
                    sepatuDitemukan = true;

                    periksa(
                            ((Sepatu) perlengkapan).getUkuranSepatu() == 42,
                            "Ukuran Sepatu tidak sesuai"
                    );

                    periksa(
                            Long.valueOf(40000L).equals(
                                    detail.getHargaSewaHarianSaatTransaksi()),
                            "Harga historis Sepatu tidak sesuai"
                    );
                }
            }

            periksa(komporDitemukan, "Objek Kompor tidak berhasil dipetakan");
            periksa(tasDitemukan, "Objek Tas tidak berhasil dipetakan");
            periksa(sepatuDitemukan, "Objek Sepatu tidak berhasil dipetakan");

            System.out.println("LULUS: Pemetaan Kompor, Tas, dan Sepatu");
            System.out.println("LULUS: Pembacaan harga sewa historis");

            LocalDate tanggalPengembalian =
                    LocalDate.of(2026, 11, 24);

            detailKompor.setTanggalPengembalianBarang(
                    tanggalPengembalian);

            detailKompor.setStatusPengembalianBarang("rusak");

            detailKompor.setBiayaGantiRugi(50000);

            detailDAO.updateDetailTransaksi(detailKompor);

            List<DetailTransaksi> hasilUpdate =
                    detailDAO.ambilDetailBerdasarkanTransaksi(idTransaksiUji);

            DetailTransaksi detailDiperbarui = null;

            for (DetailTransaksi detail : hasilUpdate) {
                if (detail.getIdDetailTransaksi()
                        == detailKompor.getIdDetailTransaksi()) {

                    detailDiperbarui = detail;
                    break;
                }
            }

            periksa(detailDiperbarui != null,
                    "Detail Kompor tidak ditemukan setelah update");

            periksa(
                    tanggalPengembalian.equals(
                            detailDiperbarui.getTanggalPengembalianBarang()),
                    "Tanggal pengembalian gagal diperbarui"
            );

            periksa(
                    "rusak".equals(
                            detailDiperbarui.getStatusPengembalianBarang()),
                    "Status pengembalian gagal diperbarui"
            );

            periksa(
                    detailDiperbarui.getBiayaGantiRugi() == 50000,
                    "Biaya ganti rugi gagal diperbarui"
            );

            System.out.println("LULUS: Memperbarui detail transaksi");
            System.out.println("LULUS: Membaca kembali hasil UPDATE");

        } finally {

            if (idTransaksiUji > 0) {

                String sql =
                        "DELETE FROM transaksi "
                        + "WHERE id_transaksi = ? AND id_vendor = ?";

                try (Connection connection =
                             DatabaseConnection.getConnection();

                     PreparedStatement statement =
                             connection.prepareStatement(sql)) {

                    statement.setInt(1, idTransaksiUji);
                    statement.setInt(2, idVendor);

                    statement.executeUpdate();
                }

                String sqlPeriksa =
                        "SELECT COUNT(*) FROM detail_transaksi "
                        + "WHERE id_transaksi = ?";

                try (Connection connection =
                             DatabaseConnection.getConnection();

                     PreparedStatement statement =
                             connection.prepareStatement(sqlPeriksa)) {

                    statement.setInt(1, idTransaksiUji);

                    try (ResultSet result = statement.executeQuery()) {

                        if (result.next()) {
                            periksa(result.getInt(1) == 0,
                                    "Detail transaksi pengujian belum dibersihkan");
                        }
                    }
                }

                System.out.println("LULUS: Pembersihan data pengujian");
            }
        }

        System.out.println(
                "\nSELURUH PENGUJIAN DETAILTRANSAKSIDao BERHASIL");
    }
}