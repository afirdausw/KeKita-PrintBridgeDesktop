package devyana.kekita.printbridge.service;

import devyana.kekita.printbridge.config.AppConfig;
import devyana.kekita.printbridge.controller.PrinterController;
import devyana.kekita.printbridge.controller.StatusController;
import devyana.kekita.printbridge.util.Log;
import io.javalin.Javalin;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class HttpService {

    static {
        // Suppress Jetty verbose DEBUG trace logs
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info");
        System.setProperty("org.eclipse.jetty.LEVEL", "WARN");
        System.setProperty("org.eclipse.jetty.util.log.announce", "false");
        java.util.logging.Logger.getLogger("org.eclipse.jetty").setLevel(java.util.logging.Level.WARNING);
    }

    public interface StateListener {
        void onServerStateChanged(boolean isRunning, int port);
    }

    private Javalin app;
    private boolean running = false;
    private final List<StateListener> listeners = new CopyOnWriteArrayList<>();

    public void addStateListener(StateListener listener) {
        listeners.add(listener);
    }

    public boolean isRunning() {
        return running;
    }

    public int getPort() {
        return AppConfig.get().getApi().getPort();
    }

    public synchronized void start() throws Exception {
        if (running) {
            Log.info("HTTP Server sudah berjalan.");
            return;
        }

        int port = AppConfig.get().getApi().getPort();

        try {
            app = Javalin.create(config -> {
                config.showJavalinBanner = false;
                // Enable CORS for all origins (web POS browser compatibility)
                config.bundledPlugins.enableCors(cors -> {
                    cors.addRule(it -> it.anyHost());
                });
            });

            registerRoutes();

            app.start(port);
            running = true;

            Log.success("HTTP Server Started pada port " + port);
            Log.info("API Endpoint: http://127.0.0.1:" + port);

            notifyListeners();
        } catch (Exception e) {
            running = false;
            Log.error("Gagal menjalankan HTTP Server pada port " + port, e);
            notifyListeners();
            throw e;
        }
    }

    private void registerRoutes() {
        StatusController statusController = new StatusController();
        PrinterController printerController = new PrinterController(this);

        // Status & Printers endpoints (GET & POST)
        app.get("/status", statusController::status);
        app.post("/status", statusController::status);

        app.get("/printers", printerController::getPrinters);
        app.post("/printers", printerController::getPrinters);

        // Print endpoints (POST only)
        app.post("/print", printerController::printRaw);
        app.post("/print/raw", printerController::printRaw);
        app.post("/printer/raw", printerController::printRaw);

        // Test print endpoint (POST only)
        app.post("/test", printerController::testPrint);
    }

    public synchronized void stop() {
        if (!running && app == null) {
            return;
        }

        if (app != null) {
            try {
                app.stop();
                app = null;
                running = false;
                Log.info("HTTP Server dihentikan.");
                notifyListeners();
            } catch (Exception e) {
                Log.error("Gagal menghentikan HTTP Server", e);
            }
        }
    }

    private void notifyListeners() {
        int port = getPort();
        for (StateListener listener : listeners) {
            try {
                listener.onServerStateChanged(running, port);
            } catch (Exception e) {
                Log.error("Error pada StateListener", e);
            }
        }
    }
}
