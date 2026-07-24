package devyana.kekita.printbridge.printer;

import javax.print.*;
import java.io.ByteArrayInputStream;

public class EscPosPrinter {

    public void print(String printerName, byte[] data) throws PrintException {

        PrintService service = PrinterFinder.find(printerName);

        if (service == null) {
            throw new PrintException("Printer not found : " + printerName);
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

}
