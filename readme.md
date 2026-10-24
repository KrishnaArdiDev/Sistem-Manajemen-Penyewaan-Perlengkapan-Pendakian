# Sistem Manajemen Penyewaan Perlengkapan Pendakian

Aplikasi Desktop berbasis **JavaFX** dan **Maven** yang terintegrasi dengan **MySQL Database** menggunakan JDBC untuk mengelola sistem penyewaan alat-alat pendakian gunung. Proyek ini dikembangkan untuk memenuhi tugas mata kuliah Pemrograman Berorientasi Objek (OOP).

---

## 🛠️ Prasyarat (Prerequisites)
Sebelum menjalankan program, pastikan komputer Anda telah terinstal perangkat lunak berikut:
1. **Java Development Kit (JDK)** versi 11 atau yang lebih baru.
2. **Apache Maven** (biasanya sudah terbundel otomatis jika menggunakan VS Code dengan ekstensi Maven).
3. **MySQL Server** (melalui XAMPP, Laragon, atau MySQL Installer).

---

## ⚙️ Panduan Instalasi dan Konfigurasi Database

1. **Buat Database Baru:**
   * Buka MySQL (phpMyAdmin atau terminal MySQL).
   * Buat database baru dengan nama `sistem_penyewaan` (atau sesuaikan dengan kebutuhan kode Anda).

2. **Clone / Buka Proyek:**
   * Buka folder proyek ini di text editor Anda (misalnya **Visual Studio Code**).
   * Pastikan ekstensi Java dan Maven di VS Code sudah aktif.

3. **Konfigurasi Koneksi Database:**
   * Sesuaikan pengaturan *URL*, *username*, dan *password* database pada kelas koneksi (misalnya di `DatabaseConnection.java`) agar sesuai dengan konfigurasi lokal MySQL Anda:
     ```java
     private static final String URL = "jdbc:mysql://localhost:3306/nama_database_anda";
     private static final String USER = "root";
     private static final String PASSWORD = "";
     ```

---

## 🚀 Panduan Menjalankan Program

Anda dapat menjalankan program ini melalui **Visual Studio Code** dengan langkah-langkah berikut:

1. **Buka Panel Maven:**
   * Di sidebar kiri VS Code, cari dan luaskan menu **Maven**.
2. **Jalankan Perintah Jalankan (Run):**
   * Cari proyek Anda, luaskan bagian *Plugins* > *javafx*, lalu klik dua kali pada **`javafx:run`**.
   * *Atau alternatif melalui Terminal:* Buka terminal di direktori proyek, lalu ketik perintah:
     ```bash
     mvn clean javafx:run
     ```
3. Program akan otomatis mengunduh dependensi (jika baru pertama kali dijalankan) dan membuka jendela antarmuka (GUI) JavaFX.

---

## 👥 Anggota Kelompok / Pembuat
* [Nama Anda / NIM]
* [Nama Rekan Tim / NIM]