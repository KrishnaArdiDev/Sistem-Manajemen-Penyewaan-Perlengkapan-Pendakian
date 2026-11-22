PANDUAN MENJALANKAN APLIKASI TAPAK
Sistem Manajemen Penyewaan Perlengkapan Pendakian

REPOSITORI PROYEK
GitHub: https://github.com/KrishnaArdiDev/Sistem-Manajemen-Penyewaan-Perlengkapan-Pendakian.git

1. PERSYARATAN

Pastikan perangkat telah memiliki:
- JDK 24 atau lebih baru.
- Apache Maven.
- MySQL Server 8.0.16 atau lebih baru.
- Koneksi internet untuk mengunduh dependensi Maven saat pertama kali dijalankan.

Proyek menggunakan JavaFX 26.0.2 dan MySQL Connector/J 9.4.0. Pengaturan kompilasi Java pada pom.xml menggunakan release 11.

2. MENYIAPKAN DATABASE

1. Jalankan MySQL Server.
2. Buka MySQL Workbench atau phpMyAdmin, lalu hubungkan ke server MySQL.
3. Jalankan seluruh isi file database/schema.sql.
4. Setelah schema.sql berhasil dijalankan, jalankan seluruh isi file database/seed.sql.
5. Pastikan database TAPAK-SistemManagement-PenyewaanPerlengkapanPendakian telah terbentuk.

3. KONFIGURASI KONEKSI DATABASE

Buka file:
src/main/java/com/sistemmanajemenpenyewaanperlengkapanpendakian/DatabaseConnection.java

Sesuaikan konfigurasi berikut dengan MySQL pada perangkat:
- URL: jdbc:mysql://localhost:3306/TAPAK-SistemManagement-PenyewaanPerlengkapanPendakian
- USER: root
- PASSWORD: kosong secara default

Jika menggunakan username, password, host, atau port yang berbeda, ubah nilai konfigurasi tersebut sebelum menjalankan aplikasi.

4. MENJALANKAN APLIKASI

1. Ekstrak ZIP proyek.
2. Buka terminal pada folder utama proyek, yaitu folder yang berisi pom.xml.
3. Pastikan JDK dan Maven telah terpasang dengan menjalankan:

   java -version
   mvn -version

4. Pastikan MySQL Server aktif dan database telah disiapkan.
5. Jalankan aplikasi dengan perintah:

   mvn clean javafx:run

6. Tunggu hingga proses kompilasi selesai dan jendela aplikasi TAPAK terbuka.

5. AKUN LOGIN

Gunakan salah satu akun berikut untuk masuk ke aplikasi setelah menjalankan database/seed.sql.

Akun 1
Username: vendor_santai
Email: vendorSantai@gmail.com
Password: Admin123

Akun 2
Username: vendor_rumah
Email: vendorRumah@gmail.com
Password: 321Admin

6. FILE DATABASE

- database/schema.sql: membuat database, tabel, constraint, dan view.
- database/seed.sql: memasukkan data awal untuk pengujian aplikasi.
