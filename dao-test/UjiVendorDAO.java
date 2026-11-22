import com.sistemmanajemenpenyewaanperlengkapanpendakian.dao.VendorDAO;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Vendor;

import java.util.Objects;

public class UjiVendorDAO {

    private static void periksa(boolean kondisi, String pesan) {
        if (!kondisi) {
            throw new AssertionError(pesan);
        }
    }

    public static void main(String[] args) throws Exception {

        VendorDAO dao = new VendorDAO();

        System.out.println("=== PENGUJIAN VENDORDao ===");

        Vendor vendor1 = dao.login("vendor_santai", "Admin123");

        periksa(vendor1 != null && vendor1.getIdVendor() == 1,
                "Login username gagal");

        System.out.println("LULUS: Login menggunakan username");

        Vendor vendor2 = dao.login(
                "vendorSantai@gmail.com", "Admin123");

        periksa(vendor2 != null && vendor2.getIdVendor() == 1,
                "Login email gagal");

        System.out.println("LULUS: Login menggunakan email");

        Vendor vendor3 = dao.login("vendor_rumah", "321Admin");

        periksa(vendor3 != null && vendor3.getIdVendor() == 2,
                "Login vendor kedua gagal");

        System.out.println("LULUS: Login vendor kedua");

        Vendor vendor4 = dao.login("vendor_santai", "PasswordSalah");

        periksa(vendor4 == null,
                "Password salah seharusnya ditolak");

        System.out.println("LULUS: Penolakan password salah");

        Vendor dataAsli = dao.login("vendor_santai", "Admin123");

        periksa(dataAsli != null,
                "Data vendor untuk pengujian tidak ditemukan");

        Vendor perubahan = new Vendor();

        perubahan.setIdVendor(dataAsli.getIdVendor());
        perubahan.setNamaVendor("Uji VendorDAO");
        perubahan.setEmail("uji-vendordao@example.test");
        perubahan.setLokasiToko("Lokasi Uji VendorDAO");
        perubahan.setFotoProfile("uji-vendordao.png");

        try {

            dao.updateProfileVendor(perubahan);

            Vendor hasil = dao.login("vendor_santai", "Admin123");

            periksa(hasil != null, "Data vendor tidak ditemukan setelah update");

            periksa(Objects.equals(
                    hasil.getNamaVendor(), perubahan.getNamaVendor()),
                    "Nama vendor gagal diperbarui");

            periksa(Objects.equals(
                    hasil.getEmail(), perubahan.getEmail()),
                    "Email vendor gagal diperbarui");

            periksa(Objects.equals(
                    hasil.getLokasiToko(), perubahan.getLokasiToko()),
                    "Lokasi vendor gagal diperbarui");

            periksa(Objects.equals(
                    hasil.getFotoProfile(), perubahan.getFotoProfile()),
                    "Foto profil gagal diperbarui");

            System.out.println("LULUS: Update seluruh data profil vendor");

        } finally {

            Vendor pemulihan = new Vendor();

            pemulihan.setIdVendor(dataAsli.getIdVendor());
            pemulihan.setNamaVendor(dataAsli.getNamaVendor());
            pemulihan.setEmail(dataAsli.getEmail());
            pemulihan.setLokasiToko(dataAsli.getLokasiToko());
            pemulihan.setFotoProfile(dataAsli.getFotoProfile());

            dao.updateProfileVendor(pemulihan);

            Vendor hasilPemulihan = dao.login("vendor_santai", "Admin123");

            periksa(hasilPemulihan != null,
                    "Pemulihan profil gagal");

            periksa(Objects.equals(
                    hasilPemulihan.getNamaVendor(), dataAsli.getNamaVendor()),
                    "Nama vendor tidak berhasil dipulihkan");

            periksa(Objects.equals(
                    hasilPemulihan.getEmail(), dataAsli.getEmail()),
                    "Email vendor tidak berhasil dipulihkan");

            periksa(Objects.equals(
                    hasilPemulihan.getLokasiToko(), dataAsli.getLokasiToko()),
                    "Lokasi vendor tidak berhasil dipulihkan");

            periksa(Objects.equals(
                    hasilPemulihan.getFotoProfile(), dataAsli.getFotoProfile()),
                    "Foto profil tidak berhasil dipulihkan");

            System.out.println("LULUS: Pemulihan data profil vendor");
        }

        System.out.println("\nSELURUH PENGUJIAN VENDORDao BERHASIL");
    }
}