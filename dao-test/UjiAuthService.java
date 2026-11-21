import com.sistemmanajemenpenyewaanperlengkapanpendakian.Service.AuthService;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.Service.TransaksiService;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.exception.LoginGagalException;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Transaksi;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Vendor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UjiAuthService {

    private static void periksa(boolean kondisi, String pesan) {
        if (!kondisi) {
            throw new AssertionError(pesan);
        }
    }

    // Memastikan sesi vendor aktif dan data transaksi
    // sesuai dengan ID vendor yang berhasil login.
    private static void periksaSesiAktif(int idVendor)
            throws Exception {

        List<Transaksi> melaluiSesi =
                TransaksiService.semua();

        List<Transaksi> melaluiId =
                TransaksiService.semua(idVendor);

        periksa(
                melaluiSesi.size() == melaluiId.size(),
                "Jumlah transaksi pada sesi tidak sesuai"
        );

        Set<Integer> idSesi = new HashSet<>();

        for (Transaksi transaksi : melaluiSesi) {
            idSesi.add(transaksi.getIdTransaksi());
        }

        for (Transaksi transaksi : melaluiId) {
            periksa(
                    idSesi.contains(transaksi.getIdTransaksi()),
                    "Data transaksi pada sesi tidak sesuai dengan vendor"
            );
        }
    }

    // Memastikan operasi yang membutuhkan sesi vendor
    // ditolak ketika belum login atau sudah logout.
    private static void periksaSesiTidakAktif() {

        boolean ditolak = false;

        try {

            TransaksiService.semua();

        } catch (IllegalArgumentException e) {

            ditolak = true;
        }

        periksa(
                ditolak,
                "Sesi vendor seharusnya sudah tidak aktif"
        );
    }

    public static void main(String[] args) throws Exception {

        AuthService authService = new AuthService();

        System.out.println("=== PENGUJIAN AUTHSERVICE ===");

        // ==========================================
        // 1. KONDISI AWAL SESI
        // ==========================================

        authService.logout();

        periksaSesiTidakAktif();

        System.out.println("LULUS: Kondisi awal tanpa sesi vendor");

        // ==========================================
        // 2. LOGIN VENDOR 1 MENGGUNAKAN USERNAME
        // ==========================================

        Vendor vendor1 = authService.login(
                "vendor_santai",
                "Admin123"
        );

        periksa(
                vendor1.getIdVendor() == 1,
                "ID Vendor 1 tidak sesuai"
        );

        periksa(
                "vendor_santai".equals(vendor1.getUsernameAdm()),
                "Username Vendor 1 tidak sesuai"
        );

        periksa(
                "vendorSantai@gmail.com".equals(vendor1.getEmail()),
                "Email Vendor 1 tidak sesuai"
        );

        periksa(
                "Vendor Summit Santai".equals(vendor1.getNamaVendor()),
                "Nama Vendor 1 tidak sesuai"
        );

        periksaSesiAktif(vendor1.getIdVendor());

        System.out.println("LULUS: Login Vendor 1 menggunakan username");
        System.out.println("LULUS: Data Vendor 1 berhasil dikembalikan");
        System.out.println("LULUS: Sesi Vendor 1 berhasil diaktifkan");

        // ==========================================
        // 3. LOGOUT VENDOR 1
        // ==========================================

        authService.logout();

        periksaSesiTidakAktif();

        System.out.println("LULUS: Logout Vendor 1 membersihkan sesi");

        // ==========================================
        // 4. LOGIN VENDOR 2 MENGGUNAKAN EMAIL
        // ==========================================

        Vendor vendor2 = authService.login(
                "vendorRumah@gmail.com",
                "321Admin"
        );

        periksa(
                vendor2.getIdVendor() == 2,
                "ID Vendor 2 tidak sesuai"
        );

        periksa(
                "vendor_rumah".equals(vendor2.getUsernameAdm()),
                "Username Vendor 2 tidak sesuai"
        );

        periksa(
                "vendorRumah@gmail.com".equals(vendor2.getEmail()),
                "Email Vendor 2 tidak sesuai"
        );

        periksa(
                "Vendor Siap Barang dari Rumah".equals(
                        vendor2.getNamaVendor()
                ),
                "Nama Vendor 2 tidak sesuai"
        );

        periksaSesiAktif(vendor2.getIdVendor());

        System.out.println("LULUS: Login Vendor 2 menggunakan email");
        System.out.println("LULUS: Data Vendor 2 berhasil dikembalikan");
        System.out.println("LULUS: Sesi Vendor 2 berhasil diaktifkan");

        // ==========================================
        // 5. PASSWORD SALAH
        // ==========================================

        boolean passwordDitolak = false;

        try {

            authService.login(
                    "vendor_santai",
                    "PasswordSalah"
            );

        } catch (LoginGagalException e) {

            passwordDitolak = true;
        }

        periksa(
                passwordDitolak,
                "Password salah seharusnya ditolak"
        );

        periksaSesiTidakAktif();

        System.out.println("LULUS: Login dengan password salah ditolak");
        System.out.println("LULUS: Sesi dibersihkan setelah login gagal");

        // ==========================================
        // 6. USERNAME TIDAK DITEMUKAN
        // ==========================================

        boolean akunTidakDitemukan = false;

        try {

            authService.login(
                    "akun_tidak_ada",
                    "Admin123"
            );

        } catch (LoginGagalException e) {

            akunTidakDitemukan = true;
        }

        periksa(
                akunTidakDitemukan,
                "Username tidak ditemukan seharusnya ditolak"
        );

        periksaSesiTidakAktif();

        System.out.println("LULUS: Username yang tidak terdaftar ditolak");

        // ==========================================
        // 7. USERNAME KOSONG
        // ==========================================

        boolean usernameKosongDitolak = false;

        try {

            authService.login(
                    "   ",
                    "Admin123"
            );

        } catch (LoginGagalException e) {

            usernameKosongDitolak = true;
        }

        periksa(
                usernameKosongDitolak,
                "Username kosong seharusnya ditolak"
        );

        periksaSesiTidakAktif();

        System.out.println("LULUS: Username kosong ditolak");

        // ==========================================
        // 8. PASSWORD KOSONG
        // ==========================================

        boolean passwordKosongDitolak = false;

        try {

            authService.login(
                    "vendor_santai",
                    ""
            );

        } catch (LoginGagalException e) {

            passwordKosongDitolak = true;
        }

        periksa(
                passwordKosongDitolak,
                "Password kosong seharusnya ditolak"
        );

        periksaSesiTidakAktif();

        System.out.println("LULUS: Password kosong ditolak");

        // ==========================================
        // 9. PASSWORD NULL
        // ==========================================

        boolean passwordNullDitolak = false;

        try {

            authService.login(
                    "vendor_santai",
                    null
            );

        } catch (LoginGagalException e) {

            passwordNullDitolak = true;
        }

        periksa(
                passwordNullDitolak,
                "Password null seharusnya ditolak"
        );

        periksaSesiTidakAktif();

        System.out.println("LULUS: Password null ditolak");

        // ==========================================
        // 10. KONDISI AKHIR
        // ==========================================

        authService.logout();

        periksaSesiTidakAktif();

        System.out.println("LULUS: Kondisi akhir tanpa sesi vendor");

        System.out.println();
        System.out.println("SELURUH PENGUJIAN AUTHSERVICE BERHASIL");
    }
}