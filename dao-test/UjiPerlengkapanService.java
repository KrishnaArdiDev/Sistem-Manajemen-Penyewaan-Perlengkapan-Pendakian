import com.sistemmanajemenpenyewaanperlengkapanpendakian.Service.PerlengkapanService;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Kompor;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Perlengkapan;

import java.sql.SQLException;
import java.util.List;

public class UjiPerlengkapanService {

    private static final int ID_VENDOR_1 = 1;
    private static final int ID_VENDOR_2 = 2;

    private static void periksa(boolean kondisi, String pesan) {
        if (!kondisi) {
            throw new AssertionError(pesan);
        }
    }

    private interface Operasi {
        void jalankan() throws Exception;
    }

    private static void ujiValidasi(String nama, Operasi operasi)
            throws Exception {

        boolean ditolak = false;

        try {
            operasi.jalankan();
        } catch (IllegalArgumentException e) {
            ditolak = true;
        }

        periksa(ditolak, nama + " seharusnya ditolak");

        System.out.println("LULUS: Validasi " + nama);
    }

    private static Kompor buatKompor(
            String nama,
            int stok,
            long harga,
            long tarif,
            String tipe) {

        Kompor kompor = new Kompor();

        kompor.setNamaPerlengkapan(nama);
        kompor.setStok(stok);
        kompor.setHargaPerlengkapan(harga);
        kompor.setHargaSewaHarian(tarif);
        kompor.setTipeKompor(tipe);

        return kompor;
    }

    private static Perlengkapan cariById(
            PerlengkapanService service,
            int idVendor,
            int idPerlengkapan) throws Exception {

        List<Perlengkapan> daftar =
                service.ambilPerlengkapanVendor(idVendor);

        for (Perlengkapan perlengkapan : daftar) {

            if (perlengkapan.getIdPerlengkapan() == idPerlengkapan) {
                return perlengkapan;
            }
        }

        return null;
    }

    private static void hapusJikaMasihAda(
            PerlengkapanService service,
            int idVendor,
            int idPerlengkapan) throws Exception {

        if (idPerlengkapan <= 0) {
            return;
        }

        Perlengkapan tersimpan =
                cariById(service, idVendor, idPerlengkapan);

        if (tersimpan != null) {
            service.hapusDataPerlengkapan(tersimpan, idVendor);
        }
    }

    public static void main(String[] args) throws Exception {

        PerlengkapanService service = new PerlengkapanService();

        String marker =
                "UJI-PERLENGKAPAN-" + System.currentTimeMillis();

        Kompor perlengkapanVendor1 = buatKompor(
                marker + "-V1",
                3,
                75000,
                25000,
                "2 sumbu"
        );

        Kompor perlengkapanVendor2 = buatKompor(
                marker + "-V2",
                2,
                85000,
                30000,
                "1 sumbu"
        );

        boolean buatVendor1 = false;
        boolean buatVendor2 = false;

        Throwable kesalahanUtama = null;

        System.out.println("=== PENGUJIAN PERLENGKAPANSERVICE ===");

        try {

            // ==========================================
            // 1. CREATE PERLENGKAPAN VENDOR 1
            // ==========================================

            service.tambahDataPerlengkapan(
                    perlengkapanVendor1,
                    ID_VENDOR_1
            );

            buatVendor1 = true;

            periksa(
                    perlengkapanVendor1.getIdPerlengkapan() > 0,
                    "ID perlengkapan Vendor 1 tidak terbentuk"
            );

            System.out.println(
                    "LULUS: Menambahkan perlengkapan Vendor 1"
            );

            // ==========================================
            // 2. READ DAN PEMETAAN DATA VENDOR 1
            // ==========================================

            Perlengkapan hasilVendor1 = cariById(
                    service,
                    ID_VENDOR_1,
                    perlengkapanVendor1.getIdPerlengkapan()
            );

            periksa(
                    hasilVendor1 != null,
                    "Perlengkapan Vendor 1 tidak ditemukan"
            );

            periksa(
                    hasilVendor1 instanceof Kompor,
                    "Jenis perlengkapan tidak dipetakan menjadi Kompor"
            );

            Kompor komporTerbaca = (Kompor) hasilVendor1;

            periksa(
                    (marker + "-V1").equals(
                            hasilVendor1.getNamaPerlengkapan()
                    ),
                    "Nama perlengkapan tidak sesuai"
            );

            periksa(
                    hasilVendor1.getStok() == 3,
                    "Stok perlengkapan tidak sesuai"
            );

            periksa(
                    hasilVendor1.getHargaPerlengkapan() == 75000,
                    "Harga perlengkapan tidak sesuai"
            );

            periksa(
                    hasilVendor1.getHargaSewaHarian() == 25000,
                    "Harga sewa harian tidak sesuai"
            );

            periksa(
                    "2 sumbu".equals(komporTerbaca.getTipeKompor()),
                    "Tipe kompor tidak sesuai"
            );

            periksa(
                    "Vendor Summit Santai".equals(
                            hasilVendor1.getNamaVendor()
                    ),
                    "Nama vendor pada hasil pemetaan tidak sesuai"
            );

            System.out.println(
                    "LULUS: Membaca perlengkapan Vendor 1"
            );

            System.out.println(
                    "LULUS: Pemetaan subclass Kompor dan atributnya"
            );

            // ==========================================
            // 3. UPDATE PERLENGKAPAN VENDOR 1
            // ==========================================

            perlengkapanVendor1.setNamaPerlengkapan(
                    marker + "-V1-UPDATED"
            );

            perlengkapanVendor1.setStok(7);
            perlengkapanVendor1.setHargaPerlengkapan(95000);
            perlengkapanVendor1.setHargaSewaHarian(35000);
            perlengkapanVendor1.setTipeKompor("3 sumbu");

            service.updateDataPerlengkapan(
                    perlengkapanVendor1,
                    ID_VENDOR_1
            );

            Perlengkapan hasilUpdate = cariById(
                    service,
                    ID_VENDOR_1,
                    perlengkapanVendor1.getIdPerlengkapan()
            );

            periksa(
                    hasilUpdate != null,
                    "Perlengkapan tidak ditemukan setelah update"
            );

            periksa(
                    (marker + "-V1-UPDATED").equals(
                            hasilUpdate.getNamaPerlengkapan()
                    ),
                    "Nama perlengkapan gagal diperbarui"
            );

            periksa(
                    hasilUpdate.getStok() == 7,
                    "Stok gagal diperbarui"
            );

            periksa(
                    hasilUpdate.getHargaPerlengkapan() == 95000,
                    "Harga perlengkapan gagal diperbarui"
            );

            periksa(
                    hasilUpdate.getHargaSewaHarian() == 35000,
                    "Harga sewa harian gagal diperbarui"
            );

            periksa(
                    hasilUpdate instanceof Kompor
                    && "3 sumbu".equals(
                            ((Kompor) hasilUpdate).getTipeKompor()
                    ),
                    "Tipe kompor gagal diperbarui"
            );

            System.out.println(
                    "LULUS: Memperbarui data perlengkapan Vendor 1"
            );

            // ==========================================
            // 4. CREATE PERLENGKAPAN VENDOR 2
            // ==========================================

            service.tambahDataPerlengkapan(
                    perlengkapanVendor2,
                    ID_VENDOR_2
            );

            buatVendor2 = true;

            periksa(
                    perlengkapanVendor2.getIdPerlengkapan() > 0,
                    "ID perlengkapan Vendor 2 tidak terbentuk"
            );

            System.out.println(
                    "LULUS: Menambahkan perlengkapan Vendor 2"
            );

            // ==========================================
            // 5. ISOLASI DATA ANTARVENDOR
            // ==========================================

            periksa(
                    cariById(
                            service,
                            ID_VENDOR_1,
                            perlengkapanVendor2.getIdPerlengkapan()
                    ) == null,
                    "Vendor 1 dapat membaca perlengkapan Vendor 2"
            );

            periksa(
                    cariById(
                            service,
                            ID_VENDOR_2,
                            perlengkapanVendor1.getIdPerlengkapan()
                    ) == null,
                    "Vendor 2 dapat membaca perlengkapan Vendor 1"
            );

            System.out.println(
                    "LULUS: Isolasi data perlengkapan antarvendor"
            );

            // ==========================================
            // 6. TOLAK UPDATE OLEH VENDOR YANG SALAH
            // ==========================================

            perlengkapanVendor2.setNamaPerlengkapan(
                    marker + "-V2-CHANGED"
            );

            boolean updateDitolak = false;

            try {

                service.updateDataPerlengkapan(
                        perlengkapanVendor2,
                        ID_VENDOR_1
                );

            } catch (SQLException e) {

                updateDitolak = true;
            }

            periksa(
                    updateDitolak,
                    "Vendor 1 seharusnya tidak dapat mengubah "
                    + "perlengkapan Vendor 2"
            );

            Perlengkapan vendor2SetelahPercobaan = cariById(
                    service,
                    ID_VENDOR_2,
                    perlengkapanVendor2.getIdPerlengkapan()
            );

            periksa(
                    vendor2SetelahPercobaan != null,
                    "Perlengkapan Vendor 2 hilang setelah percobaan update"
            );

            periksa(
                    (marker + "-V2").equals(
                            vendor2SetelahPercobaan.getNamaPerlengkapan()
                    ),
                    "Data Vendor 2 berubah akibat update oleh Vendor 1"
            );

            System.out.println(
                    "LULUS: Update lintas vendor ditolak"
            );

            // ==========================================
            // 7. TOLAK DELETE OLEH VENDOR YANG SALAH
            // ==========================================

            boolean deleteDitolak = false;

            try {

                service.hapusDataPerlengkapan(
                        perlengkapanVendor2,
                        ID_VENDOR_1
                );

            } catch (SQLException e) {

                deleteDitolak = true;
            }

            periksa(
                    deleteDitolak,
                    "Vendor 1 seharusnya tidak dapat menghapus "
                    + "perlengkapan Vendor 2"
            );

            periksa(
                    cariById(
                            service,
                            ID_VENDOR_2,
                            perlengkapanVendor2.getIdPerlengkapan()
                    ) != null,
                    "Perlengkapan Vendor 2 terhapus oleh Vendor 1"
            );

            System.out.println(
                    "LULUS: Delete lintas vendor ditolak"
            );

            // ==========================================
            // 8. VALIDASI CREATE
            // ==========================================

            ujiValidasi(
                    "objek perlengkapan null",
                    () -> service.tambahDataPerlengkapan(
                            null,
                            ID_VENDOR_1
                    )
            );

            ujiValidasi(
                    "ID vendor nol saat create",
                    () -> service.tambahDataPerlengkapan(
                            buatKompor(
                                    marker + "-INVALID",
                                    1,
                                    1000,
                                    500,
                                    "1 sumbu"
                            ),
                            0
                    )
            );

            ujiValidasi(
                    "nama perlengkapan kosong",
                    () -> service.tambahDataPerlengkapan(
                            buatKompor(
                                    "   ",
                                    1,
                                    1000,
                                    500,
                                    "1 sumbu"
                            ),
                            ID_VENDOR_1
                    )
            );

            ujiValidasi(
                    "nama perlengkapan melebihi 150 karakter",
                    () -> service.tambahDataPerlengkapan(
                            buatKompor(
                                    "N".repeat(151),
                                    1,
                                    1000,
                                    500,
                                    "1 sumbu"
                            ),
                            ID_VENDOR_1
                    )
            );

            // ==========================================
            // 9. VALIDASI READ DAN UPDATE
            // ==========================================

            ujiValidasi(
                    "ID vendor nol saat read",
                    () -> service.ambilPerlengkapanVendor(0)
            );

            Kompor tanpaId = buatKompor(
                    marker + "-TANPA-ID",
                    1,
                    1000,
                    500,
                    "1 sumbu"
            );

            ujiValidasi(
                    "ID perlengkapan nol saat update",
                    () -> service.updateDataPerlengkapan(
                            tanpaId,
                            ID_VENDOR_1
                    )
            );

            // ==========================================
            // 10. VALIDASI DELETE
            // ==========================================

            ujiValidasi(
                    "objek null saat delete",
                    () -> service.hapusDataPerlengkapan(
                            null,
                            ID_VENDOR_1
                    )
            );

            ujiValidasi(
                    "ID vendor nol saat delete",
                    () -> service.hapusDataPerlengkapan(
                            perlengkapanVendor1,
                            0
                    )
            );

            ujiValidasi(
                    "ID perlengkapan nol saat delete",
                    () -> service.hapusDataPerlengkapan(
                            tanpaId,
                            ID_VENDOR_1
                    )
            );

            // ==========================================
            // 11. DELETE PERLENGKAPAN VENDOR 1
            // ==========================================

            service.hapusDataPerlengkapan(
                    perlengkapanVendor1,
                    ID_VENDOR_1
            );

            periksa(
                    cariById(
                            service,
                            ID_VENDOR_1,
                            perlengkapanVendor1.getIdPerlengkapan()
                    ) == null,
                    "Perlengkapan Vendor 1 masih ditemukan setelah dihapus"
            );

            buatVendor1 = false;

            System.out.println(
                    "LULUS: Menghapus perlengkapan Vendor 1"
            );

            // ==========================================
            // 12. DELETE PERLENGKAPAN VENDOR 2
            // ==========================================

            Perlengkapan vendor2Valid = cariById(
                    service,
                    ID_VENDOR_2,
                    perlengkapanVendor2.getIdPerlengkapan()
            );

            periksa(
                    vendor2Valid != null,
                    "Perlengkapan Vendor 2 tidak ditemukan sebelum delete"
            );

            service.hapusDataPerlengkapan(
                    vendor2Valid,
                    ID_VENDOR_2
            );

            periksa(
                    cariById(
                            service,
                            ID_VENDOR_2,
                            perlengkapanVendor2.getIdPerlengkapan()
                    ) == null,
                    "Perlengkapan Vendor 2 masih ditemukan setelah dihapus"
            );

            buatVendor2 = false;

            System.out.println(
                    "LULUS: Menghapus perlengkapan Vendor 2"
            );

        } catch (Exception | Error e) {

            kesalahanUtama = e;
            throw e;

        } finally {

            try {

                if (buatVendor1) {
                    hapusJikaMasihAda(
                            service,
                            ID_VENDOR_1,
                            perlengkapanVendor1.getIdPerlengkapan()
                    );
                }

            } catch (Exception | Error e) {

                if (kesalahanUtama != null) {
                    kesalahanUtama.addSuppressed(e);
                } else {
                    throw e;
                }

            } finally {

                if (buatVendor2) {
                    hapusJikaMasihAda(
                            service,
                            ID_VENDOR_2,
                            perlengkapanVendor2.getIdPerlengkapan()
                    );
                }
            }
        }

        System.out.println();

        System.out.println(
                "SELURUH PENGUJIAN PERLENGKAPANSERVICE BERHASIL"
        );
    }
}