package mmud.exceptions;

public class ChatlineNotFoundException extends ChatlineException
{
    public ChatlineNotFoundException(String chatlinename)
    {
        super("Chat " + chatlinename + " not found.");
    }
}
