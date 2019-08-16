package mmud.rest.services.admin.systemlog;

public class LogComparator implements java.util.Comparator<Log>
{
  @Override
  public int compare(Log log1, Log log2)
  {
    return -log1.timestamp.compareTo(log2.timestamp);
  }
}
