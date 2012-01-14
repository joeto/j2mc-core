package to.joe.j2mc.core.log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public class Log {
    private class SideLogFormatter extends Formatter {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");

        @Override
        public String format(LogRecord lr) {
            return this.dateFormat.format(new Date(lr.getMillis())) + " " + lr.getMessage() + "\n";
        }
    }

    private final Logger mainLog;
    private final Logger sideLog;
    private FileHandler sideLogFileHandler;

    boolean sideLogOK;

    public Log(Logger mainLog) {
        this.mainLog = mainLog;
        this.sideLog = Logger.getLogger("J2SideLog");
        try {
            this.sideLogFileHandler = new FileHandler("secondary.log", true);
            this.sideLogFileHandler.setFormatter(new SideLogFormatter());
            this.sideLog.addHandler(this.sideLogFileHandler);
            this.sideLog.setUseParentHandlers(false);
        } catch (final Exception exception) {
            this.severe("Cannot initialize the side log", exception);
        }
    }

    public void info(String message) {
        this.mainLog.info(message);
    }

    public void severe(String message) {
        this.mainLog.severe(message);
    }

    public void severe(String message, Exception exception) {
        this.mainLog.log(Level.SEVERE, message, exception);
    }

    public void sideLog(String message) {
        if (this.sideLogOK) {
            this.sideLog.info(message);
        }
    }

    public void warning(String message) {
        this.mainLog.warning(message);
    }

}
