package devyana.kekita.printbridge.printer;

import com.fazecast.jSerialComm.SerialPort;
import devyana.kekita.printbridge.api.PrinterData;
import devyana.kekita.printbridge.config.AppConfig;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.util.ArrayList;
import java.util.List;

public class PrinterFinder {

    public static PrintService findPrintService(String printerName) {
        if (printerName == null) return null;

        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService service : services) {
            if (service.getName().equalsIgnoreCase(printerName)) {
                return service;
            }
        }
        return null;
    }

    public static Printer findPrinter(String printerName) {
        if (printerName == null || printerName.trim().isEmpty()) {
            printerName = AppConfig.get().getPrinter().getDefaultPrinter();
        }

        if (printerName == null || printerName.trim().isEmpty()) {
            return new VirtualPrinter();
        }

        // 1. Virtual Printer Mode
        if (printerName.equalsIgnoreCase(VirtualPrinter.VIRTUAL_PRINTER_NAME)) {
            return new VirtualPrinter();
        }

        // 2. Serial / Bluetooth COM Port
        if (printerName.toUpperCase().startsWith("COM") || isSerialPort(printerName)) {
            String portName = extractPortName(printerName);
            return new BluetoothPrinter(portName);
        }

        // 3. Network TCP Printer (misal: 192.168.1.200:9100)
        if (printerName.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}(:\\d+)?")) {
            String[] parts = printerName.split(":");
            String ip = parts[0];
            int port = parts.length > 1 ? Integer.parseInt(parts[1]) : 9100;
            return new NetworkPrinter(ip, port);
        }

        // 4. Windows Print Spooler (USB)
        return new UsbPrinter(printerName);
    }

    public static List<PrinterData> getAllPrinters() {
        List<PrinterData> list = new ArrayList<>();
        String configuredDefault = AppConfig.get().getPrinter().getDefaultPrinter();

        // 1. Virtual Printer
        boolean isVirtualDefault = VirtualPrinter.VIRTUAL_PRINTER_NAME.equalsIgnoreCase(configuredDefault);
        list.add(new PrinterData(VirtualPrinter.VIRTUAL_PRINTER_NAME, isVirtualDefault));

        // 2. Bluetooth / Serial COM Ports
        SerialPort[] serialPorts = SerialPort.getCommPorts();
        for (SerialPort port : serialPorts) {
            String displayName = formatPortDisplayName(port);
            boolean isDefault = port.getSystemPortName().equalsIgnoreCase(configuredDefault) ||
                                displayName.equalsIgnoreCase(configuredDefault) ||
                                extractPortName(configuredDefault).equalsIgnoreCase(port.getSystemPortName());
            list.add(new PrinterData(displayName, isDefault));
        }

        // 3. Windows Print Spooler Services
        PrintService defaultSpooler = PrintServiceLookup.lookupDefaultPrintService();
        PrintService[] spoolerServices = PrintServiceLookup.lookupPrintServices(null, null);

        for (PrintService ps : spoolerServices) {
            String name = ps.getName();
            boolean isDefault = false;

            if (configuredDefault != null && !configuredDefault.trim().isEmpty()) {
                isDefault = name.equalsIgnoreCase(configuredDefault);
            } else if (defaultSpooler != null) {
                isDefault = name.equals(defaultSpooler.getName());
            }

            list.add(new PrinterData(name, isDefault));
        }

        return list;
    }

    public static String formatPortDisplayName(SerialPort port) {
        String sysName = port.getSystemPortName();
        String desc = port.getDescriptivePortName();

        if (desc != null && !desc.trim().isEmpty()) {
            if (desc.toUpperCase().endsWith("(" + sysName.toUpperCase() + ")")) {
                desc = desc.substring(0, desc.length() - ("(" + sysName + ")").length()).trim();
            }
            if (!desc.equalsIgnoreCase(sysName)) {
                return sysName + " (" + desc + ")";
            }
        }
        return sysName;
    }

    private static boolean isSerialPort(String name) {
        for (SerialPort port : SerialPort.getCommPorts()) {
            if (port.getSystemPortName().equalsIgnoreCase(name) ||
                formatPortDisplayName(port).equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    private static String extractPortName(String name) {
        if (name == null) return "";
        if (name.contains(" ")) {
            return name.split(" ")[0];
        }
        return name;
    }
}
