package devyana.kekita.printbridge.ui;

import com.formdev.flatlaf.FlatClientProperties;
import devyana.kekita.printbridge.ui.components.CardPanel;
import devyana.kekita.printbridge.util.Log;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.StringSelection;

public class LogsView extends JPanel implements Log.LogListener {

    private final DefaultTableModel tableModel;
    private final JTable logTable;
    private final JButton btnClear = new JButton("Bersihkan Log");
    private final JButton btnCopy = new JButton("Salin Semua");

    public LogsView() {
        setLayout(new BorderLayout(0, 16));
        setBorder(new EmptyBorder(14, 28, 14, 28));
        setOpaque(false);

        tableModel = new DefaultTableModel(new Object[]{"Waktu", "Tingkat", "Pesan Aktivitas"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        logTable = new JTable(tableModel);
        setupTableStyle();

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createBodyPanel(), BorderLayout.CENTER);

        Log.addListener(this);
    }

    private void setupTableStyle() {
        logTable.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logTable.setRowHeight(26);
        logTable.setShowGrid(false);
        logTable.setIntercellSpacing(new Dimension(0, 0));

        logTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        logTable.getColumnModel().getColumn(0).setMaxWidth(120);

        logTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        logTable.getColumnModel().getColumn(1).setMaxWidth(120);

        // Custom renderer for level column color
        logTable.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value != null) {
                    String val = value.toString();
                    if ("ERROR".equals(val)) {
                        c.setForeground(new Color(220, 53, 69));
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else if ("SUCCESS".equals(val)) {
                        c.setForeground(new Color(40, 167, 69));
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else if ("WARN".equals(val)) {
                        c.setForeground(new Color(255, 193, 7));
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else {
                        c.setForeground(UIManager.getColor("Table.foreground"));
                        setFont(getFont().deriveFont(Font.PLAIN));
                    }
                }
                return c;
            }
        });
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);

        JLabel title = new JLabel("Log Aktivitas");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Catatan aktivitas runtime service, koneksi printer, dan HTTP request");
        subtitle.setFont(subtitle.getFont().deriveFont(13f));
        subtitle.setForeground(UIManager.getColor("Label.disabledForeground"));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        separator.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(title);
        header.add(Box.createRigidArea(new Dimension(0, 4)));
        header.add(subtitle);
        header.add(Box.createRigidArea(new Dimension(0, 14)));
        header.add(separator);

        return header;
    }

    private JPanel createBodyPanel() {
        CardPanel card = new CardPanel();

        JScrollPane scrollPane = new JScrollPane(logTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(UIManager.getColor("Table.background"));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        toolbar.setOpaque(false);

        btnClear.setFont(btnClear.getFont().deriveFont(Font.PLAIN, 13f));
        btnClear.putClientProperty(FlatClientProperties.STYLE, "arc: 10;");
        btnClear.addActionListener(e -> tableModel.setRowCount(0));

        btnCopy.setFont(btnCopy.getFont().deriveFont(Font.PLAIN, 13f));
        btnCopy.putClientProperty(FlatClientProperties.STYLE, "arc: 10;");
        btnCopy.addActionListener(e -> copyLogsToClipboard());

        toolbar.add(btnCopy);
        toolbar.add(btnClear);

        card.add(toolbar, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(card, BorderLayout.CENTER);
        return wrapper;
    }

    private void copyLogsToClipboard() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            sb.append(tableModel.getValueAt(i, 0)).append(" ")
              .append("[").append(tableModel.getValueAt(i, 1)).append("] ")
              .append(tableModel.getValueAt(i, 2)).append("\n");
        }
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(sb.toString()), null);
        JOptionPane.showMessageDialog(this, "Log aktivitas berhasil disalin ke clipboard!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void onLog(String timestamp, Log.Level level, String message) {
        SwingUtilities.invokeLater(() -> {
            tableModel.addRow(new Object[]{timestamp, level.name(), message});

            // Auto-scroll to bottom
            int lastRow = tableModel.getRowCount() - 1;
            if (lastRow >= 0) {
                logTable.scrollRectToVisible(logTable.getCellRect(lastRow, 0, true));
            }
        });
    }
}
