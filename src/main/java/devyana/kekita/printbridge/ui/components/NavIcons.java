package devyana.kekita.printbridge.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

public class NavIcons {

    public enum Type {
        DASHBOARD, PRINTER, SERVER, LOGS, HELP
    }

    public static Icon get(Type type) {
        return new VectorIcon(type);
    }

    public static Icon getDot(Color color, int size) {
        return new DotIcon(color, size);
    }

    private static class VectorIcon implements Icon {
        private final Type type;
        private final int width = 18;
        private final int height = 18;

        public VectorIcon(Type type) {
            this.type = type;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            Color color = c != null ? c.getForeground() : Color.DARK_GRAY;
            g2.setColor(color);
            g2.translate(x, y);

            switch (type) {
                case DASHBOARD -> paintDashboard(g2);
                case PRINTER -> paintPrinter(g2);
                case SERVER -> paintServer(g2);
                case LOGS -> paintLogs(g2);
                case HELP -> paintHelp(g2);
            }

            g2.dispose();
        }

        private void paintDashboard(Graphics2D g2) {
            // 2x2 grid cards layout
            g2.fill(new RoundRectangle2D.Float(1, 1, 7, 7, 2, 2));
            g2.fill(new RoundRectangle2D.Float(10, 1, 7, 7, 2, 2));
            g2.fill(new RoundRectangle2D.Float(1, 10, 7, 7, 2, 2));
            g2.fill(new RoundRectangle2D.Float(10, 10, 7, 7, 2, 2));
        }

        private void paintPrinter(Graphics2D g2) {
            // Paper out top
            g2.drawRect(5, 1, 8, 4);
            // Body
            g2.setStroke(new BasicStroke(1.5f));
            g2.draw(new RoundRectangle2D.Float(2, 5, 14, 8, 3, 3));
            // Paper out bottom
            g2.fillRect(5, 11, 8, 5);
        }

        private void paintServer(Graphics2D g2) {
            // Globe / Network circle & lines
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawOval(1, 1, 16, 16);
            g2.drawLine(1, 9, 17, 9);
            g2.drawOval(5, 1, 8, 16);
        }

        private void paintLogs(Graphics2D g2) {
            // Document paper sheet
            Path2D path = new Path2D.Float();
            path.moveTo(3, 1);
            path.lineTo(11, 1);
            path.lineTo(15, 5);
            path.lineTo(15, 17);
            path.lineTo(3, 17);
            path.closePath();

            g2.setStroke(new BasicStroke(1.4f));
            g2.draw(path);

            // Document lines
            g2.drawLine(6, 6, 10, 6);
            g2.drawLine(6, 9, 12, 9);
            g2.drawLine(6, 12, 12, 12);
        }

        private void paintHelp(Graphics2D g2) {
            // Circle with question mark
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawOval(1, 1, 16, 16);
            g2.setFont(new Font("SansSerif", Font.BOLD, 11));
            g2.drawString("?", 6, 13);
        }

        @Override
        public int getIconWidth() {
            return width;
        }

        @Override
        public int getIconHeight() {
            return height;
        }
    }

    private static class DotIcon implements Icon {
        private final Color color;
        private final int size;

        public DotIcon(Color color, int size) {
            this.color = color;
            this.size = size;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(color != null ? color : Color.GRAY);
            g2.fillOval(x, y + 2, size, size);

            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size + 4;
        }
    }
}
