export class Systemlog {
    Timestamp: string | null = null;
    Level: string | null = null;
    Version: number | null = null;
    LoggerName: string | null = null;
    ThreadID: string | null = null;
    ThreadName: string | null = null;
    TimeMillis: string | null = null;
    LevelValue: string | null = null;
    ClassName: string | null = null;
    MethodName: string | null = null;
    Throwable: ThrowableLog | null = null;
    MessageID: string | null = null;
    LogMessage: string | null = null;
}

export class ThrowableLog {
    Exception: string | null = null;
    StackTrace: string | null = null;
}