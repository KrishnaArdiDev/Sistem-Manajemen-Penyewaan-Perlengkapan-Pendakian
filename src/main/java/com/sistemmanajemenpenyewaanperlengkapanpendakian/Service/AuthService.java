package com.sistemmanajemenpenyewaanperlengkapanpendakian.Service;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Vendor;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.exception.LoginGagalException;


public class AuthService {
    // Penambahan, buat ngatur proses autentikasi vendor
    public Vendor login(String emailAtauUsername, String password) throws LoginGagalException{
        // penambahan, buat validasi input login
        if (emailAtauUsername == null || emailAtauUsername.trim().isEmpty() || password == null || password.isEmpty()){
            throw new LoginGagalException("Email atau username dan password wajib diisi");
        }

        // penambahan, membuat objek vendor untuk proses login
        Vendor vendor = new Vendor();

        vendor.setEmailAtauUsername(emailAtauUsername.trim());
        vendor.setPwAdm(password);

        // Penambahan, menjalankan proses login lewat model vendor 
        if (vendor.login()){
            return vendor;
        }

        // Penambahan, buat menangani kegagalan akses database
        throw new IllegalStateException("Terjadi kesalahan saat mengakses database. Login tidak bisa diproses");
    }
}
