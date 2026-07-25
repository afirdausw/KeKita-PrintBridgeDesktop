# Changelog - PrintBridge Desktop

Semua perubahan penting pada proyek **PrintBridge Desktop** didokumentasikan di file ini.
Format ini mengikuti prinsip [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

---

## [1.0.0] - 2026-07-25

### ✨ Fitur Baru (Added)
- **Pengaturan Logo Struk (ESC/POS Raster Graphics)**:
  - Fitur upload file gambar logo (`PNG`/`JPG`/`BMP`) dari UI `Konfigurasi Printer`.
  - Otomatis me-resize gambar (Maks 384px untuk kertas 58mm / 512px untuk 80mm) dan mengonversinya menjadi **1-bit Black & White (Monochrome)**.
  - Menyimpan file logo secara permanen di `config/logo.png`.
  - Otomatis menyisipkan byte logo ESC/POS (`GS v 0`) di bagian paling atas setiap cetakan struk.
- **Pengaturan Cash Drawer (Laci Kasir)**:
  - Checkbox sakelar buka laci kasir otomatis.
  - Mengirimkan pulsa sinyal ESC/POS (`ESC p 0 25 250`) saat transaksi diproses.
- **Pengaturan Auto Cut (Pemotong Kertas)**:
  - Sakelar pengatur pemotong kertas otomatis (`GS V 65 1`).
  - Menghindari delay/error pada printer mini portabel tanpa cutter fisik jika Auto Cut dinonaktifkan.
- **Local HTTP REST API Service**:
  - Diintegrasikan dengan **Javalin 6.7.0** pada port default `8080`.
  - Dukungan penuh **CORS (Cross-Origin Resource Sharing)** untuk mengizinkan Web POS browser memanggil API secara bebas.
  - Endpoint `/status`, `/printers`, `/print`, `/print/raw`, `/printer/raw`, dan `/test`.
- **Deteksi Printer Fleksibel**:
  - Mengintegrasikan deteksi otomatis printer Windows (Print Spooler `javax.print`) dan Bluetooth Serial SPP (COM Ports: COM9, COM12, COM13, dll).
  - Skema prioritas pemilihan printer: Request Payload > `application.json` > Virtual Printer Fallback.
- **Antarmuka Desktop Swing Modern (FlatLaf)**:
  - Menggunakan tema **FlatLaf Light** (Windows 11 Modern Flat Style).
  - Integrasi font Google Fonts **Reddit Sans**.
  - Penataan tata letak presisi rata kiri (*Flush-left alignment*) di seluruh halaman.
  - Navigasi Sidebar (Dashboard, Printer, Server, Logs, Bantuan).
  - Garis pemisah (*divider line*) header yang bersih di bawah judul halaman.
- **Fitur System Tray**:
  - Tombol Close (X) meminimalkan aplikasi ke Windows System Tray tanpa menghentikan HTTP Service.
  - Context menu pada Tray Icon (Buka PrintBridge, Start Server, Stop Server, Keluar).
- **Log Monitor Real-time**:
  - Menampilkan log aktivitas koneksi printer, HTTP request, dan status service secara *thread-safe*.
  - Fitur Salin Log dan Bersihkan Log.

### 🎨 Perbaikan UI & UX (Changed)
- Mengubah skema warna indikator status server: **Hijau** (Running) dan **Merah** (Stopped).
- Merapikan spasi margin hasil cetakan struk (*print-out*) fisik agar hemat kertas.
- Mematikan log verbose Jetty DEBUG di console agar terminal lebih bersih.

### 🛡️ Keamanan & Stabilitas (Security & Reliability)
- Menambahkan fallback otomatis parsing data cetak dari Base64 ke UTF-8 plain text string.
- Penanganan error runtime yang aman untuk koneksi serial Bluetooth COM port yang tidak merespons.
