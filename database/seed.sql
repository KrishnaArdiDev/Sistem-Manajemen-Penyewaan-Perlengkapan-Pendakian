USE `TAPAK-SistemManagement-PenyewaanPerlengkapanPendakian`;

-- Data vendor
INSERT INTO vendor (id_vendor, username_adm, pw_adm, email, nama_vendor, lokasi_toko, foto_profile)
VALUES 
    (1, 'vendor_santai', 'Admin123',
     'vendorSantai@gmail.com', 'Vendor Summit Santai',
     NULL, NULL),
    (2, 'vendor_rumah', '321Admin',
     'vendorRumah@gmail.com', 'Vendor Siap Barang dari Rumah',
     NULL, NULL);

-- Pelanggan

INSERT INTO pelanggan (
    id_pelanggan,
    nama_pelanggan,
    no_identitas
)
VALUES
    (1, 'Budi', '510000000'),

    (2, 'Ica', '510000000');


-- Perlengkapan
INSERT INTO perlengkapan (
    id_perlengkapan,
    id_vendor,
    nama_perlengkapan,
    jenis_perlengkapan,
    stok,
    harga_perlengkapan,
    harga_sewa_harian,
    tipe_kompor,
    tipe_tas,
    ukuran_sepatu, 
    foto_barang
)
VALUES
    (1, 1, 'Kompor Portable', 'kompor',
     5, 50000, 30000, '2 sumbu', NULL, NULL, NULL),

    (2, 1, 'Carrier 60L', 'tas',
     3, 90000, 50000, NULL, 'Carrier', NULL, NULL),

    (3, 1, 'Sepatu Gunung 42', 'sepatu',
     4, 70000, 40000, NULL, NULL, 42, NULL),

    (4, 2, 'Kompor Outdoor', 'kompor',
     6, 65000, 35000, '1 sumbu', NULL, NULL, NULL);
        

-- Transaksi
INSERT INTO transaksi (
    id_transaksi,
    id_vendor,
    id_pelanggan,
    tanggal_transaksi,
    tanggal_sewa,
    durasi_sewa,
    tanggal_kembali,
    total_harga,
    denda,
    metode_pembayaran,
    foto_identitas
)
VALUES
    (1, 1, 1, '2026-10-01', '2026-10-05',
     3, '2026-10-08', 240000, 0, 'Transfer', NULL),
    (2, 1, 2, '2026-10-02', '2026-10-02',
     2, '2026-10-12', 140000, 0, 'Tunai', NULL);

-- Detail transaksi
INSERT INTO detail_transaksi (
    id_detail_transaksi,
    id_transaksi,
    id_perlengkapan,
    kuantitas_per_alat,
    tanggal_pengembalian_barang,
    status_pengembalian,
    biaya_ganti_rugi,
    harga_sewa_harian_saat_transaksi
)
VALUES
    (1, 1, 1, 1, '2026-10-08', 'utuh', 0, 30000),
    (2, 1, 2, 1, '2026-10-08', 'utuh', 0, 50000),
    (3, 2, 1, 1, '2026-10-12', 'utuh', 0, 30000),
    (4, 2, 3, 1, '2026-10-12', 'utuh', 0, 40000);