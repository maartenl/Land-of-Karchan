package mmud.rest.services.admin.systemlog;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Optional;

/**
 * The Format
 * <p>
 * The log item entries are structured in a certain format. This allows the Log Viewer (or any other tooling like a log aggregation tool) to parse it and understand the log data. There are 3 formats supported; ODL, ULF, and JSON
 * </p><p>
 * ODL : Oracle Diagnostic Log Format (Log Working Group in Oracle)
 * </p>
 * <p>
 * Example
 * </p><p>
 * <tt>[2019-01-18T13:10:02.420+0100] [Payara 5.184] [INFO] [NCLS-LOGGING-00009] [javax.enterprise.logging] [tid: _ThreadID=31 _ThreadName=RunLevelControllerThread-1547813402253] [timeMillis: 1547813402420] [levelValue: 800] [[ Running Payara Version: Payara Server 5.184.1 #badassfish (build 90)]]</tt>
 * </p>
 * <p>
 * The format uses brackets as delimiter and has a few required fields. The overall format is:
 * </p>
 * <p><tt>[[timestamp] [organization ID] [Message Type/Level] [Message ID] [Logger Name] [Thread ID] [User ID] [ECID] [Extra Attributes] [Message]]</tt></p>
 */
public class LogAggregator
{
  public Optional<Log> parse(String logEntry) throws ParseLogException
  {
    if (logEntry == null || logEntry.trim().equals(""))
    {
      return Optional.empty();
    }
    int beginMessage = logEntry.indexOf("[[");
    if (beginMessage == -1)
    {
      throw new ParseLogException("No [[ found in log " + logEntry + ".");
    }
    int endMessage = logEntry.lastIndexOf("]]");
    if (endMessage == -1)
    {
      throw new ParseLogException("No ]] found in log " + logEntry + ".");
    }
    String temp = logEntry;
    // ofPattern("uuuu-MM-ddTHH:mm:ss.SSS+timezone")
    Log log = new Log();
    try
    {
      log.timestamp = take(temp)
        .map(timestampStr -> Instant.parse(timestampStr
          .replace("+0100", "Z")
          .replace("+0200", "Z")))
        .orElseThrow(() -> new ParseLogException("Unable to parse timestamp"));
    } catch (DateTimeParseException exception)
    {
      throw new ParseLogException(exception);
    }
    temp = temp.substring(temp.indexOf(']') + 1);
    log.organizationID = take(temp).orElse(null);
    temp = temp.substring(temp.indexOf(']') + 1);
    log.messageTypeLevel = take(temp).orElse(null);
    temp = temp.substring(temp.indexOf(']') + 1);
    log.messageID = take(temp).orElse(null);
    temp = temp.substring(temp.indexOf(']') + 1);
    log.loggerName = take(temp).orElse(null);
    log.message = logEntry.substring(beginMessage + 2, endMessage);
    return Optional.of(log);
  }

  private Optional<String> take(String input)
  {
    int begin = input.indexOf('[');
    if (begin == -1)
    {
      return Optional.empty();
    }
    int end = input.indexOf(']');
    if (end == -1)
    {
      return Optional.empty();
    }
    return Optional.of(input.substring(begin + 1, end));
  }

}
