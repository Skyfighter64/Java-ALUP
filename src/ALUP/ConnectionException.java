package ALUP;

public class ConnectionException extends RuntimeException
{
    public ConnectionException(String errorMessage)
    {
        super("ConnectionException: " + errorMessage);
    }
}
