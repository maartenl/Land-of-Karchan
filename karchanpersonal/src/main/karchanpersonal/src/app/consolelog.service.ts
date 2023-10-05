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
        if (logLevel === null) {
            this.logError("Loglevel provided is empty!", logLevel);
            return;
        }
        if (logLevel === undefined) {
            this.logError("Loglevel provided is undefined!", logLevel);
            return;
        }
        this.logLevel = logLevel;
    }

    public static log(message: string, logLevel: LogLevel = LogLevel.DEBUG) {
        if (!this.isLogEnabled(logLevel)) {
            return;
        }
        console.log(logLevel + " " + message);
    }

    public static logObject(object: Object, logLevel: LogLevel = LogLevel.DEBUG) {
        if (!this.isLogEnabled(logLevel)) {
            return;
        }
        console.log(object);
    }

    private static isLogEnabled(logLevel: LogLevel = LogLevel.DEBUG) {
        var isEnabled: boolean = true;
        if (this.logLevel == LogLevel.NONE) {
            isEnabled = false;
        }
        if (logLevel.valueOf() <= this.logLevel.valueOf()) {
            isEnabled = false;
        }
        if (!window.console) {
            isEnabled = false;
        }
        return isEnabled;
    }

    static logError(errormessage: string, err: Object) {
        this.log(errormessage, LogLevel.SEVERE);
        this.logObject(err, LogLevel.SEVERE);
    }
}
