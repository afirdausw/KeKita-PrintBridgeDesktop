package devyana.kekita.printbridge.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import devyana.kekita.printbridge.model.ApplicationConfig;

import java.io.IOException;
import java.io.InputStream;

public class ConfigLoader {

    public static ApplicationConfig load() {

        ObjectMapper mapper = new ObjectMapper();

        try (InputStream input = ConfigLoader.class
                .getClassLoader()
                .getResourceAsStream("application.json")) {

            if (input == null) {
                throw new RuntimeException("application.json tidak ditemukan");
            }

            return mapper.readValue(input, ApplicationConfig.class);

        } catch (IOException e) {
            throw new RuntimeException("Gagal membaca konfigurasi", e);
        }

    }

}
