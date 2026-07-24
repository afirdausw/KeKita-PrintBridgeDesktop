package devyana.kekita.printbridge.service;

import devyana.kekita.printbridge.api.PrintRequest;
import devyana.kekita.printbridge.api.PrinterData;
import devyana.kekita.printbridge.printer.EscPosPrinter;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class PrinterService {

    private final EscPosPrinter escPosPrinter = new EscPosPrinter();

    public List<PrinterData> getPrinters() {

        List<PrinterData> printers = new ArrayList<>();

        PrintService defaultPrinter = PrintServiceLookup.lookupDefaultPrintService();
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);

        for (PrintService printer : services) {
            boolean isDefault =
                    defaultPrinter != null &&
                            printer.getName().equals(defaultPrinter.getName());

            printers.add(new PrinterData(
                    printer.getName(),
                    isDefault
            ));
        }

        return printers;
    }

    public void printRaw(PrintRequest request) throws Exception {
        byte[] bytes = Base64.getDecoder().decode(request.getData());

        escPosPrinter.print(
                request.getPrinter(),
                bytes
        );
    }

}
