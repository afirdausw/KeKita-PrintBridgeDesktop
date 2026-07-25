package devyana.kekita.printbridge.printer;

public class EscPosPrinter {

    public void print(String printerName, byte[] data) throws Exception {
        Printer printer = PrinterFinder.findPrinter(printerName);
        printer.print(data);
    }

}
