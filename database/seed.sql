
USE sistem_penyewaan;

INSERT INTO vendor (
    nama_vendor, username_adm, pw_adm, email
) VALUES (
    'Vendor Perlengkapan Pendakian',
    'admin',
    '',
    'admin@example.com'
);

INSERT INTO pelanggan (nama_pelanggan, no_identitas) VALUES
('William', '1234567789'),
('Raditya', '1234567889');

INSERT INTO perlengkapan (
    id_vendor, nama_perlengkapan, jenis_perlengkapan,
    stok, harga_perlengkapan, harga_sewa_harian,
    tipe_kompor, tipe_tas, ukuran_sepatu
) VALUES
(1, 'Tas Carrier 60L', 'tas', 100, 600000, 40000,
 NULL, 'Carrier 60L', NULL),
(1, 'Sepatu Gunung', 'sepatu', 200, 500000, 35000,
 NULL, NULL, 42),
(1, 'Kompor Portable', 'kompor', 4, 300000, 80000,
 'Portable', NULL, NULL);
