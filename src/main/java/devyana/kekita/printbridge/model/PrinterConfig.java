package devyana.kekita.printbridge.model;

public class PrinterConfig {

    private int paperSize = 58;
    private boolean autoReconnect = true;
    private String defaultPrinter = "";

    private boolean enableLogo = false;
    private String logoPath = "config/logo.png";
    private boolean enableCashDrawer = false;
    private boolean autoCut = true;

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

    public boolean isEnableLogo() {
        return enableLogo;
    }

    public void setEnableLogo(boolean enableLogo) {
        this.enableLogo = enableLogo;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }

    public boolean isEnableCashDrawer() {
        return enableCashDrawer;
    }

    public void setEnableCashDrawer(boolean enableCashDrawer) {
        this.enableCashDrawer = enableCashDrawer;
    }

    public boolean isAutoCut() {
        return autoCut;
    }

    public void setAutoCut(boolean autoCut) {
        this.autoCut = autoCut;
    }
}
