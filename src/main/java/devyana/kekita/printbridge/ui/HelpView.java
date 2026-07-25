package devyana.kekita.printbridge.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class HelpView extends JPanel {

    public HelpView() {
        setLayout(new BorderLayout(0, 16));
        setBorder(new EmptyBorder(14, 28, 14, 28));
        setOpaque(false);

        add(createHeaderPanel(), BorderLayout.NORTH);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);

        JLabel title = new JLabel("Bantuan");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Panduan dan dokumentasi penggunaan PrintBridge");
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
}
