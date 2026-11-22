package com.sistemmanajemenpenyewaanperlengkapanpendakian.Service;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Vendor;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.exception.LoginGagalException;

public class AuthService {
    public static volatile Vendor vendorAktif;
    public Vendor login(String emailAtauUsername, String password) throws LoginGagalException {
        vendorAktif = null;
        TransaksiService.bersihkanVendorAktif();

        if (emailAtauUsername == null || emailAtauUsername.trim().isEmpty() || password == null || password.isEmpty()) {
            throw new LoginGagalException("Email atau username dan password wajib diisi");
        }

        Vendor vendor = new Vendor();
        vendor.setEmailAtauUsername(emailAtauUsername.trim());
        vendor.setPwAdm(password);

        if (vendor.login()) {
            TransaksiService.aturanVendorAktif(vendor.getIdVendor());
            vendorAktif = vendor;
            return vendor;
        }
        throw new IllegalStateException("Terjadi kesalahan saat mengakses database. Login tidak bisa diproses");
    }

    public static Vendor getVendorAktif() {
        return vendorAktif;
    }

    public static void perbaruiVendorAktif(Vendor vendor) {
        if (vendor == null || vendor.getIdVendor() <= 0) {
            throw new IllegalArgumentException("Data vendor aktif tidak valid");
        }
        vendorAktif = vendor;
    }
    public static void logout() {
        vendorAktif = null;
        TransaksiService.bersihkanVendorAktif();
    }
}
