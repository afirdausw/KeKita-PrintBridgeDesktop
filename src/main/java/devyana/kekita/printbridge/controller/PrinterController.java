package devyana.kekita.printbridge.controller;

import devyana.kekita.printbridge.api.ApiResponse;
import devyana.kekita.printbridge.api.PrintRequest;
import devyana.kekita.printbridge.api.PrinterData;
import devyana.kekita.printbridge.service.PrinterService;
import io.javalin.http.Context;

import java.util.List;

public class PrinterController {

    private final PrinterService printerService = new PrinterService();

    public void getPrinters(Context ctx) {
        List<PrinterData> printers = printerService.getPrinters();

        ctx.json(
                new ApiResponse<>(
                        true,
                        "Printer list",
                        printers
                )
        );
    }

    public void printRaw(Context ctx) {
        try {
            PrintRequest request = ctx.bodyAsClass(PrintRequest.class);

            printerService.printRaw(request);

            ctx.json(new ApiResponse<>(
                    true,
                    "Print success",
                    null
            ));

        } catch (Exception e) {
            ctx.status(500);

            ctx.json(new ApiResponse<>(
                    false,
                    e.getMessage(),
                    null
            ));
        }
    }

}
