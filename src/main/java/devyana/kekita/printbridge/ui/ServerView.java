package devyana.kekita.printbridge.ui;

import com.formdev.flatlaf.FlatClientProperties;
import devyana.kekita.printbridge.config.AppConfig;
import devyana.kekita.printbridge.service.HttpService;
import devyana.kekita.printbridge.ui.components.CardPanel;
import devyana.kekita.printbridge.ui.components.NavIcons;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ServerView extends JPanel {

    private final HttpService httpService;
    private final JTextField portTextField = new JTextField();
    private final JLabel statusBadge = new JLabel("Server Stopped");
    private final JButton btnStart = new JButton("Start Server");
    private final JButton btnStop = new JButton("Stop Server");
    private final JButton btnSavePort = new JButton("Simpan Port");

    public ServerView(HttpService httpService) {
        this.httpService = httpService;

        setLayout(new BorderLayout(0, 16));
        setBorder(new EmptyBorder(14, 28, 14, 28));
        setOpaque(false);

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createBodyPanel(), BorderLayout.CENTER);

        refreshData();
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);

        JLabel title = new JLabel("Pengaturan Server API");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Kelola port HTTP REST API dan status Javalin service");
        subtitle.setFont(subtitle.getFont().deriveFont(13f));
        subtitle.setForeground(UIManager.getColor("Label.disabledForeground"));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel dividerLine = new JPanel();
        dividerLine.setOpaque(true);
        dividerLine.setBackground(UIManager.getColor("Component.borderColor"));
        dividerLine.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        dividerLine.setPreferredSize(new Dimension(0, 1));
        dividerLine.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(title);
        header.add(Box.createRigidArea(new Dimension(0, 4)));
        header.add(subtitle);
        header.add(Box.createRigidArea(new Dimension(0, 12)));
        header.add(dividerLine);

        return header;
    }

    private JPanel createBodyPanel() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setOpaque(false);

        container.add(createServerControlCard());
        container.add(Box.createRigidArea(new Dimension(0, 16)));
        container.add(createApiDocCard());

        return container;
    }

    private CardPanel createServerControlCard() {
        CardPanel card = new CardPanel("Status & Port Server");
        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);
        content.setAlignmentX(Component.LEFT_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 8);

        // Row 1: Server Status
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        JLabel lblStatus = new JLabel("Status Service:");
        lblStatus.setFont(lblStatus.getFont().deriveFont(Font.BOLD, 13f));
        content.add(lblStatus, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        statusBadge.setIcon(NavIcons.getDot(new Color(220, 53, 69), 10));
        statusBadge.setIconTextGap(8);
        statusBadge.setFont(statusBadge.getFont().deriveFont(Font.BOLD, 14f));
        content.add(statusBadge, gbc);

        // Row 2: Port Setting
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        JLabel lblPort = new JLabel("Port API:");
        lblPort.setFont(lblPort.getFont().deriveFont(Font.BOLD, 13f));
        content.add(lblPort, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        portTextField.setFont(portTextField.getFont().deriveFont(13f));
        portTextField.putClientProperty(FlatClientProperties.STYLE, "arc: 10;");
        content.add(portTextField, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.0;
        btnSavePort.setFont(btnSavePort.getFont().deriveFont(Font.PLAIN, 13f));
        btnSavePort.putClientProperty(FlatClientProperties.STYLE, "arc: 10;");
        btnSavePort.addActionListener(e -> savePortConfig());
        content.add(btnSavePort, gbc);

        // Row 3: Action Buttons (Start / Stop)
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnPanel.setOpaque(false);
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnStart.setFont(btnStart.getFont().deriveFont(Font.BOLD, 13f));
        btnStart.putClientProperty(FlatClientProperties.STYLE, ""
                + "background: #0067C0;"
                + "foreground: #FFFFFF;"
                + "arc: 10;"
        );
        btnStart.addActionListener(e -> {
            try {
                httpService.start();
                refreshData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal mengaktifkan server: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnStop.setFont(btnStop.getFont().deriveFont(Font.BOLD, 13f));
        btnStop.putClientProperty(FlatClientProperties.STYLE, ""
                + "background: #DC3545;"
                + "foreground: #FFFFFF;"
                + "arc: 10;"
        );
        btnStop.addActionListener(e -> {
            httpService.stop();
            refreshData();
        });

        btnPanel.add(btnStart);
        btnPanel.add(Box.createRigidArea(new Dimension(12, 0)));
        btnPanel.add(btnStop);
        content.add(btnPanel, gbc);

        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private CardPanel createApiDocCard() {
        CardPanel card = new CardPanel("Dokumentasi Endpoint REST API");
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.putClientProperty(FlatClientProperties.STYLE, ""
                + "background: $Table.background;"
                + "arc: 8;"
        );

        String doc = "1. GET /status\n" +
                     "   Mengembalikan status aplikasi & versi.\n\n" +
                     "2. GET /printers\n" +
                     "   Mengembalikan daftar seluruh printer Windows yang terdeteksi.\n\n" +
                     "3. POST /print (atau /print/raw atau /printer/raw)\n" +
                     "   Payload JSON: {\"printer\": \"POS-58\", \"data\": \"BASE64...\"} atau {\"text\": \"Hello\"}\n" +
                     "   Atau kirim raw byte stream langsung di HTTP request body.\n\n" +
                     "4. POST /test\n" +
                     "   Memicu test print langsung ke printer default.";

        textArea.setText(doc);
        textArea.setBorder(new EmptyBorder(12, 12, 12, 12));

        content.add(textArea);
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    public void refreshData() {
        SwingUtilities.invokeLater(() -> {
            boolean running = httpService.isRunning();
            int port = httpService.getPort();

            if (running) {
                statusBadge.setIcon(NavIcons.getDot(new Color(40, 167, 69), 10));
                statusBadge.setText("Server Running (Port " + port + ")");
                statusBadge.setForeground(new Color(40, 167, 69));
                btnStart.setEnabled(false);
                btnStop.setEnabled(true);
                portTextField.setEnabled(false);
                btnSavePort.setEnabled(false);
            } else {
                statusBadge.setIcon(NavIcons.getDot(new Color(220, 53, 69), 10));
                statusBadge.setText("Server Stopped");
                statusBadge.setForeground(new Color(220, 53, 69));
                btnStart.setEnabled(true);
                btnStop.setEnabled(false);
                portTextField.setEnabled(true);
                btnSavePort.setEnabled(true);
            }

            portTextField.setText(String.valueOf(port));
        });
    }

    private void savePortConfig() {
        try {
            int port = Integer.parseInt(portTextField.getText().trim());
            if (port < 1 || port > 65535) {
                throw new NumberFormatException("Port harus antara 1 - 65535");
            }

            AppConfig.get().getApi().setPort(port);
            boolean saved = AppConfig.save();

            if (saved) {
                JOptionPane.showMessageDialog(this,
                        "Port API berhasil diperbarui ke " + port,
                        "Sukses", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Nomor port tidak valid. Masukkan angka antara 1 - 65535",
                    "Validasi Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
