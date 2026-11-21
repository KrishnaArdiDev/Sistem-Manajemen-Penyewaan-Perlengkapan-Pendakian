package com.sistemmanajemenpenyewaanperlengkapanpendakian.Service;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Transaksi;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.DetailTransaksi;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

public class NotaService {

    // Ini menghitung lamanya penyewaan berdasarkan pada tanggal sewa dan tanggal kembali
    public static long hitungLamaSewa(LocalDate tanggalSewa, LocalDate tanggalKembali) {
        if (tanggalSewa == null || tanggalKembali == null){
            return 1;
        }
        long hari = ChronoUnit.DAYS.between(tanggalSewa, tanggalKembali);
        return Math.max(hari, 1);
    }
    // Menghitung total seluruh kuantitas perlengkapan dalam transaksi
    private static long hitungTotalKuantitas(Transaksi transaksi){
        if (transaksi == null || transaksi.getDaftarDetail() == null){
            return 0;
        }
        long totalKuantitas = 0;
        for (DetailTransaksi detail : transaksi.getDaftarDetail()){
            if (detail != null && detail.getPerlengkapanYangDisewa() != null && detail.getKuantitasPerAlat() >0){
                totalKuantitas = Math.addExact(totalKuantitas, detail.getKuantitasPerAlat());
            }
        }
        return totalKuantitas;
    }

    // Hitung rata rata harga sewa per hari
    public static long hitungHargaPerHari(Transaksi transaksi) {
        if (transaksi == null){
            return 0;
        }

        long totalKuantitas = hitungTotalKuantitas(transaksi);
        if (totalKuantitas <=0){
            return 0;
        }
        long hari = transaksi.getDurasiSewa() >0 ? transaksi.getDurasiSewa() : hitungLamaSewa(transaksi.getTanggalSewa(), ambilTanggalKembali(transaksi));
        
        long pembagi = Math.multiplyExact(totalKuantitas, hari);
        return transaksi.getTotalHarga()/pembagi;
    }

    // Mengambil tanggal kembali, termasuk menghitungnya jika belum tersedia
    private static LocalDate ambilTanggalKembali(Transaksi transaksi){

        if (transaksi == null){
            return null;
        }
        if (transaksi.getTanggalKembali() !=  null){
            return  transaksi.getTanggalKembali();
        }

        if (transaksi.getTanggalSewa() != null && transaksi.getDurasiSewa() >0){
            return transaksi.hitungTanggalKembali();
        }
        return null;
    }

     // Mengubah nilai angka menjadi format Rupiah
    public static String formatRupiah(long nilai) {
        return String.format(Locale.forLanguageTag("id-ID"), "Rp %,d", nilai);
    }

    // Membuat ringkasan nota berdasarkan seluruh detail transaksi
    public static String buatRingkasanNota(Transaksi transaksi) {
        if (transaksi == null){
            return "";
        }
        StringBuilder ringkasan = new StringBuilder();
        long hari = transaksi.getDurasiSewa() >0 ? transaksi.getDurasiSewa() : hitungLamaSewa(transaksi.getTanggalSewa(), ambilTanggalKembali(transaksi));

        LocalDate tanggalKembali = ambilTanggalKembali(transaksi);

        long totalKuantitas = hitungTotalKuantitas(transaksi);
        ringkasan.append("=== NOTA TRANSAKSI TAPAK ===\n");
        ringkasan.append("ID Transaksi : ").append(transaksi.getIdTransaksi()).append("\n");
        ringkasan.append("Penyewa: ").append(transaksi.getPelanggan() != null ? nilaiAtauStrip(transaksi.getPelanggan().getNamaPelanggan()) : "-").append("\n");
        ringkasan.append("Tanggal sewa: ").append(transaksi.getTanggalSewa() != null ? transaksi.getTanggalSewa() : "-").append("\n");
        ringkasan.append("Tanggal kembali: ").append(tanggalKembali != null ? tanggalKembali : "-").append("\n");
        ringkasan.append("Lama sewa: ").append(hari).append(" hari\n");
        

        ringkasan.append("\nDETAIL PERLENGKAPAN\n");
        List<DetailTransaksi> daftarDetail = transaksi.getDaftarDetail();
        boolean adaDetailDitampilkan = false;

        if (daftarDetail != null){
            for (DetailTransaksi detail : daftarDetail){
                if (detail== null || detail.getPerlengkapanYangDisewa() == null || detail.getKuantitasPerAlat()<=0){
                    continue;
                }
                adaDetailDitampilkan = true;
                ringkasan.append("Barang: ").append(nilaiAtauStrip(detail.getPerlengkapanYangDisewa().getNamaPerlengkapan())).append("\n");
                ringkasan.append("Kuantitas: ").append(detail.getKuantitasPerAlat()).append(" unit\n");
                ringkasan.append("Subtotal: ").append(formatRupiah(detail.getSubTotal(hariAsInt(hari)))).append("\n");
                String status = detail.getStatusPengembalianBarang();
                ringkasan.append("Status pengembalian: ").append(status != null && !status.trim().isEmpty() ? status :"Belum dicatat").append("\n\n");
            }
        } 
        if (!adaDetailDitampilkan) {
            ringkasan.append("Tidak ada detail perlengkapan\n");
        }

        ringkasan.append("Total barang: ").append(totalKuantitas).append(" Unit\n");
        ringkasan.append("Total biaya: ").append(formatRupiah(transaksi.getTotalHarga())).append("\n");
        ringkasan.append("Pembayaran: ").append(nilaiAtauStrip(transaksi.getMetodePembayaran())).append("\n");
        if(adaPengembalianTercatat(transaksi)){
            long totalDenda = transaksi.hitungDenda();
            long totalGantiRugi = hitungTotalGantiRugi(transaksi);
            long totalTagihanPengembalian = Math.addExact(totalDenda, totalGantiRugi);

            ringkasan.append("\nTAGIHAN SAAT PENGEMBALIAN\n");
            ringkasan.append("Denda keterlambatan: ").append(formatRupiah(totalDenda)).append("\n");
            ringkasan.append("Biaya ganti rugi: ").append(formatRupiah(totalGantiRugi)).append("\n");
            ringkasan.append("--------------------------------\n");
            ringkasan.append("Total tagihan pengembalian: ").append(formatRupiah(totalTagihanPengembalian)).append("\n");
        }
        ringkasan.append("===========");
        return ringkasan.toString();
    }

    // Mengubah lama sewa ke int karena method getSubTotal menerima int
    private static int hariAsInt(long hari){
        if (hari <=0 || hari > Integer.MAX_VALUE){
            throw new IllegalArgumentException("Lama sewa tidak valid");
        }
        return (int) hari;
    }

    // Menghindari nilai null atau teks kosong pada ringkasan nota
    private static String nilaiAtauStrip(String nilai){
        return nilai == null || nilai.trim().isEmpty() ? "-" : nilai;
    }

// HELPER
    // Memeriksa apakah sudah ada barang yang dikembalikan
    private static boolean adaPengembalianTercatat(Transaksi transaksi) {
        if (transaksi == null || transaksi.getDaftarDetail() == null) {
            return false;
        }

        for (DetailTransaksi detail : transaksi.getDaftarDetail()) {
            if (detail != null && detail.getTanggalPengembalianBarang() != null) {
                return true;
            }
        }

        return false;
    }

    // Menghitung seluruh biaya ganti rugi dari detail transaksi
    private static long hitungTotalGantiRugi(Transaksi transaksi) {
        if (transaksi == null || transaksi.getDaftarDetail() == null) {
            return 0;
        }

        long total = 0;

        for (DetailTransaksi detail : transaksi.getDaftarDetail()) {
            if (detail != null) {
                total = Math.addExact(total, detail.getBiayaGantiRugi());
            }
        }

        return total;
    }
}
