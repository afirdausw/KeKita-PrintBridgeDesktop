package devyana.kekita.printbridge.printer;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;

public class PrinterFinder {

    public static PrintService find(String printerName) {

        PrintService[] services =
                PrintServiceLookup.lookupPrintServices(null, null);

        for (PrintService service : services) {

            if (service.getName().equalsIgnoreCase(printerName)) {
                return service;
            }

        }

        return null;
    }

}
