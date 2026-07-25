package devyana.kekita.printbridge.ui;

import com.formdev.flatlaf.FlatClientProperties;
import devyana.kekita.printbridge.config.AppConfig;
import devyana.kekita.printbridge.ui.components.NavIcons;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
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

        JLabel titleLabel = new JLabel("PrintBridge");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));
        titleLabel.setForeground(UIManager.getColor("Label.foreground"));

        String version = AppConfig.get().getApplication().getVersion();
        JLabel verLabel = new JLabel("v" + version);
        verLabel.setFont(verLabel.getFont().deriveFont(Font.BOLD, 11f));
        verLabel.setForeground(UIManager.getColor("Label.disabledForeground"));
        verLabel.setBorder(new EmptyBorder(2, 7, 2, 7)); // Internal padding for version background badge!
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

        header.add(topRow);
        header.add(Box.createRigidArea(new Dimension(0, 4)));
        header.add(descLabel);

        return header;
    }

    private JPanel createNavMenuPanel() {
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setOpaque(false);
        menuPanel.setBorder(new EmptyBorder(10, 12, 10, 12));

        for (NavigationItem item : NavigationItem.values()) {
            JButton btn = createNavButton(item);
            navButtons.add(btn);
            menuPanel.add(btn);
            menuPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        }

        updateButtonStyles();
        return menuPanel;
    }

    private JButton createNavButton(NavigationItem item) {
        JButton btn = new JButton(item.getLabel());
        btn.setIcon(NavIcons.get(item.getIconType()));
        btn.setIconTextGap(12);
        btn.setFont(btn.getFont().deriveFont(Font.PLAIN, 14f));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setPreferredSize(new Dimension(200, 40));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> {
            activeItem = item;
            updateButtonStyles();
            if (onNavigate != null) {
                onNavigate.accept(item);
            }
        });

        return btn;
    }

    private void updateButtonStyles() {
        for (int i = 0; i < NavigationItem.values().length; i++) {
            NavigationItem item = NavigationItem.values()[i];
            JButton btn = navButtons.get(i);
            boolean isSelected = item == activeItem;

            if (isSelected) {
                btn.putClientProperty(FlatClientProperties.STYLE, ""
                        + "background: $Tree.selectionBackground;"
                        + "foreground: $Tree.selectionForeground;"
                        + "arc: 10;"
                        + "borderWidth: 0;"
                );
                btn.setFont(btn.getFont().deriveFont(Font.BOLD, 14f));
            } else {
                btn.putClientProperty(FlatClientProperties.STYLE, ""
                        + "background: #00000000;"
                        + "foreground: $Label.foreground;"
                        + "arc: 10;"
                        + "borderWidth: 0;"
                );
                btn.setFont(btn.getFont().deriveFont(Font.PLAIN, 14f));
            }
        }
    }

    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(12, 16, 16, 16));
        footer.putClientProperty(FlatClientProperties.STYLE, "border: 1,0,0,0,$Component.borderColor");

        statusDotLabel.setIcon(NavIcons.getDot(new Color(220, 53, 69), 10)); // Red for stopped
        statusTextLabel.setFont(statusTextLabel.getFont().deriveFont(Font.BOLD, 12f));

        footer.add(statusDotLabel);
        footer.add(statusTextLabel);

        return footer;
    }

    public void updateServerStatus(boolean running, int port) {
        SwingUtilities.invokeLater(() -> {
            if (running) {
                statusDotLabel.setIcon(NavIcons.getDot(new Color(40, 167, 69), 10)); // Green for running
                statusTextLabel.setText("Running (:" + port + ")");
            } else {
                statusDotLabel.setIcon(NavIcons.getDot(new Color(220, 53, 69), 10)); // Red for stopped
                statusTextLabel.setText("Server Stopped");
            }
        });
    }

    public void setActiveItem(NavigationItem item) {
        this.activeItem = item;
        updateButtonStyles();
    }
}
