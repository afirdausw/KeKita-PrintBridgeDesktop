package devyana.kekita.printbridge.printer;

import com.fazecast.jSerialComm.SerialPort;
import devyana.kekita.printbridge.util.Log;

import java.io.OutputStream;

public class BluetoothPrinter implements Printer {

    private final String portDescriptor;
    private final int baudRate;

    public BluetoothPrinter(String portDescriptor) {
        this(portDescriptor, 9600);
    }

    public BluetoothPrinter(String portDescriptor, int baudRate) {
        this.portDescriptor = portDescriptor;
        this.baudRate = baudRate;
    }

    @Override
    public void print(byte[] data) throws Exception {
        SerialPort comPort = SerialPort.getCommPort(portDescriptor);
        comPort.setBaudRate(baudRate);
        comPort.setNumDataBits(8);
        comPort.setNumStopBits(SerialPort.ONE_STOP_BIT);
        comPort.setParity(SerialPort.NO_PARITY);
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 2000, 2000);

        Log.info("Membuka koneksi Bluetooth Serial PORT [" + portDescriptor + "] (BaudRate: " + baudRate + ")...");

        // Timeout set to 2000 ms (2 seconds)
        if (!comPort.openPort(2000)) {
            throw new Exception("Bluetooth Port [" + portDescriptor + "] tidak merespon (Timeout 2s). Kemungkinan ini adalah Incoming Port atau printer Bluetooth dalam kondisi mati/terputus.");
        }

        try (OutputStream os = comPort.getOutputStream()) {
            os.write(data);
            os.flush();
            // Sleep briefly to ensure serial buffer completes transmission before port closure
            Thread.sleep(200);
            Log.success("Data ESC/POS berhasil terkirim via Bluetooth SPP [" + portDescriptor + "]");
        } finally {
            comPort.closePort();
        }
    }

    @Override
    public String getName() {
        return portDescriptor;
    }

    @Override
    public String getType() {
        return "Bluetooth / Serial (COM)";
    }
}
