package devyana.kekita.printbridge.printer;

public interface Printer {
    void print(byte[] data) throws Exception;
    String getName();
    String getType();
}
