package devyana.kekita.printbridge.model;

public class PrinterConfig {

    private int paperSize;

    private boolean autoReconnect;

    private String defaultPrinter;

    public PrinterConfig() {
    }

    public int getPaperSize() {
        return paperSize;
    }

    public void setPaperSize(int paperSize) {
        this.paperSize = paperSize;
    }

    public boolean isAutoReconnect() {
        return autoReconnect;
    }

    public void setAutoReconnect(boolean autoReconnect) {
        this.autoReconnect = autoReconnect;
    }

    public String getDefaultPrinter() {
        return defaultPrinter;
    }

    public void setDefaultPrinter(String defaultPrinter) {
        this.defaultPrinter = defaultPrinter;
    }

}
