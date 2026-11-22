import com.sistemmanajemenpenyewaanperlengkapanpendakian.DatabaseConnection;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.Service.PelangganService;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.exception.DataTidakLengkapException;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Pelanggan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UjiPelangganService {

    private static void periksa(boolean kondisi, String pesan) {
        if (!kondisi) {
            throw new AssertionError(pesan);
        }
    }

    private interface Operasi {
        void jalankan() throws Exception;
    }

    private static void ujiDataTidakLengkap(
            String nama,
            Operasi operasi
    ) throws Exception {

        boolean ditolak = false;

        try {
            operasi.jalankan();
        } catch (DataTidakLengkapException e) {
            ditolak = true;
        }

        periksa(
                ditolak,
                nama + " seharusnya menghasilkan DataTidakLengkapException"
        );

        System.out.println("LULUS: Validasi " + nama);
    }

    private static int hitungIdentitas(String identitas)
            throws Exception {

        String sql =
                "SELECT COUNT(*) FROM pelanggan WHERE no_identitas = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, identitas);

            try (ResultSet result = statement.executeQuery()) {
                result.next();
                return result.getInt(1);
            }
        }
    }

    private static void hapusPelangganUji(String identitas)
            throws Exception {

        String sql =
                "DELETE FROM pelanggan WHERE no_identitas = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, identitas);

            int terhapus = statement.executeUpdate();

            periksa(
                    terhapus <= 1,
                    "Lebih dari satu pelanggan memakai marker pengujian"
            );
        }

        periksa(
                hitungIdentitas(identitas) == 0,
                "Identitas pelanggan uji masih tersimpan"
        );

        System.out.println("LULUS: Pembersihan data pelanggan uji");
    }

    public static void main(String[] args) throws Exception {

        String marker =
                "UJI-PELANGGAN-" + System.currentTimeMillis();

        String namaUji =
                "Pelanggan Uji " + System.currentTimeMillis();

        String identitasTidakAda =
                marker + "-TIDAK-ADA";

        int idPelangganUji = 0;

        boolean markerDipastikanBaru = false;

        Throwable kesalahanUtama = null;

        System.out.println("=== PENGUJIAN PELANGGANSERVICE ===");

        try {

            periksa(
                    hitungIdentitas(marker) == 0,
                    "Marker pengujian sudah digunakan. Jalankan ulang."
            );

            markerDipastikanBaru = true;

            Pelanggan budi =
                    PelangganService.prosesDataPelanggan(
                            "  bUdI  ",
                            " 510000000 "
                    );

            periksa(
                    budi.getIdPelanggan() == 1,
                    "Identitas ganda tidak cocok ke Budi"
            );

            periksa(
                    "Budi".equals(budi.getNamaPelanggan()),
                    "Data Budi tidak sesuai"
            );

            System.out.println(
                    "LULUS: Pencocokan identitas ganda berdasarkan nama Budi"
            );


            Pelanggan ica =
                    PelangganService.prosesDataPelanggan(
                            " Ica ",
                            "510000000"
                    );

            periksa(
                    ica.getIdPelanggan() == 2,
                    "Identitas ganda tidak cocok ke Ica"
            );

            System.out.println(
                    "LULUS: Pencocokan identitas ganda berdasarkan nama Ica"
            );

            ujiDataTidakLengkap(
                    "identitas ganda dengan nama yang tidak cocok",
                    () -> PelangganService.prosesDataPelanggan(
                            "Nama Tidak Cocok",
                            "510000000"
                    )
            );

            boolean pencarianGandaDitolak = false;

            try {
                PelangganService.cariByNoIdentitas("510000000");

            } catch (SQLException e) {

                pencarianGandaDitolak =
                        e.getMessage() != null
                        && e.getMessage().contains("beberapa pelanggan");
            }

            periksa(
                    pencarianGandaDitolak,
                    "Pencarian identitas ganda seharusnya ditolak"
            );

            System.out.println(
                    "LULUS: Pencarian langsung identitas ganda ditolak"
            );

            Pelanggan baru =
                    PelangganService.prosesDataPelanggan(
                            "  " + namaUji + "  ",
                            "  " + marker + "  "
                    );

            idPelangganUji = baru.getIdPelanggan();

            periksa(
                    idPelangganUji > 0,
                    "ID pelanggan baru tidak terbentuk"
            );

            periksa(
                    namaUji.equals(baru.getNamaPelanggan()),
                    "Nama pelanggan tidak dibersihkan dari spasi"
            );

            periksa(
                    marker.equals(baru.getNoIdentitasPelanggan()),
                    "Nomor identitas tidak dibersihkan dari spasi"
            );

            System.out.println(
                    "LULUS: Menambahkan pelanggan baru dengan trim data"
            );


            Pelanggan ditemukan =
                    PelangganService.cariByNoIdentitas(
                            "  " + marker + "  "
                    );

            periksa(
                    ditemukan != null
                    && ditemukan.getIdPelanggan() == idPelangganUji,
                    "Pencarian pelanggan baru tidak mengembalikan data yang benar"
            );

            System.out.println(
                    "LULUS: Mencari pelanggan baru berdasarkan identitas"
            );


            Pelanggan hasilUlang =
                    PelangganService.prosesDataPelanggan(
                            "Nama Berbeda",
                            marker
                    );

            periksa(
                    hasilUlang.getIdPelanggan() == idPelangganUji,
                    "Pelanggan yang sudah ada malah dibuat ulang"
            );

            periksa(
                    hitungIdentitas(marker) == 1,
                    "Terdapat duplikasi pelanggan untuk identitas baru"
            );

            System.out.println(
                    "LULUS: Identitas yang sudah terdaftar tidak dibuat ulang"
            );

            periksa(
                    PelangganService.cariByNoIdentitas(
                            identitasTidakAda
                    ) == null,
                    "Pencarian identitas yang tidak ada seharusnya null"
            );

            periksa(
                    PelangganService.cariByNoIdentitas(null) == null,
                    "Pencarian dengan identitas null seharusnya null"
            );

            periksa(
                    PelangganService.cariByNoIdentitas("   ") == null,
                    "Pencarian dengan identitas kosong seharusnya null"
            );

            System.out.println(
                    "LULUS: Pencarian identitas tidak ditemukan, null, dan kosong"
            );

            List<Pelanggan> semua =
                    PelangganService.semua();

            boolean ditemukanDalamSemua = false;

            for (Pelanggan pelanggan : semua) {

                if (pelanggan.getIdPelanggan() == idPelangganUji) {
                    ditemukanDalamSemua = true;
                    break;
                }
            }

            periksa(
                    ditemukanDalamSemua,
                    "Pelanggan baru tidak ada dalam daftar semua pelanggan"
            );

            System.out.println(
                    "LULUS: Membaca seluruh data pelanggan"
            );


            boolean tidakDapatDiubah = false;

            try {
                semua.clear();

            } catch (UnsupportedOperationException e) {
                tidakDapatDiubah = true;
            }

            periksa(
                    tidakDapatDiubah,
                    "Daftar semua pelanggan seharusnya tidak dapat diubah"
            );

            System.out.println(
                    "LULUS: Daftar semua pelanggan bersifat unmodifiable"
            );

            ujiDataTidakLengkap(
                    "nama null",
                    () -> PelangganService.prosesDataPelanggan(
                            null,
                            marker
                    )
            );

            ujiDataTidakLengkap(
                    "nama kosong",
                    () -> PelangganService.prosesDataPelanggan(
                            "   ",
                            marker
                    )
            );

            ujiDataTidakLengkap(
                    "nomor identitas null",
                    () -> PelangganService.prosesDataPelanggan(
                            namaUji,
                            null
                    )
            );

            ujiDataTidakLengkap(
                    "nomor identitas kosong",
                    () -> PelangganService.prosesDataPelanggan(
                            namaUji,
                            "   "
                    )
            );

            ujiDataTidakLengkap(
                    "nama lebih dari 150 karakter",
                    () -> PelangganService.prosesDataPelanggan(
                            "N".repeat(151),
                            marker
                    )
            );

            ujiDataTidakLengkap(
                    "nomor identitas lebih dari 50 karakter",
                    () -> PelangganService.prosesDataPelanggan(
                            namaUji,
                            "I".repeat(51)
                    )
            );

            periksa(
                    hitungIdentitas(marker) == 1,
                    "Validasi input mengubah data pelanggan uji"
            );

        } catch (Exception | Error e) {

            kesalahanUtama = e;
            throw e;

        } finally {

            if (markerDipastikanBaru) {

                try {
                    hapusPelangganUji(marker);

                } catch (Exception | Error e) {

                    if (kesalahanUtama != null) {
                        kesalahanUtama.addSuppressed(e);
                    } else {
                        throw e;
                    }
                }
            }
        }

        System.out.println();

        System.out.println(
                "SELURUH PENGUJIAN PELANGGANSERVICE BERHASIL"
        );
    }
}