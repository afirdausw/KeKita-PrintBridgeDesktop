package devyana.kekita.printbridge.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import devyana.kekita.printbridge.model.ApiConfig;
import devyana.kekita.printbridge.model.ApplicationConfig;
import devyana.kekita.printbridge.model.ApplicationInfo;
import devyana.kekita.printbridge.model.PrinterConfig;
import devyana.kekita.printbridge.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ConfigLoader {

    private static final String CONFIG_FILE_NAME = "application.json";

    public static ApplicationConfig load() {
        ObjectMapper mapper = new ObjectMapper();

        // 1. Coba load dari file eksternal di working directory
        File externalFile = new File(CONFIG_FILE_NAME);
        if (externalFile.exists()) {
            try {
                Log.info("Membaca konfigurasi dari file lokal: " + externalFile.getAbsolutePath());
                return mapper.readValue(externalFile, ApplicationConfig.class);
            } catch (IOException e) {
                Log.error("Gagal membaca file konfigurasi lokal, menggunakan default classpath", e);
            }
        }

        // 2. Fallback: load dari classpath resources
        try (InputStream input = ConfigLoader.class
                .getClassLoader()
                .getResourceAsStream(CONFIG_FILE_NAME)) {

            if (input != null) {
                Log.info("Membaca konfigurasi dari classpath resources");
                return mapper.readValue(input, ApplicationConfig.class);
            }

        } catch (IOException e) {
            Log.error("Gagal membaca konfigurasi dari classpath", e);
        }

        // 3. Fallback default jika resource tidak ditemukan
        Log.warn("Konfigurasi tidak ditemukan, membuat konfigurasi default");
        return createDefaultConfig();
    }

    public static boolean save(ApplicationConfig config) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        File externalFile = new File(CONFIG_FILE_NAME);
        try {
            mapper.writeValue(externalFile, config);
            Log.success("Konfigurasi berhasil disimpan ke " + externalFile.getAbsolutePath());
            return true;
        } catch (IOException e) {
            Log.error("Gagal menyimpan konfigurasi", e);
            return false;
        }
    }

    private static ApplicationConfig createDefaultConfig() {
        ApplicationConfig config = new ApplicationConfig();

        ApplicationInfo info = new ApplicationInfo();
        info.setName("PrintBridge");
        info.setVersion("1.0.0");
        config.setApplication(info);

        ApiConfig api = new ApiConfig();
        api.setPort(8080);
        config.setApi(api);

        PrinterConfig printer = new PrinterConfig();
        printer.setPaperSize(58);
        printer.setAutoReconnect(true);
        printer.setDefaultPrinter("");
        config.setPrinter(printer);

        return config;
    }

}
