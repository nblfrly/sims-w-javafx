# Dokumentasi Use Case WarMis

## UC-00 Login

**Deskripsi:** Memungkinkan Admin atau Pegawai masuk ke sistem menggunakan username dan password.

**Aktor:** Admin, Pegawai.

**Prasyarat:** Akun pengguna telah tersimpan pada sistem.

**Urutan langkah:**

1. Admin atau Pegawai membuka halaman Login.
2. Sistem menampilkan form username dan password.
3. Pengguna memasukkan username dan password.
4. Sistem memvalidasi kredensial dengan data pengguna yang tersimpan.
5. Jika valid, sistem menyimpan sesi pengguna dan membuka Dashboard.
6. Jika tidak valid, sistem menampilkan pesan kesalahan dan pengguna mengulangi input.

**Kebutuhan sistem:**

- Menampilkan halaman login sebagai halaman awal.
- Memvalidasi username dan password.
- Menentukan peran Admin atau Pegawai.
- Membatasi menu Manajemen Pegawai untuk peran Pegawai.
- Menampilkan informasi pengguna aktif pada header aplikasi.

## UC-01 Kelola Inventaris

**Deskripsi:** Menampilkan dan mengelola data barang, stok, kategori, serta batas stok minimum.

**Aktor:** Admin, Pegawai; penghapusan barang hanya untuk Admin.

**Prasyarat:** Pengguna telah login.

**Urutan langkah:**

1. Pengguna memilih menu Inventaris.
2. Sistem mengambil dan menampilkan daftar barang pada tabel.
3. Pengguna dapat mencari barang berdasarkan nama atau kategori.
4. Pengguna memilih data barang pada tabel atau saran nama item untuk mengisi form secara otomatis.
5. Pengguna menambah atau memperbarui stok barang.
6. Admin dapat menghapus barang setelah konfirmasi.
7. Sistem menyimpan perubahan dan mencatat riwayat stok.

**Kebutuhan sistem:**

- Menampilkan daftar barang, kategori, stok, stok minimum, dan status.
- Menyediakan pencarian berdasarkan nama maupun kategori.
- Memberikan saran nama barang dan kategori yang sudah ada.
- Mencegah nama barang duplikat.
- Menampilkan status Aman, Menipis, atau Habis.
- Menampilkan pesan saat data yang dicari tidak tersedia.

## UC-02 Kelola Menu Warmindo

**Deskripsi:** Mengelola daftar menu, harga, kategori, dan status ketersediaan menu Warmindo.

**Aktor:** Admin, Pegawai.

**Prasyarat:** Pengguna telah login.

**Urutan langkah:**

1. Pengguna memilih menu Menu Warmindo.
2. Sistem menampilkan daftar menu pada tabel.
3. Pengguna memasukkan nama, harga, kategori, dan status menu.
4. Sistem memberi saran menu atau kategori yang telah tersimpan.
5. Saat pengguna memilih menu yang ada, form terisi otomatis untuk pembaruan.
6. Sistem menambah, memperbarui, atau menghapus data menu sesuai tindakan pengguna.

**Kebutuhan sistem:**

- Mencegah nama menu duplikat.
- Menyediakan pencarian berdasarkan nama dan kategori.
- Menyediakan status Aktif dan Nonaktif.
- Menyediakan akses ke Kelola Resep untuk menu terpilih.

## UC-03 Kelola Resep

**Deskripsi:** Menghubungkan menu dengan bahan baku dan jumlah pemakaian per porsi.

**Aktor:** Admin, Pegawai.

**Prasyarat:** Data menu dan barang telah tersedia.

**Urutan langkah:**

1. Pengguna membuka Kelola Resep dari modul Menu Warmindo.
2. Sistem menampilkan daftar resep yang telah tersimpan.
3. Pengguna memilih menu, bahan baku, dan jumlah dipakai berbentuk bilangan bulat.
4. Sistem menambahkan resep baru bila kombinasi menu dan barang belum ada.
5. Saat pengguna memilih baris resep pada tabel, form terisi otomatis dan tombol berubah menjadi Update Resep.
6. Pengguna dapat memperbarui, menghapus, atau mereset form resep.

**Kebutuhan sistem:**

- Menyimpan jumlah bahan sebagai bilangan bulat.
- Mencegah resep duplikat untuk kombinasi menu dan barang yang sama.
- Menampilkan nama menu dan barang pada tabel resep.

## UC-04 Penggunaan Barang

**Deskripsi:** Mengurangi stok bahan baku berdasarkan menu, resep, dan jumlah porsi yang diproduksi.

**Aktor:** Admin, Pegawai.

**Prasyarat:** Menu aktif, resep, dan data barang tersedia.

**Urutan langkah:**

1. Pengguna membuka Penggunaan Barang.
2. Pengguna memilih menu aktif dan jumlah porsi.
3. Sistem menghitung kebutuhan tiap bahan berdasarkan resep.
4. Sistem menampilkan preview stok awal, jumlah dipakai, dan sisa stok.
5. Jika stok mencukupi, pengguna mengonfirmasi penggunaan barang.
6. Sistem mengurangi stok dan menyimpan riwayat penggunaan.

**Kebutuhan sistem:**

- Mencegah stok menjadi negatif.
- Menampilkan informasi bahan yang stoknya tidak mencukupi.
- Menghitung kebutuhan bahan dengan bilangan bulat.
- Memperbarui stok dan riwayat penggunaan secara konsisten.

## UC-05 Riwayat

**Deskripsi:** Menampilkan audit log perubahan stok barang.

**Aktor:** Admin, Pegawai.

**Prasyarat:** Pengguna telah login.

**Urutan langkah:**

1. Pengguna membuka menu Riwayat.
2. Sistem menampilkan seluruh aktivitas inventaris.
3. Pengguna dapat mencari berdasarkan barang, user, atau aktivitas.
4. Pengguna dapat memfilter berdasarkan tanggal dan jenis aktivitas.
5. Pengguna dapat mereset filter atau mengekspor hasil menjadi CSV.

**Kebutuhan sistem:**

- Menampilkan waktu, aktivitas, barang, stok lama, stok baru, dan user.
- Menyediakan filter tanggal, aktivitas, dan kata kunci.
- Menampilkan riwayat hari ini ketika kartu Aktivitas Hari Ini di Dashboard dipilih.

## UC-06 Notifikasi Stok

**Deskripsi:** Memberikan informasi kondisi stok barang agar pengguna dapat melakukan pengadaan tepat waktu.

**Aktor:** Admin, Pegawai.

**Prasyarat:** Setiap barang memiliki nilai stok minimum.

**Urutan langkah:**

1. Sistem membandingkan stok saat ini dengan stok minimum setiap barang.
2. Sistem menandai barang menjadi Aman, Menipis, atau Habis.
3. Dashboard menampilkan jumlah stok yang perlu diperhatikan.
4. Pengguna dapat memilih kartu Stok Menipis untuk membuka Inventaris dengan filter stok menipis.

**Kebutuhan sistem:**

- Status Aman untuk stok di atas batas minimum.
- Status Menipis untuk stok lebih dari nol namun kurang dari atau sama dengan batas minimum.
- Status Habis untuk stok bernilai nol.
- Navigasi cepat dari Dashboard ke daftar barang yang memerlukan perhatian.

## UC-07 Manajemen Pegawai

**Deskripsi:** Mengelola data akun pegawai dan peran pengguna aplikasi.

**Aktor:** Admin.

**Prasyarat:** Admin telah login.

**Urutan langkah:**

1. Admin membuka menu Pegawai.
2. Sistem menampilkan daftar pegawai.
3. Admin memasukkan atau memilih data pegawai.
4. Admin menambah, memperbarui, atau menghapus data pegawai.
5. Sistem menyimpan data nama, username, password, dan role.

**Kebutuhan sistem:**

- Menyediakan role Admin dan Pegawai.
- Mencegah akses modul Manajemen Pegawai untuk role Pegawai.
- Menampilkan pencarian data pegawai.
- Menampilkan informasi pengguna aktif pada header halaman.
