import com.sistemmanajemenpenyewaanperlengkapanpendakian.dao.PerlengkapanDAO;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Kompor;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Perlengkapan;

import java.util.List;

public class UjiPerlengkapanDAO {

    private static void periksa(boolean kondisi, String pesan) {
        if (!kondisi) {
            throw new AssertionError(pesan);
        }
    }

    public static void main(String[] args) throws Exception {

        PerlengkapanDAO dao = new PerlengkapanDAO();

        int idVendorUji = 1;
        int idPerlengkapanUji = 0;
        boolean berhasilDihapus = false;

        System.out.println("=== PENGUJIAN PERLENGKAPANDao ===");

        Kompor perlengkapanBaru = new Kompor();

        perlengkapanBaru.setNamaPerlengkapan("UJI Perlengkapan DAO");
        perlengkapanBaru.setStok(5);
        perlengkapanBaru.setHargaPerlengkapan(100000);
        perlengkapanBaru.setHargaSewaHarian(20000);
        perlengkapanBaru.setTipeKompor("2 sumbu");
        perlengkapanBaru.setFotoBarang(null);

        try {

            dao.tambahDataPerlengkapan(perlengkapanBaru, idVendorUji);

            idPerlengkapanUji = perlengkapanBaru.getIdPerlengkapan();

            periksa(idPerlengkapanUji > 0,
                    "ID perlengkapan gagal dibuat");

            System.out.println("LULUS: Menambahkan perlengkapan");

            Perlengkapan hasilID =
                    dao.ambilPerlengkapanBerdasarkanId(idPerlengkapanUji);

            periksa(hasilID != null,
                    "Perlengkapan berdasarkan ID tidak ditemukan");

            periksa(hasilID instanceof Kompor,
                    "Jenis perlengkapan tidak sesuai");

            periksa(
                    hasilID.getNamaPerlengkapan().equals("UJI Perlengkapan DAO"),
                    "Nama perlengkapan tidak sesuai");

            System.out.println("LULUS: Membaca perlengkapan berdasarkan ID");

            List<Perlengkapan> seluruhPerlengkapan =
                    dao.ambilSemuaPerlengkapan();

            boolean ditemukanSemua = false;

            for (Perlengkapan p : seluruhPerlengkapan) {
                if (p.getIdPerlengkapan() == idPerlengkapanUji) {
                    ditemukanSemua = true;
                    break;
                }
            }

            periksa(ditemukanSemua,
                    "Perlengkapan tidak ditemukan dalam seluruh data");

            System.out.println("LULUS: Membaca seluruh perlengkapan");

            List<Perlengkapan> perlengkapanVendor =
                    dao.ambilPerlengkapanVendor(idVendorUji);

            boolean ditemukanVendor = false;

            for (Perlengkapan p : perlengkapanVendor) {
                if (p.getIdPerlengkapan() == idPerlengkapanUji) {
                    ditemukanVendor = true;
                    break;
                }
            }

            periksa(ditemukanVendor,
                    "Perlengkapan tidak ditemukan berdasarkan vendor");

            System.out.println("LULUS: Membaca perlengkapan berdasarkan vendor");

            perlengkapanBaru.setNamaPerlengkapan(
                    "UJI Perlengkapan DAO UPDATE");

            perlengkapanBaru.setStok(9);
            perlengkapanBaru.setHargaPerlengkapan(125000);
            perlengkapanBaru.setHargaSewaHarian(25000);
            perlengkapanBaru.setTipeKompor("3 sumbu");
            perlengkapanBaru.setFotoBarang("uji-perlengkapan-update.png");

            dao.updateDataPerlengkapan(perlengkapanBaru, idVendorUji);

            Perlengkapan hasilUpdate =
                    dao.ambilPerlengkapanBerdasarkanId(idPerlengkapanUji);

            periksa(hasilUpdate != null,
                    "Data perlengkapan tidak ditemukan setelah update");

            periksa(
                    hasilUpdate.getNamaPerlengkapan()
                            .equals("UJI Perlengkapan DAO UPDATE"),
                    "Nama gagal diperbarui");

            periksa(hasilUpdate.getStok() == 9,
                    "Stok gagal diperbarui");

            periksa(hasilUpdate.getHargaPerlengkapan() == 125000,
                    "Harga perlengkapan gagal diperbarui");

            periksa(hasilUpdate.getHargaSewaHarian() == 25000,
                    "Harga sewa gagal diperbarui");

            periksa(hasilUpdate instanceof Kompor
                    && ((Kompor) hasilUpdate).getTipeKompor().equals("3 sumbu"),
                    "Tipe kompor gagal diperbarui");

            periksa(
                    "uji-perlengkapan-update.png".equals(
                            hasilUpdate.getFotoBarang()),
                    "Foto perlengkapan gagal diperbarui");

            System.out.println("LULUS: Memperbarui data perlengkapan");

            dao.updateStokPerlengkapan(idPerlengkapanUji, 13);

            Perlengkapan hasilStok =
                    dao.ambilPerlengkapanBerdasarkanId(idPerlengkapanUji);

            periksa(hasilStok != null && hasilStok.getStok() == 13,
                    "Update stok khusus gagal");

            System.out.println("LULUS: Memperbarui stok perlengkapan");

            dao.hapusDataPerlengkapan(perlengkapanBaru, idVendorUji);

            berhasilDihapus = true;

            Perlengkapan hasilHapus =
                    dao.ambilPerlengkapanBerdasarkanId(idPerlengkapanUji);

            periksa(hasilHapus == null,
                    "Perlengkapan seharusnya sudah terhapus");

            System.out.println("LULUS: Menghapus perlengkapan");

        } finally {

            if (idPerlengkapanUji > 0 && !berhasilDihapus) {

                Perlengkapan sisa =
                        dao.ambilPerlengkapanBerdasarkanId(idPerlengkapanUji);

                if (sisa != null) {
                    dao.hapusDataPerlengkapan(sisa, idVendorUji);
                }

                System.out.println("PEMBERSIHAN: Data sementara diperiksa");
            }
        }

        System.out.println("\nSELURUH PENGUJIAN PERLENGKAPANDao BERHASIL");
    }
}