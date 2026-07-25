package devyana.kekita.printbridge.ui;

import com.formdev.flatlaf.FlatClientProperties;
import devyana.kekita.printbridge.service.HttpService;
import devyana.kekita.printbridge.service.PrinterService;
import devyana.kekita.printbridge.util.Log;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.net.URL;

public class MainFrame extends JFrame {

    private final HttpService httpService;
    private final PrinterService printerService;

    private SidebarPanel sidebarPanel;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    private DashboardView dashboardView;
    private PrinterView printerView;
    private ServerView serverView;
    private LogsView logsView;
    private HelpView helpView;

    private TrayIcon trayIcon;

    public MainFrame(HttpService httpService, PrinterService printerService) {
        this.httpService = httpService;
        this.printerService = printerService;

        setTitle("PrintBridge - Desktop HTTP ESC/POS Bridge");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(860, 580));
        setSize(960, 640);
        setLocationRelativeTo(null);

        Image appIcon = loadAppIcon();
        if (appIcon != null) {
            setIconImage(appIcon);
        }

        initUI();
        initTrayIcon();
        initEvents();
    }

    private Image loadAppIcon() {
        try {
            URL url = getClass().getResource("/icons/icon.png");
            if (url != null) {
                return ImageIO.read(url);
            }
        } catch (Exception e) {
            Log.warn("Gagal memuat icons/icon.png: " + e.getMessage());
        }
        return null;
    }

    private void initUI() {
        getRootPane().putClientProperty(FlatClientProperties.STYLE, "arc: 16;");

        sidebarPanel = new SidebarPanel(this::navigateTo);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setOpaque(false);

        dashboardView = new DashboardView(httpService, printerService, this::navigateTo);
        printerView = new PrinterView(printerService);
        serverView = new ServerView(httpService);
        logsView = new LogsView();
        helpView = new HelpView();

        contentPanel.add(dashboardView, SidebarPanel.NavigationItem.DASHBOARD.name());
        contentPanel.add(printerView, SidebarPanel.NavigationItem.PRINTER.name());
        contentPanel.add(serverView, SidebarPanel.NavigationItem.SERVER.name());
        contentPanel.add(logsView, SidebarPanel.NavigationItem.LOGS.name());
        contentPanel.add(helpView, SidebarPanel.NavigationItem.HELP.name());

        Container container = getContentPane();
        container.setLayout(new BorderLayout());
        container.add(sidebarPanel, BorderLayout.WEST);
        container.add(contentPanel, BorderLayout.CENTER);
    }

    private void initEvents() {
        httpService.addStateListener((running, port) -> {
            sidebarPanel.updateServerStatus(running, port);
            dashboardView.refreshData();
            serverView.refreshData();
        });

        sidebarPanel.updateServerStatus(httpService.isRunning(), httpService.getPort());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                minimizeToTrayOrExit();
            }
        });
    }

    private void navigateTo(SidebarPanel.NavigationItem item) {
        sidebarPanel.setActiveItem(item);
        cardLayout.show(contentPanel, item.name());

        if (item == SidebarPanel.NavigationItem.DASHBOARD) {
            dashboardView.refreshData();
        } else if (item == SidebarPanel.NavigationItem.PRINTER) {
            printerView.loadPrinterData();
        } else if (item == SidebarPanel.NavigationItem.SERVER) {
            serverView.refreshData();
        }
    }

    private void initTrayIcon() {
        if (!SystemTray.isSupported()) {
            return;
        }

        try {
            SystemTray tray = SystemTray.getSystemTray();
            Image iconImage = createTrayIconImage();

            PopupMenu popup = new PopupMenu();

            MenuItem openItem = new MenuItem("Buka PrintBridge");
            openItem.addActionListener(e -> restoreFromTray());

            MenuItem startServerItem = new MenuItem("Start Server");
            startServerItem.addActionListener(e -> {
                try {
                    httpService.start();
                } catch (Exception ex) {
                    Log.error("Gagal start server dari Tray", ex);
                }
            });

            MenuItem stopServerItem = new MenuItem("Stop Server");
            stopServerItem.addActionListener(e -> httpService.stop());

            MenuItem exitItem = new MenuItem("Keluar (Exit)");
            exitItem.addActionListener(e -> exitApplication());

            popup.add(openItem);
            popup.addSeparator();
            popup.add(startServerItem);
            popup.add(stopServerItem);
            popup.addSeparator();
            popup.add(exitItem);

            trayIcon = new TrayIcon(iconImage, "PrintBridge Service", popup);
            trayIcon.setImageAutoSize(true);
            trayIcon.addActionListener(e -> restoreFromTray());

            tray.add(trayIcon);
        } catch (Exception e) {
            Log.warn("Gagal inisialisasi Tray Icon: " + e.getMessage());
        }
    }

    private Image createTrayIconImage() {
        try {
            URL url = getClass().getResource("/icons/tray_icon.png");
            if (url != null) {
                return ImageIO.read(url);
            }
        } catch (Exception e) {
            Log.warn("Gagal memuat icons/tray_icon.png: " + e.getMessage());
        }

        // Fallback drawing if image fails to load
        int size = 16;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(new Color(0, 103, 192));
        g.fillRoundRect(1, 1, 14, 14, 4, 4);

        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 10));
        g.drawString("P", 4, 12);

        g.dispose();
        return img;
    }

    private void minimizeToTrayOrExit() {
        if (SystemTray.isSupported() && trayIcon != null) {
            setVisible(false);
            trayIcon.displayMessage("PrintBridge",
                    "PrintBridge tetap berjalan di System Tray",
                    TrayIcon.MessageType.INFO);
        } else {
            exitApplication();
        }
    }

    private void restoreFromTray() {
        setVisible(true);
        setExtendedState(JFrame.NORMAL);
        toFront();
        requestFocus();
    }

    private void exitApplication() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Apakah Anda yakin ingin menutup PrintBridge?\nService HTTP Server akan dihentikan.",
                "Konfirmasi Keluar",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            if (httpService != null && httpService.isRunning()) {
                httpService.stop();
            }
            if (trayIcon != null && SystemTray.isSupported()) {
                SystemTray.getSystemTray().remove(trayIcon);
            }
            System.exit(0);
        }
    }
}
