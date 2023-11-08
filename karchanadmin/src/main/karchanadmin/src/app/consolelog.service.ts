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
    if (!window.console) {
      return false;
    }
    return true;
  }

  static logError(errormessage: string, err: Object) {
    this.log(errormessage, LogLevel.SEVERE);
    this.logObject(err, LogLevel.SEVERE);
  }
}
