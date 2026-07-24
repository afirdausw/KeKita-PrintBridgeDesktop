package devyana.kekita.printbridge.model;

public class ApplicationConfig {

    private ApplicationInfo application;

    private ApiConfig api;

    private PrinterConfig printer;

    public ApplicationConfig() {
    }

    public ApplicationInfo getApplication() {
        return application;
    }

    public void setApplication(ApplicationInfo application) {
        this.application = application;
    }

    public ApiConfig getApi() {
        return api;
    }

    public void setApi(ApiConfig api) {
        this.api = api;
    }

    public PrinterConfig getPrinter() {
        return printer;
    }

    public void setPrinter(PrinterConfig printer) {
        this.printer = printer;
    }

}
