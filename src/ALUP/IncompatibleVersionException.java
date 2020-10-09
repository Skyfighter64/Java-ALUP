package ALUP;

/**
 * exception indicating that the protocol versions of the master device and the client device are not compatible
 */
public class IncompatibleVersionException extends RuntimeException
{
    public IncompatibleVersionException(String errorMessage)
    {
        super("IncompatibleVersionException: " + errorMessage);
    }
}
