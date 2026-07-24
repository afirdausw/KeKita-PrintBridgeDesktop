package devyana.kekita.printbridge.service;

import devyana.kekita.printbridge.config.AppConfig;
import devyana.kekita.printbridge.controller.PrinterController;
import devyana.kekita.printbridge.controller.StatusController;
import io.javalin.Javalin;

public class HttpService implements Service {

    private Javalin app;

    @Override
    public void start() {

        app = Javalin.create();

        registerRoutes();

        app.start(AppConfig.get().getApi().getPort());

        System.out.println("HTTP Server Started");
        System.out.println("http://localhost:" + AppConfig.get().getApi().getPort());
    }

    private void registerRoutes() {

        StatusController statusController = new StatusController();
        PrinterController printerController = new PrinterController();

        app.get("/status", statusController::status);
        app.get("/printers", printerController::getPrinters);
        app.post("/printer/raw", printerController::printRaw);

    }

    @Override
    public void stop() {

        if (app != null) {
            app.stop();
        }

    }

}
