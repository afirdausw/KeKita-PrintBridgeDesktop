package devyana.kekita.printbridge;

import com.formdev.flatlaf.FlatLightLaf;
import devyana.kekita.printbridge.config.AppConfig;
import devyana.kekita.printbridge.model.ApplicationConfig;
import devyana.kekita.printbridge.printer.PrinterManager;
import devyana.kekita.printbridge.service.HttpService;
import devyana.kekita.printbridge.service.PrinterService;
import devyana.kekita.printbridge.ui.MainFrame;
import devyana.kekita.printbridge.util.FontUtil;
import devyana.kekita.printbridge.util.Log;

import javax.swing.*;

public class Application {

    private final HttpService httpService;
    private final PrinterManager printerManager;
    private final PrinterService printerService;

    public Application() {
        this.httpService = new HttpService();
        this.printerManager = new PrinterManager();
        this.printerService = new PrinterService();
    }

    public void start() {
        // 1. Inisialisasi Font Inter dan Look and Feel FlatLaf untuk tampilan modern Windows 11
        try {
            FontUtil.setupCustomFont();
            FlatLightLaf.setup();
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 10);
            UIManager.put("ProgressBar.arc", 10);
            UIManager.put("TextComponent.arc", 10);
        } catch (Exception e) {
            Log.error("Gagal menginisialisasi FlatLaf / Inter Font", e);
        }

        // 2. Load konfigurasi dari application.json
        AppConfig.load();
        ApplicationConfig config = AppConfig.get();

        Log.info("=================================");
        Log.info(" " + config.getApplication().getName());
        Log.info(" Version : " + config.getApplication().getVersion());
        Log.info(" Status  : Starting...");
        Log.info("=================================");

        // 3. Inisialisasi Printer Manager
        printerManager.initialize();

        // 4. Jalankan HTTP Service
        try {
            httpService.start();
        } catch (Exception e) {
            Log.error("Gagal memulai HTTP Service saat startup", e);
        }

        // 5. Tampilkan GUI Desktop pada Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame(httpService, printerService);
            mainFrame.setVisible(true);
            Log.success("PrintBridge UI berhasil dijalankan.");
        });
    }
}
