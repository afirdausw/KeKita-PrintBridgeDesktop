package devyana.kekita.printbridge.ui;

import com.formdev.flatlaf.FlatClientProperties;
import devyana.kekita.printbridge.config.AppConfig;
import devyana.kekita.printbridge.ui.components.NavIcons;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SidebarPanel extends JPanel {

    public enum NavigationItem {
        DASHBOARD("Dashboard", NavIcons.Type.DASHBOARD),
        PRINTER("Printer", NavIcons.Type.PRINTER),
        SERVER("Server", NavIcons.Type.SERVER),
        LOGS("Logs", NavIcons.Type.LOGS),
        HELP("Bantuan", NavIcons.Type.HELP);

        private final String label;
        private final NavIcons.Type iconType;

        NavigationItem(String label, NavIcons.Type iconType) {
            this.label = label;
            this.iconType = iconType;
        }

        public String getLabel() {
            return label;
        }

        public NavIcons.Type getIconType() {
            return iconType;
        }
    }

    private final Consumer<NavigationItem> onNavigate;
    private final List<JButton> navButtons = new ArrayList<>();
    private final JLabel statusDotLabel = new JLabel();
    private final JLabel statusTextLabel = new JLabel("Stopped");
    private NavigationItem activeItem = NavigationItem.DASHBOARD;

    public SidebarPanel(Consumer<NavigationItem> onNavigate) {
        this.onNavigate = onNavigate;
        setPreferredSize(new Dimension(230, 0));
        setLayout(new BorderLayout());
        putClientProperty(FlatClientProperties.STYLE, ""
                + "background: $Menu.background;"
                + "border: 0,0,0,1,$Component.borderColor;"
        );

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createNavMenuPanel(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(24, 20, 16, 20));

        JLabel logoLabel = new JLabel();
        try {
            URL logoUrl = getClass().getResource("/icons/icon.png");
            if (logoUrl != null) {
                Image logoImg = ImageIO.read(logoUrl);
                if (logoImg != null) {
                    Image scaledLogo = logoImg.getScaledInstance(36, 36, Image.SCALE_SMOOTH);
                    logoLabel.setIcon(new ImageIcon(scaledLogo));
                }
            }
        } catch (Exception ignored) {
        }
        logoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel("PrintBridge");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));
        titleLabel.setForeground(UIManager.getColor("Label.foreground"));

        String version = AppConfig.get().getApplication().getVersion();
        JLabel verLabel = new JLabel("v" + version);
        verLabel.setFont(verLabel.getFont().deriveFont(Font.BOLD, 11f));
        verLabel.setForeground(UIManager.getColor("Label.disabledForeground"));
        verLabel.setBorder(new EmptyBorder(2, 7, 2, 7));
        verLabel.putClientProperty(FlatClientProperties.STYLE, ""
                + "background: $Badge.background;"
                + "arc: 10;"
        );

        JPanel topRow = new JPanel();
        topRow.setLayout(new BoxLayout(topRow, BoxLayout.X_AXIS));
        topRow.setOpaque(false);
        topRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        topRow.add(titleLabel);
        topRow.add(Box.createRigidArea(new Dimension(8, 0)));
        topRow.add(verLabel);

        JLabel descLabel = new JLabel("POS ESC/POS Bridge");
        descLabel.setFont(descLabel.getFont().deriveFont(11f));
        descLabel.setForeground(UIManager.getColor("Label.disabledForeground"));
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (logoLabel.getIcon() != null) {
            header.add(logoLabel);
            header.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        header.add(topRow);
        header.add(Box.createRigidArea(new Dimension(0, 4)));
        header.add(descLabel);

        return header;
    }

    private JPanel createNavMenuPanel() {
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setOpaque(false);
        menuPanel.setBorder(new EmptyBorder(0, 12, 0, 12));

        for (NavigationItem item : NavigationItem.values()) {
            JButton btn = createNavButton(item);
            navButtons.add(btn);
            menuPanel.add(btn);
            menuPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        }

        updateButtonSelection();
        return menuPanel;
    }

    private JButton createNavButton(NavigationItem item) {
        JButton btn = new JButton(item.getLabel());
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setIcon(NavIcons.get(item.getIconType()));
        btn.setIconTextGap(12);
        btn.setFont(btn.getFont().deriveFont(Font.PLAIN, 13f));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btn.setPreferredSize(new Dimension(200, 38));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setFocusable(false);

        btn.addActionListener(e -> {
            if (onNavigate != null) {
                onNavigate.accept(item);
            }
        });

        return btn;
    }

    public void setActiveItem(NavigationItem item) {
        this.activeItem = item;
        updateButtonSelection();
    }

    private void updateButtonSelection() {
        for (int i = 0; i < NavigationItem.values().length; i++) {
            NavigationItem item = NavigationItem.values()[i];
            JButton btn = navButtons.get(i);
            boolean isSelected = (item == activeItem);

            if (isSelected) {
                btn.putClientProperty(FlatClientProperties.STYLE, ""
                        + "background: #0067C0;"
                        + "foreground: #FFFFFF;"
                        + "focusedBackground: #0067C0;"
                        + "hoverBackground: #0056A3;"
                        + "arc: 10;"
                );
            } else {
                btn.putClientProperty(FlatClientProperties.STYLE, ""
                        + "background: #00000000;"
                        + "foreground: $Label.foreground;"
                        + "hoverBackground: $Button.hoverBackground;"
                        + "arc: 10;"
                );
            }
            btn.setIcon(NavIcons.get(item.getIconType()));
        }
        repaint();
    }

    private JPanel createFooterPanel() {
        JPanel footer = new JPanel();
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(12, 20, 20, 20));

        JSeparator divider = new JSeparator(SwingConstants.HORIZONTAL);
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        divider.setPreferredSize(new Dimension(0, 1));
        divider.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        statusPanel.setOpaque(false);
        statusPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        statusDotLabel.setText("");
        statusDotLabel.setIcon(NavIcons.getDot(Color.decode("#DC3545"), 8));

        statusTextLabel.setFont(statusTextLabel.getFont().deriveFont(Font.BOLD, 12f));

        statusPanel.add(statusDotLabel);
        statusPanel.add(Box.createRigidArea(new Dimension(6, 0)));
        statusPanel.add(statusTextLabel);

        footer.add(divider);
        footer.add(Box.createRigidArea(new Dimension(0, 12)));
        footer.add(statusPanel);

        return footer;
    }

    public void updateServerStatus(boolean isRunning, int port) {
        SwingUtilities.invokeLater(() -> {
            if (isRunning) {
                statusDotLabel.setText("");
                statusDotLabel.setIcon(NavIcons.getDot(Color.decode("#28A745"), 8)); // Green vector dot
                statusTextLabel.setText("Server Running (:" + port + ")");
                statusTextLabel.setForeground(UIManager.getColor("Label.foreground"));
            } else {
                statusDotLabel.setText("");
                statusDotLabel.setIcon(NavIcons.getDot(Color.decode("#DC3545"), 8)); // Red vector dot
                statusTextLabel.setText("Server Stopped");
                statusTextLabel.setForeground(UIManager.getColor("Label.disabledForeground"));
            }
        });
    }
}
