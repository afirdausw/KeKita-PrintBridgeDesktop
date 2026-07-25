package devyana.kekita.printbridge.service;

import devyana.kekita.printbridge.api.PrintRequest;
import devyana.kekita.printbridge.api.PrinterData;
import devyana.kekita.printbridge.config.AppConfig;
import devyana.kekita.printbridge.printer.Printer;
import devyana.kekita.printbridge.printer.PrinterFinder;
import devyana.kekita.printbridge.printer.VirtualPrinter;
import devyana.kekita.printbridge.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

public class PrinterService {

    public List<PrinterData> getPrinters() {
        return PrinterFinder.getAllPrinters();
    }

    public void printRaw(PrintRequest request) throws Exception {
        String printerName = request != null ? request.getPrinter() : null;

        if (printerName == null || printerName.trim().isEmpty()) {
            printerName = AppConfig.get().getPrinter().getDefaultPrinter();
        }

        byte[] bytes = extractBytesFromRequest(request);
        if (bytes == null || bytes.length == 0) {
            throw new IllegalArgumentException("Data cetak (data / text) tidak boleh kosong.");
        }

        printBytesToPrinter(printerName, bytes);
    }

    public void printDirectBytes(String targetPrinter, byte[] rawBytes) throws Exception {
        String printerName = targetPrinter;
        if (printerName == null || printerName.trim().isEmpty()) {
            printerName = AppConfig.get().getPrinter().getDefaultPrinter();
        }

        if (rawBytes == null || rawBytes.length == 0) {
            throw new IllegalArgumentException("Byte data cetak tidak boleh kosong.");
        }

        printBytesToPrinter(printerName, rawBytes);
    }

    private void printBytesToPrinter(String printerName, byte[] bytes) throws Exception {
        if (printerName == null || printerName.trim().isEmpty()) {
            printerName = VirtualPrinter.VIRTUAL_PRINTER_NAME;
        }

        Printer printer = PrinterFinder.findPrinter(printerName);
        Log.info("Mencetak via [" + printer.getType() + "] -> Target: " + printer.getName() + " (" + bytes.length + " bytes)...");
        printer.print(bytes);
        Log.success("Pencetakan berhasil diproses oleh [" + printer.getName() + "]");
    }

    private byte[] extractBytesFromRequest(PrintRequest request) throws IOException {
        if (request == null) {
            return new byte[0];
        }

        // 1. Cek field Base64 'data'
        if (request.getData() != null && !request.getData().trim().isEmpty()) {
            String dataStr = request.getData().trim();
            try {
                return Base64.getDecoder().decode(dataStr);
            } catch (Exception e) {
                // Jika bukan Base64 valid, gunakan byte UTF-8 langsung
                return dataStr.getBytes(StandardCharsets.UTF_8);
            }
        }

        // 2. Cek field plain text 'text'
        if (request.getText() != null && !request.getText().isEmpty()) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // ESC @ (Init)
            baos.write(new byte[]{0x1B, 0x40});
            baos.write(request.getText().getBytes(StandardCharsets.UTF_8));
            baos.write(new byte[]{0x0A, 0x0A});
            // GS V 65 1 (Cut)
            baos.write(new byte[]{0x1D, 0x56, 0x41, 0x01});
            return baos.toByteArray();
        }

        return new byte[0];
    }

    public void testPrint(String targetPrinter) throws Exception {
        String printerName = targetPrinter;
        if (printerName == null || printerName.trim().isEmpty()) {
            printerName = AppConfig.get().getPrinter().getDefaultPrinter();
        }

        if (printerName == null || printerName.trim().isEmpty()) {
            printerName = VirtualPrinter.VIRTUAL_PRINTER_NAME;
        }

        int paperSize = AppConfig.get().getPrinter().getPaperSize();
        byte[] testBytes = generateTestPrintPayload(paperSize);

        Printer printer = PrinterFinder.findPrinter(printerName);
        Log.info("Mengirim Test Print (" + paperSize + "mm) via [" + printer.getType() + "] -> Target: " + printer.getName() + "...");
        printer.print(testBytes);
        Log.success("Test Print (" + paperSize + "mm) berhasil diproses oleh [" + printer.getName() + "]");
    }

    private byte[] generateTestPrintPayload(int paperSize) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // Initialize Printer ESC @
        baos.write(new byte[]{0x1B, 0x40});

        // Center Alignment ESC a 1
        baos.write(new byte[]{0x1B, 0x61, 0x01});

        int lineWidth = (paperSize == 80) ? 48 : 32;
        String divider = "*".repeat(lineWidth) + "\n";

        String appName = AppConfig.get().getApplication().getName();
        String appVersion = AppConfig.get().getApplication().getVersion();

        StringBuilder sb = new StringBuilder();
        sb.append(divider);
        sb.append("PRINT TEST\n");
        sb.append(appName).append("\n");
        sb.append("Version: ").append(appVersion).append("\n");
        sb.append("Paper Size: ").append(paperSize).append(" mm\n");
        sb.append("Status: OK\n");
        sb.append(divider);
        sb.append("\n\n");

        baos.write(sb.toString().getBytes(StandardCharsets.US_ASCII));

        // Cut Paper GS V 65 1
        baos.write(new byte[]{0x1D, 0x56, 0x41, 0x01});

        return baos.toByteArray();
    }
}
