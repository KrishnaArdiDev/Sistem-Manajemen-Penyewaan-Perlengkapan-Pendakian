package com.sistemmanajemenpenyewaanperlengkapanpendakian.Service;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Transaksi;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class NotaService {
    public static long hitungLamaSewa(LocalDate tglSewa, LocalDate tglKembali) {
        if (tglSewa == null || tglKembali == null)
            return 1;
        long hari = ChronoUnit.DAYS.between(tglSewa, tglKembali);
        return Math.max(hari, 1);
    }

    public static long hitungHargaPerHari(Transaksi t) {
        if (t == null || t.getJumlah() <= 0)
            return 0;
        long hari = hitungLamaSewa(t.getTglSewa(), t.getTglKembali());
        return t.getTotalHarga() / (t.getJumlah() * hari);
    }

    public static String formatRupiah(long nilai) {
        return "Rp " + String.format("%,d", nilai).replace(',', '.');
    }

    public static String buatRingkasanNota(Transaksi t) {
        if (t == null)
            return "";
        long hari = hitungLamaSewa(t.getTglSewa(), t.getTglKembali());
        return "=== NOTA TRANSAKSI TAPAK ===\n"
                + "Penyewa      : " + t.getNamaPenyewa() + "\n"
                + "Barang       : " + t.getNamaBarang() + " (" + t.getJumlah() + " unit)\n"
                + "Lama Sewa    : " + hari + " hari\n"
                + "Total Biaya  : " + formatRupiah(t.getTotalHarga()) + "\n"
                + "Status       : " + t.getStatus() + "\n"
                + "=============================";
    }
}
