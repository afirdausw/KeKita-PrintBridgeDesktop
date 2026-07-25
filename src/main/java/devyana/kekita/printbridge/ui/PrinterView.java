package devyana.kekita.printbridge.ui;

import com.formdev.flatlaf.FlatClientProperties;
import devyana.kekita.printbridge.api.PrinterData;
import devyana.kekita.printbridge.config.AppConfig;
import devyana.kekita.printbridge.service.PrinterService;
import devyana.kekita.printbridge.ui.components.CardPanel;
import devyana.kekita.printbridge.util.EscPosImageUtil;
import devyana.kekita.printbridge.util.Log;
import devyana.kekita.printbridge.util.WindowsStartupUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

public class PrinterView extends JPanel {

    private final PrinterService printerService;
    private final JComboBox<String> printerComboBox = new JComboBox<>();
    private final JRadioButton radio58mm = new JRadioButton("58 mm");
    private final JRadioButton radio80mm = new JRadioButton("80 mm");
    private final JCheckBox autoReconnectCheckBox = new JCheckBox("Otomatis Koneksi Ulang (Auto Reconnect)");
    private final JCheckBox autoStartCheckBox = new JCheckBox("Jalankan Otomatis Saat Windows Dinyalakan (Auto Start)");

    private final JCheckBox enableLogoCheckBox = new JCheckBox("Aktifkan Cetak Logo di Atas Struk");
    private final JButton btnUploadLogo = new JButton("Upload Logo");
    private final JButton btnDeleteLogo = new JButton("Hapus Logo");
    private final JLabel logoPreviewLabel = new JLabel("Belum ada logo terpasang");

    private final JCheckBox cashDrawerCheckBox = new JCheckBox("Buka Laci Kasir Otomatis (Cash Drawer Pulse)");
    private final JCheckBox autoCutCheckBox = new JCheckBox("Potong Kertas Otomatis (Auto Cut)");

    private final JButton btnRefresh = new JButton("Refresh Printer");
    private final JButton btnSave = new JButton("Simpan Konfigurasi");
    private final JButton btnTestPrint = new JButton("Test Print");

    public PrinterView(PrinterService printerService) {
        this.printerService = printerService;

        setLayout(new BorderLayout(0, 16));
        setBorder(new EmptyBorder(14, 28, 14, 28));
        setOpaque(false);

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createBodyPanel(), BorderLayout.CENTER);

        loadPrinterData();
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);

        JLabel title = new JLabel("Konfigurasi Printer");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Pilih printer default Windows, ukuran kertas thermal, dan fitur hardware");
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

        container.add(createPrinterSelectionCard());
        container.add(Box.createRigidArea(new Dimension(0, 14)));
        container.add(createPaperConfigCard());
        container.add(Box.createRigidArea(new Dimension(0, 14)));
        container.add(createHardwareOptionsCard());
        container.add(Box.createRigidArea(new Dimension(0, 14)));
        container.add(createActionsCard());

        JScrollPane scrollPane = new JScrollPane(container);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setBlockIncrement(64);

        JPanel mainWrapper = new JPanel(new BorderLayout());
        mainWrapper.setOpaque(false);
        mainWrapper.add(scrollPane, BorderLayout.CENTER);
        return mainWrapper;
    }

    private CardPanel createPrinterSelectionCard() {
        CardPanel card = new CardPanel("Pilih Default Printer Windows");
        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);
        content.setAlignmentX(Component.LEFT_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 8);

        // Row 1: Label & Dropdown & Refresh button
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        JLabel lblSelect = new JLabel("Windows Printer:");
        lblSelect.setFont(lblSelect.getFont().deriveFont(Font.BOLD, 13f));
        content.add(lblSelect, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        printerComboBox.setFont(printerComboBox.getFont().deriveFont(13f));
        printerComboBox.putClientProperty(FlatClientProperties.STYLE, "arc: 10;");
        content.add(printerComboBox, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.0;
        btnRefresh.setFont(btnRefresh.getFont().deriveFont(Font.PLAIN, 13f));
        btnRefresh.putClientProperty(FlatClientProperties.STYLE, "arc: 10;");
        btnRefresh.addActionListener(e -> loadPrinterData());
        content.add(btnRefresh, gbc);

        // Row 2: Info Text
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        JLabel infoLabel = new JLabel("Daftar terdeteksi dari System Windows Print Spooler & Bluetooth COM Ports");
        infoLabel.setFont(infoLabel.getFont().deriveFont(11f));
        infoLabel.setForeground(UIManager.getColor("Label.disabledForeground"));
        content.add(infoLabel, gbc);

        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private CardPanel createPaperConfigCard() {
        CardPanel card = new CardPanel("Pengaturan Kertas & System Startup");
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblPaper = new JLabel("Ukuran Kertas Thermal:");
        lblPaper.setFont(lblPaper.getFont().deriveFont(Font.BOLD, 13f));
        lblPaper.setAlignmentX(Component.LEFT_ALIGNMENT);

        ButtonGroup bg = new ButtonGroup();
        bg.add(radio58mm);
        bg.add(radio80mm);

        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        radioPanel.setOpaque(false);
        radioPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        radioPanel.add(radio58mm);
        radioPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        radioPanel.add(radio80mm);

        autoReconnectCheckBox.setFont(autoReconnectCheckBox.getFont().deriveFont(13f));
        autoReconnectCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        autoStartCheckBox.setFont(autoStartCheckBox.getFont().deriveFont(13f));
        autoStartCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(lblPaper);
        content.add(Box.createRigidArea(new Dimension(0, 6)));
        content.add(radioPanel);
        content.add(Box.createRigidArea(new Dimension(0, 10)));
        content.add(autoReconnectCheckBox);
        content.add(Box.createRigidArea(new Dimension(0, 8)));
        content.add(autoStartCheckBox);

        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private CardPanel createHardwareOptionsCard() {
        CardPanel card = new CardPanel("Fitur Tambahan (Logo, Laci Kasir & Cutter)");
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Logo Checkbox & Controls
        enableLogoCheckBox.setFont(enableLogoCheckBox.getFont().deriveFont(Font.BOLD, 13f));
        enableLogoCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel logoActionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        logoActionPanel.setOpaque(false);
        logoActionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnUploadLogo.setFont(btnUploadLogo.getFont().deriveFont(Font.PLAIN, 12f));
        btnUploadLogo.putClientProperty(FlatClientProperties.STYLE, "arc: 8;");
        btnUploadLogo.addActionListener(e -> chooseAndUploadLogo());

        btnDeleteLogo.setFont(btnDeleteLogo.getFont().deriveFont(Font.PLAIN, 12f));
        btnDeleteLogo.putClientProperty(FlatClientProperties.STYLE, "arc: 8;");
        btnDeleteLogo.addActionListener(e -> deleteLogo());

        logoActionPanel.add(btnUploadLogo);
        logoActionPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        logoActionPanel.add(btnDeleteLogo);

        logoPreviewLabel.setFont(logoPreviewLabel.getFont().deriveFont(11f));
        logoPreviewLabel.setForeground(UIManager.getColor("Label.disabledForeground"));
        logoPreviewLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel logoHintLabel = new JLabel("Resized ke ~40% lebar (150px/200px) & rata tengah. Disarankan menggunakan gambar ber-background putih solid.");
        logoHintLabel.setFont(logoHintLabel.getFont().deriveFont(11f));
        logoHintLabel.setForeground(UIManager.getColor("Label.disabledForeground"));
        logoHintLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Cash Drawer & Auto Cut Checkboxes
        cashDrawerCheckBox.setFont(cashDrawerCheckBox.getFont().deriveFont(13f));
        cashDrawerCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        autoCutCheckBox.setFont(autoCutCheckBox.getFont().deriveFont(13f));
        autoCutCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(enableLogoCheckBox);
        content.add(Box.createRigidArea(new Dimension(0, 6)));
        content.add(logoActionPanel);
        content.add(Box.createRigidArea(new Dimension(0, 6)));
        content.add(logoPreviewLabel);
        content.add(Box.createRigidArea(new Dimension(0, 2)));
        content.add(logoHintLabel);
        content.add(Box.createRigidArea(new Dimension(0, 14)));
        content.add(cashDrawerCheckBox);
        content.add(Box.createRigidArea(new Dimension(0, 8)));
        content.add(autoCutCheckBox);

        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private CardPanel createActionsCard() {
        CardPanel card = new CardPanel("Aksi");
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnSave.setFont(btnSave.getFont().deriveFont(Font.BOLD, 13f));
        btnSave.putClientProperty(FlatClientProperties.STYLE, ""
                + "background: #0067C0;"
                + "foreground: #FFFFFF;"
                + "arc: 10;"
        );
        btnSave.addActionListener(e -> saveConfiguration());

        btnTestPrint.setFont(btnTestPrint.getFont().deriveFont(Font.BOLD, 13f));
        btnTestPrint.putClientProperty(FlatClientProperties.STYLE, "arc: 10;");
        btnTestPrint.addActionListener(e -> doTestPrint());

        panel.add(btnSave);
        panel.add(Box.createRigidArea(new Dimension(12, 0)));
        panel.add(btnTestPrint);

        card.add(panel, BorderLayout.CENTER);
        return card;
    }

    private void chooseAndUploadLogo() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Pilih Gambar Logo Struk");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Gambar Logo (PNG, JPG, JPEG, BMP)", "png", "jpg", "jpeg", "bmp"));

        int userSelection = fileChooser.showOpenDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                int paperSize = radio80mm.isSelected() ? 80 : 58;
                // Target logo width ~40% of paper width (compact & centered)
                int maxWidth = (paperSize == 80) ? 200 : 150;
                String targetPath = AppConfig.get().getPrinter().getLogoPath();

                BufferedImage bwImage = EscPosImageUtil.processAndSaveLogo(selectedFile, maxWidth, targetPath);

                enableLogoCheckBox.setSelected(true);
                updateLogoPreview(targetPath, bwImage);

                JOptionPane.showMessageDialog(this,
                        "Logo berhasil diunggah, dikonversi ke Hitam Putih, dan disimpan secara permanen!",
                        "Sukses Upload Logo", JOptionPane.INFORMATION_MESSAGE);
                Log.success("Logo struk diperbarui dari " + selectedFile.getName());
            } catch (Exception ex) {
                Log.error("Gagal memproses upload logo", ex);
                JOptionPane.showMessageDialog(this,
                        "Gagal memproses gambar logo: " + ex.getMessage(),
                        "Error Upload", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteLogo() {
        String targetPath = AppConfig.get().getPrinter().getLogoPath();
        File logoFile = new File(targetPath);
        if (logoFile.exists()) {
            logoFile.delete();
        }

        enableLogoCheckBox.setSelected(false);
        logoPreviewLabel.setIcon(null);
        logoPreviewLabel.setText("Belum ada logo terpasang");
        Log.info("Logo struk telah dihapus.");
    }

    private void refreshLogoPreview() {
        String targetPath = AppConfig.get().getPrinter().getLogoPath();
        File logoFile = new File(targetPath);
        if (logoFile.exists()) {
            try {
                BufferedImage img = ImageIO.read(logoFile);
                if (img != null) {
                    updateLogoPreview(targetPath, img);
                    return;
                }
            } catch (Exception ignored) {
            }
        }
        logoPreviewLabel.setIcon(null);
        logoPreviewLabel.setText("Belum ada logo terpasang");
    }

    private void updateLogoPreview(String path, BufferedImage img) {
        int previewWidth = Math.min(img.getWidth(), 180);
        int previewHeight = (int) ((double) previewWidth / img.getWidth() * img.getHeight());

        Image scaled = img.getScaledInstance(previewWidth, previewHeight, Image.SCALE_SMOOTH);
        logoPreviewLabel.setIcon(new ImageIcon(scaled));
        logoPreviewLabel.setText(" Preview Logo (" + img.getWidth() + "x" + img.getHeight() + "px)");
    }

    public void loadPrinterData() {
        SwingUtilities.invokeLater(() -> {
            printerComboBox.removeAllItems();
            List<PrinterData> printers = printerService.getPrinters();
            String configuredDefault = AppConfig.get().getPrinter().getDefaultPrinter();

            String selectedToSet = null;

            for (PrinterData p : printers) {
                printerComboBox.addItem(p.getName());
                if (configuredDefault != null && configuredDefault.equalsIgnoreCase(p.getName())) {
                    selectedToSet = p.getName();
                } else if (selectedToSet == null && p.isDefault()) {
                    selectedToSet = p.getName();
                }
            }

            if (selectedToSet != null) {
                printerComboBox.setSelectedItem(selectedToSet);
            }

            int paperSize = AppConfig.get().getPrinter().getPaperSize();
            if (paperSize == 80) {
                radio80mm.setSelected(true);
            } else {
                radio58mm.setSelected(true);
            }

            autoReconnectCheckBox.setSelected(AppConfig.get().getPrinter().isAutoReconnect());
            autoStartCheckBox.setSelected(WindowsStartupUtil.isAutoStartEnabled());
            enableLogoCheckBox.setSelected(AppConfig.get().getPrinter().isEnableLogo());
            cashDrawerCheckBox.setSelected(AppConfig.get().getPrinter().isEnableCashDrawer());
            autoCutCheckBox.setSelected(AppConfig.get().getPrinter().isAutoCut());

            refreshLogoPreview();

            Log.info("Daftar printer diperbarui: " + printers.size() + " printer terdeteksi");
        });
    }

    private void saveConfiguration() {
        String selectedPrinter = (String) printerComboBox.getSelectedItem();
        if (selectedPrinter == null) {
            selectedPrinter = "";
        }

        int paperSize = radio80mm.isSelected() ? 80 : 58;
        boolean autoReconnect = autoReconnectCheckBox.isSelected();
        boolean autoStart = autoStartCheckBox.isSelected();
        boolean enableLogo = enableLogoCheckBox.isSelected();
        boolean enableCashDrawer = cashDrawerCheckBox.isSelected();
        boolean autoCut = autoCutCheckBox.isSelected();

        AppConfig.get().getPrinter().setDefaultPrinter(selectedPrinter);
        AppConfig.get().getPrinter().setPaperSize(paperSize);
        AppConfig.get().getPrinter().setAutoReconnect(autoReconnect);
        AppConfig.get().getPrinter().setEnableLogo(enableLogo);
        AppConfig.get().getPrinter().setEnableCashDrawer(enableCashDrawer);
        AppConfig.get().getPrinter().setAutoCut(autoCut);

        WindowsStartupUtil.setAutoStartEnabled(autoStart);

        boolean saved = AppConfig.save();
        if (saved) {
            JOptionPane.showMessageDialog(this,
                    "Konfigurasi printer, hardware, dan Windows Startup berhasil disimpan!",
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Gagal menyimpan konfigurasi printer.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doTestPrint() {
        String selectedPrinter = (String) printerComboBox.getSelectedItem();
        btnTestPrint.setEnabled(false);

        java.util.concurrent.CompletableFuture.runAsync(() -> {
            try {
                printerService.testPrint(selectedPrinter);
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this,
                        "Test Print berhasil dikirim ke " + (selectedPrinter != null ? selectedPrinter : "default printer"),
                        "Sukses", JOptionPane.INFORMATION_MESSAGE));
            } catch (Exception ex) {
                Log.error("Test Print gagal", ex);
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this,
                        "Gagal mencetak: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE));
            } finally {
                SwingUtilities.invokeLater(() -> btnTestPrint.setEnabled(true));
            }
        });
    }
}
