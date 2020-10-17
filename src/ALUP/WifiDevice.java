package ALUP;

import java.io.IOException;
import java.net.Socket;
import java.rmi.server.ExportException;

/**
 * add me
 */
public class WifiDevice extends Device
{
    //the tcp socked used for the hardware connection
    Socket socket;



    private final String address;
    private final int port;


    /**
     * specialized default constructor for this class
      * @param address the IP address of the new device
     * @param port the port of the new device
     */
  public WifiDevice(String address, int port)
  {
      this.address = address;
      this.port = port;
  }

    /**
     * function opening the connection to a TCP socket
     * @throws IOException The connection to the socket could not be
     * established; for more information, see the documentation
     * of Socket(String address, int port)
     */
    @Override
    protected void openConnection ( ) throws IOException
    {
        socket = new Socket ( address, port);
    }

    @Override
    protected void closeConnection ( )
    {
        try
        {
            socket.close ();
        }
        catch (IOException e)
        {
            //the socket can't be closed; It is assumed that it already is closed
        }
    }

    /**
     *  function reading the given number of bytes from the TCP socket into
     *  the given buffer
     * @param buffer the buffer to store the received data in; has to have a
     *               size of bytesToRead
     * @param bytesToRead the number of bytes to read
     * @throws IOException the bytes could not be read because an IO error
     * occurred
     */
    @Override
    protected void readBytes (byte[] buffer, int bytesToRead) throws IOException
    {
        socket.getInputStream ().readNBytes (buffer, 0, bytesToRead);
    }

    /**
     * function sending the given bytes over the TCP Socket
     * @param buffer an array containing the data which should be sent.
     * @throws IOException an IO error occurred while sending the data
     */
    @Override
    protected void writeBytes (byte[] buffer) throws IOException
    {
        socket.getOutputStream ().write (buffer);
    }

    @Override
    protected int bytesAvailable ( )
    {
        try
        {
            return socket.getInputStream ().available ();
        }
        catch (IOException e)
        {
            //the input stream is not available
            return 0;
        }
    }


    private String getAddress ( )
    {
        return address;
    }

    private int getPort ( )
    {
        return port;
    }

}
