package mmud.exceptions;

public class ChatlineAlreadyExistsException extends ChatlineException
{
    public ChatlineAlreadyExistsException(String chatlinename)
    {
        super("Chat " + chatlinename + " already exists.");
    }
}
