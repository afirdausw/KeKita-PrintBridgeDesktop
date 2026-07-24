# Product Requirement Document

# PrintBridge

Version 1.0

---

# Objective

Membuat aplikasi desktop yang dapat menerima perintah cetak dari aplikasi web dan meneruskannya ke printer ESC/POS melalui USB, Bluetooth, maupun LAN.

---

# Target User

- Kasir
- Restoran
- Cafe
- Klinik
- Rumah Sakit
- Perpustakaan
- Retail

---

# Platform

Desktop Windows

---

# Functional Requirements

## 1. Local REST API

Bridge menyediakan REST API.

Contoh:

GET /status

GET /printers

POST /print

POST /test

POST /openDrawer

---

## 2. Start Service

User dapat menjalankan service.

Status:

● Running

---

## 3. Stop Service

User dapat menghentikan service.

Status:

● Stopped

---

## 4. Auto Start

Bridge dapat otomatis berjalan saat Windows Login.

---

## 5. Printer Detection

Bridge dapat mendeteksi:

- USB
- Bluetooth
- Network

---

## 6. Printer Selection

User dapat memilih printer aktif.

Contoh:

Default Printer

[ POS Bluetooth ]

---

## 7. Test Print

User dapat melakukan test print.

Isi:

```
********************
PrintBridge
Test Print

Printer OK
********************
```

---

## 8. Paper Size

Pilihan:

○ 58 mm

○ 80 mm

---

## 9. Save Configuration

Semua konfigurasi otomatis disimpan.

---

## 10. Tray Application

Saat ditutup:

Tidak Exit.

Masuk ke System Tray.

---

## 11. Print Queue

Bridge memiliki antrean cetak.

Request masuk:

↓

Queue

↓

Printer

---

## 12. Logging

Halaman log menampilkan:

- Time
- Printer
- Status
- Message

---

# Non Functional Requirement

## Startup

< 3 detik

---

## API Response

< 100 ms

---

## Memory

< 150 MB

---

## CPU

Idle < 1%

---

# Future Feature

- Print HTML
- Print PDF
- Print Image
- Print QRCode
- Print Barcode
- Print Table
- Multi Printer
- Printer Group
- Authentication Token
- HTTPS
- Auto Update
- Silent Install
- WebSocket
- MQTT
- Cloud Sync

---

# REST API

GET /status

Response

```json
{
    "status":"running",
    "version":"1.0.0"
}
```

---

GET /printers

```json
[
    {
        "id":"1",
        "name":"POS Bluetooth",
        "type":"Bluetooth"
    }
]
```

---

POST /print

```json
{
    "printer":"POS Bluetooth",
    "paper":"58",
    "copies":1,
    "text":"Hello World"
}
```

---

POST /test

```json
{
    "printer":"POS Bluetooth"
}
```

---

# Desktop UI

Dashboard

```
--------------------------------------------

PrintBridge

Status

● Running

API

http://127.0.0.1:8080

--------------------------------------------

Printer

[ POS Bluetooth        ▼ ]

Connection

Bluetooth

Paper

(•) 58 mm

( ) 80 mm

--------------------------------------------

[ Test Print ]

[ Start Service ]

[ Stop Service ]

--------------------------------------------

Log

10:00 Running...

10:01 Printer Connected

10:02 Test Print Success

--------------------------------------------
```

---

# Success Criteria

Bridge dapat digunakan dari browser hanya dengan:

```javascript
fetch("http://127.0.0.1:8080/print", {
    method: "POST",
    body: JSON.stringify(...)
});
```

tanpa bergantung pada driver printer Windows.