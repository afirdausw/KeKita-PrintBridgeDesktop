package devyana.kekita.printbridge.api;

public class PrintRequest {

    private String printer;
    private String data;

    public PrintRequest() {
    }

    public String getPrinter() {
        return printer;
    }

    public void setPrinter(String printer) {
        this.printer = printer;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

}
