package com.sistemmanajemenpenyewaanperlengkapanpendakian.Service;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.exception.DataTidakLengkapException;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Pelanggan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PelangganService {

    // PENAMBAHAN: cache list pelanggan untuk keperluan transaksi
    private static final List<Pelanggan> daftarPelanggan = new ArrayList<>();

    static {
        // Data awal pelanggan untuk demo transaksi
        Pelanggan p1 = new Pelanggan();
        p1.setIdPelanggan(1);
        p1.setNamaPelanggan("Budi");
        p1.setNoIdentitasPelanggan("5100000001");
        daftarPelanggan.add(p1);

        Pelanggan p2 = new Pelanggan();
        p2.setIdPelanggan(2);
        p2.setNamaPelanggan("Ica");
        p2.setNoIdentitasPelanggan("5100000002");
        daftarPelanggan.add(p2);
    }

    // PENAMBAHAN: Memvalidasi data pelanggan dan memproses untuk transaksi (Issue #86)
    public static Pelanggan prosesDataPelanggan(String nama, String noIdentitas) throws DataTidakLengkapException {
        if (nama == null || nama.trim().isEmpty()) {
            throw new DataTidakLengkapException("Nama pelanggan wajib diisi.");
        }
        if (noIdentitas == null || noIdentitas.trim().isEmpty()) {
            throw new DataTidakLengkapException("Nomor identitas pelanggan wajib diisi.");
        }

        String namaBersih = nama.trim();
        String noIdBersih = noIdentitas.trim();

        // Cari apakah pelanggan sudah pernah terdaftar
        for (Pelanggan p : daftarPelanggan) {
            if (p.getNoIdentitasPelanggan() != null && p.getNoIdentitasPelanggan().equalsIgnoreCase(noIdBersih)) {
                return p;
            }
        }

        // Jika belum ada, buat objek Pelanggan baru dan tambahkan
        Pelanggan pelangganBaru = new Pelanggan();
        pelangganBaru.setIdPelanggan(daftarPelanggan.size() + 1);
        pelangganBaru.setNamaPelanggan(namaBersih);
        pelangganBaru.setNoIdentitasPelanggan(noIdBersih);
        daftarPelanggan.add(pelangganBaru);

        return pelangganBaru;
    }

    // PENAMBAHAN: Mendapatkan seluruh daftar pelanggan
    public static List<Pelanggan> semua() {
        return Collections.unmodifiableList(daftarPelanggan);
    }

    // PENAMBAHAN: Mencari pelanggan berdasarkan nomor identitas
    public static Pelanggan cariByNoIdentitas(String noIdentitas) {
        if (noIdentitas == null || noIdentitas.trim().isEmpty()) {
            return null;
        }
        for (Pelanggan p : daftarPelanggan) {
            if (noIdentitas.trim().equalsIgnoreCase(p.getNoIdentitasPelanggan())) {
                return p;
            }
        }
        return null;
    }
}