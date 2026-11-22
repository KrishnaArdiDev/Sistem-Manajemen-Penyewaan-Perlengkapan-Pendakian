import com.sistemmanajemenpenyewaanperlengkapanpendakian.DatabaseConnection;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.dao.PelangganDAO;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Pelanggan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

public class UjiPelangganDAO {

    private static void periksa(boolean kondisi, String pesan) {
        if (!kondisi) {
            throw new AssertionError(pesan);
        }
    }

    public static void main(String[] args) throws Exception {

        PelangganDAO dao = new PelangganDAO();

        String identitasUji = "UJI-DAO-" + System.currentTimeMillis();
        String namaUji = "Uji Pelanggan DAO";

        System.out.println("=== PENGUJIAN PELANGGANDao ===");

        try {

            List<Pelanggan> pelangganGanda =
                    dao.ambilBerdasarkanNoIdentitas("510000000");

            periksa(pelangganGanda.size() == 2,
                    "Seharusnya ditemukan dua pelanggan dengan identitas tersebut");

            System.out.println("LULUS: Membaca pelanggan berdasarkan identitas");

            List<Pelanggan> tidakDitemukan =
                    dao.ambilBerdasarkanNoIdentitas(identitasUji);

            periksa(tidakDitemukan.isEmpty(),
                    "Pelanggan uji seharusnya belum tersedia");

            System.out.println("LULUS: Pencarian pelanggan yang tidak ditemukan");

            Pelanggan pelangganBaru = new Pelanggan();

            pelangganBaru.setNamaPelanggan(namaUji);
            pelangganBaru.setNoIdentitasPelanggan(identitasUji);

            dao.tambahDataPelanggan(pelangganBaru);

            periksa(pelangganBaru.getIdPelanggan() > 0,
                    "ID pelanggan tidak berhasil dibuat");

            System.out.println("LULUS: Penambahan pelanggan");

            List<Pelanggan> hasilPencarian =
                    dao.ambilBerdasarkanNoIdentitas(identitasUji);

            periksa(hasilPencarian.size() == 1,
                    "Pelanggan baru tidak ditemukan");

            Pelanggan hasil = hasilPencarian.get(0);

            periksa(hasil.getIdPelanggan() == pelangganBaru.getIdPelanggan(),
                    "ID pelanggan tidak sesuai");

            periksa(namaUji.equals(hasil.getNamaPelanggan()),
                    "Nama pelanggan tidak sesuai");

            System.out.println("LULUS: Membaca kembali pelanggan baru");

            List<Pelanggan> seluruhPelanggan =
                    dao.ambilSemuaPelanggan();

            boolean ditemukan = false;

            for (Pelanggan pelanggan : seluruhPelanggan) {
                if (pelanggan.getIdPelanggan()
                        == pelangganBaru.getIdPelanggan()) {
                    ditemukan = true;
                    break;
                }
            }

            periksa(ditemukan,
                    "Pelanggan baru tidak ditemukan dalam daftar pelanggan");

            System.out.println("LULUS: Mengambil seluruh data pelanggan");

        } finally {

            String sql = "DELETE FROM pelanggan "
                    + "WHERE no_identitas = ? AND nama_pelanggan = ?";

            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {

                statement.setString(1, identitasUji);
                statement.setString(2, namaUji);

                statement.executeUpdate();
            }

            List<Pelanggan> hasilPembersihan =
                    dao.ambilBerdasarkanNoIdentitas(identitasUji);

            periksa(hasilPembersihan.isEmpty(),
                    "Data pengujian belum berhasil dibersihkan");

            System.out.println("LULUS: Pembersihan data pengujian");
        }

        System.out.println("\nSELURUH PENGUJIAN PELANGGANDao BERHASIL");
    }
}