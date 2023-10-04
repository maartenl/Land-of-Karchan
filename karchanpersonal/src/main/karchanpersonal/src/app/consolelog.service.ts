export enum LogLevel {
    NONE = 0,
    SEVERE = 1,
    INFO = 2,
    WARNING = 4,
    /**
     * Useful to display Object contents in console log.
     */
    DEBUG = 8,
}

/**
 * Wrapper around the whole "if (window.console) console.log(stuff)".
 */
export class Logger {
    private static logLevel: LogLevel = LogLevel.NONE;

    private constructor() {
    }

    public static setLogLevel(logLevel: LogLevel) {
        this.log("Setting loglevel to " + logLevel, LogLevel.SEVERE);
        this.logLevel = logLevel;
    }

    public static log(message: string, logLevel: LogLevel = LogLevel.DEBUG) {
        if (this.logLevel == LogLevel.NONE) {
            return;
        }
        if (logLevel.valueOf() <= this.logLevel.valueOf()) {
            return;
        }
        if (!window.console) {
            return;
        }
        console.log(logLevel + " " + message);
    }

    public static logObject(object: Object) {
        if (this.logLevel == LogLevel.NONE) {
            return;
        }
        if (LogLevel.DEBUG.valueOf() <= this.logLevel.valueOf()) {
            return;
        }
        if (!window.console) {
            return;
        }
        console.log(object);
    }

}
