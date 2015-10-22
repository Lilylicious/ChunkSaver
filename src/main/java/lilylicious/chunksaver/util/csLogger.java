package lilylicious.chunksaver.util;

import lilylicious.chunksaver.ChunkSaverCore;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class csLogger {
    private static Logger logger = LogManager.getLogger(ChunkSaverCore.MODID);

    public static void log(Level level, String msg) {
        logger.log(level, msg);
    }

    public static void logInfo(String msg) {
        logger.info(msg);
    }

    public static void logWarn(String msg) {
        logger.warn(msg);
    }

    public static void logFatal(String msg) {
        logger.fatal(msg);
    }

    public static void log(Level level, String msg, Object... args) {
        logger.log(level, String.format(msg, args));
    }

    public static void logInfo(String msg, Object... args) {
        logger.info(String.format(msg, args));
    }

    public static void logWarn(String msg, Object... args) {
        logger.warn(String.format(msg, args));
    }

    public static void logFatal(String msg, Object... args) {
        logger.fatal(String.format(msg, args));
    }
    
}
