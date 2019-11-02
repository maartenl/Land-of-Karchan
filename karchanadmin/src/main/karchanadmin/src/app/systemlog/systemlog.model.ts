export class Systemlog {
    Timestamp: string;
    Level: string;
    Version: number;
    LoggerName: string;
    ThreadID: string;
    ThreadName: string;
    TimeMillis: string;
    LevelValue: string;
    ClassName: string;
    MethodName: string;
    Throwable: ThrowableLog;
    MessageID: string;
    LogMessage: string;
}

export class ThrowableLog {
    Exception: string;
    StackTrace: string;
}