package devyana.kekita.printbridge.ui;

import com.formdev.flatlaf.FlatClientProperties;
import devyana.kekita.printbridge.config.AppConfig;
import devyana.kekita.printbridge.model.ApplicationConfig;
import devyana.kekita.printbridge.service.HttpService;
import devyana.kekita.printbridge.service.PrinterService;
import devyana.kekita.printbridge.ui.components.CardPanel;
import devyana.kekita.printbridge.ui.components.NavIcons;
import devyana.kekita.printbridge.util.Log;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.function.Consumer;

public class DashboardView extends JPanel {

    private final HttpService httpService;
    private final PrinterService printerService;
    private final Consumer<SidebarPanel.NavigationItem> onNavigate;

    private final JLabel serverStatusDot = new JLabel();
    private final JLabel serverStatusText = new JLabel("Stopped");
    private final JLabel apiPortLabel = new JLabel("http://127.0.0.1:8080");
    private final JLabel defaultPrinterLabel = new JLabel("Loading...");
    private final JLabel paperSizeLabel = new JLabel("58 mm");

    private final JButton btnStartServer = new JButton("Start Server");
    private final JButton btnStopServer = new JButton("Stop Server");
    private final JButton btnTestPrint = new JButton("Test Print");

    public DashboardView(HttpService httpService, PrinterService printerService, Consumer<SidebarPanel.NavigationItem> onNavigate) {
        this.httpService = httpService;
        this.printerService = printerService;
        this.onNavigate = onNavigate;

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

        JLabel title = new JLabel("Dashboard");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Ringkasan status service dan printer aktif");
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

        // Top Row Cards Grid: Application Info, Server Status, Printer Status
        JPanel cardsGrid = new JPanel(new GridLayout(1, 3, 24, 0));
        cardsGrid.setOpaque(false);
        cardsGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        cardsGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

        cardsGrid.add(createAppInfoCard());
        cardsGrid.add(createServerStatusCard());
        cardsGrid.add(createPrinterStatusCard());

        container.add(cardsGrid);
        container.add(Box.createRigidArea(new Dimension(0, 24)));
        container.add(createQuickActionsCard());

        return container;
    }

    private CardPanel createAppInfoCard() {
        CardPanel card = new CardPanel("Aplikasi");
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setAlignmentX(Component.LEFT_ALIGNMENT);

        ApplicationConfig config = AppConfig.get();
        JLabel nameLabel = new JLabel(config.getApplication().getName());
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, 16f));

        JLabel versionLabel = new JLabel("Versi " + config.getApplication().getVersion());
        versionLabel.setFont(versionLabel.getFont().deriveFont(Font.BOLD, 11f));
        versionLabel.setForeground(UIManager.getColor("Label.disabledForeground"));
        versionLabel.setBorder(new EmptyBorder(2, 6, 2, 6));
        versionLabel.putClientProperty(FlatClientProperties.STYLE, ""
                + "background: $Badge.background;"
                + "arc: 8;"
        );

        JPanel nameRow = new JPanel();
        nameRow.setLayout(new BoxLayout(nameRow, BoxLayout.X_AXIS));
        nameRow.setOpaque(false);
        nameRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        nameRow.add(nameLabel);
        nameRow.add(Box.createRigidArea(new Dimension(8, 0)));
        nameRow.add(versionLabel);

        JLabel desc = new JLabel("HTTP Bridge ESC/POS");
        desc.setFont(desc.getFont().deriveFont(12f));
        desc.setForeground(UIManager.getColor("Label.disabledForeground"));
        desc.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(nameRow);
        content.add(Box.createRigidArea(new Dimension(0, 8)));
        content.add(desc);

        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private CardPanel createServerStatusCard() {
        CardPanel card = new CardPanel("HTTP Server");
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel statusRow = new JPanel();
        statusRow.setLayout(new BoxLayout(statusRow, BoxLayout.X_AXIS));
        statusRow.setOpaque(false);
        statusRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        serverStatusDot.setIcon(NavIcons.getDot(new Color(220, 53, 69), 12));
        serverStatusText.setFont(serverStatusText.getFont().deriveFont(Font.BOLD, 15f));

        statusRow.add(serverStatusDot);
        statusRow.add(Box.createRigidArea(new Dimension(8, 0)));
        statusRow.add(serverStatusText);

        apiPortLabel.setFont(apiPortLabel.getFont().deriveFont(12f));
        apiPortLabel.setForeground(new Color(0, 102, 204));
        apiPortLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        apiPortLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        apiPortLabel.setToolTipText("Klik untuk menyalin URL API");
        apiPortLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                Toolkit.getDefaultToolkit().getSystemClipboard()
                        .setContents(new StringSelection(apiPortLabel.getText()), null);
                JOptionPane.showMessageDialog(DashboardView.this,
                        "URL API berhasil disalin ke clipboard!",
                        "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        content.add(statusRow);
        content.add(Box.createRigidArea(new Dimension(0, 8)));
        content.add(apiPortLabel);

        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private CardPanel createPrinterStatusCard() {
        CardPanel card = new CardPanel("Default Printer");
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setAlignmentX(Component.LEFT_ALIGNMENT);

        defaultPrinterLabel.setFont(defaultPrinterLabel.getFont().deriveFont(Font.BOLD, 14f));
        defaultPrinterLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel paperRow = new JPanel();
        paperRow.setLayout(new BoxLayout(paperRow, BoxLayout.X_AXIS));
        paperRow.setOpaque(false);
        paperRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel labelPaper = new JLabel("Ukuran Kertas: ");
        labelPaper.setFont(labelPaper.getFont().deriveFont(12f));
        labelPaper.setForeground(UIManager.getColor("Label.disabledForeground"));

        paperSizeLabel.setFont(paperSizeLabel.getFont().deriveFont(Font.BOLD, 12f));

        paperRow.add(labelPaper);
        paperRow.add(paperSizeLabel);

        content.add(defaultPrinterLabel);
        content.add(Box.createRigidArea(new Dimension(0, 8)));
        content.add(paperRow);

        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private CardPanel createQuickActionsCard() {
        CardPanel card = new CardPanel("Aksi Cepat");
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel actionsGrid = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        actionsGrid.setOpaque(false);
        actionsGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnStartServer.setFont(btnStartServer.getFont().deriveFont(Font.BOLD, 13f));
        btnStartServer.putClientProperty(FlatClientProperties.STYLE, ""
                + "background: #0067C0;"
                + "foreground: #FFFFFF;"
                + "arc: 10;"
        );
        btnStartServer.addActionListener(e -> {
            try {
                httpService.start();
                refreshData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal menjalankan server: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnStopServer.setFont(btnStopServer.getFont().deriveFont(Font.BOLD, 13f));
        btnStopServer.putClientProperty(FlatClientProperties.STYLE, ""
                + "background: #DC3545;"
                + "foreground: #FFFFFF;"
                + "arc: 10;"
        );
        btnStopServer.addActionListener(e -> {
            httpService.stop();
            refreshData();
        });

        btnTestPrint.setFont(btnTestPrint.getFont().deriveFont(Font.BOLD, 13f));
        btnTestPrint.putClientProperty(FlatClientProperties.STYLE, "arc: 10;");
        btnTestPrint.addActionListener(e -> doTestPrint());

        JButton btnOpenPrinterConfig = new JButton("Konfigurasi Printer");
        btnOpenPrinterConfig.setFont(btnOpenPrinterConfig.getFont().deriveFont(Font.PLAIN, 13f));
        btnOpenPrinterConfig.putClientProperty(FlatClientProperties.STYLE, "arc: 10;");
        btnOpenPrinterConfig.addActionListener(e -> {
            if (onNavigate != null) {
                onNavigate.accept(SidebarPanel.NavigationItem.PRINTER);
            }
        });

        actionsGrid.add(btnStartServer);
        actionsGrid.add(Box.createRigidArea(new Dimension(12, 0)));
        actionsGrid.add(btnStopServer);
        actionsGrid.add(Box.createRigidArea(new Dimension(12, 0)));
        actionsGrid.add(btnTestPrint);
        actionsGrid.add(Box.createRigidArea(new Dimension(12, 0)));
        actionsGrid.add(btnOpenPrinterConfig);

        card.add(actionsGrid, BorderLayout.CENTER);
        return card;
    }

    private void doTestPrint() {
        btnTestPrint.setEnabled(false);
        java.util.concurrent.CompletableFuture.runAsync(() -> {
            try {
                String defPrinter = AppConfig.get().getPrinter().getDefaultPrinter();
                printerService.testPrint(defPrinter);
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this,
                        "Test Print berhasil dikirim ke printer!",
                        "Sukses", JOptionPane.INFORMATION_MESSAGE));
            } catch (Exception ex) {
                Log.error("Gagal melakukan Test Print", ex);
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this,
                        "Gagal melakukan Test Print: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE));
            } finally {
                SwingUtilities.invokeLater(() -> btnTestPrint.setEnabled(true));
            }
        });
    }

    public void refreshData() {
        SwingUtilities.invokeLater(() -> {
            boolean isRunning = httpService.isRunning();
            int port = httpService.getPort();

            if (isRunning) {
                serverStatusDot.setIcon(NavIcons.getDot(new Color(40, 167, 69), 12));
                serverStatusText.setText("Running");
                serverStatusText.setForeground(new Color(40, 167, 69));
                btnStartServer.setEnabled(false);
                btnStopServer.setEnabled(true);
            } else {
                serverStatusDot.setIcon(NavIcons.getDot(new Color(220, 53, 69), 12));
                serverStatusText.setText("Stopped");
                serverStatusText.setForeground(new Color(220, 53, 69));
                btnStartServer.setEnabled(true);
                btnStopServer.setEnabled(false);
            }

            apiPortLabel.setText("http://127.0.0.1:" + port);

            String defPrinter = AppConfig.get().getPrinter().getDefaultPrinter();
            if (defPrinter == null || defPrinter.trim().isEmpty()) {
                defaultPrinterLabel.setText("Belum Dipilih");
                defaultPrinterLabel.setForeground(new Color(220, 53, 69));
            } else {
                defaultPrinterLabel.setText(defPrinter);
                defaultPrinterLabel.setForeground(UIManager.getColor("Label.foreground"));
            }

            paperSizeLabel.setText(AppConfig.get().getPrinter().getPaperSize() + " mm");
        });
    }
}
