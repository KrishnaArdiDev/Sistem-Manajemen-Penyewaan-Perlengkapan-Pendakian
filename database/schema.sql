-- ini adalah isi struktur dari dbnya

CREATE DATABASE IF NOT EXISTS `TAPAK-SistemManagement-PenyewaanPerlengkapanPendakian`
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE `TAPAK-SistemManagement-PenyewaanPerlengkapanPendakian`;

-- Tabel vendor
CREATE TABLE IF NOT EXISTS vendor(
    id_vendor INT NOT NULL AUTO_INCREMENT,
    username_adm VARCHAR(100) NOT NULL,
    pw_adm VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    nama_vendor VARCHAR(150) NOT NULL,
    
    PRIMARY KEY (id_vendor),
   --  ini penambahan, bahwa usn harus unik, biar ga sama kek vendor yg lain
   UNIQUE KEY uq_vendor_username(username_adm)
) ENGINE = InnoDB;

-- Tabel pelanggan
CREATE TABLE IF NOT EXISTS pelanggan(
   id_pelanggan INT NOT NULL AUTO_INCREMENT,
   nama_pelanggan VARCHAR(150) NOT NULL,
   no_identitas VARCHAR(50) NOT NULL,

   PRIMARY KEY (id_pelanggan)
)ENGINE = InnoDB;

-- Tabel perlengkapan
CREATE TABLE IF NOT EXISTS perlengkapan(
   id_perlengkapan INT NOT NULL AUTO_INCREMENT,
   id_vendor INT NOT NULL,
   nama_perlengkapan VARCHAR(150) NOT NULL,
   jenis_perlengkapan ENUM('kompor', 'tas', 'sepatu') NOT NULL,
   stok INT NOT NULL DEFAULT 0,
   harga_perlengkapan BIGINT NOT NULL,
   harga_sewa_harian BIGINT NOT NULL, 
   tipe_kompor VARCHAR(100) DEFAULT NULL,
   tipe_tas VARCHAR(100) DEFAULT NULL,
   ukuran_sepatu INT DEFAULT NULL,

   PRIMARY KEY(id_perlengkapan),

   CONSTRAINT fk_perlengkapan_vendor
      FOREIGN KEY (id_vendor)
      REFERENCES vendor(id_vendor)
      ON UPDATE CASCADE
      ON DELETE RESTRICT,

   -- ini penambahan, untuk validasi stok dan harga
   CONSTRAINT chk_perlengkapan_stok
      CHECK (stok >= 0),

   CONSTRAINT chk_perlengkapan_harga
      CHECK(
         harga_perlengkapan >=0 AND harga_sewa_harian >= 0
      )
)ENGINE = InnoDB;

-- Tabel transaksi
CREATE TABLE IF NOT EXISTS transaksi(
   id_transaksi INT NOT NULL AUTO_INCREMENT,
   id_vendor INT NOT NULL,
   id_pelanggan INT NOT NULL,
   tanggal_transaksi DATE NOT NULL,
   tanggal_sewa DATE NOT NULL,
   durasi_sewa INT NOT NULL,
   tanggal_kembali DATE NOT NULL,
   total_harga BIGINT NOT NULL,
   denda BIGINT NOT NULL DEFAULT 0,
   metode_pembayaran VARCHAR(100) NOT NULL,

   PRIMARY KEY(id_transaksi),

   CONSTRAINT fk_transaksi_vendor
      FOREIGN KEY (id_vendor)
      REFERENCES vendor(id_vendor)
      ON UPDATE CASCADE
      ON DELETE RESTRICT,
      
   CONSTRAINT fk_transaksi_pelanggan
      FOREIGN KEY (id_pelanggan)
      REFERENCES pelanggan(id_pelanggan)
      ON UPDATE CASCADE
      ON DELETE RESTRICT,

   -- penambahan validasi durasi dan biaya
   CONSTRAINT chk_transaksi_durasi
      CHECK(durasi_sewa >0),
   
   CONSTRAINT chk_transaksi_biaya
      CHECK(total_harga >=0 AND denda >=0
      )
)ENGINE = InnoDB;

-- Tabel detail_transaksi
CREATE TABLE IF NOT EXISTS detail_transaksi(
   id_detail_transaksi INT NOT NULL AUTO_INCREMENT,
   id_transaksi INT NOT NULL,
   id_perlengkapan INT NOT NULL,
   kuantitas_per_alat INT NOT NULL,
   tanggal_pengembalian_barang DATE DEFAULT NULL,
   status_pengembalian ENUM('utuh', 'rusak', 'hilang') DEFAULT NULL,
   biaya_ganti_rugi BIGINT NOT NULL DEFAULT 0,

   PRIMARY KEY(id_detail_transaksi),

   CONSTRAINT fk_detail_transaksi_transaksi
      FOREIGN KEY (id_transaksi)
      REFERENCES transaksi(id_transaksi)
      ON UPDATE CASCADE
      ON DELETE CASCADE,

   CONSTRAINT fk_detail_transaksi_perlengkapan
      FOREIGN KEY (id_perlengkapan)
      REFERENCES perlengkapan(id_perlengkapan)
      ON UPDATE CASCADE
      ON DELETE RESTRICT,

   -- Penambahan validasi kuantitas dan ganti rugi
   CONSTRAINT chk_detail_kuantitas
      CHECK(kuantitas_per_alat >0),

   CONSTRAINT chk_detail_ganti_rugi
      CHECK(biaya_ganti_rugi >=0)
)ENGINE = InnoDB;

-- SQL VIEW 

-- Vendor | Format ID: 1V, 2V, 3V, dan seterusnya......

CREATE OR REPLACE VIEW vw_vendor_adm AS
SELECT 
   CONCAT(id_vendor, 'V') AS id_vendor,
   username_adm,
   pw_adm,
   email,
   nama_vendor
FROM vendor;

-- Pelanggan | Format ID: 1P, 2P, 3P, dan seterusnya......
CREATE OR REPLACE VIEW vw_pelanggan_adm AS
SELECT
   CONCAT(id_pelanggan, 'P') AS id_pelanggan,
   nama_pelanggan,
   no_identitas
FROM pelanggan;

-- Perlengkapan | Format ID: 1Prl, 2Prl, 3Prl, dan seterusnya......
CREATE OR REPLACE VIEW vw_perlengkapan_adm AS
SELECT
   CONCAT(id_perlengkapan, 'Prl') AS id_perlengkapan,
   CONCAT(id_vendor, 'V') AS id_vendor,
   nama_perlengkapan,
   jenis_perlengkapan,
   stok,
   harga_perlengkapan,
   harga_sewa_harian,
   tipe_kompor,
   tipe_tas,
   ukuran_sepatu
FROM perlengkapan;

-- Transaksi | Format ID: 1T, 2T, 3T, dan seterusnya......
CREATE OR REPLACE VIEW vw_transaksi_adm AS
SELECT
   CONCAT(id_transaksi, 'T') AS id_transaksi,
   CONCAT(id_vendor, 'V') AS id_vendor,
   CONCAT(id_pelanggan, 'P') AS id_pelanggan,
   tanggal_transaksi,
   tanggal_sewa,
   durasi_sewa,
   tanggal_kembali,
   total_harga,
   denda,
   metode_pembayaran
FROM transaksi;

-- Detail transaksi | Format ID: 1Dt, 2Dt, 3Dt, dan seterusnya......
CREATE OR REPLACE VIEW vw_detail_transaksi_adm AS
SELECT
   CONCAT(id_detail_transaksi, 'Dt') AS id_detail_transaksi,
   CONCAT(id_transaksi, 'T') AS id_transaksi,
   CONCAT(id_perlengkapan, 'Prl') AS id_perlengkapan,
   kuantitas_per_alat,
   tanggal_pengembalian_barang,
   status_pengembalian,
   biaya_ganti_rugi
FROM detail_transaksi





/*
================================================================
                    PERINGATAN - WAJIB DIBACA
================================================================

FILE INI MERUPAKAN STRUKTUR DATABASE FINAL PROYEK TAPAK.

ATURAN WAJIB UNTUK SIAPA PUN YANG MEMBACA ATAU MENGEDIT FILE INI,
TERMASUK AI ASSISTANT:

1. DILARANG mengubah, menghapus, atau mengurangi struktur yang
   sudah ditetapkan dalam file ini.

2. DILARANG mengubah:
   - Nama tabel dan kolom.
   - Tipe data dan panjang kolom.
   - Primary Key dan Foreign Key.
   - Relasi antartabel.
   - Constraint.
   - AUTO_INCREMENT.
   - SQL VIEW dan format prefiks ID.

3. DILARANG mengubah schema hanya untuk menyesuaikan implementasi
   Java, DAO, Service, Controller, atau bagian lainnya.
   Seluruh implementasi harus mengikuti schema yang sudah ada.

4. PENAMBAHAN diperbolehkan apabila memang diperlukan, tetapi
   tidak boleh merusak, mengganti, atau mengurangi struktur
   yang sudah ditetapkan.

5. Jika ditemukan ketidaksesuaian atau kebutuhan perubahan,
   JANGAN langsung mengedit schema. Jelaskan masalahnya dan
   minta persetujuan pemilik proyek terlebih dahulu.

6. Sebelum memberikan saran perubahan, periksa terlebih dahulu
   struktur schema secara keseluruhan agar tidak terjadi
   perubahan yang bertentangan dengan rancangan.

PRINSIP UTAMA:
                    NGURANG GABOLEH, NAMBAH BOLEH.

ANGGAP FILE INI SEBAGAI SINGLE SOURCE OF TRUTH DATABASE TAPAK.
STRUKTUR INI HARUS DIPERTAHANKAN SELAMA PENGEMBANGAN PROYEK.

================================================================
                    AKHIR PERINGATAN
================================================================
*/