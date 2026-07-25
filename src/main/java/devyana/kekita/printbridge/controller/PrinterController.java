package devyana.kekita.printbridge.controller;

import devyana.kekita.printbridge.api.ApiResponse;
import devyana.kekita.printbridge.api.PrintRequest;
import devyana.kekita.printbridge.api.PrinterData;
import devyana.kekita.printbridge.service.HttpService;
import devyana.kekita.printbridge.service.PrinterService;
import devyana.kekita.printbridge.util.Log;
import io.javalin.http.Context;

import java.util.List;

public class PrinterController {

    private final PrinterService printerService;

    public PrinterController() {
        this.printerService = new PrinterService();
    }

    public PrinterController(HttpService httpService) {
        this.printerService = new PrinterService();
    }

    public void getPrinters(Context ctx) {
        List<PrinterData> printers = printerService.getPrinters();
        ctx.json(new ApiResponse<>(true, "Printer list", printers));
    }

    public void printRaw(Context ctx) {
        try {
            byte[] bodyBytes = ctx.bodyAsBytes();
            if (bodyBytes == null || bodyBytes.length == 0) {
                ctx.status(400);
                ctx.json(new ApiResponse<>(false, "Request body tidak boleh kosong.", null));
                return;
            }

            // 1. Cek apakah payload berupa JSON Object {"printer":..., "data":..., "text":...}
            try {
                PrintRequest request = ctx.bodyAsClass(PrintRequest.class);
                if (request != null && ((request.getData() != null && !request.getData().isEmpty()) || (request.getText() != null && !request.getText().isEmpty()))) {
                    printerService.printRaw(request);
                    ctx.json(new ApiResponse<>(true, "Print success", null));
                    return;
                }
            } catch (Exception e) {
                // Bukan JSON terstruktur, teruskan byte mentah langsung ke printer
            }

            // 2. Direct raw byte stream
            String qPrinter = ctx.queryParam("printer");
            printerService.printDirectBytes(qPrinter, bodyBytes);
            ctx.json(new ApiResponse<>(true, "Direct raw print success", null));

        } catch (Exception e) {
            Log.error("Gagal memproses print request", e);
            ctx.status(500);
            ctx.json(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    public void testPrint(Context ctx) {
        try {
            String targetPrinter = null;

            byte[] bodyBytes = ctx.bodyAsBytes();
            if (bodyBytes != null && bodyBytes.length > 0) {
                try {
                    PrintRequest req = ctx.bodyAsClass(PrintRequest.class);
                    if (req != null) {
                        targetPrinter = req.getPrinter();
                    }
                } catch (Exception ignored) {
                }
            }

            if (targetPrinter == null || targetPrinter.isEmpty()) {
                targetPrinter = ctx.queryParam("printer");
            }

            printerService.testPrint(targetPrinter);
            ctx.json(new ApiResponse<>(true, "Test print success", null));
        } catch (Exception e) {
            Log.error("Test print API error", e);
            ctx.status(500);
            ctx.json(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
}
