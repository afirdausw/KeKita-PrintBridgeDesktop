package devyana.kekita.printbridge.config;

import devyana.kekita.printbridge.model.ApplicationConfig;

public class AppConfig {

    private static ApplicationConfig config;

    private AppConfig() {
    }

    public static void load() {
        config = ConfigLoader.load();
    }

    public static ApplicationConfig get() {
        return config;
    }

}
