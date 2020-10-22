package ALUP;

import java.io.IOException;
import java.net.Socket;

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
        //enable the TCP_NODELAY flag for faster transmission
        socket.setTcpNoDelay (true);
    }

    /**
     * function closing the TCP connection to the server
     * Note: This function waits until all data was sent before closing the connection
     */
    @Override
    protected void closeConnection ( )
    {
        try
        {
            //start the hardware disconnection process by shutting down the OutputStream
            socket.shutdownOutput ();

            while(! socket.isOutputShutdown ())
            {
                    //wait until the output is shut down
            }

            //close the connection
            socket.close ();
        }
        catch (IOException e)
        {
            e.printStackTrace ( );
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
       /* System.out.print ( "Read bytes: ");
        for(byte b : buffer)
        {
            System.out.print(((int)b & 0xFF) + ", ");
        }

        System.out.println ( );*/
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
     /*   System.out.print ( "Written bytes: ");
        for(byte b : buffer)
        {
            System.out.print(((int)b & 0xFF) + ", ");
        }

        System.out.println ( );*/
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
