package mmud.rest.services.admin.systemlog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LogReader
{

  /**
   * A regular expression matching "[YYYY-MM-DDTHH:MM:SS.sss+1234]" matching for
   * example [2019-08-16T23:23:29.399+0200] [Payara 5.184].
   */
  public static final String LOGFILEENTRY_TIMESTAMP_REG_EXP = "^\\[\\d\\d\\d\\d-\\d\\d-\\d\\dT\\d\\d:\\d\\d:\\d\\d\\.\\d\\d\\d\\+\\d\\d\\d\\d\\].*";

  boolean checkLog(String logEntry)
  {
    return logEntry.matches(LOGFILEENTRY_TIMESTAMP_REG_EXP);
  }

  public List<Log> readFile(InputStream fileName) throws IOException, ParseLogException
  {
    List<Log> logs = new ArrayList<>();
    // This will reference one line at a time
    String line;
    LogAggregator aggregator = new LogAggregator();
    try (// FileReader reads text files in the default encoding.
            InputStreamReader fileReader
            = new InputStreamReader(fileName);
            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader
            = new BufferedReader(fileReader);)
    {
      StringBuilder buffer = new StringBuilder();
      while ((line = bufferedReader.readLine()) != null)
      {
        if (checkLog(line))
        {
          String string = buffer.toString();
          aggregator.parse(string).map(this::cleanUp).ifPresent(logs::add);
          buffer = new StringBuilder();
        }
        buffer.append(line).append("\n");
      }
    }
    logs.sort(new LogComparator());
    return logs;
  }

  private Log cleanUp(Log log)
  {
    if (log.message != null && log.message.startsWith("\n"))
    {
      log.message = log.message.substring(1).trim();
    }

    return log;
  }
}
