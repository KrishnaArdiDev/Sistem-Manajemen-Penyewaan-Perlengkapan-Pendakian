package com.sistemmanajemenpenyewaanperlengkapanpendakian.Service;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Vendor;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.exception.LoginGagalException;


public class AuthService {
    // Penambahan, buat ngatur proses autentikasi vendor
    public Vendor login(String emailAtauUsername, String password) throws LoginGagalException{
        // Membersihkan sesi sebelumnya sebelum login baru
        TransaksiService.bersihkanVendorAktif();

        if (emailAtauUsername == null || emailAtauUsername.trim().isEmpty() || password == null || password.isEmpty()){
            throw new LoginGagalException("Email atau username dan password wajib diisi");
        }

        Vendor vendor = new Vendor();

        vendor.setEmailAtauUsername(emailAtauUsername.trim());
        vendor.setPwAdm(password);

        if (vendor.login()){
            TransaksiService.aturanVendorAktif(vendor.getIdVendor());
            return vendor;
        }

        throw new IllegalStateException("Terjadi kesalahan saat mengakses database. Login tidak bisa diproses");
    }

    // Membersihkan sesi vendor saat logout
    public void logout(){
        TransaksiService.bersihkanVendorAktif();
    }
}
