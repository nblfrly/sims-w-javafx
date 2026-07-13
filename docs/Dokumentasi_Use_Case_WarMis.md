# Dokumentasi Use Case WarMis

## Informasi Dokumen

| Item | Keterangan |
| --- | --- |
| Nama sistem | WarMis — Warmindo Management Information System |
| Tujuan | Mengelola inventaris, menu, resep, penggunaan barang, riwayat, dan pegawai Warmindo. |
| Aktor | Admin dan Pegawai |
| Penyimpanan data | XML melalui XStream |

## Hak Akses Aktor

| Fitur | Admin | Pegawai |
| --- | --- | --- |
| Login dan melihat Dashboard | Ya | Ya |
| Melihat Inventaris, Menu, Resep, Penggunaan Barang, Riwayat | Ya | Ya |
| Tambah, ubah, hapus Inventaris | Ya | Tidak |
| Tambah, ubah, hapus Menu | Ya | Tidak |
| Tambah, ubah, hapus Resep | Ya | Tidak |
| Menggunakan barang berdasarkan resep | Ya | Ya |
| Ekspor Riwayat | Ya | Tidak |
| Mengelola seluruh data Pegawai | Ya | Tidak |
| Mengubah password akun sendiri | Ya | Ya |

---

## UC-00 Login

**Deskripsi:** Memungkinkan Admin atau Pegawai masuk ke sistem menggunakan username dan password.

**Aktor:** Admin, Pegawai.

**Prasyarat:** Akun pengguna telah tersimpan pada sistem.

**Pemicu:** Pengguna membuka aplikasi WarMis.

**Alur utama:**

1. Admin atau Pegawai membuka aplikasi.
2. Sistem menampilkan halaman Login.
3. Pengguna memasukkan username dan password.
4. Pengguna menekan tombol Login.
5. Sistem memvalidasi username dan password dengan data pegawai yang tersimpan.
6. Sistem menyimpan sesi pengguna aktif beserta perannya.
7. Sistem membuka Dashboard dan menampilkan nama serta role pengguna pada header.

**Alur alternatif:**

- 5a. Jika username atau password tidak cocok, sistem menampilkan pesan “Username atau Password salah!” dan pengguna tetap berada pada halaman Login.

**Pasca-kondisi:** Sesi pengguna aktif tersedia sampai pengguna menekan tombol Keluar.

**Kebutuhan sistem:**

- Menampilkan form username dan password.
- Melakukan validasi data login.
- Menentukan hak akses Admin atau Pegawai.
- Menampilkan pesan kesalahan bila login gagal.
- Menjaga informasi pengguna aktif pada setiap halaman setelah login.

---

## UC-01 Kelola Inventaris

**Deskripsi:** Menampilkan dan mengelola data barang, stok, kategori, serta batas stok minimum.

**Aktor:** Admin, Pegawai.

**Prasyarat:** Pengguna telah login.

**Pemicu:** Pengguna memilih menu Inventaris atau kartu Stok Menipis pada Dashboard.

**Alur utama melihat dan mencari data:**

1. Pengguna membuka modul Inventaris.
2. Sistem mengambil data barang dari penyimpanan dan menampilkannya pada tabel.
3. Sistem menampilkan nama barang, kategori, stok, batas minimum, dan status stok.
4. Pengguna memasukkan kata kunci pada kolom pencarian.
5. Sistem memfilter tabel berdasarkan nama barang atau kategori.
6. Pengguna dapat memilih data dari tabel atau saran nama item.
7. Sistem mengisi otomatis form Detail Item sesuai data yang dipilih.

**Alur utama tambah atau ubah data (Admin):**

1. Admin mengisi nama item, kategori, stok, dan batas minimum.
2. Sistem memeriksa apakah nama barang sudah ada.
3. Bila nama barang sudah ada, sistem meminta Admin memilih data lama untuk diperbarui.
4. Bila kategori belum tersedia, sistem menampilkan popup konfirmasi kategori baru.
5. Admin menyetujui penambahan kategori baru.
6. Admin menekan tombol Tambah Item atau Update Item.
7. Sistem menyimpan perubahan barang dan membuat riwayat stok.

**Alur utama hapus data (Admin):**

1. Admin memilih item pada tabel.
2. Admin menekan tombol Hapus Item.
3. Sistem menampilkan konfirmasi penghapusan.
4. Admin menyetujui konfirmasi.
5. Sistem menghapus item dan membuat riwayat penghapusan.
6. Sistem memuat ulang tabel; kategori khusus yang sudah tidak dipakai item mana pun tidak lagi muncul pada daftar kategori.

**Alur alternatif:**

- Jika Pegawai menekan tombol tambah, ubah, atau hapus, sistem menampilkan pesan bahwa pengguna tidak memiliki hak akses.
- Jika stok atau batas minimum bukan angka atau bernilai negatif, sistem menampilkan pesan validasi.
- Jika data pencarian tidak ditemukan, tabel tidak menampilkan hasil yang cocok.

**Pasca-kondisi:** Data inventaris dan riwayat stok diperbarui setelah aksi Admin berhasil.

**Kebutuhan sistem:**

- Menyediakan pencarian berdasarkan nama dan kategori.
- Mencegah nama barang duplikat, termasuk perbedaan huruf besar-kecil.
- Menyediakan saran nama barang dan kategori.
- Menampilkan status **Aman** bila stok di atas batas minimum, **Menipis** bila stok lebih dari nol dan kurang dari atau sama dengan batas minimum, serta **Habis** bila stok nol.
- Menampilkan daftar stok menipis saat pengguna membuka kartu Stok Menipis pada Dashboard.

---

## UC-02 Kelola Menu Warmindo

**Deskripsi:** Mengelola daftar menu Warmindo, harga, kategori, dan status ketersediaannya.

**Aktor:** Admin, Pegawai.

**Prasyarat:** Pengguna telah login.

**Pemicu:** Pengguna memilih menu Menu Warmindo atau kartu Total Menu pada Dashboard.

**Alur utama melihat dan mencari data:**

1. Pengguna membuka modul Menu Warmindo.
2. Sistem menampilkan daftar menu pada tabel.
3. Pengguna dapat mencari menu berdasarkan nama atau kategori.
4. Pengguna dapat mengetik nama menu pada form.
5. Sistem menampilkan saran menu yang sesuai.
6. Saat menu lama dipilih, sistem mengisi nama, harga, kategori, dan status secara otomatis.

**Alur utama tambah atau ubah menu (Admin):**

1. Admin mengisi nama menu, harga, kategori, dan status.
2. Sistem memvalidasi seluruh input.
3. Sistem memeriksa duplikasi nama menu.
4. Admin menekan Tambah Menu atau Update Menu.
5. Sistem menyimpan data menu dan memuat ulang tabel.

**Alur utama hapus menu (Admin):**

1. Admin memilih menu pada tabel.
2. Admin menekan Hapus Menu dan menyetujui konfirmasi.
3. Sistem menghapus menu dan memuat ulang tabel.

**Alur alternatif:**

- Jika Pegawai menjalankan aksi tambah, ubah, atau hapus, sistem menampilkan pesan tidak memiliki hak akses.
- Jika harga bukan angka atau bernilai negatif, sistem menampilkan pesan validasi.
- Jika nama menu sudah tersedia, sistem menolak penambahan menu duplikat.

**Pasca-kondisi:** Daftar menu diperbarui sesuai aksi Admin.

**Kebutuhan sistem:**

- Menyediakan status menu Aktif dan Nonaktif.
- Mencegah nama menu duplikat.
- Menyediakan pencarian, saran nama menu, dan saran kategori.
- Menyediakan tombol Kelola Resep Bahan untuk membuka UC-03.

---

## UC-03 Kelola Resep

**Deskripsi:** Menghubungkan menu dengan barang atau bahan baku beserta jumlah pemakaian per porsi.

**Aktor:** Admin, Pegawai.

**Prasyarat:** Data menu dan data barang telah tersedia.

**Pemicu:** Pengguna memilih tombol Kelola Resep Bahan pada modul Menu Warmindo.

**Alur utama:**

1. Sistem menampilkan daftar resep yang telah tersimpan.
2. Pengguna memilih menu, bahan baku, dan jumlah dipakai.
3. Sistem menerima jumlah dipakai sebagai bilangan bulat.
4. Admin menekan tombol Tambah Resep.
5. Sistem memeriksa apakah kombinasi menu dan barang sudah tersedia.
6. Jika belum ada, sistem menyimpan resep baru.
7. Jika pengguna memilih baris resep pada tabel, sistem mengisi form otomatis dan tombol berubah menjadi Update Resep.
8. Admin dapat memperbarui atau menghapus resep yang dipilih.

**Alur alternatif:**

- Jika Pegawai melakukan tambah, ubah, atau hapus, sistem menampilkan pesan tidak memiliki hak akses.
- Jika kombinasi menu dan barang sudah ada, sistem menampilkan pesan bahwa resep sudah tersedia.
- Jika jumlah tidak berupa bilangan bulat positif, sistem menampilkan pesan validasi.

**Pasca-kondisi:** Resep baru atau perubahan resep tersimpan dan siap dipakai pada transaksi penggunaan barang.

**Kebutuhan sistem:**

- Menampilkan nama menu dan bahan baku pada tabel resep.
- Menyimpan jumlah pemakaian sebagai integer.
- Mencegah resep duplikat untuk kombinasi menu dan barang yang sama.
- Mengubah label tombol dari Tambah Resep menjadi Update Resep saat data dipilih.

---

## UC-04 Penggunaan Barang

**Deskripsi:** Mengurangi stok bahan baku berdasarkan menu, resep, dan jumlah porsi yang akan diproduksi.

**Aktor:** Admin, Pegawai.

**Prasyarat:** Menu berstatus Aktif, resep, dan data barang tersedia.

**Pemicu:** Pengguna memilih menu Penggunaan Barang.

**Alur utama:**

1. Pengguna membuka modul Penggunaan Barang.
2. Sistem menampilkan daftar menu Aktif.
3. Pengguna memilih menu dan memasukkan jumlah porsi.
4. Pengguna menekan Preview Penggunaan.
5. Sistem mengambil resep menu lalu menghitung kebutuhan tiap bahan.
6. Sistem menampilkan stok awal, jumlah yang akan digunakan, serta sisa stok perkiraan.
7. Jika stok mencukupi, pengguna menekan Gunakan Barang.
8. Sistem menghitung ulang data stok terbaru, mengurangi stok setiap bahan, dan menyimpan riwayat penggunaan.
9. Sistem menampilkan pesan transaksi berhasil dan mereset form.

**Alur alternatif:**

- Jika menu belum dipilih, resep tidak tersedia, atau jumlah porsi tidak valid, sistem menampilkan peringatan.
- Jika stok bahan tidak mencukupi, sistem menampilkan bahan yang kurang dan menonaktifkan penggunaan.

**Pasca-kondisi:** Stok barang berkurang sesuai resep dan jumlah porsi; riwayat penggunaan tersimpan.

**Kebutuhan sistem:**

- Hanya menampilkan menu Aktif.
- Menghitung penggunaan dengan bilangan bulat.
- Mencegah stok negatif.
- Menampilkan preview sebelum stok benar-benar dikurangi.
- Menyimpan siapa pengguna yang melakukan transaksi.

---

## UC-05 Riwayat

**Deskripsi:** Menampilkan audit log perubahan inventaris dan penggunaan barang.

**Aktor:** Admin, Pegawai.

**Prasyarat:** Pengguna telah login.

**Pemicu:** Pengguna memilih menu Riwayat atau kartu Aktivitas Hari Ini pada Dashboard.

**Alur utama:**

1. Pengguna membuka modul Riwayat.
2. Sistem menampilkan tabel waktu, aktivitas, nama barang, stok lama, stok baru, dan pengguna.
3. Pengguna dapat mencari riwayat berdasarkan barang, user, atau aktivitas.
4. Pengguna dapat memfilter riwayat berdasarkan tanggal dan jenis aktivitas.
5. Sistem menampilkan hasil yang sesuai dengan filter.
6. Admin dapat mengekspor data yang tampil ke CSV.

**Alur alternatif:**

- Saat kartu Aktivitas Hari Ini dipilih, sistem membuka Riwayat dengan tanggal hari ini sebagai filter aktif.
- Untuk Pegawai, tombol Export tidak ditampilkan.
- Jika tidak ada riwayat yang sesuai, tabel menampilkan keadaan kosong.

**Pasca-kondisi:** Tidak ada perubahan data; pengguna memperoleh informasi audit log sesuai filter.

**Kebutuhan sistem:**

- Menyediakan filter tanggal, aktivitas, dan kata kunci.
- Menampilkan statistik total, tambah, update, dan hapus.
- Membatasi ekspor CSV hanya untuk Admin.

---

## UC-06 Notifikasi Stok

**Deskripsi:** Memberikan informasi kondisi stok agar pengadaan barang dapat dilakukan tepat waktu.

**Aktor:** Admin, Pegawai.

**Prasyarat:** Setiap barang memiliki stok dan batas minimum stok.

**Pemicu:** Sistem memuat data inventaris atau pengguna membuka Dashboard.

**Alur utama:**

1. Sistem membandingkan stok saat ini dengan stok minimum setiap barang.
2. Sistem menentukan status stok setiap barang.
3. Sistem menghitung jumlah barang dengan stok yang perlu diperhatikan.
4. Dashboard menampilkan jumlah tersebut pada kartu Stok Menipis.
5. Pengguna dapat memilih kartu tersebut.
6. Sistem membuka Inventaris dengan filter stok menipis aktif.

**Aturan status:**

| Kondisi | Status |
| --- | --- |
| Stok = 0 | Habis |
| Stok lebih dari 0 dan stok ≤ stok minimum | Menipis |
| Stok > stok minimum | Aman |

**Kebutuhan sistem:**

- Menampilkan status Aman, Menipis, dan Habis pada tabel Inventaris.
- Menyediakan navigasi cepat dari Dashboard ke inventaris yang membutuhkan perhatian.

---

## UC-07 Manajemen Pegawai

**Deskripsi:** Mengelola data akun pegawai dan memberikan akses ubah password mandiri bagi Pegawai.

**Aktor:** Admin, Pegawai.

**Prasyarat:** Pengguna telah login.

**Pemicu:** Pengguna memilih menu Pegawai.

**Alur utama Admin:**

1. Admin membuka modul Pegawai.
2. Sistem menampilkan seluruh daftar pegawai.
3. Admin dapat mencari, memilih, menambah, memperbarui, atau menghapus akun pegawai.
4. Admin mengisi nama, username, password, dan role.
5. Sistem menyimpan perubahan dan memuat ulang tabel.

**Alur utama Pegawai:**

1. Pegawai membuka modul Pegawai.
2. Sistem menampilkan informasi akun Pegawai yang sedang login pada form.
3. Sistem menonaktifkan tabel, pencarian, nama, username, role, dan tombol hapus.
4. Pegawai memasukkan password baru.
5. Pegawai menekan tombol Ubah Password Saya.
6. Sistem memperbarui password akun Pegawai yang sedang login.

**Alur alternatif:**

- Jika Pegawai mengosongkan password baru, sistem menampilkan peringatan.
- Jika proses penyimpanan gagal, sistem menampilkan pesan kesalahan.

**Pasca-kondisi:** Data akun berubah sesuai hak akses pengguna; Pegawai hanya dapat mengubah password miliknya sendiri.

**Kebutuhan sistem:**

- Menyediakan role Admin dan Pegawai.
- Admin dapat mengelola seluruh akun.
- Pegawai tidak dapat memilih data pegawai lain atau mengubah role.
- Pegawai hanya dapat mengubah password akun sendiri.
- Sistem menampilkan nama dan role pengguna aktif pada header halaman.
