package com.sistemmanajemenpenyewaanperlengkapanpendakian.model;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.exception.DataTidakLengkapException;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.exception.StokTidakCukupException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.IdentityHashMap;
import java.util.Map;

public class Transaksi implements DapatDicetak{

    // ini atribut
    private int idTransaksi;
    private LocalDate tanggalSewa;
    private LocalDate tanggalKembali;
    private LocalDate tanggalTransaksi;
    private int durasiSewa;
    private long totalHarga;
    private long denda;
    private String metodePembayaran;

    // ini buat asosiasi diagram
    private Pelanggan pelanggan;
    private List<DetailTransaksi> daftarDetail;

    // penambahan, ini untuk menyimpan hasil penyusunan nota untuk ditampilkan oleh GUI
    private String isiNota;
    private String ringkasanTransaksi;

    // ini penambahan, ini buat pencegahan stok dikurangi berulang
    private boolean stokSudahDikurangi;

    // Ini konstruktor buat transaksi baru
    public Transaksi(Pelanggan pelanggan, List <DetailTransaksi> daftarDetail, LocalDate tanggalTransaksi, LocalDate tanggalSewa, int durasiSewa, String metodePembayaran){
        this.pelanggan = pelanggan;
        this.daftarDetail = (daftarDetail != null) ? daftarDetail:new ArrayList<>();
        this.tanggalTransaksi = tanggalTransaksi;
        this.tanggalSewa = tanggalSewa;
        this.durasiSewa = durasiSewa;
        this.metodePembayaran = metodePembayaran;
        
        this.stokSudahDikurangi = false;// ini penambahan
    }

    // ini konstruktor buat ngerepresentasiin transaksi yang udah kesimpen
    public Transaksi(int idTransaksi, Pelanggan pelanggan, List <DetailTransaksi> daftarDetail, LocalDate tanggalTransaksi, LocalDate tanggalSewa, int durasiSewa, String metodePembayaran, LocalDate tanggalKembali, long totalHarga, long denda){
        this(pelanggan, daftarDetail, tanggalTransaksi, tanggalSewa, durasiSewa, metodePembayaran);

        this.idTransaksi = idTransaksi;
        this.tanggalKembali = tanggalKembali;
        this.totalHarga = totalHarga;
        this.denda = denda;


        // ini penambahan
        this.stokSudahDikurangi = true;
    } 

    //#region ini setter getter
    // ini setter getter sesuai yang di laporan(diagram)
    public int getIdTransaksi(){
        return idTransaksi;
    }

    public LocalDate getTanggalSewa(){
        return tanggalSewa;
    }

    public void setTanggalSewa(LocalDate tanggalSewa){
        this.tanggalSewa = tanggalSewa;
    }

    public int getDurasiSewa(){
        return durasiSewa;
    }

    public void setDurasiSewa(int durasiSewa){
        this.durasiSewa = durasiSewa;
    }

    public LocalDate getTanggalKembali(){
        return tanggalKembali;
    }

    public long getTotalHarga(){
        return totalHarga;
    }

    public long getDenda(){
        return denda;
    }

    public String getMetodePembayaran(){
        return metodePembayaran;
    }

    public void setMetodePembayaran(String metodePembayaran){
        this.metodePembayaran = metodePembayaran;
    }

    // ini setter getter tambahan
    public LocalDate getTanggalTransaksi(){
        return tanggalTransaksi;
    }

    public Pelanggan getPelanggan(){
        return pelanggan;
    }

    public List<DetailTransaksi> getDaftarDetail(){
        return daftarDetail;
    }

    // tambahan, getter hasil penyusunan nota untuk dibaca oleh controller
    public String getIsiNota(){
        return isiNota;
    }

    // tambahan, getter hasil penyusunan ringkasan untuk dibaca oleh controller
    public String getRingkasanTransaksi(){
        return ringkasanTransaksi;
    }
    //#endregion

    // Method perhitungan yang sesuai diagram(laporan)
    /*
    - Total harganya dihitung dari subtotal seluruh detail
    - perhitungan menggunakan tarif masing masing barang, durasi penyewaan, dan kuantitas
    */

    public long hitungTotalHarga(){
        if (durasiSewa <=0){
            throw new IllegalStateException("Durasi penyewaan harus lebih dari 0");
        }

        if (daftarDetail == null || daftarDetail.isEmpty()){
            throw new IllegalStateException("Detail transaksi belum tersedia");
        }

        long total = 0;
        for (DetailTransaksi detail : daftarDetail){
            if (detail == null){
                throw new IllegalStateException("Terdapat detail transaksi yang kosong");
            }
            if (detail.getPerlengkapanYangDisewa() == null){
                throw new IllegalStateException("Perlengkapan pada detail transaksi belum ditentukan");
            }
            if (detail.getKuantitasPerAlat() <= 0){
                throw new IllegalStateException("Kuantitas perlengkapan harus lebih dari 0");
            }

            total = Math.addExact(total, detail.getSubTotal(durasiSewa));
        }
        this.totalHarga = total;
        return total;
    }
    // Tanggal kembali = tanggal sewa + durasi sewa
    public LocalDate hitungTanggalKembali(){
        if (tanggalSewa == null){
            throw new IllegalStateException("Tanggal sewa belum ditentukan");
        }
        if (durasiSewa <= 0){
            throw new IllegalStateException("Durasi sewa harus lebih dari 0");
        }
        return tanggalSewa.plusDays(durasiSewa);
    }

    /*
    - Denda dihitung untuk setiap DetailTransaksi
    - Hari terlambat x tarif sewa harian barang x kuantitas
    - Denda transaksi adalah jumlah denda seluruh detail
    */
   public long hitungDenda(){
    if (tanggalKembali == null){
        this.tanggalKembali = hitungTanggalKembali();
    }
    if (daftarDetail == null || daftarDetail.isEmpty()){
        this.denda = 0;
        return 0;
    }
    long totalDenda = 0;
    for (DetailTransaksi detail : daftarDetail){
        if (detail == null){
            throw new IllegalStateException("Terdapat detail transaksi yang kosong");
        }
        Perlengkapan perlengkapan = detail.getPerlengkapanYangDisewa();
        if (perlengkapan == null){
            throw new IllegalStateException("Perlengkapan pada detail transaksi belum ditentukan");
        }
        int kuantitas = detail.getKuantitasPerAlat();
        if (kuantitas <= 0){
            throw new IllegalStateException("Kuantitas perlengkapan harus lebih dari 0");
        }
        LocalDate tanggalAktual = detail.getTanggalPengembalianBarang();
        
        // Barang yang belum memiliki tanggal pengembalian belum dihitung dendanya
        if (tanggalAktual == null){
            continue;
        }
        long hariTerlambat = Math.max(0, ChronoUnit.DAYS.between(tanggalKembali, tanggalAktual));
        long dendaDetail = Math.multiplyExact(Math.multiplyExact(hariTerlambat, perlengkapan.getHargaSewaHarian()), kuantitas);
        totalDenda = Math.addExact(totalDenda, dendaDetail);
    }
    this.denda = totalDenda;
    return this.denda;
   }

   /*
   - Memvalidasi data transaksi dan stok, menghitung total dan tanggal kembali, lalu mengurangi stok setelah seluruh perlengkapan dinyatakan mencukupi
   */
  public void konfirmasiTransaksi() throws DataTidakLengkapException{
        if (pelanggan == null || isKosong(pelanggan.getNamaPelanggan()) || isKosong(pelanggan.getNoIdentitasPelanggan())){
            throw new DataTidakLengkapException("Data pelanggan wajib diisi lengkap");
        }
        if (daftarDetail == null || daftarDetail.isEmpty()){
            throw new DataTidakLengkapException("Perlengkapan yang disewa wajib dipilih");
        }
        if (tanggalTransaksi == null){
            throw new DataTidakLengkapException("Tanggal transaksi wajib diisi");
        }
        if (tanggalSewa == null){
            throw new DataTidakLengkapException("Tanggal sewa wajib diisi");
        }
        if (durasiSewa <= 0){
            throw new DataTidakLengkapException("Durasi sewa minimal 1 hari");
        }
        if (isKosong(metodePembayaran)){
            throw new DataTidakLengkapException("Metode pembayaran wajib diisi");
        }

        for (DetailTransaksi detail : daftarDetail){
            if (detail == null){
                throw new DataTidakLengkapException("Data transaksi tidak boleh kosong");
            }
            if (detail.getPerlengkapanYangDisewa() == null){
                throw new DataTidakLengkapException("Perlengkapan pada detail transaksi wajib dipilih");
            }
            if (detail.getKuantitasPerAlat() <= 0){
                throw new DataTidakLengkapException("Kuantitas perlengkapan harus lebih dari 0");
            }
        }
        this.tanggalKembali = hitungTanggalKembali();
        hitungTotalHarga();

        // Penambahan untuk mencegah pengurangan stok berulang
        if (stokSudahDikurangi){
            return;
        }

        // Penambahan untuk menggabungkan kuantitas jikabarang yang sama muncul pada beberapa DetailTransaksi
        Map<Perlengkapan, Integer> jumlahPerPerlengkapan = new IdentityHashMap<>();
        for (DetailTransaksi detail : daftarDetail){
            Perlengkapan perlengkapan = detail.getPerlengkapanYangDisewa();
            int kuantitas = detail.getKuantitasPerAlat();

            jumlahPerPerlengkapan.merge(perlengkapan, kuantitas, (jumlahLama, jumlahBaru) -> Math.addExact(jumlahLama, jumlahBaru));
        }
        // Penambahan, memeriksa seluruh stok sebelum melakukan pengurangan
        for (Map.Entry<Perlengkapan, Integer> entry : jumlahPerPerlengkapan.entrySet()){
            Perlengkapan perlengkapan = entry.getKey();
            int jumlahDibutuhkan = entry.getValue();

            if (perlengkapan.getStok() < jumlahDibutuhkan){
                throw new StokTidakCukupException("Stok " + perlengkapan.getNamaPerlengkapan() +" Tidak mencukupi. Stok yang tersedia: " + perlengkapan.getStok() + ", Jumlah dibutuhkan: "+jumlahDibutuhkan);
            }
        }
        // Penambahan, mengurangi stok setelah seluruh pemeriksaan berhasil
        for (Map.Entry<Perlengkapan, Integer> entry : jumlahPerPerlengkapan.entrySet()){
            entry.getKey().kurangiStok(entry.getValue());
        }
        this.stokSudahDikurangi = true;
    }

    // Implementasi dapatDicetak
    @Override 
    public void cetakNota(){
        StringBuilder nota = new StringBuilder();

        // Nota transaksi awal hanya memuat biaya sewa yang dibayar saat pencatatan transaksi
        long biayaSewa = totalHarga;
        if (daftarDetail != null && !daftarDetail.isEmpty() && durasiSewa>0){
            biayaSewa = hitungTotalHarga();
        }

        nota.append("====================================\n");
        nota.append("       NOTA TRANSAKSI PENYEWAAN     \n");
        nota.append("====================================\n\n");

        nota.append("ID transaksi: ").append(idTransaksi).append("\n");
        nota.append("Tanggal transaksi: ").append(tanggalTransaksi).append("\n");
        nota.append("Nama Pelanggan: ").append(pelanggan != null ? pelanggan.getNamaPelanggan() : "-").append("\n");
        nota.append("Tanggal sewa: ").append(tanggalSewa).append("\n");
        nota.append("Durasi sewa: ").append(durasiSewa).append("\n");
        nota.append("Tanggal kembali: ").append(tanggalKembali).append("\n");

        nota.append("\nDETAIL PERLENGKAPAN\n");
        if (daftarDetail != null){
            for (DetailTransaksi detail : daftarDetail){
                if (detail == null || detail.getPerlengkapanYangDisewa() == null){
                    continue;
                }
            
                nota.append("Nama perlengkapan: ").append(detail.getPerlengkapanYangDisewa().getNamaPerlengkapan()).append("\n");
                nota.append("Kuantitas: ").append(detail.getKuantitasPerAlat()).append("\n");
                nota.append("Subtotal sewa: ").append(rupiah(detail.getSubTotal(durasiSewa))).append("\n");
            }
        }
        nota.append("--------------------------\n");
        nota.append("Total Biaya Sewa: ").append(rupiah(biayaSewa)).append("\n");
        nota.append("Metode pembayaran: ").append(metodePembayaran).append("\n");
        nota.append("Pembayaran biaya sewa dilakukan saat pencatatan transaksi dan sebelum mengambil barang\n");


        // penambahan, show tagihan denda dan ganti rugi ditampilkan terpisah saat pengembalian
        if (adaPengembalianTercatat()){
            long totalDenda = hitungDenda();
            long totalGantiRugi = hitungTotalGantiRugi();
            long totalTagihanPengembalian = Math.addExact(totalDenda, totalGantiRugi);

            nota.append("\nTAGIHAN SAAT PENGEMBALIAN\n");
            nota.append("Denda Keterlambatan: ").append(rupiah(totalDenda)).append("\n");
            nota.append("Biaya ganti rugi: ").append(rupiah(totalGantiRugi)).append("\n");
            nota.append("---------------------------\n");
            nota.append("Total Tagihan Pengembalian: ").append(rupiah(totalTagihanPengembalian)).append("\n");
        }
        nota.append("\n====================================\n");
        this.isiNota = nota.toString();
    }

    @Override 
    public void ringkasanCetak(){
        StringBuilder ringkasan = new StringBuilder();

        ringkasan.append("RINGKASAN TRANSAKSI\n============\n");

        ringkasan.append("Id transaksi: ").append(idTransaksi).append("\n");
        ringkasan.append("Nama pelanggan: ").append(pelanggan != null ? pelanggan.getNamaPelanggan(): "-").append("\n");
        ringkasan.append("Tanggal sewa: ").append(tanggalSewa).append("\n");
        ringkasan.append("Durasi sewa: ").append(durasiSewa).append("\n");
        ringkasan.append("Tanggal kembali: ").append(tanggalKembali).append("\n");
        ringkasan.append("Total biaya sewa: ").append(rupiah(totalHarga)).append("\n");
        ringkasan.append("Metode pembayaran: ").append(metodePembayaran).append("\n");
        ringkasan.append("Pembayaran biaya sewa dilakukan saat pencatatan transaksi dan sebelum mengambil barang\n");

        // penambahan, ringkasan biaya yang muncul pada proses pengembalian
        if (adaPengembalianTercatat()){
            long totalDenda = hitungDenda();
            long totalGantiRugi = hitungTotalGantiRugi();

            long totalTagihanPengembalian = Math.addExact(totalDenda, totalGantiRugi);

            ringkasan.append("Denda: ").append(rupiah(totalDenda)).append("\n");
            ringkasan.append("Ganti rugi: ").append(rupiah(totalGantiRugi)).append("\n");
            ringkasan.append("Total pengembalian: ").append(rupiah(totalTagihanPengembalian)).append("\n");
        }
        this.ringkasanTransaksi = ringkasan.toString();
    }

    // penambahan, menghitung seluruh biaya ganti rugi dari DetailTransaksi
    private long hitungTotalGantiRugi(){
        long total = 0;
        if (daftarDetail == null){
            return 0;
        }

        for (DetailTransaksi detail : daftarDetail){
            if (detail != null){
                total = Math.addExact(total, detail.getBiayaGantiRugi());
            }
        }
        return total;
    }

    // penambahan, memeriksa apakah sudah ada perlengkapan yang dikembalikan
    private boolean adaPengembalianTercatat(){
        if (daftarDetail == null){
            return false;
        }

        for (DetailTransaksi detail : daftarDetail){
            if (detail != null && detail.getTanggalPengembalianBarang() != null){
                return true;
            }
        }
        return false;
    }

    // penambahan, memeriksa string kosong
    private boolean isKosong(String nilai){
        return nilai == null || nilai.trim().isEmpty();
    }

    // penambahan, mengubah angka menjadi format rupiah
    private String rupiah(long nilai){
        return String.format(Locale.forLanguageTag("id-ID"), "Rp %,d", nilai);
    }
}
