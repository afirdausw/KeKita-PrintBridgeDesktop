package devyana.kekita.printbridge.api;

public class PrinterData {

    private String name;
    private boolean isDefault;

    public PrinterData() {
    }

    public PrinterData(String name, boolean isDefault) {
        this.name = name;
        this.isDefault = isDefault;
    }

    public String getName() {
        return name;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

}
