package devyana.kekita.printbridge.util;

import com.formdev.flatlaf.FlatLaf;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;

public class FontUtil {

    public static final String FONT_FAMILY = "Reddit Sans";

    public static void setupCustomFont() {
        boolean loaded = loadRedditSansFromResources();

        String targetFamily = loaded ? FONT_FAMILY : getInstalledFontFamily();

        if (targetFamily != null) {
            // 1. Set FlatLaf preferred font family
            FlatLaf.setPreferredFontFamily(targetFamily);

            // 2. Set UIManager default font for all Swing components
            Font defaultFont = new Font(targetFamily, Font.PLAIN, 13);
            UIManager.put("defaultFont", defaultFont);

            Log.info("Font '" + targetFamily + "' berhasil diaktifkan secara global untuk UI.");
        } else {
            Log.warn("Font Reddit Sans tidak dapat dimuat, menggunakan font bawaan sistem.");
        }
    }

    private static boolean loadRedditSansFromResources() {
        String[] fontPaths = {
                "/fonts/RedditSans-Regular.ttf",
                "/fonts/RedditSans-Bold.ttf",
                "/fonts/RedditSans-Medium.ttf"
        };

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        boolean anyLoaded = false;

        for (String path : fontPaths) {
            try (InputStream is = FontUtil.class.getResourceAsStream(path)) {
                if (is != null) {
                    Font font = Font.createFont(Font.TRUETYPE_FONT, is);
                    boolean registered = ge.registerFont(font);
                    Log.info("Loaded font resource: " + font.getFontName() + " (Family: " + font.getFamily() + ", Registered: " + registered + ")");
                    anyLoaded = true;
                }
            } catch (Exception e) {
                Log.warn("Gagal membaca font dari resource " + path + ": " + e.getMessage());
            }
        }

        return anyLoaded;
    }

    private static String getInstalledFontFamily() {
        String[] families = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        for (String name : families) {
            if (name.equalsIgnoreCase(FONT_FAMILY) || name.toLowerCase().contains("reddit")) {
                return name;
            }
        }
        return null;
    }
}
