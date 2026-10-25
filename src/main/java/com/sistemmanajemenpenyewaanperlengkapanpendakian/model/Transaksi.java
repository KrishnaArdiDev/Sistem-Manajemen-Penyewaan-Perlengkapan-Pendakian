package com.sistemmanajemenpenyewaanperlengkapanpendakian.model;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.exception.DataTidakLengkapException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Transaksi  {

    // // ===== Atribut sesuai diagram class =====
    // private int idTransaksi;
    // private LocalDate tanggalSewa;
    // private int durasiSewa;
    // private LocalDate tanggalKembali;
    // private LocalDate tanggalTransaksi;
    // private long totalHarga;
    // private long denda;
    // private String metodePembayaran;

    // // ===== Asosiasi pada diagram =====
    // // Pelanggan -> Transaksi, dan Transaksi "Memiliki" DetailTransaksi
    // private Pelanggan pelanggan;
    // private List<DetailTransaksi> daftarDetail;

    // // Konstruktor untuk transaksi baru (belum dikonfirmasi/disimpan)
    // public Transaksi(Pelanggan pelanggan, List<DetailTransaksi> daftarDetail,
    //                  LocalDate tanggalTransaksi, LocalDate tanggalSewa,
    //                  int durasiSewa, String metodePembayaran) {
    //     this.pelanggan = pelanggan;
    //     this.daftarDetail = (daftarDetail != null) ? daftarDetail : new ArrayList<>();
    //     this.tanggalTransaksi = tanggalTransaksi;
    //     this.tanggalSewa = tanggalSewa;
    //     this.durasiSewa = durasiSewa;
    //     this.metodePembayaran = metodePembayaran;
    // }

    // // Konstruktor lengkap (dipakai DAO saat membaca dari database)
    // public Transaksi(int idTransaksi, Pelanggan pelanggan, List<DetailTransaksi> daftarDetail,
    //                  LocalDate tanggalTransaksi, LocalDate tanggalSewa, int durasiSewa,
    //                  LocalDate tanggalKembali, long totalHarga, long denda,
    //                  String metodePembayaran) {
    //     this(pelanggan, daftarDetail, tanggalTransaksi, tanggalSewa, durasiSewa, metodePembayaran);
    //     this.idTransaksi = idTransaksi;
    //     this.tanggalKembali = tanggalKembali;
    //     this.totalHarga = totalHarga;
    //     this.denda = denda;
    // }

    // // ===== Getter & Setter sesuai diagram =====
    // public int getIdTransaksi() { return idTransaksi; }

    // public LocalDate getTanggalSewa() { return tanggalSewa; }
    // public void setTanggalSewa(LocalDate tanggalSewa) { this.tanggalSewa = tanggalSewa; }

    // public int getDurasiSewa() { return durasiSewa; }
    // public void setDurasiSewa(int durasiSewa) { this.durasiSewa = durasiSewa; }

    // public LocalDate getTanggalKembali() { return tanggalKembali; }

    // public long getTotalHarga() { return totalHarga; }

    // public long getDenda() { return denda; }

    // public String getMetodePembayaran() { return metodePembayaran; }
    // public void setMetodePembayaran(String metodePembayaran) { this.metodePembayaran = metodePembayaran; }

    // // ===== Tambahan di luar diagram (dibutuhkan Service/DAO) =====
    // public LocalDate getTanggalTransaksi() { return tanggalTransaksi; }
    // public Pelanggan getPelanggan() { return pelanggan; }
    // public List<DetailTransaksi> getDaftarDetail() { return daftarDetail; }

    // // ===== Method perhitungan sesuai diagram =====

    // /**
    //  * Total harga = jumlah (kuantitas x hitungHargaSewa(durasiSewa)) seluruh detail.
    //  * hitungHargaSewa() bersifat polimorfik: implementasinya berbeda di Kompor, Tas, Sepatu.
    //  */
    // public long hitungTotalHarga() {
    //     long total = 0;
    //     for (DetailTransaksi detail : daftarDetail) {
    //         total += detail.getPerlengkapanYangDisewa().hitungHargaSewa(durasiSewa)
    //                 * detail.getKuantitasPerAlat();
    //     }
    //     this.totalHarga = total;
    //     return total;
    // }

    // /** Tanggal kembali seharusnya = tanggal sewa + durasi sewa. */
    // public LocalDate hitungTanggalKembali() {
    //     return tanggalSewa.plusDays(durasiSewa);
    // }

    // /**
    //  * Denda = jumlah hari terlambat x biaya sewa satu hari.
    //  * Hari terlambat dihitung dari tanggal pengembalian paling akhir pada detail
    //  * dibanding tanggalKembali. Tidak terlambat -> denda 0.
    //  */
    // public long hitungDenda() {
    //     if (tanggalSewa == null || durasiSewa <= 0) {
    //         this.denda = 0;
    //         return 0;
    //     }
    //     LocalDate batas = (tanggalKembali != null) ? tanggalKembali : hitungTanggalKembali();

    //     LocalDate terakhir = null;
    //     for (DetailTransaksi detail : daftarDetail) {
    //         LocalDate tgl = detail.getTanggalPengembalianBarang();
    //         if (tgl != null && (terakhir == null || tgl.isAfter(terakhir))) {
    //             terakhir = tgl;
    //         }
    //     }
    //     if (terakhir == null) {
    //         this.denda = 0;
    //         return 0;
    //     }
    //     long hariTerlambat = Math.max(0, ChronoUnit.DAYS.between(batas, terakhir));
    //     long biayaSewaSehari = hitungTotalHarga() / durasiSewa;
    //     this.denda = hariTerlambat * biayaSewaSehari;
    //     return denda;
    // }

    // /**
    //  * Memvalidasi kelengkapan data, lalu menghitung tanggal kembali dan total harga.
    //  * @throws DataTidakLengkapException jika data pelanggan/transaksi belum lengkap
    //  */
    // public void konfirmasiTransaksi() throws DataTidakLengkapException {
    //     if (pelanggan == null
    //             || isKosong(pelanggan.getNamaPelanggan())
    //             || isKosong(pelanggan.getNoIdentitasPelanggan())) {
    //         throw new DataTidakLengkapException("Data pelanggan wajib diisi lengkap.");
    //     }
    //     if (daftarDetail == null || daftarDetail.isEmpty()) {
    //         throw new DataTidakLengkapException("Perlengkapan yang disewa wajib dipilih.");
    //     }
    //     if (tanggalTransaksi == null) {
    //         throw new DataTidakLengkapException("Tanggal transaksi wajib diisi.");
    //     }
    //     if (tanggalSewa == null) {
    //         throw new DataTidakLengkapException("Tanggal sewa wajib diisi.");
    //     }
    //     if (durasiSewa <= 0) {
    //         throw new DataTidakLengkapException("Durasi sewa minimal 1 hari.");
    //     }
    //     if (isKosong(metodePembayaran)) {
    //         throw new DataTidakLengkapException("Metode pembayaran wajib diisi.");
    //     }
    //     this.tanggalKembali = hitungTanggalKembali();
    //     hitungTotalHarga();
    // }

    // // ===== Implementasi DapatDicetak =====
    // @Override
    // public void cetakNota() {
    //     System.out.println("========== NOTA PENYEWAAN ==========");
    //     System.out.println("No. Transaksi     : " + idTransaksi);
    //     System.out.println("Pelanggan         : " + pelanggan.getNamaPelanggan());
    //     System.out.println("No. Identitas     : " + pelanggan.getNoIdentitasPelanggan());
    //     System.out.println("Tanggal Transaksi : " + tanggalTransaksi);
    //     System.out.println("Tanggal Sewa      : " + tanggalSewa);
    //     System.out.println("Durasi            : " + durasiSewa + " hari");
    //     System.out.println("Tanggal Kembali   : " + tanggalKembali);
    //     System.out.println("Metode Pembayaran : " + metodePembayaran);
    //     System.out.println("------------------------------------");
    //     System.out.println("Total Harga       : " + rupiah(totalHarga));
    //     System.out.println("Denda             : " + rupiah(denda));
    //     System.out.println("TOTAL BAYAR       : " + rupiah(totalHarga + denda));
    //     System.out.println("====================================");
    // }

    // @Override
    // public void ringkasanCetak() {
    //     System.out.println("Transaksi #" + idTransaksi
    //             + " | " + pelanggan.getNamaPelanggan()
    //             + " | " + tanggalSewa + " s/d " + tanggalKembali
    //             + " | Total: " + rupiah(totalHarga + denda));
    // }

    // // ===== Helper =====
    // private boolean isKosong(String s) {
    //     return s == null || s.trim().isEmpty();
    // }

    // private String rupiah(long nilai) {
    //     return String.format(Locale.forLanguageTag("id-ID"), "Rp%,d", nilai);
    // }
}