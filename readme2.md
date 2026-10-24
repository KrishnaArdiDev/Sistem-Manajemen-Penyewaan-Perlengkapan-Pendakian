PANDUAN MENJALANKAN PROGRAM
SISTEM MANAJEMEN PENYEWAAN PERLENGKAPAN PENDAKIAN

1. DESKRIPSI PROGRAM
Program ini merupakan aplikasi desktop untuk membantu Vendor mengelola penyewaan perlengkapan pendakian. Fitur yang direncanakan meliputi login Vendor, pengelolaan data perlengkapan, pencatatan transaksi penyewaan, pengembalian perlengkapan, perhitungan denda dan ganti rugi, serta pencetakan nota.

Catatan: README ini masih tentatif, tergantung pada perubahan yang terjadi

2. TEKNOLOGI
- Bahasa pemrograman: Java
- Antarmuka: JavaFX
- Database: MySQL
- Koneksi database: JDBC
- Build tool: Apache Maven

3. PERSIAPAN
Pastikan perangkat yang digunakan sudah memiliki:
- JDK dengan versi yang sesuai dengan konfigurasi pada pom.xml.
- Apache Maven, jika proyek tidak menyediakan Maven Wrapper.
- MySQL Server.
- IDE seperti Visual Studio Code (opsional).

4. LANGKAH MENYIAPKAN PROGRAM
1. Ekstrak file ZIP program ke folder yang diinginkan.
2. Pastikan folder hasil ekstraksi berisi pom.xml, src, dan file proyek lainnya.
3. Jalankan MySQL Server.
4. Buat database MySQL yang digunakan oleh aplikasi.
5. Jika paket pengumpulan menyertakan file SQL database, jalankan atau impor file tersebut untuk membuat tabel dan data awal. Jika file SQL belum disertakan, struktur tabel perlu dibuat sesuai rancangan database proyek sebelum aplikasi dijalankan.
6. Buka file DatabaseConnection.java, kemudian sesuaikan host, port, nama database, username, dan password MySQL dengan konfigurasi di komputer yang digunakan.
7. Simpan perubahan konfigurasi. Jangan mengunggah password pribadi atau kredensial database ke repositori publik.

5. CARA MENJALANKAN PROGRAM
Cara melalui terminal:
1. Buka terminal pada folder utama proyek, yaitu folder yang berisi pom.xml.
2. Jalankan perintah berikut:

   mvn clean javafx:run

3. Tunggu hingga proses build selesai dan jendela aplikasi muncul.
4. Gunakan aplikasi sesuai fitur yang telah diimplementasikan.

Jika perintah tersebut tidak dapat dijalankan, periksa apakah Maven dan JDK sudah terpasang, versi Java sesuai dengan pom.xml, serta plugin JavaFX telah dikonfigurasi dengan benar.

Cara melalui Visual Studio Code:
1. Buka Visual Studio Code.
2. Pilih File > Open Folder, kemudian pilih folder utama proyek yang berisi pom.xml.
3. Pastikan ekstensi Java yang diperlukan telah terpasang dan proyek selesai dimuat.
4. Buka terminal terintegrasi melalui menu Terminal > New Terminal.
5. Jalankan perintah mvn clean javafx:run.

6. AKUN LOGIN
Gunakan akun Vendor yang disediakan pada data awal database atau yang telah dikonfigurasi untuk demonstrasi. Jangan mengasumsikan username atau password tertentu jika tidak tercantum pada data demonstrasi yang disertakan.

7. PEMECAHAN MASALAH
- Jika koneksi database gagal, pastikan MySQL Server aktif dan pengaturan pada DatabaseConnection.java sudah benar.
- Jika muncul error terkait dependensi, periksa koneksi Maven, konfigurasi pom.xml, dan versi JDK.
- Jika halaman FXML gagal dimuat, periksa lokasi file FXML, nama resource yang dipanggil, nilai fx:controller, serta konfigurasi module-info.java.
- Jika tabel atau data tidak muncul, pastikan database dan tabel sudah dibuat serta berisi data yang diperlukan.

8. BERKAS PENGUMPULAN
File ZIP berisi source code dan berkas konfigurasi yang diperlukan untuk menjalankan program. Screenshot program yang sedang berjalan dicantumkan dalam laporan sesuai instruksi tugas. Pastikan screenshot jelas, tidak buram, dan memperlihatkan antarmuka aplikasi dengan baik.

9. CATATAN SEBELUM PENGUMPULAN
Sebelum dikumpulkan, uji kembali program dari awal sampai fitur yang dilaporkan berjalan. Pastikan instruksi, nama database, file SQL, akun demonstrasi, dan langkah menjalankan pada README.txt sesuai dengan isi ZIP yang benar-benar dikumpulkan.
