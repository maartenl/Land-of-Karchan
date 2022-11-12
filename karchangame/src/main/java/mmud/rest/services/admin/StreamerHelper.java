package mmud.rest.services.admin;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import java.util.stream.Stream;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.ws.rs.core.StreamingOutput;
import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.config.ResultSetConcurrency;
import org.eclipse.persistence.config.ResultSetType;
import org.eclipse.persistence.queries.ScrollableCursor;

public class StreamerHelper
{
  private static final Logger LOGGER = Logger.getLogger(StreamerHelper.class.getName());

  private StreamerHelper()
  {
    // hide implicit public constructor
  }

  /**
   * Converts a stream of strings into a streaming json array.
   *
   * @param incoming the stream of strings (usually received from a database).
   * @return a stream for streaming to the client/JAX-RS
   */
  public static StreamingOutput getStream(Stream<String> incoming)
  {
    return output ->
    {
      Writer writer = new BufferedWriter(new OutputStreamWriter(output));
      writer.write("[");
      AtomicBoolean first = new AtomicBoolean(true);
      incoming.forEach(item ->
      {
        try
        {
          if (first.get())
          {
            first.set(false);
          } else
          {
            writer.write(",");
          }
          writer.write(item);
        } catch (IOException e)
        {
          LOGGER.throwing("StreamerHelper", item, e);
        }
      });
      writer.write("]");
      writer.flush();
    };
  }

  public static StreamingOutput getStream(ScrollableCursor cursor)
  {
    return output ->
    {
      Writer writer = new BufferedWriter(new OutputStreamWriter(output));
      writer.write("[");
      while (cursor.hasNext())
      {
        String item = (String) cursor.next();
        writer.write(item);
        if (cursor.hasNext())
        {
          writer.write(",");
        }
      }
      cursor.close();
      writer.write("]");
      writer.flush();
    };
  }

  public static ScrollableCursor getScrollableCursor(EntityManager entityManager, String getQuery)
  {
    Query query = entityManager.createNativeQuery(getQuery);
    query.setHint(QueryHints.CURSOR, HintValues.TRUE);
    query.setHint(QueryHints.CURSOR_INITIAL_SIZE, 100);
    query.setHint(QueryHints.CURSOR_PAGE_SIZE, 100);
    query.setHint(QueryHints.RESULT_SET_CONCURRENCY, ResultSetConcurrency.ReadOnly);
    query.setHint(QueryHints.RESULT_SET_TYPE, ResultSetType.ForwardOnly);
    return (ScrollableCursor) query.getSingleResult();
  }

  public static StreamingOutput getStream(EntityManager entityManager, String getQuery)
  {
    return getStream(getScrollableCursor(entityManager, getQuery));
  }
}
