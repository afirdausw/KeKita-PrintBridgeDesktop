# Roadmap Pengembangan - PrintBridge Desktop

Dokumen ini memuat rencana fitur dan pengembangan masa depan untuk aplikasi **PrintBridge Desktop**.

---

## 🎯 Rencana Versi & Fitur

### 🟢 Short-Term (Versi 1.1.0)
* **Auto Update Launcher**:
  - Penambahan modul otomatis mengecek dan memperbarui versi aplikasi dari server rilis GitHub Releases.
* **Deteksi Network / LAN Thermal Printer**:
  - Penambahan modul scanning printer thermal jaringan via IP Address (Port 9100 TCP/IP).
* **Auto Start di Windows Login**:
  - Fitur registrasi Startup Windows Registry (`HKCU\Software\Microsoft\Windows\CurrentVersion\Run`) langsung dari UI Desktop.

---

### 🟡 Mid-Term (Versi 1.2.0)
* **Modul Render Gambar & Logo (Graphic ESC/POS)**:
  - Kemampuan menerima URL / Base64 gambar logo toko dan mengonversinya menjadi byte grafis ESC/POS (`GS v 0` / `ESC *`).
* **Generator Barcode & QR Code ESC/POS**:
  - Pustaka internal untuk merakit perintah cetak QR Code dan Barcode (GS ( k) secara otomatis dari string teks.
* **Fitur Open Cash Drawer**:
  - Endpoint `POST /openDrawer` untuk mengirimkan sinyal pulsa laci kasir (`ESC p 0 25 250`).

---

### 🔴 Long-Term (Versi 2.0.0)
* **Windows Native Background Service**:
  - Kemampuan berjalan sebagai Windows Service sejati tanpa memerlukan user login ke desktop.
* **WebSocket / MQTT Bridge**:
  - Mendukung koneksi dua arah real-time untuk mencetak struk secara instan dari aplikasi cloud tanpa polling HTTP.
* **Multi-Printer Grouping & Intelligent Fallback**:
  - Mengelompokkan printer dapur, printer kasir, dan printer bar dengan pengalihan otomatis ke printer cadangan jika salah satu printer habis kertas / mati.
