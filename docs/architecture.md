# Dokumentasi Arsitektur Sistem - PrintBridge Desktop

## 📐 Ringkasan Arsitektur

PrintBridge adalah aplikasi desktop berbasis **Java 21** yang bertindak sebagai **HTTP Bridge** antara aplikasi POS (Web / Cloud / Hybrid) dan Printer Thermal ESC/POS (USB, Bluetooth SPP Serial COM, LAN, Windows Print Spooler).

PrintBridge **tidak membuat layout struk**, melainkan menerima data formatting atau byte mentah ESC/POS dari aplikasi POS via HTTP REST API dan meneruskannya secara langsung ke driver printer target.

```text
+-----------------------+           +-----------------------------+
|   Aplikasi Web POS    |  HTTP     |     PrintBridge Desktop     |
| (Browser / Javascript)| --------> |  (Javalin REST API Server)  |
+-----------------------+  JSON/RAW +--------------+--------------+
                                                   |
                                                   v
                                    +--------------+--------------+
                                    |    PrinterService Manager   |
                                    +--------------+--------------+
                                                   |
                        +--------------------------+--------------------------+
                        |                                                     |
                        v                                                     v
          +-------------+-------------+                         +-------------+-------------+
          |   Windows Print Spooler   |                         |  Bluetooth / Serial COM Port|
          |  (javax.print Service)    |                         |  (Direct OutputStream/COM)  |
          +-------------+-------------+                         +-------------+-------------+
                        |                                                     |
                        v                                                     v
            [ Physical USB Printer ]                              [ Bluetooth Thermal Printer ]
```

---

## 🛠️ Technology Stack

* **Bahasa Pemrograman**: Java 21 LTS
* **Build Tool**: Apache Maven 3.x
* **HTTP Framework**: Javalin 6.7.0 (Embedded Jetty Server)
* **JSON Parser**: Jackson 2.17.x
* **GUI Framework**: Java Swing + FlatLaf 3.5.2 (Windows 11 Modern Flat Style)
* **Font**: Google Fonts Reddit Sans
* **Print Subsystem**: Java Print Service (`javax.print`) + Native Serial SPP Output Stream

---

## 📂 Struktur Package Source Code

```text
src/main/java/devyana/kekita/printbridge/
├── Application.java        # Inisialisasi FlatLaf, font, service, & GUI startup
├── Main.java               # Main Entry Point aplikasi
├── api/                    # Data Transfer Objects (DTO)
│   ├── ApiResponse.java    # Standard Wrapper Response JSON
│   ├── PrintRequest.java   # Model payload POST /print
│   ├── PrinterData.java    # DTO daftar printer
│   └── StatusData.java     # DTO status versi
├── config/                 # Pengelola Konfigurasi JSON
│   ├── AppConfig.java      # Global Config Registry
│   └── ConfigLoader.java   # Reader/Writer application.json
├── controller/             # Controller Endpoint HTTP REST API
│   ├── PrinterController.java
│   └── StatusController.java
├── model/                  # POJO Configuration Models
│   ├── ApiConfig.java
│   ├── ApplicationConfig.java
│   └── PrinterConfig.java
├── printer/                # Printer Driver Subsystem
│   ├── EscPosPrinter.java  # Custom ESC/POS Byte Renderer
│   ├── Printer.java         # Base Interface Printer
│   ├── PrinterFinder.java   # Detector Windows & Serial COM
│   ├── PrinterManager.java  # Hardware Connection Orchestrator
│   └── VirtualPrinter.java  # Fallback Virtual Printer
├── service/                # Business Logic Services
│   ├── HttpService.java    # Embedded Javalin HTTP Server
│   └── PrinterService.java # Executor pencetakan & Test Print
├── ui/                     # User Interface Components (Swing + FlatLaf)
│   ├── DashboardView.java  # Overview Status & Quick Actions
│   ├── HelpView.java       # Halaman Bantuan
│   ├── LogsView.java        # Console Live Log Monitoring
│   ├── MainFrame.java      # Window Frame Utama & System Tray Integrator
│   ├── PrinterView.java    # Form Konfigurasi Printer & Ukuran Kertas
│   ├── ServerView.java     # Form Pengaturan Port REST API
│   └── SidebarPanel.java   # Navigasi Kiri & Server Status Indicator
└── util/                   # Utility Classes
    ├── FontUtil.java       # Font Loader (Reddit Sans)
    └── Log.java            # Thread-safe Logging Utility
```

---

## 🔄 Dynamic Configuration Engine

Konfigurasi aplikasi disimpan dalam file external `application.json` di root folder aplikasi atau `src/main/resources/application.json`:

```json
{
  "application": {
    "name": "PrintBridge",
    "version": "1.0.0"
  },
  "api": {
    "port": 8080
  },
  "printer": {
    "defaultPrinter": "COM9",
    "paperSize": 80,
    "autoReconnect": true
  }
}
```

Setiap kali pengguna mengubah **Port API**, **Default Printer**, atau **Ukuran Kertas** pada UI desktop, `ConfigLoader.save()` akan memperbarui file `application.json` secara atomis.
