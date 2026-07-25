package devyana.kekita.printbridge.printer;

import devyana.kekita.printbridge.util.Log;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class NetworkPrinter implements Printer {

    private final String ipAddress;
    private final int port;

    public NetworkPrinter(String ipAddress) {
        this(ipAddress, 9100);
    }

    public NetworkPrinter(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    @Override
    public void print(byte[] data) throws Exception {
        Log.info("Menghubungkan ke Network Printer [" + ipAddress + ":" + port + "]...");

        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(ipAddress, port), 3000);
            try (OutputStream os = socket.getOutputStream()) {
                os.write(data);
                os.flush();
                Log.success("Data ESC/POS berhasil terkirim via Network [" + ipAddress + ":" + port + "]");
            }
        }
    }

    @Override
    public String getName() {
        return ipAddress + ":" + port;
    }

    @Override
    public String getType() {
        return "Network (TCP/IP)";
    }
}
