package ALUP;

public class OutOfRangeException extends RuntimeException
{
    public OutOfRangeException(String errorMessage)
    {
        super("OutOfRangeException: " + errorMessage);
    }
}
