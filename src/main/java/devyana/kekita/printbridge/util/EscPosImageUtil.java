package devyana.kekita.printbridge.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class EscPosImageUtil {

    /**
     * Memproses file logo (Fill background putih, Resize & B/W Monochrome thresholding) lalu menyimpannya.
     */
    public static BufferedImage processAndSaveLogo(File inputFile, int targetWidth, String targetSavePath) throws IOException {
        BufferedImage original = ImageIO.read(inputFile);
        if (original == null) {
            throw new IllegalArgumentException("File yang dipilih bukan gambar yang valid.");
        }

        int originalWidth = original.getWidth();
        int originalHeight = original.getHeight();

        // Hitung dimensi baru (jika original > targetWidth, scale down)
        int width = originalWidth;
        int height = originalHeight;

        if (targetWidth > 0 && width > targetWidth) {
            double ratio = (double) targetWidth / originalWidth;
            width = targetWidth;
            height = (int) (originalHeight * ratio);
        }

        // 1. Buat kanvas RGB baru dan ISI BACKGROUND DENGAN WARNA PUTIH SOLID (untuk menangani transparansi PNG)
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Isi background putih solid
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        // Gambar PNG di atas background putih
        g.drawImage(original, 0, 0, width, height, null);
        g.dispose();

        // 2. Konversi ke Monochrome (1-Bit Black & White Thresholding)
        BufferedImage bwImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = resized.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int gVal = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                // Luminance formula
                int luminance = (int) (0.299 * r + 0.587 * gVal + 0.114 * b);

                // Threshold: luminance < 160 dianggap hitam (pixel cetak), selebihnya putih
                if (luminance < 160) {
                    bwImage.setRGB(x, y, Color.BLACK.getRGB());
                } else {
                    bwImage.setRGB(x, y, Color.WHITE.getRGB());
                }
            }
        }

        // 3. Simpan file ke lokasi target
        File targetFile = new File(targetSavePath);
        File parentDir = targetFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        ImageIO.write(bwImage, "png", targetFile);
        return bwImage;
    }

    /**
     * Rescales BufferedImage proportionally if its width exceeds maxWidth.
     */
    public static BufferedImage scaleImageIfNeeded(BufferedImage original, int maxWidth) {
        if (original == null || maxWidth <= 0 || original.getWidth() <= maxWidth) {
            return original;
        }

        int width = maxWidth;
        int height = (int) ((double) maxWidth / original.getWidth() * original.getHeight());

        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        g.drawImage(original, 0, 0, width, height, null);
        g.dispose();

        return resized;
    }

    /**
     * Mengonversi BufferedImage Monochrome menjadi ESC/POS Raster Bit Image Bytes (GS v 0).
     */
    public static byte[] generateRasterBytes(BufferedImage image) throws IOException {
        if (image == null) {
            return new byte[0];
        }

        int width = image.getWidth();
        int height = image.getHeight();

        // Lebar dalam byte (setiap 1 byte menyimpan 8 pixel horizontal)
        int widthBytes = (width + 7) / 8;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // Center Alignment ESC a 1
        baos.write(new byte[]{0x1B, 0x61, 0x01});

        // GS v 0 0 xL xH yL yH
        int xL = widthBytes % 256;
        int xH = widthBytes / 256;
        int yL = height % 256;
        int yH = height / 256;

        baos.write(new byte[]{0x1D, 0x76, 0x30, 0x00, (byte) xL, (byte) xH, (byte) yL, (byte) yH});

        // Loop piksel untuk merakit bit data
        for (int y = 0; y < height; y++) {
            for (int xByte = 0; xByte < widthBytes; xByte++) {
                byte b = 0;
                for (int bit = 0; bit < 8; bit++) {
                    int x = xByte * 8 + bit;
                    if (x < width) {
                        int pixel = image.getRGB(x, y);
                        int red = (pixel >> 16) & 0xFF;
                        if (red < 128) {
                            b |= (1 << (7 - bit)); // Set bit 1 (hitam)
                        }
                    }
                }
                baos.write(b);
            }
        }

        // Add newline feed after logo
        baos.write(new byte[]{0x0A});

        return baos.toByteArray();
    }
}
