package devyana.kekita.printbridge.printer;

import javax.print.*;
import java.io.ByteArrayInputStream;

public class UsbPrinter implements Printer {

    private final String printerName;

    public UsbPrinter(String printerName) {
        this.printerName = printerName;
    }

    @Override
    public void print(byte[] data) throws Exception {
        PrintService service = PrinterFinder.findPrintService(printerName);

        if (service == null) {
            throw new PrintException("Windows Spooler Printer tidak ditemukan: " + printerName);
        }

        DocPrintJob job = service.createPrintJob();
        DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;

        Doc doc = new SimpleDoc(
                new ByteArrayInputStream(data),
                flavor,
                null
        );

        job.print(doc, null);
    }

    @Override
    public String getName() {
        return printerName;
    }

    @Override
    public String getType() {
        return "USB / Windows Spooler";
    }
}
