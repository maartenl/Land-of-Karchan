package mmud.rest.services.admin.systemlog;

public class ParseLogException extends Throwable
{
  public ParseLogException(String s)
  {
    super(s);
  }

  public ParseLogException(RuntimeException exception)
  {
    super(exception);
  }
}
