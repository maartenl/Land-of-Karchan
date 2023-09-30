package mmud.exceptions;

public abstract class ChatlineException extends MudException
{
    protected ChatlineException(String string)
    {
        super(string);
    }

    protected ChatlineException(Throwable ex)
    {
        super(ex);
    }

    protected ChatlineException(String aString, Exception ex)
    {
        super(aString, ex);
    }
}
