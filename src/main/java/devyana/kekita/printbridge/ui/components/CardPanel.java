package devyana.kekita.printbridge.ui.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CardPanel extends JPanel {

    public CardPanel() {
        this(null, 0);
    }

    public CardPanel(String title) {
        this(title, 0);
    }

    public CardPanel(String title, int arcRadius) {
        super();
        setLayout(new BorderLayout(0, 10));
        setOpaque(false);
        setBorder(new EmptyBorder(4, 0, 8, 0));

        if (title != null && !title.isEmpty()) {
            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 15f));
            titleLabel.setForeground(UIManager.getColor("Label.foreground"));
            titleLabel.setBorder(new EmptyBorder(0, 0, 6, 0));
            titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            add(titleLabel, BorderLayout.NORTH);
        }
    }
}
