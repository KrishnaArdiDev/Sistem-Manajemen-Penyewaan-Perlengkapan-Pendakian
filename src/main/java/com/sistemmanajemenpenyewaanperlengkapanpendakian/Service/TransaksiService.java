package com.sistemmanajemenpenyewaanperlengkapanpendakian.Service;
 
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
 

import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Transaksi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TransaksiService {
<<<<<<< HEAD

    private static final List<Transaksi> daftar = new ArrayList<>();

    public static void tambah(Transaksi t) {
        daftar.add(0, t);
    }

    public static List<Transaksi> semua() {
=======
 
    public static class DataTransaksi {
        public final String namaBarang;
        public final int jumlah;
        public final String penyewa;
        public final String telepon;
        public final String tglSewa;
        public final String tglKembali;
        public final long total;
        public final String status;
 
        public DataTransaksi(String namaBarang, int jumlah, String penyewa, String telepon,
                             String tglSewa, String tglKembali, long total, String status) {
            this.namaBarang = namaBarang;
            this.jumlah = jumlah;
            this.penyewa = penyewa;
            this.telepon = telepon;
            this.tglSewa = tglSewa;
            this.tglKembali = tglKembali;
            this.total = total;
            this.status = status;
        }
    }
 
    private static final List<DataTransaksi> daftar = new ArrayList<>();
 
    
    public static void tambah(DataTransaksi transaksi) {
        daftar.add(0, transaksi);
    }
 
    public static List<DataTransaksi> semua() {
>>>>>>> 20632ef9d7692802d7a4f0ea7f21982267612e17
        return Collections.unmodifiableList(daftar);
    }
}
