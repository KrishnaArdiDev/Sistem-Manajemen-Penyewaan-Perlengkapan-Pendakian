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
            throw new DataTidakLengkapException("Derasi sewa minimal 1 hari");
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
                throw new StokTidakCukupException("Stok" + perlengkapan.getNamaPerlengkapan() +" Tidak mencukupi. Sot yang tersedia: " + perlengkapan.getStok() + ", Jumlah dibutuhkan: "+jumlahDibutuhkan);
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
        long totalGantiRugi = hitungTotalGantiRugi();

        // Total sewa dan denda ini dipertahankan sesuai dengan perhitungan sebelumnya
        // Ganti rugi ditampilkan terpisah karena laporan belum menentukan 
    }

}