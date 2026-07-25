# Dokumentasi REST API - PrintBridge Desktop

PrintBridge menyediakan Local REST API berbasis HTTP yang berjalan di port default `8080` (dapat dikonfigurasi). API ini dirancang untuk diakses oleh aplikasi Web POS, Kasir, atau sistem eksternal tanpa memerlukan instalasi driver printer khusus.

---

## 📌 Base URL
```http
http://127.0.0.1:8080
```
*Atau `http://localhost:8080`*

---

## 🌐 Fitur CORS (Cross-Origin Resource Sharing)
PrintBridge secara otomatis mengaktifkan **CORS All Origins (`*`)**, sehingga aplikasi Web POS berbasis browser (React, Vue, Angular, PHP, HTML/JS) dari origin mana pun dapat melakukan HTTP Request tanpa terhalang browser CORS policy.

---

## 📋 Daftar Endpoint

### 1. Cek Status Service
Mengembalikan informasi status aplikasi dan versi PrintBridge.

* **URL**: `/status`
* **Method**: `GET` | `POST`
* **Headers**: `Accept: application/json`

#### Response (`200 OK`):
```json
{
  "success": true,
  "message": "PrintBridge is running",
  "data": {
    "name": "PrintBridge",
    "version": "1.0.0"
  }
}
```

---

### 2. Daftar Printer Terdeteksi
Mengambil seluruh daftar printer Windows (Print Spooler) dan Bluetooth Serial COM Ports yang siap digunakan.

* **URL**: `/printers`
* **Method**: `GET` | `POST`
* **Headers**: `Accept: application/json`

#### Response (`200 OK`):
```json
{
  "success": true,
  "message": "Printer list",
  "data": [
    {
      "name": "POS-80 (Bluetooth)",
      "type": "Bluetooth / Serial (COM9)",
      "default": true
    },
    {
      "name": "EPSON TM-T82 Receipt",
      "type": "Windows Spooler",
      "default": false
    }
  ]
}
```

---

### 3. Cetak Struk (Print ESC/POS)
Mengirimkan perintah cetak struk ke printer thermal terpilih atau ke Default Printer PrintBridge.

* **URL**: `/print` | `/print/raw` | `/printer/raw`
* **Method**: `POST`
* **Headers**: `Content-Type: application/json`

#### Request Payload Model 1 (Plain Text Struk):
```json
{
  "printer": "COM9",
  "text": "        KOPERASI SIKOMO        \n     Toko Sembako Koperasi     \n--------------------------------\nJeruk Medan Manis x 5  Rp 140.000\n--------------------------------\nTOTAL                 Rp 140.000\n\n\n"
}
```

#### Request Payload Model 2 (Base64 Byte ESC/POS):
```json
{
  "printer": "",
  "data": "G0BHAQAqKiomVUVTVCBQUklOVCoqKg0K"
}
```
*Catatan: Jika field `"printer"` dikosongkan (`""`), PrintBridge akan otomatis menggunakan **Default Printer** yang dikonfigurasikan pada aplikasi desktop.*

#### Response Sukses (`200 OK`):
```json
{
  "success": true,
  "message": "Print success",
  "data": null
}
```

#### Response Error (`500 Internal Server Error`):
```json
{
  "success": false,
  "message": "Data cetak (data / text) tidak boleh kosong.",
  "data": null
}
```

---

### 4. Trigger Test Print
Memicu pencetakan struk pengujian (Test Print) ke printer terpilih.

* **URL**: `/test`
* **Method**: `POST`
* **Headers**: `Content-Type: application/json`

#### Request Payload:
```json
{
  "printer": "COM9"
}
```
*Atau kirim `{}` untuk menggunakan Default Printer.*

#### Response Sukses (`200 OK`):
```json
{
  "success": true,
  "message": "Test print success",
  "data": null
}
```

---

## 💻 Contoh Integrasi Client (Javascript `fetch`)

```javascript
async function cetakStrukPOS(teksStruk) {
    try {
        const response = await fetch("http://127.0.0.1:8080/print", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                printer: "", // Menggunakan default printer
                text: teksStruk
            })
        });

        const result = await response.json();
        if (result.success) {
            console.log("Struk berhasil terkirim ke PrintBridge!");
        } else {
            alert("Gagal cetak: " + result.message);
        }
    } catch (err) {
        alert("Gagal terhubung ke PrintBridge Desktop service!");
    }
}
```
