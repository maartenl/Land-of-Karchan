export enum LogLevel {
  NONE = 'NONE',
  SEVERE = 'SEVERE',
  INFO = 'INFO',
  WARNING = 'WARNING',
  /**
   * Useful to display Object contents in console log.
   */
  DEBUG = 'DEBUG',
}

/**
 * Wrapper around the whole "if (window.console) console.log(stuff)".
 */
export class Logger {
  private static logLevel: LogLevel = LogLevel.NONE;

  public static setLogLevel(logLevel: LogLevel) {
    if (logLevel === null) {
      if (window.console) {
        console.log("Loglevel provided is empty!");
      }
      return;
    }
    if (logLevel === undefined) {
      if (window.console) {
        console.log("Loglevel provided is undefined!");
      }
      return;
    }
    if (window.console) {
      console.log("Setting loglevel to " + logLevel);
    }
    this.logLevel = logLevel;
  }

  public static getLogLevel(): LogLevel {
    if (window.console) {
      console.log("Getting loglevel " + this.logLevel);
    }
    return this.logLevel;
  }

  public static log(message: string, logLevel: LogLevel = LogLevel.DEBUG) {
    if (!this.isLogEnabled(logLevel)) {
      return;
    }
    console.log(Logger.getLogLevelDescription(logLevel) + ": " + message);
  }

  private static getLogLevelDescription(logLevel: LogLevel = LogLevel.DEBUG): string {
    var description: string = "DEBUG";
    switch (logLevel) {
      case LogLevel.SEVERE: {
        description = "SEVERE";
        break;
      }
      case LogLevel.NONE: {
        description = "NONE";
        break;
      }
      case LogLevel.DEBUG: {
        description = "DEBUG";
        break;
      }
      case LogLevel.INFO: {
        description = "INFO";
        break;
      }
      case LogLevel.WARNING: {
        description = "WARNING";
        break;
      }
    }
    return description;
  }

  public static logObject(object: Object, logLevel: LogLevel = LogLevel.DEBUG) {
    if (!this.isLogEnabled(logLevel)) {
      return;
    }
    console.log(object);
  }

  private static isLogEnabled(logLevel: LogLevel = LogLevel.DEBUG) {
    if (this.logLevel === LogLevel.NONE) {
      return false;
    }
    if (!window.console) {
      return false;
    }
    if (this.logLevel === LogLevel.SEVERE) {
      return logLevel === LogLevel.SEVERE;
    }
    if (this.logLevel === LogLevel.INFO) {
      return logLevel === LogLevel.SEVERE || logLevel === LogLevel.INFO;
    }
    if (this.logLevel === LogLevel.WARNING) {
      return logLevel === LogLevel.SEVERE || logLevel === LogLevel.INFO || logLevel === LogLevel.WARNING;
    }
    return true;
  }

  static logError(errormessage: string, err: Object) {
    this.log(errormessage, LogLevel.SEVERE);
    this.logObject(err, LogLevel.SEVERE);
  }

  static logEntering(message: string) {
    this.log(message, LogLevel.DEBUG);
  }
}
