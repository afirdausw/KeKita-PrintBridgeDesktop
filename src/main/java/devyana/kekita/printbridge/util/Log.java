package devyana.kekita.printbridge.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Log {

    public enum Level {
        INFO, SUCCESS, WARN, ERROR
    }

    public interface LogListener {
        void onLog(String timestamp, Level level, String message);
    }

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final List<LogListener> listeners = new CopyOnWriteArrayList<>();

    public static void addListener(LogListener listener) {
        listeners.add(listener);
    }

    public static void removeListener(LogListener listener) {
        listeners.remove(listener);
    }

    public static void info(String message) {
        log(Level.INFO, message);
    }

    public static void success(String message) {
        log(Level.SUCCESS, message);
    }

    public static void warn(String message) {
        log(Level.WARN, message);
    }

    public static void error(String message) {
        log(Level.ERROR, message);
    }

    public static void error(String message, Throwable throwable) {
        String msg = message + (throwable != null ? " (" + throwable.getMessage() + ")" : "");
        log(Level.ERROR, msg);
    }

    private static void log(Level level, String message) {
        String timestamp = LocalDateTime.now().format(TIME_FORMATTER);
        String formatted = String.format("[%s] [%s] %s", timestamp, level, message);

        if (level == Level.ERROR) {
            System.err.println(formatted);
        } else {
            System.out.println(formatted);
        }

        for (LogListener listener : listeners) {
            try {
                listener.onLog(timestamp, level, message);
            } catch (Exception ignored) {
            }
        }
    }
}
