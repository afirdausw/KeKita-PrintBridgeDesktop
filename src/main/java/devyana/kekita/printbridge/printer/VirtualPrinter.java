package devyana.kekita.printbridge.printer;

import devyana.kekita.printbridge.util.Log;

import javax.swing.*;
import java.awt.*;
import java.nio.charset.StandardCharsets;

public class VirtualPrinter implements Printer {

    public static final String VIRTUAL_PRINTER_NAME = "Virtual Printer (Simulasi/Preview)";

    @Override
    public void print(byte[] data) throws Exception {
        String rawText = new String(data, StandardCharsets.UTF_8);
        String printableText = cleanEscPosBytes(data);

        Log.info("[SIMULASI] Menerima job cetak (" + data.length + " bytes)...");
        Log.success("[SIMULASI] Hasil Struk:\n" + printableText);

        SwingUtilities.invokeLater(() -> showReceiptPreview(printableText, data.length));
    }

    private String cleanEscPosBytes(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            if (b >= 32 && b <= 126) {
                sb.append((char) b);
            } else if (b == '\n') {
                sb.append('\n');
            } else if (b == '\r') {
                // skip
            } else {
                // Ignore control codes in raw ASCII view
            }
        }
        return sb.toString().trim();
    }

    private void showReceiptPreview(String content, int byteLength) {
        JDialog dialog = new JDialog((Frame) null, "Virtual Printer - Preview Struk Thermal", false);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(380, 500);
        dialog.setLocationRelativeTo(null);

        JTextArea textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setEditable(false);
        textArea.setText(content);
        textArea.setCaretPosition(0);
        textArea.setMargin(new Insets(16, 16, 16, 16));
        textArea.setBackground(new Color(255, 255, 240)); // Thermal paper color tint

        JLabel infoLabel = new JLabel(" Simulasi Sukses (" + byteLength + " bytes)");
        infoLabel.setFont(infoLabel.getFont().deriveFont(Font.BOLD, 12f));
        infoLabel.setForeground(new Color(40, 167, 69));

        dialog.add(infoLabel, BorderLayout.NORTH);
        dialog.add(new JScrollPane(textArea), BorderLayout.CENTER);

        JButton btnClose = new JButton("Tutup Preview");
        btnClose.addActionListener(e -> dialog.dispose());
        dialog.add(btnClose, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    @Override
    public String getName() {
        return VIRTUAL_PRINTER_NAME;
    }

    @Override
    public String getType() {
        return "Virtual / Simulator";
    }
}
