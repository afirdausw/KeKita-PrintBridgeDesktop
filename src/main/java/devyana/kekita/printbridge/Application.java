package devyana.kekita.printbridge;

import devyana.kekita.printbridge.config.AppConfig;
import devyana.kekita.printbridge.model.ApplicationConfig;
import devyana.kekita.printbridge.printer.PrinterManager;
import devyana.kekita.printbridge.service.HttpService;

public class Application {

    private final HttpService httpService;
    private final PrinterManager printerManager;

    public Application() {

        this.httpService = new HttpService();
        this.printerManager = new PrinterManager();

    }

    public void start() {

        AppConfig.load();

        ApplicationConfig config = AppConfig.get();

        System.out.println("=================================");
        System.out.println(" " + config.getApplication().getName());
        System.out.println(" Version : " + config.getApplication().getVersion());
        System.out.println(" Status  : Starting...");
        System.out.println("=================================");

        httpService.start();

        printerManager.initialize();

    }

}
