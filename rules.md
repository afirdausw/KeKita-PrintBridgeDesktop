# PrintBridge - Development Rules

## Project Overview

PrintBridge adalah aplikasi desktop berbasis Java yang bertugas sebagai bridge antara aplikasi web dan printer ESC/POS.

Bridge berjalan sebagai local service dan menyediakan REST API sehingga aplikasi web dapat melakukan pencetakan tanpa bergantung pada browser, driver tertentu, ataupun jenis koneksi printer.

Target platform:

- Windows 10
- Windows 11

Target printer:

- USB ESC/POS
- Bluetooth ESC/POS (Serial/SPP)
- Network ESC/POS (TCP/IP)

---

# Development Principles

## 1. Clean Architecture

Setiap fitur harus dipisahkan berdasarkan tanggung jawab.

Contoh:

```
api/
printer/
escpos/
config/
server/
tray/
service/
```

Tidak diperbolehkan membuat class besar (>500 baris) yang menangani banyak fungsi sekaligus.

---

## 2. SOLID Principle

Seluruh kode harus mengikuti prinsip SOLID.

Printer harus menggunakan interface.

Contoh:

```
Printer
    ├── UsbPrinter
    ├── BluetoothPrinter
    └── NetworkPrinter
```

---

## 3. Tidak ada Hardcode

Semua konfigurasi berada pada file konfigurasi.

Contoh:

```
config.json
```

---

## 4. Semua API menggunakan JSON

Request:

```json
{
    "printer":"Kasir Depan",
    "paper":"58",
    "text":"Hello"
}
```

Response:

```json
{
    "success":true,
    "message":"Printed"
}
```

---

## 5. Logging

Semua aktivitas wajib dicatat.

Contoh:

- Application Start
- Application Stop
- Printer Connected
- Printer Disconnected
- Print Success
- Print Failed
- API Request

---

## 6. Error Handling

Tidak boleh menggunakan stacktrace sebagai response API.

Gunakan format:

```json
{
    "success":false,
    "message":"Printer not found"
}
```

---

## 7. Configuration

Semua konfigurasi disimpan pada:

```
config/config.json
```

Contoh:

- Port API
- Printer Default
- Paper Size
- Auto Start
- Auto Reconnect

---

## 8. REST API First

Seluruh fitur harus dapat digunakan melalui REST API.

UI Desktop hanyalah antarmuka.

---

## 9. UI

Menggunakan FlatLaf.

Dark Mode.

Tidak menggunakan Swing bawaan.

---

## 10. Code Style

- Java 21
- Maven
- CamelCase
- Maksimal 1 public class per file
- Hindari static kecuali utilitas

---

## 11. Dependency

Library seminimal mungkin.

Prioritas:

- jSerialComm
- Jackson
- NanoHTTPD / SparkJava
- FlatLaf

---

## 12. Future Ready

Struktur harus memudahkan penambahan:

- QR Code
- Barcode
- Logo
- Cash Drawer
- Auto Cutter
- Multi Printer
- Print Queue
- WebSocket
- Authentication