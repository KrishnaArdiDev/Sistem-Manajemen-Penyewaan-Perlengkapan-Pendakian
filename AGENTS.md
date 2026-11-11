# AGENTS.md

# PEDOMAN AI DAN KONTEKS PENGEMBANGAN PROYEK TAPAK

Dokumen ini merupakan pedoman wajib bagi AI Assistant yang membantu pengembangan proyek TAPAK, baik untuk membaca, menganalisis, memperbaiki, menambahkan, maupun mengubah kode program.

Tujuan utama dokumen ini adalah menjaga konsistensi implementasi, mempertahankan rancangan yang telah disepakati, mencegah perubahan yang tidak diperlukan, serta memastikan setiap perubahan dilakukan berdasarkan pemahaman terhadap keseluruhan proyek.

**AI WAJIB MEMBACA DAN MEMAHAMI DOKUMEN INI SEBELUM MEMBERIKAN SARAN ATAU MELAKUKAN PERUBAHAN KODE.**

---

# 1. IDENTITAS DAN KONTEKS PROYEK

## 1.1 Identitas Proyek

- **Nama aplikasi:** TAPAK
- **Nama lengkap:** Sistem Manajemen Penyewaan Perlengkapan Pendakian
- **Jenis aplikasi:** Desktop Application
- **Bahasa pemrograman:** Java
- **Framework antarmuka:** JavaFX
- **Build system:** Apache Maven
- **Database Management System:** MySQL
- **Koneksi database:** JDBC
- **Tujuan proyek:** Memenuhi tugas mata kuliah Pemrograman Berorientasi Objek.

## 1.2 Gambaran Umum Aplikasi

TAPAK merupakan aplikasi desktop untuk membantu pengelolaan usaha penyewaan perlengkapan pendakian.

Aplikasi digunakan oleh Vendor untuk mengelola data perlengkapan, pelanggan, transaksi penyewaan, pengembalian perlengkapan, serta pencatatan nota transaksi.

**Perlu diperhatikan:**

- Vendor merupakan pengguna aplikasi.
- Pelanggan merupakan pihak yang datanya dicatat dalam sistem.
- Pelanggan bukan aktor yang melakukan login atau menggunakan aplikasi secara langsung.
- Jangan menambahkan sistem akun pelanggan, aplikasi pelanggan, atau fitur lain yang tidak termasuk dalam rancangan tanpa persetujuan pemilik proyek.

## 1.3 Fitur Utama

Fitur yang menjadi bagian dari aplikasi meliputi:

1. Login Vendor.
2. Dashboard atau halaman utama.
3. Pengelolaan data perlengkapan.
4. Pengelolaan transaksi penyewaan.
5. Pengelolaan pengembalian perlengkapan.
6. Pengelolaan profil Vendor.
7. Pembuatan dan pencetakan nota transaksi sebagai fitur pendukung.

Terdapat halaman atau komponen pendukung seperti pemilihan item transaksi, penambahan transaksi, dan konfirmasi pengembalian.

Jangan menghapus atau mengganti fitur yang sudah tersedia hanya karena AI menganggap struktur atau alur lain lebih sederhana.

---

# 2. SUMBER KEBENARAN PROYEK

AI harus memahami bahwa tidak semua informasi dalam proyek memiliki tingkat otoritas yang sama.

Gunakan urutan acuan berikut:

## 2.1 Instruksi Pemilik Proyek

Instruksi terbaru dari pemilik proyek memiliki prioritas tertinggi.

Apabila pemilik proyek memberikan keputusan atau perubahan baru, jangan mempertahankan aturan lama yang sudah digantikan.

Namun, jangan menganggap suatu usulan sebagai keputusan final sebelum pemilik proyek menyetujuinya.

## 2.2 Dokumen Rancangan PBO

Dokumen laporan PBO terbaru merupakan acuan rancangan sistem, khususnya untuk:

- Struktur class Model.
- Atribut dan tipe data.
- Constructor.
- Method dan parameter.
- Relasi antarkelas.
- Inheritance.
- Encapsulation.
- Polymorphism.
- Interface.
- Exception.
- Rancangan database dan relasinya.

Jangan mengurangi elemen rancangan yang sudah ditetapkan dalam dokumen tersebut.

Apabila dokumen terbaru tidak tersedia dalam workspace, jangan mengarang isi rancangan. Minta pemilik proyek menyediakan dokumen yang diperlukan.

## 2.3 Database Schema

File berikut merupakan acuan utama struktur database:

`database/schema.sql`

File tersebut menetapkan struktur database yang telah disepakati.

Jangan mengganti struktur database hanya untuk mempermudah implementasi Java.

## 2.4 Kode Program Saat Ini

Kode yang tersedia dalam repository merupakan representasi implementasi proyek saat ini.

Sebelum mengubah suatu bagian, periksa kode yang benar-benar digunakan dan hubungan ketergantungannya dengan bagian lain.

Jangan hanya mengandalkan potongan kode, contoh dari internet, atau asumsi berdasarkan nama file.

## 2.5 Riwayat dan Dokumentasi Lain

README, komentar, file percobaan, catatan lama, dan contoh implementasi dapat membantu memahami proyek.

Namun, apabila isinya bertentangan dengan instruksi terbaru, rancangan PBO, atau struktur database yang telah disepakati, jangan langsung menjadikannya acuan.

Contoh ketidaksesuaian pada snapshot repository yang pernah diperiksa:

- `readme.md` masih memuat contoh nama database `sistem_penyewaan`.
- Nama database yang digunakan dalam `schema.sql` adalah `TAPAK-SistemManagement-PenyewaanPerlengkapanPendakian`.

Jangan mengubah nama database mengikuti dokumentasi lama tersebut.

---

# 3. ATURAN UTAMA PENGEMBANGAN

## 3.1 Prinsip Utama

# NGURANG GABOLEH, NAMBAH BOLEH.

Prinsip ini berlaku untuk seluruh implementasi, terutama bagian yang telah ditentukan dalam rancangan PBO dan struktur database.

Artinya:

**DILARANG:**

- Menghapus atribut yang telah ditentukan.
- Menghapus method yang telah ditentukan.
- Menghilangkan constructor yang dibutuhkan.
- Mengubah tipe data yang telah ditetapkan dalam rancangan laporan. Tipe data pada implementasi wajib mengikuti rancangan laporan dan tidak boleh berbeda.
- Mengubah nama class, atribut, atau method yang telah ditetapkan dalam rancangan laporan. Penamaan pada implementasi wajib sama dengan rancangan laporan.
- Mengubah parameter atau return type method yang sudah ditetapkan.
- Menghilangkan relasi antarkelas.
- Menghilangkan inheritance atau interface yang telah dirancang.
- Mengurangi fitur yang sudah tersedia.
- Mengganti alur bisnis dengan alur baru hanya karena dianggap lebih mudah.
- Menghapus validasi atau penanganan exception yang masih diperlukan.

**DIPERBOLEHKAN DENGAN SYARAT:**

Penambahan hanya diperbolehkan untuk mendukung implementasi fitur dan rancangan yang sudah ditetapkan, bukan untuk mengembangkan sistem berdasarkan asumsi atau preferensi AI sendiri.

Penambahan yang diperbolehkan meliputi:

- Menambahkan method pendukung yang tidak menggantikan atau mengubah method yang telah ditetapkan dalam laporan rancangan.
- Menambahkan atribut pendukung selama seluruh atribut yang diwajibkan laporan rancangan tetap dipertahankan dengan nama dan tipe data yang sama.
- Menambahkan validasi untuk menjaga integritas data dan mencegah kesalahan input tanpa mengubah aturan bisnis yang telah ditetapkan.
- Menambahkan exception untuk menangani kondisi kesalahan yang relevan.
- Menambahkan helper atau utilitas untuk menghindari pengulangan kode.
- Menambahkan class atau file pendukung apabila benar-benar diperlukan dan tetap mengikuti struktur arsitektur proyek.
- Menambahkan komponen pendukung pada antarmuka apabila diperlukan untuk menjalankan fitur yang sudah dirancang.

**BATASAN PENAMBAHAN:**

1. Penambahan tidak boleh menghapus, mengganti, mengubah, atau mengurangi bagian yang sudah ditetapkan dalam laporan rancangan.
2. Penambahan tidak boleh mengubah nama, tipe data, parameter, return type, maupun kontrak elemen yang sudah ditetapkan dalam laporan rancangan.
3. Penambahan tidak boleh mengubah struktur database, termasuk menambahkan tabel, kolom, relasi, constraint, maupun VIEW.
4. Penambahan tidak boleh menciptakan fitur baru, proses bisnis baru, aktor baru, atau kebutuhan sistem baru yang belum ditetapkan.
5. Penambahan tidak boleh mengubah tampilan atau mekanisme styling berdasarkan inisiatif AI sendiri.
6. Penambahan tidak boleh mengubah cara kerja fitur yang sudah ada sehingga bertentangan dengan rancangan atau perilaku yang telah disepakati.
7. Penambahan tidak boleh menimbulkan duplikasi fungsi, konflik, atau ketidaksesuaian dengan implementasi lain.

Apabila suatu penambahan berpotensi mengubah rancangan, struktur database, fitur, atau alur bisnis, AI tidak boleh menerapkannya secara otomatis. AI harus menjelaskan kebutuhan dan dampaknya serta menunggu arahan pemilik proyek. Khusus perubahan struktur database, tetap berlaku larangan pada Bagian 6.4.

Setiap penambahan harus memiliki alasan teknis yang jelas, benar-benar diperlukan, dan tetap kompatibel dengan keseluruhan sistem.

## 3.2 Jangan Melakukan Refactoring Tanpa Alasan

AI tidak boleh melakukan refactoring besar-besaran hanya karena menemukan pola kode yang dianggap kurang ideal.

Contoh tindakan yang tidak boleh dilakukan tanpa persetujuan:

- Mengganti seluruh struktur package.
- Mengubah seluruh nama class.
- Memindahkan banyak file.
- Mengganti arsitektur yang sudah berjalan.
- Menggabungkan beberapa class yang memiliki tanggung jawab berbeda.
- Memecah satu class menjadi banyak class tanpa kebutuhan yang jelas.
- Mengganti seluruh implementasi dengan kode hasil generasi AI.
- Mengubah format seluruh file hanya demi konsistensi formatter.

Prioritaskan perbaikan terkecil yang benar-benar menyelesaikan masalah.

## 3.3 Pertahankan Implementasi yang Sudah Ada

Jangan menganggap kode yang terlihat panjang, berbeda dari tutorial, atau tidak sesuai preferensi pribadi AI sebagai kode yang harus diganti.

AI wajib memahami alasan keberadaan implementasi tersebut sebelum menyarankan perubahan.

Apabila suatu bagian sudah benar dan tidak berhubungan dengan permintaan, biarkan bagian tersebut tetap seperti semula.

## 3.4 Jangan Mengarang Fitur atau Kebutuhan

Jangan menambahkan fitur, atribut, tabel, kolom, relasi, atau proses bisnis berdasarkan asumsi sendiri.

Apabila ada kebutuhan yang belum jelas, jelaskan bagian yang belum diketahui dan minta keputusan pemilik proyek sebelum mengubah implementasi yang berdampak besar.

---

# 4. KONTEKS STRUKTUR PROJECT

Struktur utama package Java saat ini:

```text
src/
└── main/
    ├── java/
    │   └── com/
    │       └── sistemmanajemenpenyewaanperlengkapanpendakian/
    │           ├── App.java
    │           ├── DatabaseConnection.java
    │           │
    │           ├── controller/
    │           │   ├── components/
    │           │   ├── DashboardController.java
    │           │   ├── ItemTransaksiController.java
    │           │   ├── LoginController.java
    │           │   ├── MainLayoutController.java
    │           │   ├── NotaController.java
    │           │   ├── PengembalianController.java
    │           │   ├── PerlengkapanController.java
    │           │   ├── ProfileController.java
    │           │   ├── TambahTransaksiController.java
    │           │   └── TransaksiController.java
    │           │
    │           ├── dao/
    │           │   ├── DetailTransaksiDAO.java
    │           │   ├── PelangganDAO.java
    │           │   ├── PerlengkapanDAO.java
    │           │   ├── TransaksiDAO.java
    │           │   └── VendorDAO.java
    │           │
    │           ├── model/
    │           │   ├── DapatDicetak.java
    │           │   ├── DetailTransaksi.java
    │           │   ├── Kompor.java
    │           │   ├── Pelanggan.java
    │           │   ├── Perlengkapan.java
    │           │   ├── Sepatu.java
    │           │   ├── Tas.java
    │           │   ├── Transaksi.java
    │           │   └── Vendor.java
    │           │
    │           ├── Service/
    │           │   ├── AuthService.java
    │           │   ├── NotaService.java
    │           │   ├── PelangganService.java
    │           │   ├── PengembalianService.java
    │           │   ├── PerlengkapanService.java
    │           │   └── TransaksiService.java
    │           │
    │           └── exception/
    │               ├── DataTidakLengkapException.java
    │               ├── LoginGagalException.java
    │               └── StokTidakCukupException.java
    │
    └── resources/
        ├── com/
        │   └── sistemmanajemenpenyewaanperlengkapanpendakian/
        │       └── fxml/
        │           ├── components/
        │           ├── main-layout.fxml
        │           └── pages/
        │
        └── images/
```

Selain itu, terdapat folder:

```text
database/
├── schema.sql
└── seed.sql
```

**Catatan penting:**

Struktur di atas merupakan gambaran struktur dari snapshot repository yang diperiksa. Selalu periksa repository terbaru sebelum melakukan perubahan karena struktur dapat berkembang.

Jangan mengubah nama package, memindahkan file, atau membuat struktur folder baru tanpa kebutuhan yang jelas dan persetujuan apabila berdampak pada banyak file.

Perhatikan bahwa nama package `Service` menggunakan huruf kapital sesuai kondisi repository saat ini. Jangan mengubahnya menjadi `service` hanya karena preferensi penamaan sendiri.

---

# 5. PEMBAGIAN TANGGUNG JAWAB ANTARLAYER

Implementasi menggunakan beberapa bagian dengan tanggung jawab yang berbeda.

## 5.1 Model

Model merepresentasikan objek dan aturan dasar domain aplikasi.

Model saat ini mencakup:

- Vendor
- Pelanggan
- Perlengkapan
- Kompor
- Tas
- Sepatu
- Transaksi
- DetailTransaksi
- DapatDicetak

Perhatikan ketentuan berikut:

- Pertahankan atribut dan method yang ditetapkan rancangan PBO.
- Pertahankan inheritance dan implementasi interface.
- Jangan memindahkan tanggung jawab Model secara sembarangan.
- Jangan menghilangkan validasi dan proses bisnis yang sudah diterapkan.
- Pastikan perubahan Model tidak merusak penggunaan oleh DAO, Service, dan Controller.

## 5.2 DAO

DAO bertanggung jawab terhadap akses dan manipulasi data pada database menggunakan JDBC.

Secara umum, DAO menangani operasi seperti:

- Create.
- Read.
- Update.
- Delete.
- Pencarian data.
- Pengambilan data berdasarkan ID.
- Pemetaan hasil query ke objek Model.

Ketentuan:

- Gunakan struktur database yang sudah ditetapkan.
- Jangan menulis query berdasarkan nama kolom yang hanya diasumsikan.
- Periksa tabel, kolom, tipe data, dan relasi sebelum membuat query.
- Gunakan parameterized query melalui `PreparedStatement` untuk input.
- Perhatikan penutupan koneksi dan resource JDBC.
- Pertahankan kontrak method DAO yang sudah digunakan bagian lain.

## 5.3 Service

Service bertanggung jawab mengatur proses bisnis dan koordinasi antara DAO dengan bagian lain.

Ketentuan:

- Jangan memindahkan seluruh logika bisnis ke Controller.
- Jangan memasukkan query database secara sembarangan ke Controller.
- Jangan menggandakan aturan bisnis yang sudah tersedia di Model atau Service lain.
- Periksa hubungan antara Service, DAO, dan Model sebelum mengubah method.

## 5.4 Controller

Controller menghubungkan interaksi pengguna dengan proses aplikasi.

Selain menangani fungsi dan alur interaksi aplikasi, Controller juga bertanggung jawab terhadap pengaturan tampilan (design) antarmuka melalui JavaFX dan FXML.

Ketentuan:

- Pengaturan tampilan yang berkaitan dengan halaman dan komponen antarmuka dilakukan melalui mekanisme JavaFX yang sudah digunakan dalam proyek.
- Jangan langsung membuat file CSS baru atau menambahkan stylesheet tanpa memeriksa mekanisme styling yang sudah diterapkan.
- Jangan memindahkan pengaturan tampilan ke CSS hanya berdasarkan preferensi atau inisiatif AI.
- Sebelum mengubah mekanisme styling, periksa implementasi yang sudah ada dan pastikan perubahan memang diperlukan serta sesuai arahan pemilik proyek.
- Pertahankan hubungan Controller dengan FXML.
- Pertahankan `fx:id` yang digunakan oleh Controller.
- Pertahankan event handler yang digunakan oleh komponen antarmuka.
- Periksa alur pemanggilan Service dan DAO sebelum mengubah Controller.
- Jangan memasukkan seluruh logika bisnis ke Controller.
- Jangan menghapus komponen antarmuka yang masih digunakan oleh proses aplikasi.

**PENTING:**

Pekerjaan DAO, Service, dan Controller dibagi antara pemilik proyek dan anggota kelompok lainnya.

Jangan menganggap seluruh bagian tersebut dikerjakan oleh satu orang atau seluruhnya telah diserahkan kepada anggota lain.

Sebelum melakukan perubahan pada bagian-bagian tersebut, perhatikan pembagian tugas yang diberikan oleh pemilik proyek.

Jangan mengubah pekerjaan anggota kelompok lain tanpa kebutuhan yang jelas dan koordinasi.

---

# 6. DATABASE

## 6.1 Identitas Database

Nama database yang digunakan:

```text
TAPAK-SistemManagement-PenyewaanPerlengkapanPendakian
```

File struktur database:

```text
database/schema.sql
```

File data awal:

```text
database/seed.sql
```

## 6.2 Struktur Tabel

Database memiliki lima tabel utama:

| Tabel | Fungsi |
|---|---|
| `vendor` | Menyimpan data Vendor |
| `pelanggan` | Menyimpan data pelanggan |
| `perlengkapan` | Menyimpan data perlengkapan |
| `transaksi` | Menyimpan data transaksi penyewaan |
| `detail_transaksi` | Menyimpan rincian perlengkapan dalam transaksi |

Relasi utama:

- `perlengkapan.id_vendor` mengacu pada `vendor.id_vendor`.
- `transaksi.id_vendor` mengacu pada `vendor.id_vendor`.
- `transaksi.id_pelanggan` mengacu pada `pelanggan.id_pelanggan`.
- `detail_transaksi.id_transaksi` mengacu pada `transaksi.id_transaksi`.
- `detail_transaksi.id_perlengkapan` mengacu pada `perlengkapan.id_perlengkapan`.

Seluruh struktur kolom, tipe data, constraint, primary key, foreign key, dan ketentuan lainnya harus diperiksa langsung melalui `database/schema.sql`.

## 6.3 SQL VIEW

Database memiliki lima VIEW:

- `vw_vendor_adm`
- `vw_pelanggan_adm`
- `vw_perlengkapan_adm`
- `vw_transaksi_adm`
- `vw_detail_transaksi_adm`

VIEW digunakan untuk menampilkan ID dengan format prefiks sesuai rancangan ADM.

Contoh:

- Vendor: `1V`
- Pelanggan: `1P`
- Perlengkapan: `1Prl`
- Transaksi: `1T`
- Detail transaksi: `1Dt`

ID pada tabel utama tetap menggunakan integer dengan `AUTO_INCREMENT`.

Format prefiks diperoleh melalui fungsi `CONCAT` pada VIEW. Jangan mengubah ID utama menjadi VARCHAR hanya untuk mendapatkan format tersebut.

## 6.4 Larangan Perubahan Database

**STRUKTUR DATABASE YANG TELAH DITETAPKAN MERUPAKAN BAGIAN YANG TIDAK BOLEH DIUBAH OLEH AI.**

AI DILARANG mengubah struktur database, termasuk:

- Menghapus atau menambahkan tabel.
- Menghapus atau menambahkan kolom.
- Mengganti nama tabel.
- Mengganti nama kolom.
- Mengubah tipe data.
- Mengganti primary key.
- Mengganti foreign key.
- Menghapus atau mengubah constraint.
- Mengubah relasi antartabel.
- Menghapus, mengganti, atau mengubah VIEW.
- Mengganti format prefiks ID.
- Mengubah struktur database agar sesuai dengan kode Java yang keliru.

Struktur database wajib mengikuti rancangan yang telah ditetapkan dalam laporan PBO dan diwujudkan dalam `database/schema.sql`.

Apabila ditemukan ketidaksesuaian antara kode Java dan database, periksa dan perbaiki implementasi Java tanpa mengubah struktur database.

Apabila AI menemukan masalah yang menurutnya mengharuskan perubahan struktur database, AI hanya boleh menjelaskan masalah, dampak, dan kemungkinan solusinya kepada pemilik proyek. AI tidak boleh melakukan perubahan tersebut secara sepihak.

## 6.5 Perhatian terhadap Seed Data

Periksa isi aktual `seed.sql` sebelum menggunakannya.

Pada snapshot ZIP yang pernah diperiksa, file tersebut masih terlihat belum lengkap. Jangan menganggapnya siap dijalankan tanpa memeriksa kembali versi terbaru.

Database lokal sebelumnya telah berhasil dibuat dan diisi melalui proses terpisah. Hal tersebut tidak otomatis berarti isi `seed.sql` di repository sudah diperbarui.

Jangan menjalankan perintah yang dapat menghapus atau mereset data database tanpa persetujuan pemilik proyek.

---

# 7. KONFIGURASI DAN DEPENDENCY

Konfigurasi Maven yang terdapat dalam snapshot proyek:

| Komponen | Konfigurasi |
|---|---|
| Bahasa pemrograman | Java |
| Java Compiler Target | 11 |
| JavaFX Controls | 13 |
| JavaFX FXML | 13 |
| MySQL Connector/J | 9.4.0 |
| Build System | Maven |

File konfigurasi:

```text
pom.xml
```

Ketentuan:

- Jangan mengganti versi dependency tanpa alasan teknis yang jelas.
- Jangan menghapus dependency yang masih digunakan.
- Jangan mengganti versi Java hanya berdasarkan asumsi.
- Jangan mengubah `module-info.java` tanpa memeriksa seluruh kebutuhan modul.
- Jangan mengganti konfigurasi Maven hanya untuk mencoba mengatasi error tanpa memahami penyebabnya terlebih dahulu.

Apabila terdapat error dependency atau konfigurasi, jelaskan penyebabnya dan berikan perubahan sekecil mungkin.

---

# 8. ATURAN PERUBAHAN FXML DAN UI

FXML merupakan bagian dari antarmuka JavaFX dan memiliki hubungan langsung dengan Controller.

Sebelum mengubah FXML:

1. Periksa Controller yang menggunakan file tersebut.
2. Periksa `fx:id`.
3. Periksa event handler.
4. Periksa struktur komponen.
5. Periksa apakah komponen digunakan oleh halaman lain.

Jangan:

- Menghapus `fx:id` tanpa memeriksa Controller.
- Mengganti nama elemen yang diakses Controller tanpa penyesuaian yang disetujui.
- Menghapus event handler yang masih digunakan.
- Mengganti seluruh layout tanpa permintaan.
- Mengubah desain hanya karena AI memiliki preferensi UI sendiri.
- Menghapus resource gambar yang masih digunakan.

Apabila diminta memperbaiki UI, pertahankan struktur dan tampilan yang telah disepakati selama tidak ada instruksi perubahan desain.

---

# 9. ATURAN KERJA AI SEBELUM MENGUBAH KODE

Bagian ini wajib diikuti setiap kali AI menerima permintaan perubahan.

## 9.1 Tahap Pemeriksaan

Sebelum mengedit, AI harus:

1. Membaca pedoman ini.
2. Memahami tujuan perubahan yang diminta.
3. Memeriksa file yang berkaitan dengan permintaan.
4. Memeriksa class atau file lain yang menggunakan bagian tersebut.
5. Memeriksa hubungan antarlayer.
6. Memeriksa rancangan PBO apabila menyangkut Model.
7. Memeriksa `schema.sql` apabila menyangkut database.
8. Mengidentifikasi potensi dampak terhadap bagian lain.

Jangan langsung mengedit hanya berdasarkan satu file atau satu potongan kode.

## 9.2 Tahap Perencanaan

Sebelum melakukan perubahan, tentukan:

- Masalah yang ingin diselesaikan.
- File yang perlu diubah.
- File yang mungkin terdampak.
- Alasan perubahan.
- Apakah perubahan masih sesuai rancangan.
- Apakah perubahan membutuhkan persetujuan pemilik proyek.

Jika permintaan hanya menyangkut satu method, jangan mengubah seluruh class tanpa alasan yang kuat.

## 9.3 Tahap Implementasi

Saat melakukan perubahan:

- Gunakan perubahan yang minimal dan terarah.
- Pertahankan implementasi yang tidak berkaitan dengan masalah.
- Jangan menghapus kode hanya karena terlihat tidak digunakan sebelum memeriksa seluruh pemanggilannya.
- Nama class, atribut, dan method wajib sama persis dengan yang ditetapkan dalam laporan rancangan. Dilarang mengganti, mengubah, atau menyesuaikan penamaan tersebut berdasarkan preferensi AI sendiri.
- Tipe data wajib sama persis dengan yang ditetapkan dalam laporan rancangan. Dilarang mengubah tipe data berdasarkan preferensi atau asumsi AI sendiri.
- Jangan mengganti struktur kode secara massal.
- Jangan menambahkan dependency tanpa kebutuhan.
- Jangan membuat file baru apabila kebutuhan dapat diselesaikan dengan aman menggunakan struktur yang ada.

## 9.4 Penandaan Perubahan Kode

Apabila memberikan kode hasil perubahan, gunakan penanda berikut:

Untuk penambahan kode baru:

```java
// PENAMBAHAN
```

Untuk perbaikan terhadap kode yang sudah ada:

```java
// diperbaiki
```

Gunakan penanda tersebut secara relevan pada bagian yang diubah.

Jangan memenuhi seluruh file dengan komentar yang tidak diperlukan.

## 9.5 Tahap Pemeriksaan Setelah Perubahan

Setelah selesai:

1. Periksa kembali bagian yang diubah.
2. Pastikan method dan class yang sebelumnya digunakan tetap tersedia.
3. Periksa kemungkinan kesalahan import.
4. Periksa kecocokan nama package.
5. Periksa hubungan antara FXML dan Controller jika relevan.
6. Periksa kesesuaian query dengan schema jika relevan.
7. Jalankan kompilasi atau pengujian apabila lingkungan memungkinkan.
8. Laporkan hasil pemeriksaan secara jujur.

AI tidak boleh menyatakan aplikasi berhasil dijalankan, database berhasil terhubung, atau pengujian berhasil apabila tindakan tersebut belum benar-benar dilakukan.

---

# 10. LARANGAN OPERASI GIT DAN PERUBAHAN REPOSITORY

AI tidak boleh melakukan operasi Git yang mengubah riwayat atau kondisi repository tanpa permintaan eksplisit.

Contoh tindakan yang memerlukan persetujuan:

- `git reset`
- `git checkout` yang mengganti atau membuang perubahan kerja.
- `git restore`
- `git clean`
- `git rebase`
- `git merge`
- `git cherry-pick`
- `git push`
- `git push --force`
- Penghapusan branch.
- Perubahan konfigurasi branch protection.

Jangan pernah menghapus perubahan yang sudah dikerjakan anggota kelompok.

Sebelum melakukan operasi Git, periksa:

```bash
git status
```

Perhatikan perubahan lokal, file yang belum di-commit, konflik merge, serta branch yang sedang aktif.

Jangan melakukan commit atau push secara otomatis.

Keputusan integrasi dan penggabungan perubahan tetap berada pada pemilik proyek sesuai pembagian kerja kelompok.

Folder `.git` merupakan metadata repository. Jangan memodifikasi isi internalnya secara langsung.

---

# 11. HAL-HAL YANG TIDAK BOLEH DILAKUKAN AI

AI dilarang dan tidak boleh melakukan tindakan berikut secara sepihak:

1. Menulis ulang file secara keseluruhan hanya untuk memperbaiki satu bagian kecil.
2. Menghapus atribut atau method yang berasal dari rancangan PBO.
3. Mengganti struktur database.
4. Mengubah nama package secara massal.
5. Mengganti versi dependency tanpa kebutuhan.
6. Menghapus fitur yang telah tersedia.
7. Mengubah alur transaksi atau pengembalian berdasarkan asumsi.
8. Membuat tabel atau kolom baru berdasarkan asumsi.
9. Mengubah FXML tanpa memeriksa Controller terkait.
10. Mengubah file yang tidak berhubungan dengan permintaan.
11. Menghapus pekerjaan anggota kelompok lain.
12. Menganggap saran AI sebelumnya sebagai keputusan resmi proyek.
13. Mengklaim bahwa kode sudah teruji tanpa melakukan pengujian.
14. Menggunakan server, database, atau data tiruan lalu menyatakan implementasi sudah berhasil terhubung ke sistem sebenarnya.
15. Menjalankan operasi destruktif pada database atau repository tanpa persetujuan.

---

# 12. PENANGANAN ERROR DAN KETIDAKSESUAIAN

Apabila AI menemukan error atau ketidaksesuaian, jangan langsung mengganti struktur yang dianggap bermasalah.

Ikuti proses berikut:

**Langkah 1: Identifikasi**

Tentukan lokasi error dan penyebab yang paling mungkin.

**Langkah 2: Periksa Ketergantungan**

Periksa apakah masalah berasal dari file lain, konfigurasi, database, atau pemanggilan method.

**Langkah 3: Cocokkan dengan Rancangan**

Pastikan solusi tidak bertentangan dengan rancangan PBO dan struktur database.

**Langkah 4: Tentukan Solusi Minimal**

Pilih perbaikan terkecil yang mampu menyelesaikan masalah.

**Langkah 5: Jelaskan Dampaknya**

Sampaikan file yang perlu berubah dan alasan perubahan.

**Langkah 6: Implementasikan**

Lakukan perubahan sesuai lingkup permintaan dan persetujuan yang diperlukan.

Apabila solusi yang benar mengharuskan perubahan terhadap rancangan yang sudah disepakati, jangan mengambil keputusan sendiri.

Sampaikan masalah dan tunggu arahan pemilik proyek.

---

# 13. FORMAT LAPORAN SETIAP SELESAI MENGERJAKAN TUGAS

Setiap kali selesai melakukan perubahan, AI harus memberikan laporan yang menjelaskan:

## A. Perubahan yang Dilakukan

Sebutkan file yang benar-benar diubah.

## B. Alasan Perubahan

Jelaskan masalah yang diselesaikan dan alasan solusi tersebut dipilih.

## C. Bagian yang Dipertahankan

Sebutkan bagian penting yang tidak diubah apabila relevan.

## D. Pengujian

Jelaskan pengujian yang benar-benar dilakukan, termasuk perintah yang digunakan dan hasilnya.

Apabila belum diuji, tuliskan secara eksplisit bahwa pengujian belum dilakukan.

## E. Catatan Lanjutan

Sampaikan potensi dampak, keterbatasan, atau pekerjaan yang masih perlu dilakukan.

Jangan membuat laporan seolah-olah seluruh sistem telah selesai apabila yang dikerjakan hanya sebagian kecil.

---

# 14. PRINSIP KOMUNIKASI AI DENGAN PENGEMBANG

AI harus membantu pengembang memahami perubahan, bukan sekadar menghasilkan kode.

Ketentuan komunikasi:

- Gunakan penjelasan yang langsung dan spesifik.
- Jangan memberikan asumsi seolah-olah fakta.
- Bedakan antara kondisi yang sudah diperiksa dan dugaan.
- Jangan menyembunyikan error.
- Jangan mengklaim suatu fitur sudah selesai apabila belum.
- Berikan lokasi kode secara jelas.
- Jelaskan hubungan perubahan dengan file lain.
- Jangan memberikan alternatif yang mengubah rancangan tanpa menjelaskan konsekuensinya.
- Apabila konteks tidak cukup, sampaikan informasi yang belum tersedia.

Untuk perubahan kode yang kecil, berikan perubahan pada bagian yang relevan, bukan seluruh file.

Berikan satu file lengkap hanya apabila memang diminta atau diperlukan untuk menghindari ketidakjelasan.

---

# 15. CHECKLIST WAJIB SEBELUM AI MENGUBAH KODE

Sebelum melakukan perubahan, pastikan seluruh poin berikut telah dipertimbangkan:

- [ ] Saya sudah membaca `AGENTS.md`.
- [ ] Saya memahami tujuan perubahan yang diminta.
- [ ] Saya sudah memeriksa file yang berkaitan.
- [ ] Saya sudah memeriksa hubungan dengan file lain.
- [ ] Saya tidak menghapus atribut atau method yang sudah ditetapkan.
- [ ] Saya tidak mengubah struktur database yang telah ditetapkan dalam laporan rancangan, termasuk tabel, kolom, relasi, constraint, primary key, foreign key, dan VIEW.
- [ ] Saya memastikan nama class, atribut, method, serta tipe data sama persis dengan yang ditetapkan dalam laporan rancangan. Tidak boleh mengubah, mengganti, atau menyesuaikannya berdasarkan preferensi AI sendiri.
- [ ] Saya tidak mengubah bagian yang tidak berkaitan.
- [ ] Saya sudah mempertimbangkan dampak terhadap DAO, Service, Controller, Model, dan FXML apabila relevan.
- [ ] Saya tidak mengganggu pekerjaan anggota kelompok lain.
- [ ] Saya sudah menentukan cara memeriksa hasil perubahan.

Apabila terdapat poin penting yang belum terpenuhi, lakukan pemeriksaan terlebih dahulu sebelum mengubah kode.

---

# 16. PERINTAH UTAMA UNTUK AI

AI harus selalu mengingat prinsip berikut selama bekerja pada proyek TAPAK:

> Proyek ini memiliki rancangan, struktur, implementasi, dan pembagian kerja yang telah disepakati. Tugas AI adalah membantu mengembangkan dan memperbaiki sistem berdasarkan konteks tersebut, bukan membangun ulang proyek berdasarkan preferensi AI sendiri.

> Jangan mengubah sesuatu hanya karena ada cara lain yang dianggap lebih baik. Pastikan perubahan memang diperlukan, sesuai rancangan, dan tidak merusak bagian yang sudah ada.

> Pertahankan seluruh ketentuan yang telah disepakati. Apabila perlu menambahkan sesuatu, pastikan penambahan tersebut kompatibel dengan implementasi sebelumnya.

# PRINSIP FINAL

## NGURANG GABOLEH, NAMBAH BOLEH.

**PAHAMI PROYEK TERLEBIH DAHULU. PERIKSA DAMPAK SEBELUM MENGUBAH. JANGAN MENGAMBIL KEPUTUSAN RANCANGAN SECARA SEPIHAK.**