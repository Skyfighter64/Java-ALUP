package ALUP;

public class FrameErrorException extends RuntimeException
{
    public FrameErrorException(String errorMessage)
    {
        super("FrameErrorException: " + errorMessage);
    }
}
