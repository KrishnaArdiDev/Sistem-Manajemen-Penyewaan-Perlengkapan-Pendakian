package com.sistemmanajemenpenyewaanperlengkapanpendakian.Service;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.exception.DataTidakLengkapException;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.exception.StokTidakCukupException;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Transaksi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TransaksiService {

    private static final List<Transaksi> daftar = new ArrayList<>();

    public static void tambah(Transaksi t) {
        if (t == null) {
            throw new IllegalArgumentException("Transaksi tidak boleh kosong");
        }

        try {
            if (t.getDaftarDetail() != null && !t.getDaftarDetail().isEmpty()) {
                t.konfirmasiTransaksi();
            }
        } catch (DataTidakLengkapException e) {
            throw new IllegalArgumentException("Data transaksi belum lengkap: " + e.getMessage(), e);
        } catch (StokTidakCukupException e) {
            throw e; //
        }

        daftar.add(0, t);
    }

    public static List<Transaksi> semua() {
        return Collections.unmodifiableList(daftar);
    }

    public static Transaksi cariById(int idTransaksi) {
        for (Transaksi t : daftar) {
            if (t.getIdTransaksi() == idTransaksi) {
                return t;
            }
        }
        return null;
    }
}