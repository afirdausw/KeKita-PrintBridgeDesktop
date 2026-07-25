# Panduan Build & Release - PrintBridge Desktop

Dokumen ini berisi panduan kompilasi, pembuatan paket rilis, dan pendistribusian aplikasi **PrintBridge Desktop** untuk lingkungan Windows.

---

## 🛠️ Persyaratan Lingkungan (Prerequisites)

Sebelum melakukan build, pastikan perangkat pengembang telah terinstall:
- **Java Development Kit (JDK)**: OpenJDK 15+ atau JDK 21 LTS (Rekomendasi)
- **Apache Maven**: Versi 3.8+
- **Sistem Operasi Target**: Windows 10 / Windows 11 (64-bit)

---

## 📦 1. Kompilasi Source Code

Jalankan perintah berikut di Command Prompt / Terminal pada root folder project:

```cmd
mvn clean compile
```

---

## 🔨 2. Pembuatan Executable JAR (Fat / Shade JAR)

Untuk menggabungkan seluruh dependensi (Javalin, Jackson, FlatLaf, MigLayout, jSerialComm) ke dalam 1 file `.jar` yang dapat langsung dijalankan:

```cmd
mvn clean package
```

File hasil build akan tersimpan di directory `target/`:
```text
target/printbridge-1.0-SNAPSHOT.jar
```

---

## 🚀 3. Pembuatan Installer Standalone Windows (`.exe`)

Agar pengguna akhir (kasir/klien) dapat menginstall PrintBridge tanpa perlu menginstall Java (JRE) secara terpisah, manfaatkan **`jpackage`** bawaan JDK 21.

Pilih perintah sesuai terminal yang Anda gunakan di bawah ini:

### 🔹 Option A: Git Bash (1 Baris)
```bash
jpackage --type exe --name "PrintBridge" --app-version "1.0.0" --input target/ --main-jar printbridge-1.0-SNAPSHOT.jar --main-class devyana.kekita.printbridge.Main --icon src/main/resources/icons/icon.ico --win-shortcut --win-menu
```
Compressed :
```bash
jpackage --type app-image --name "PrintBridge" --app-version "1.0.0" --input target/ --main-jar printbridge-1.0-SNAPSHOT.jar --main-class devyana.kekita.printbridge.Main --icon src/main/resources/icons/icon.ico --jlink-options "--strip-debug --no-header-files --no-man-pages --compress=2"
```

### 🔹 Option B: Windows Command Prompt (CMD)
```cmd
jpackage ^
  --type exe ^
  --name "PrintBridge" ^
  --app-version "1.0.0" ^
  --input target/ ^
  --main-jar printbridge-1.0-SNAPSHOT.jar ^
  --main-class devyana.kekita.printbridge.Main ^
  --icon src/main/resources/icons/icon.ico ^
  --win-shortcut ^
  --win-menu
```

### 🔹 Option C: Windows PowerShell
```powershell
jpackage `
  --type exe `
  --name "PrintBridge" `
  --app-version "1.0.0" `
  --input target/ `
  --main-jar printbridge-1.0-SNAPSHOT.jar `
  --main-class devyana.kekita.printbridge.Main `
  --icon src/main/resources/icons/icon.ico `
  --win-shortcut `
  --win-menu
```

### Hasil Build:
Akan menghasilkan file installer **`PrintBridge-1.0.0.exe`**.

---

## 📋 Checklist Rilis Publikasi

Sebelum mendistribusikan installer rilis ke pengguna akhir:
1. [x] Verifikasi nomor versi pada `application.json` sudah sesuai (`1.0.0`).
2. [x] Pastikan file font `RedditSans-Regular.ttf` terikut di dalam folder `resources/fonts/`.
3. [x] Pastikan ikon aplikasi `icon.ico`, `icon.png`, dan `tray_icon.png` sudah ada di `resources/icons/`.
4. [x] Lakukan uji coba Test Print (58mm dan 80mm) ke printer thermal fisik.
5. [x] Verifikasi HTTP Server berjalan lancar di port `8080` dan indikator status Sistem Tray berfungsi dengan baik saat ditutup.
