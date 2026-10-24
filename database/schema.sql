
CREATE DATABASE IF NOT EXISTS sistem_penyewaan;
USE sistem_penyewaan;

CREATE TABLE IF NOT EXISTS vendor (
    id_vendor INT AUTO_INCREMENT PRIMARY KEY,
    nama_vendor VARCHAR(100) NOT NULL,
    username_adm VARCHAR(50),
    pw_adm VARCHAR(255),
    email VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS pelanggan (
    id_pelanggan INT AUTO_INCREMENT PRIMARY KEY,
    nama_pelanggan VARCHAR(100) NOT NULL,
    no_identitas VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS perlengkapan (
    id_perlengkapan INT AUTO_INCREMENT PRIMARY KEY,
    id_vendor INT NOT NULL,
    nama_perlengkapan VARCHAR(100) NOT NULL,
    jenis_perlengkapan ENUM('kompor', 'tas', 'sepatu') NOT NULL,
    stok INT NOT NULL DEFAULT 0,
    harga_perlengkapan BIGINT NOT NULL,
    harga_sewa_harian BIGINT NOT NULL,
    tipe_kompor VARCHAR(100),
    tipe_tas VARCHAR(100),
    ukuran_sepatu INT,
    FOREIGN KEY (id_vendor) REFERENCES vendor(id_vendor)
);

CREATE TABLE IF NOT EXISTS transaksi (
    id_transaksi INT AUTO_INCREMENT PRIMARY KEY,
    id_pelanggan INT NOT NULL,
    id_vendor INT NOT NULL,
    tanggal_sewa DATE NOT NULL,
    tanggal_kembali_rencana DATE NOT NULL,
    tanggal_kembali_aktual DATE,
    total_biaya DOUBLE NOT NULL DEFAULT 0,
    denda DOUBLE NOT NULL DEFAULT 0,
    ganti_rugi DOUBLE NOT NULL DEFAULT 0,
    status ENUM('BERLANGSUNG', 'SELESAI')
        NOT NULL DEFAULT 'BERLANGSUNG',
    FOREIGN KEY (id_pelanggan) REFERENCES pelanggan(id_pelanggan),
    FOREIGN KEY (id_vendor) REFERENCES vendor(id_vendor)
);

CREATE TABLE IF NOT EXISTS detail_transaksi (
    id_detail_transaksi INT AUTO_INCREMENT PRIMARY KEY,
    id_transaksi INT NOT NULL,
    id_perlengkapan INT NOT NULL,
    jumlah INT NOT NULL DEFAULT 1,
    harga_sewa BIGINT NOT NULL DEFAULT 0,
    FOREIGN KEY (id_transaksi) REFERENCES transaksi(id_transaksi),
    FOREIGN KEY (id_perlengkapan)
        REFERENCES perlengkapan(id_perlengkapan)
);
