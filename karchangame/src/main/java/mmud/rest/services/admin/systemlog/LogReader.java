package mmud.rest.services.admin.systemlog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LogReader
{

  public List<String> readFile(InputStream fileName) throws IOException
  {
    List<String> logs = new ArrayList<>();
    // This will reference one line at a time
    String line;
    try (// FileReader reads text files in the default encoding.
            InputStreamReader fileReader
            = new InputStreamReader(fileName);
            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader
            = new BufferedReader(fileReader);)
    {
      while ((line = bufferedReader.readLine()) != null)
      {
        logs.add(line);
      }
    }
    Collections.reverse(logs);
    return logs;
  }

}
