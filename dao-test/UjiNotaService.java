import com.sistemmanajemenpenyewaanperlengkapanpendakian.Service.NotaService;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.*;

import java.time.LocalDate;
import java.util.*;

public class UjiNotaService {

    static void cek(boolean kondisi, String pesan) {
        if (!kondisi) {
            throw new AssertionError(pesan);
        }
    }

    static void berisi(String sumber, String bagian, String pesan) {
        cek(
                sumber != null && sumber.contains(bagian),
                pesan + " | Tidak ditemukan: " + bagian
        );
    }

    static DetailTransaksi detail(
            Perlengkapan perlengkapan,
            int kuantitas,
            long hargaHistoris) {

        DetailTransaksi detail = new DetailTransaksi();

        detail.setPerlengkapanYangDisewa(perlengkapan);
        detail.setKuantitasPerAlat(kuantitas);
        detail.setHargaSewaHarianSaatTransaksi(hargaHistoris);

        return detail;
    }

    public static void main(String[] args) {

        System.out.println("=== PENGUJIAN NOTASERVICE ===");


        cek(
                NotaService.hitungLamaSewa(
                        LocalDate.of(2026, 11, 21),
                        LocalDate.of(2026, 11, 24)
                ) == 3,
                "Lama sewa tiga hari tidak sesuai"
        );

        cek(
                NotaService.hitungLamaSewa(
                        LocalDate.of(2026, 11, 21),
                        LocalDate.of(2026, 11, 21)
                ) == 1,
                "Lama sewa minimum satu hari tidak sesuai"
        );

        cek(
                NotaService.hitungLamaSewa(
                        LocalDate.of(2026, 11, 22),
                        LocalDate.of(2026, 11, 21)
                ) == 1,
                "Tanggal akhir sebelum awal tidak sesuai"
        );

        cek(
                NotaService.hitungLamaSewa(null, null) == 1,
                "Tanggal null seharusnya menghasilkan satu hari"
        );

        System.out.println(
                "LULUS: Perhitungan lama sewa dan batas minimum"
        );

        cek(
                NotaService.formatRupiah(1234567)
                        .equals("Rp 1.234.567"),
                "Format Rupiah Indonesia tidak sesuai"
        );

        cek(
                NotaService.formatRupiah(0).equals("Rp 0"),
                "Format Rupiah nol tidak sesuai"
        );

        System.out.println(
                "LULUS: Format mata uang Rupiah"
        );

        Kompor kompor = new Kompor();

        kompor.setNamaPerlengkapan("Kompor Uji");
        kompor.setHargaSewaHarian(80000);
        kompor.setHargaPerlengkapan(200000);
        kompor.setTipeKompor("2 sumbu");

        Tas tas = new Tas();

        tas.setNamaPerlengkapan("Carrier Uji");
        tas.setHargaSewaHarian(90000);
        tas.setHargaPerlengkapan(300000);
        tas.setTipeTas("Carrier");


        DetailTransaksi d1 = detail(kompor, 2, 50000);
        DetailTransaksi d2 = detail(tas, 1, 50000);

        Pelanggan pelanggan = new Pelanggan();

        pelanggan.setNamaPelanggan("Budi Uji");
        pelanggan.setNoIdentitasPelanggan("UJI-123");

        LocalDate tanggalSewa =
                LocalDate.of(2026, 11, 21);

        LocalDate tanggalKembali =
                LocalDate.of(2026, 11, 24);

        List<DetailTransaksi> detailAwal = new ArrayList<>();

        detailAwal.add(d1);
        detailAwal.add(d2);

        Transaksi transaksi = new Transaksi(
                101,
                pelanggan,
                detailAwal,
                LocalDate.of(2026, 11, 20),
                tanggalSewa,
                3,
                "Transfer",
                tanggalKembali,
                450000,
                0
        );

        cek(
                NotaService.hitungHargaPerHari(transaksi) == 50000,
                "Rata-rata harga sewa per unit per hari tidak sesuai"
        );

        String notaAwal =
                NotaService.buatRingkasanNota(transaksi);

        berisi(
                notaAwal,
                "ID Transaksi : 101",
                "ID transaksi"
        );

        berisi(
                notaAwal,
                "Penyewa: Budi Uji",
                "Nama penyewa"
        );

        berisi(
                notaAwal,
                "Tanggal sewa: 2026-11-21",
                "Tanggal sewa"
        );

        berisi(
                notaAwal,
                "Tanggal kembali: 2026-11-24",
                "Tanggal kembali"
        );

        berisi(
                notaAwal,
                "Lama sewa: 3 hari",
                "Durasi sewa"
        );

        berisi(
                notaAwal,
                "Barang: Kompor Uji",
                "Detail kompor"
        );

        berisi(
                notaAwal,
                "Subtotal: Rp 300.000",
                "Subtotal kompor berdasarkan tarif historis"
        );

        berisi(
                notaAwal,
                "Barang: Carrier Uji",
                "Detail carrier"
        );

        berisi(
                notaAwal,
                "Subtotal: Rp 150.000",
                "Subtotal carrier berdasarkan tarif historis"
        );

        berisi(
                notaAwal,
                "Total barang: 3 Unit",
                "Total kuantitas"
        );

        berisi(
                notaAwal,
                "Total biaya: Rp 450.000",
                "Total biaya"
        );

        berisi(
                notaAwal,
                "Pembayaran: Transfer",
                "Metode pembayaran"
        );

        berisi(
                notaAwal,
                "Status pengembalian: Belum dicatat",
                "Status awal pengembalian"
        );

        cek(
                !notaAwal.contains("TAGIHAN SAAT PENGEMBALIAN"),
                "Tagihan pengembalian tidak boleh muncul sebelum ada pengembalian"
        );

        System.out.println(
                "LULUS: Ringkasan transaksi multi-perlengkapan sebelum pengembalian"
        );

        System.out.println(
                "LULUS: Tarif historis digunakan pada subtotal nota"
        );

        System.out.println(
                "LULUS: Total kuantitas, biaya, dan metode pembayaran"
        );


        transaksi.cetakNota();

        berisi(
                transaksi.getIsiNota(),
                "Subtotal sewa: Rp 300.000",
                "Nota Model memuat subtotal historis"
        );

        berisi(
                transaksi.getIsiNota(),
                "Total Biaya Sewa: Rp 450.000",
                "Nota Model memuat total biaya"
        );

        transaksi.ringkasanCetak();

        berisi(
                transaksi.getRingkasanTransaksi(),
                "Total biaya sewa: Rp 450.000",
                "Ringkasan Model memuat total biaya"
        );

        System.out.println(
                "LULUS: Implementasi DapatDicetak pada Transaksi"
        );


        Kompor komporParsial = new Kompor();

        komporParsial.setNamaPerlengkapan("Kompor Parsial");
        komporParsial.setHargaSewaHarian(80000);
        komporParsial.setHargaPerlengkapan(200000);

        Tas tasParsial = new Tas();

        tasParsial.setNamaPerlengkapan("Tas Belum Kembali");
        tasParsial.setHargaSewaHarian(70000);
        tasParsial.setHargaPerlengkapan(300000);

        DetailTransaksi sudahKembali =
                detail(komporParsial, 1, 50000);

        sudahKembali.setTanggalPengembalianBarang(
                tanggalKembali.plusDays(2)
        );

        sudahKembali.setStatusPengembalianBarang("rusak");
        sudahKembali.setBiayaGantiRugi(90000);

        DetailTransaksi belumKembali =
                detail(tasParsial, 1, 30000);

        List<DetailTransaksi> detailParsial = new ArrayList<>();

        detailParsial.add(sudahKembali);
        detailParsial.add(belumKembali);

        Transaksi transaksiParsial = new Transaksi(
                102,
                pelanggan,
                detailParsial,
                LocalDate.of(2026, 11, 20),
                tanggalSewa,
                3,
                "Tunai",
                tanggalKembali,
                240000,
                0
        );

        String notaParsial =
                NotaService.buatRingkasanNota(transaksiParsial);

        berisi(
                notaParsial,
                "Status pengembalian: rusak",
                "Status detail yang sudah dikembalikan"
        );

        berisi(
                notaParsial,
                "Status pengembalian: Belum dicatat",
                "Status detail yang belum dikembalikan"
        );

        berisi(
                notaParsial,
                "Denda keterlambatan: Rp 100.000",
                "Denda detail yang sudah dikembalikan"
        );

        berisi(
                notaParsial,
                "Biaya ganti rugi: Rp 90.000",
                "Ganti rugi detail yang rusak"
        );

        berisi(
                notaParsial,
                "Total tagihan pengembalian: Rp 190.000",
                "Total tagihan pengembalian parsial"
        );

        cek(
                transaksiParsial.getDenda() == 100000,
                "Denda tidak tersinkron pada objek transaksi"
        );

        transaksiParsial.cetakNota();

        berisi(
                transaksiParsial.getIsiNota(),
                "Denda Keterlambatan: Rp 100.000",
                "Nota Model memuat denda"
        );

        berisi(
                transaksiParsial.getIsiNota(),
                "Biaya ganti rugi: Rp 90.000",
                "Nota Model memuat ganti rugi"
        );

        berisi(
                transaksiParsial.getIsiNota(),
                "Total Tagihan Pengembalian: Rp 190.000",
                "Nota Model memuat total tagihan pengembalian"
        );

        transaksiParsial.ringkasanCetak();

        berisi(
                transaksiParsial.getRingkasanTransaksi(),
                "Denda: Rp 100.000",
                "Ringkasan Model memuat denda"
        );

        berisi(
                transaksiParsial.getRingkasanTransaksi(),
                "Ganti rugi: Rp 90.000",
                "Ringkasan Model memuat ganti rugi"
        );

        berisi(
                transaksiParsial.getRingkasanTransaksi(),
                "Total pengembalian: Rp 190.000",
                "Ringkasan Model memuat total tagihan pengembalian"
        );

        System.out.println(
                "LULUS: Ringkasan transaksi dengan pengembalian parsial"
        );

        System.out.println(
                "LULUS: Perhitungan denda dan ganti rugi pada NotaService"
        );

        System.out.println(
                "LULUS: Nota dan ringkasan Model setelah pengembalian"
        );


        cek(
                NotaService.buatRingkasanNota(null).isEmpty(),
                "Transaksi null seharusnya menghasilkan ringkasan kosong"
        );

        cek(
                NotaService.hitungHargaPerHari(null) == 0,
                "Harga per hari transaksi null tidak sesuai"
        );

        Transaksi kosong = new Transaksi(
                103,
                pelanggan,
                new ArrayList<>(),
                LocalDate.of(2026, 11, 20),
                tanggalSewa,
                3,
                "Tunai",
                tanggalKembali,
                0,
                0
        );

        String notaKosong =
                NotaService.buatRingkasanNota(kosong);

        berisi(
                notaKosong,
                "Tidak ada detail perlengkapan",
                "Penanganan transaksi tanpa detail"
        );

        berisi(
                notaKosong,
                "Total barang: 0 Unit",
                "Kuantitas kosong"
        );

        cek(
                NotaService.hitungHargaPerHari(kosong) == 0,
                "Harga per hari tanpa detail tidak sesuai"
        );

        System.out.println(
                "LULUS: Penanganan transaksi null dan tanpa detail"
        );

        System.out.println();

        System.out.println(
                "SELURUH PENGUJIAN NOTASERVICE BERHASIL"
        );
    }
}