package com.sistemmanajemenpenyewaanperlengkapanpendakian.Service;
 
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
 

public class TransaksiService {
<<<<<<< HEAD

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
        return Collections.unmodifiableList(daftar);
    }
>>>>>>> staging
}
