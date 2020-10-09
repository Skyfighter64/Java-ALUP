package ALUP;

import java.nio.ByteBuffer;

/**
 * class hosting static methods used for conversion
 */
public  class Convert
{
    /**
     * function converting an array of bytes to an integer number
     * @param bytes an array containing 4 bytes which get converted to a number
     * @return the number to which the given bytes were converted
     */
    public static int BytesToInt(byte[] bytes)
    {
        ByteBuffer buffer = ByteBuffer.wrap (bytes);

        return  buffer.getInt ();
    }

    /**
     * function converting the given integer value to an array of bytes
     * @param i the integer value to convert
     * @return the converted array of bytes; has a length of 4
     */
    public static byte[] IntToBytes(int i)
    {
        //convert the given integer to an array of bytes
        return ByteBuffer.allocate (4).putInt (i).array ();
    }

}
