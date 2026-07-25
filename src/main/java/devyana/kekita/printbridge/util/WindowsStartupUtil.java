package devyana.kekita.printbridge.util;

import java.io.File;

public class WindowsStartupUtil {

    private static final String REG_KEY = "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Run";
    private static final String APP_NAME = "PrintBridge";

    public static boolean isAutoStartEnabled() {
        try {
            Process process = new ProcessBuilder("reg", "query", REG_KEY, "/v", APP_NAME).start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean setAutoStartEnabled(boolean enable) {
        try {
            if (enable) {
                String appPath = getCurrentAppPath();
                if (appPath == null || appPath.isEmpty()) {
                    return false;
                }
                String cmdValue;
                if (appPath.toLowerCase().endsWith(".exe")) {
                    cmdValue = "\"" + appPath + "\"";
                } else if (appPath.toLowerCase().endsWith(".jar")) {
                    File jarFile = new File(appPath);
                    File standaloneExe = new File(jarFile.getParentFile().getParentFile(), "PrintBridge/PrintBridge.exe");
                    if (standaloneExe.exists()) {
                        cmdValue = "\"" + standaloneExe.getAbsolutePath() + "\"";
                    } else {
                        cmdValue = "javaw -jar \"" + appPath + "\"";
                    }
                } else {
                    cmdValue = "\"" + appPath + "\"";
                }

                Process process = new ProcessBuilder(
                        "reg", "add", REG_KEY, "/v", APP_NAME, "/t", "REG_SZ", "/d", cmdValue, "/f"
                ).start();
                boolean success = process.waitFor() == 0;
                if (success) {
                    Log.success("Auto Start Windows Registry berhasil diaktifkan: " + cmdValue);
                }
                return success;
            } else {
                Process process = new ProcessBuilder(
                        "reg", "delete", REG_KEY, "/v", APP_NAME, "/f"
                ).start();
                boolean success = process.waitFor() == 0;
                if (success) {
                    Log.info("Auto Start Windows Registry berhasil dinonaktifkan.");
                }
                return success;
            }
        } catch (Exception e) {
            Log.error("Gagal mengubah konfigurasi Windows Startup Registry", e);
            return false;
        }
    }

    private static String getCurrentAppPath() {
        try {
            File codeSourceFile = new File(WindowsStartupUtil.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            return codeSourceFile.getAbsolutePath();
        } catch (Exception e) {
            return "";
        }
    }
}
