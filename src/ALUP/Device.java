package ALUP;

import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;


/**
 *  Class representing a base for all devices.
 *
 *  When extending/implementing this class:
 *  Implement a "connect(...)" function with any arguments needed.
 *  This function  should establish the connection and then call
 *  super.connect() to establish the protocol connection.
 *
 *  For an example, see SerialDevice.java
 */
public abstract class Device
{
    //the configuration object for this device; null until a connection was
    // established successfully
    DeviceConfiguration configuration;

    //the ping of the device for sending of the last frame in nanoseconds or
    // 0 if no frame was sent yet
    long ping;

    //the current frame to be sent next
    Frame frame;

    //the timeout for receiving a value in ms
    private static final int RECEIVER_TIMEOUT = 10000;

    /**
     * the current connection state of this device
     * Note: do not change this value when implementing the abstract
     * functions of this class; This is already taken care of
     */
     CONNECTION_STATE connectionState;


    /**
     * enumeration object representing all connection states of a device
     */
    public enum CONNECTION_STATE
    {
        DISCONNECTED,
        CONNECTED
    }

    /**
     * function which should receive the given number of bytes and store
     * them into the given buffer over the device connection
     * <br/>
     * This function may block until the specified number of bytes is read
     * @param buffer the buffer to store the received data in; has to have a
     *               size of bytesToRead
     * @param bytesToRead the number of bytes to read
     */
    protected abstract void readBytes(byte[] buffer, int bytesToRead ) throws IOException;

    /**
     * function which should send the given bytes over the device connection
     * @param buffer an array containing the data which should be sent.
     */
    protected abstract void writeBytes(byte[] buffer ) throws IOException;

    /**
     * function which should return the number of bytes which are currently
     * available to be read
     * @return the number of bytes which is currently available to be read
     */
    protected abstract int bytesAvailable();

    /**
     * function which should open the connection to the device
     * @throws IOException an exception thrown by the implementing class
     * when calling openConnection(); for more info, see the implementing class
     */
    protected abstract void openConnection() throws IOException;

    /**
     * function which should close the connection to the device
     */
    protected abstract void closeConnection();


    //default constructor of this class
    public Device()
    {
        connectionState = CONNECTION_STATE.DISCONNECTED;
    }


    /**
     *  function establishing a protocol connection according to the ALUP v.0.1
     *  <br/>
     *  This function has to be called inside the connect(...)  function of the
     *  implementing class, after establishing all other connections.
     @throws TimeoutException the connection could not be established
     because the serial device did not send a connection request within 10 seconds
      * @throws  IncompatibleVersionException The protocol Version of the
     * SerialDevice and the version at Constants.VERSION
     * do not match and are therefore incompatible
     * @throws IllegalArgumentException The configuration received from
     * the device or parts of it were invalid. Therefore the
     *                           connection attempt was stopped.
     * @throws IOException the hardware connection could not be established
     * because of various reasons. For more, see openConnection() of the
     * implementing class
     *
     */
    public void connect() throws TimeoutException, IncompatibleVersionException, IllegalArgumentException, IOException
    {
        //open the hardware connection to the device
        openConnection ();

        if(!waitForConnectionRequest())
        {
            //no connection request received (timed out);
            throw  new TimeoutException ( "No Connection request received within " + RECEIVER_TIMEOUT + " ms");
        }
        sendConnectionAcknowledgement ();
        receiveConfiguration();

        //set the connection State to connected
        connectionState = CONNECTION_STATE.CONNECTED;
    }

    /**
     * function connecting this device so it can be used for led data
     * transmission according to the ALUP protocol v. 0.1
     * In Comparison to Connect(), this function handles timeOutExceptions
     * to be easier to use. If you want to handle timeOuts yourself, use Connect()
     */

    public void simpleConnect()
    {
        try
        {
            connect ();
        }
        catch (TimeoutException | IOException e)
        {
            connectionState = CONNECTION_STATE.DISCONNECTED;
            e.printStackTrace ( );
        }
    }



    /**
     * function disconnecting this device so it can't be used for led data
     * transmission any longer
     */
    public void disconnect()
    {
        //send a disconnect command to the slave device
        try
        {
            sendDisconnectFrame ();
        }
        catch (IOException e)
        {
            //disconnect frame could not be sent; assume that the device already disconnected
        }
        //disconnect on this side of the device
        invalidateConnection ();
        //set the connection state to disconnected
        connectionState = CONNECTION_STATE.DISCONNECTED;
    }


    /**
     * function invalidating the connection to the device when it times out
     * Note: if you want to disconnect from the device, use Disconnect()
     * instead as it also tells the device to disconnect
     */
    private void invalidateConnection()
    {
        //invalidate the configuration and the serial connection
        closeConnection ();
        configuration = null;

        //set the connection state to "DISCONNECTED"
        connectionState = CONNECTION_STATE.DISCONNECTED;
    }


    /**
     * function comparing the given protocolVersion to the one specified in
     * Constants.VERSION
     * @param protocolVersion the protocol version to compare
     * @throws IncompatibleVersionException The given protocol Version
     * and the version at Constants.VERSION do not match and are therefore
     * incompatible
     */
    private void checkProtocolVersion(String protocolVersion) throws IncompatibleVersionException
    {
        if(!protocolVersion.equals (Constants.VERSION))
        {
            //tell the device that the configuration could not be applied and throw an error
            try
            {
                sendConfigurationError ();
            }
            catch (IOException e)
            {
                //the configuration error could not be sent; assume that the device disconnected itself
            }
            throw new IncompatibleVersionException("The protocol version of the Device ('" + protocolVersion + "') does not match this version ('" + Constants.VERSION + "')");
        }
    }


    //region LED clamping

    /**
     * function clamping the given LED array inside the range of the LED strip
     * connected to the device returning it
     * @param leds the LEDs to be trimmed and set
     * @param offset the offset for which the LEDs should be trimmed
     * @return the new offset for the trimmed LEDs
     */
    private LED[] clampLeds(LED[] leds, int offset)
    {
        //check if the given led array is null
        if(leds == null)
        {
            //return an empty array
            return new LED[0];
        }

        //Trim the led array according to the offset
        return trimEnd(trimStart(getLeds (), offset), offset);
    }


    /**
     * function clamping the offset if it exceeds the maximum number of
     * LEDs or is smaller than 0.
     * @param offset the offset to be trimmed
     * @param maxLeds the maximum number of LEDs
     * @return the clamped offset
     */
    static int clampOffset(int offset, int maxLeds)
    {
        //clamp the given offset between 0 and the number of LEDs connected
        // to the device
        return Math.max (0, Math.min (maxLeds, offset));
    }



    /**
     *  function trimming the start of given LED array for the given offset if
     *  it exceeds the numOfLeds of this device
     *  Note: when using this function, you need to trim the offset separately
     *  using TrimOffset()
     * @param leds the LED array which should be trimmed; has to be non-null
     * @param offset the offset for which the LED array should be trimmed;
     *               can be negative
     * @return the trimmed LED array
     */
    private LED[] trimStart(LED[] leds, int offset)
    {
        //check if it needs trimming
        if(offset >= 0)
        {
            //no trimming needed
            return leds;
        }

        //trim the start of the  LEDs

        //create a new LED array with the trimmed size
        int newLedSize = fitArrayLength(getConfiguration ().getNumOfLeds (), leds.length, offset);
        LED[] newLEDs  = new LED[newLedSize];

        //loop through the trimmed leds and set their values
        for(int i = 0; i < newLEDs.length; i++)
        {
            newLEDs[i] = leds[i];
        }

        return  newLEDs;
    }


    /**
     *  function trimming the end of given LED array for the given offset if it
     *  exceeds the numOfLeds of this device
     *  <br/>
     *  Note: when using this function, you need to trim the offset separately
     *  using TrimOffset()
     * @param leds the LED array which should be trimmed; has to be non-null
     * @param offset the offset for which the LED array should be trimmed;
     *              can be negative
     * @return the trimmed LED array
     */
    private LED[] trimEnd(LED[] leds, int offset)
    {
        //check if it needs trimming
        if(offset + leds.length <= getConfiguration ().getNumOfLeds ())
        {
            //no trimming needed
            return  leds;
        }

        //copy the valid elements into a new array
        //create a new LED array with the trimmed size
        int newLedSize = fitArrayLength(getConfiguration ().getNumOfLeds (), leds.length, offset);
        LED[] newLEDs  = new LED[newLedSize];

        //copy parts of the array into the new one
        if (newLEDs.length >= 0) System.arraycopy (leds, 0, newLEDs, 0, newLEDs.length);

        return  newLEDs;
    }


    /**
     * function fitting the given length combined with the given offset into
     * the range of 0 to maxLength
     * @param maxLength the maxLength which should not be exceeded
     * @param length the current length of the array
     * @param offset the offset of the array
     * @return the new length of the array; guaranteed to be  <= length and
     * >= 0, depending on if the given length exceeds one end of the range
     * from 0 to maxLength
     */
    static int fitArrayLength(int maxLength, int length, int offset)
    {
        //check if the given length combined with the offset is exceeding the
        // max length
        if( (length  + offset) > maxLength)
        {
            //cut the length to fit within the range
            return  Math.max(maxLength - offset , 0);
        }
        //check if the offset is smaller than 0
        else if ( offset < 0)
        {
            //cut the length to fit within the range
            //note that the offset is negative
            return Math.max(length + offset, 0);
        }
        else
        {
            //the given length is already within range
            return  length;
        }
    }

    //endregion

    //region sender functions

    /**
     * unction sending the currently saved frame over the Serial connection
     * of this Device and waiting for "Frame Acknowledgement" or a "Frame Error"
     * response
     * In comparison to Send(), this function catches TimeoutExceptions by
     * printing the StackTrace to the terminal.
     * When the Frame is null, sending will get skipped without an error
     * message or warning
     */
    public void simpleSend()
    {
        try
        {
            send();
        }
        catch (TimeoutException | IOException e)
        {
            //just print the StackTrace and return
            e.printStackTrace ( );
        }
    }

    /**
     * function sending the given LED array over the Serial connection of this
     * Device and waiting for "Frame Acknowledgement" or a "Frame Error" response
     * <br/>
     * In comparison to Send(), this function catches TimeoutExceptions
     * by printing the StackTrace to the terminal.
     *  <br/>
     * When the Frame is null, sending will get skipped without an error
     * message or warning
     * @param leds the led array to be sent; will be cut to the right size if it
     *             does not fit onto the actual LED strip
     */
    public void simpleSend(LED[] leds)
    {
        setLeds (leds);
        simpleSend ();
    }

    /**
     * function applying the given LED array and offset to the current frame
     * and sending the current frame over the Serial connection of this Device
     * and waiting for "Frame Acknowledgement" or a "Frame Error" response,
     * overloading the Send() function
     * <br/>
     *  In comparison to Send(), this function catches TimeoutExceptions by
     *  printing the StackTrace to the terminal.
     *  <br/>
     * When the Frame is null, sending will get skipped without an error
     * message or warning
     * @param leds the LED array to be sent; will be cut to the right size if it
     *             does not fit onto the actual LED strip
     * @param offset the offset for the given LED array when applying;
     */
    public void simpleSend(LED[] leds, int offset)
    {
        setLeds (leds);
        setOffset (offset);
        simpleSend ( );
    }


    /**
     * function sending the currently saved frame over the Serial connection
     * of this Device and waiting for "Frame Acknowledgement" or a "Frame Error"
     * response
     * <br/>
     *When the Frame is null, sending will get skipped without an error message
     *  or warning
     * @throws ConnectionException The device is not connected; Connect
     * first by establishing a serial connection and using Connect()
     * @throws TimeoutException no Frame Acknowledgement or frame
     * error byte received within the timeOut; the device may be disconnected
     * @throws  FrameErrorException a Frame Error byte was received,
     * indicating that the previously sent frame could not be applied by the device
     * @throws IOException an IO error occurred while waiting for a response
     *
     */
    public void send() throws TimeoutException, FrameErrorException, IOException
    {

        //check if the connection was established before
        if(connectionState != CONNECTION_STATE.CONNECTED)
        {
            throw new ConnectionException ("The device is not connected; Please establish a connection before sending data");
        }
        //check if there is data to send
        if(frame == null)
        {
            //there is no data to send
            return;
        }

        //Clamp the LED array and the offset
        /* this is done in order to make it possible to use negative offsets or
        offset values which would otherwise be out of range */

        //clamp the LED array so it allways fits to the actual LED strip
        setLeds (clampLeds (getLeds (), getOffset ()));

        //clamp the offset value according to the amount of connected LEDs,
        // so it is within the range of the connected LEDs
        setOffset (clampOffset (getOffset(), getConfiguration ().getNumOfLeds ()));



        //begin measuring ping here
        long startTime = System.nanoTime ();
        //send the data to the device
        sendFrame(frame);

        //wait for a response from the device and react accordingly
        switch(waitForOneOf(RECEIVER_TIMEOUT, Constants.FRAME_ACKNOWLEDGEMENT_BYTE, Constants.FRAME_ERROR_BYTE))
        {
            case -1:
                //the connection timed out
                //disconnect the device and throw an error
                invalidateConnection ();
                throw  new TimeoutException ( "No Frame Response received within " + RECEIVER_TIMEOUT + " ms: Device timed out");

            case 0:
                //FRAME_ACKNOWLEDGEMENT_BYTE received
                //do noting and continue
                break;

            case 1:
                //FRAME_ERROR_BYTE received
                //throw an error notifying the caller of the problem
                throw new FrameErrorException ("The frame could not be applied by the Slave device: Frame Error Byte received");
        }

        //calculate the ping and set it to the local variable
        ping = System.nanoTime () - startTime;
    }



    /**
     * function applying the given LED array to the current frame and sending
     * the current frame over the Serial connection of this Device and waiting
     * for "Frame Acknowledgement" or a "Frame Error" response, overloading
     * the Send() function.
     * <br/>
     *When the Frame is null, sending will get skipped without an error message or warning
     * @param leds the led array to be sent; will be cut to the right size if it
     *             does not fit onto the actual LED strip
     * @throws ConnectionException The device is not connected; Connect
     * first by establishing a serial connection and using Connect()
     * @throws TimeoutException no Frame Acknowledgement or frame error
     * byte received within the timeOut; the device may be disconnected
     * @throws  FrameErrorException a Frame Error byte was received,
     * indicating that the previously sent frame could not be applied by the device
     * @throws IOException the data could not be sent because an IO error
     * occurred
     */
    public void send(LED[] leds) throws TimeoutException, FrameErrorException, IOException
    {
        setLeds (leds);
        send ();
    }



    /**
     * function applying the given LED array and offset to the current frame
     * and sending the current frame over the Serial connection of this Device
     * and waiting for "Frame Acknowledgement" or a "Frame Error" response,
     * overloading the Send() function
     * <br/>
     *When the Frame is null, sending will get skipped without an error message or warning
     * @param leds the LED array to be sent; will be cut to the right size if it
     *            does not fit onto the actual LED strip
     * @param offset the offset for the given LED array when applying;
     * @throws ConnectionException The device is not connected; Connect
     * first by establishing a serial connection and using Connect()
     * @throws TimeoutException no Frame Acknowledgement or frame error
     * byte received within the timeOut; the device may be disconnected
     * @throws  FrameErrorException a Frame Error byte was received,
     * indicating that the previously sent frame could not be applied by the device
     * @throws OutOfRangeException the given offset value is invalid
     * according to the ALUP v. 0.1
     * @throws IOException the data could not be sent because an IO error
     *  occurred
     */
    public void send(LED[] leds, int offset) throws TimeoutException, FrameErrorException, IOException
    {
        setLeds (leds);
        setOffset (offset);
        send ();
    }


    /**
     * function serializing the given frame according to the ALUP v. 0.1 frame
     * specifications and sending it over the serial connection
     * <br/>
     * Note: This function is for raw frame sending only. Please use Send()
     * instead as it also checks for acknowledgements and is more save to use
     * @param frame the frame to be sent; has to be non-null
     * @throws IOException an IO error occurred while sending the data
     */
    private void sendFrame(Frame frame) throws IOException
    {
        //serialize the content of the header and frame body to unsigned byte values
        //Note: an array type of short is needed because Java does not support
        // unsigned values.
        short[] header = frame.getHeader ().serialize ();
        short[] body = LED.serializeArray (frame.getLeds ());

        //TODO: remove debug message
        System.out.println ( "Sending frame: " + frame.toString ());
        System.out.println ( "Serialized header: " + ArrayUtils.toString (header));
        System.out.println ( "Serialized body: " + ArrayUtils.toString (body));
        System.out.println ( "Body size: " + frame.getHeader ().getBodySize ());


        //send the serialized bytes over the serial connection
        sendUnsignedBytes ( header);
        sendUnsignedBytes ( body);
    }


    /**
     * function sending the first byte of the given short value over the serial
     * connection of this device
     * @param value a number ranging from 0-255
     * @throws IOException an IO error occurred while sending the data
     *              <br/>
     *  Note: Only the first byte of the given short number will be sent over
     *              the serial connection.
     *              <br/>
     *              This has to be done because Java has no support for unsigned
     *              values.
     *              <br/>
     *              Therefore the actual value from 0-255 given to this function will be sent
     */
    private void sendUnsignedByte(short value) throws IOException
    {
        //get the first byte of the short
        byte lowByte = (byte) (value & 0xff);

        //send it over the device's serial connection
        writeBytes (new byte[]{lowByte});
    }



    /**
     * function sending the first byte of the given short value over the serial
     * connection of this device
     * @param values an array of numbers ranging from 0-255
     * @throws IOException an IO error occurred while sending the data
     *               <br/>
     *  Note: Only the first byte of each given short number will be sent over
     *              the serial connection.
     *               <br/>
     *              This has to be done because Java has no support for unsigned
     *               values.
     *               <br/>
     *              Therefore the actual usnigned byte value of each short from 0-255
     *              given to this function will be sent
     */
    private void sendUnsignedBytes(short[] values) throws IOException
    {
        //convert the short values to unsigned bytes
        byte[] lowBytes = new byte[values.length];
        for(int i = 0; i < values.length; i++)
        {
            //get the lower byte of each value
            lowBytes[i] =  (byte) (values[i]& 0xff);
        }
        //send all the low bytes over the serial connection of this device
        writeBytes (lowBytes);

    }


    /**
     * function sending a connection acknowledgement to the serial device
     * @throws IOException an IO error occurred while sending the data
     */
    private void sendConnectionAcknowledgement ( ) throws IOException
    {
        writeBytes (new byte[]{Constants.CONNECTION_ACKNOWLEDGEMENT_BYTE});
    }

    /**
     * function sending a configuration acknowledgement to the serial device
     * indicating that the configuration was received and applied correctly
     * @throws IOException an IO error occurred while sending the data
     */
    private void sendConfigurationAcknowledgement ( ) throws IOException
    {
        writeBytes (new byte[]{Constants.CONFIGURATION_ACKNOWLEDGEMENT_BYTE});
    }

    /**
     * function sending a connection abort signal to the serial device
     * indicating that the configuration caused an error and the connection
     * attempt will be stopped
     * @throws IOException an IO error occurred while sending the data
     */
    private void sendConfigurationError() throws IOException
    {
        writeBytes (new byte[]{Constants.CONFIGURATION_ERROR_BYTE});
    }


    /**
     * function sending a disconnect command inside an empty frame to the device
     * @throws IOException an IO error occurred while sending the data
     */
    private  void sendDisconnectFrame() throws IOException
    {
        //make a new empty frame
        frame = new Frame ();
        //set the disconnect command to the frame
        frame.setProtocolCommand (Constants.COMMAND_DISCONNECT);
        //send the frame without waiting for a response from the device
        sendFrame (frame);
    }

    //endregion

    //region receiver functions


    /**
     * function receiving the configuration of the device over the serial
     * connection and applying it to this device
     * <br/>
     * Note: This function blocks until the configuration was received successfully.
     * <br/>
     *  Retrieve it using getConfiguration()
     *  @throws TimeoutException the configuration could not be received
     *  because the serial device did not send a CONFIGURATION_START_BYTE
     *  within 10 seconds
     *  @throws IncompatibleVersionException The protocol Version of the
     *  SerialDevice and the version at Constants.VERSION
     *                      do not match and are therefore incompatible
     * @throws IllegalArgumentException The configuration received from
     * the device or parts of it were invalid. Therefore the
     *                      connection attempt was stopped
     * @throws IOException the configuration could not be received, because
     * an IO error occurred
     */
    private void receiveConfiguration ( ) throws IncompatibleVersionException, TimeoutException, IllegalArgumentException, IOException
    {
        //Wait for the configuration start byte
        if(!waitForByte(Constants.CONFIGURATION_START_BYTE, RECEIVER_TIMEOUT))
        {
            //no configuration start byte received within 10 seconds
            throw  new TimeoutException ( "No configuration start byte received within " + RECEIVER_TIMEOUT + " ms");
        }

        //read the configuration values
        String protocolVersion = receiveString();
        //check if the protocol version is compatible with the one used here
        checkProtocolVersion(protocolVersion);

        String deviceName = receiveString();
        int numOfLeds = receiveInt();
        int dataPin = receiveInt();
        int clockPin = receiveInt();
        String extraValues = receiveString();

        try
        {
            //apply the values to the configuration
            configuration = new DeviceConfiguration(protocolVersion, deviceName, numOfLeds, dataPin, clockPin, extraValues);
        }
        catch (IllegalArgumentException e)
        {
            //at least one of the given configuration values was illegal
            sendConfigurationError ();
            throw e;
        }

        //configuration received and applied successfully
        //send a configuration acknowledgement
        sendConfigurationAcknowledgement();

        //wait for a configuration acknowledgement from the slave device to
        // finish the configuration process
        if(!waitForByte (Constants.CONFIGURATION_ACKNOWLEDGEMENT_BYTE, RECEIVER_TIMEOUT))
        {
            //no configuration Acknowledgement received within the timeOut
            //something probably went wrong on the device
            invalidateConnection ();
            throw new TimeoutException ( "No Configuration Acknowledgement received: Device not responding");
        }
    }


    /**
     * receive a String over the serial connection until a null terminator ('\0')
     * is received
     * <br/>
     * Note: This function blocks until a null byte is received and a string is
     * returned
     * @return the string which was read from the serial Connection
     * @throws IOException the string could not be received because an
     * IO error occurred
     */
    private String receiveString ( ) throws IOException
    {

        ArrayList<Byte> byteBuffer = new ArrayList<> ( );
        while(true)
        {
            int availableBytes = bytesAvailable ();
            if(availableBytes > 0)
            {
                byte[] rxByte = new byte[1];
                readBytes (rxByte, 1);


                //check if this byte is a null byte and therefore marks the end of the String
                if(rxByte[0] == 0x00)
                {
                    //end of the string reached
                    break;
                }

                //add the byte to the buffer
                byteBuffer.add (rxByte[0]);
            }
        }

        //convert the byte buffer to a string and return it
        Byte[] finalBytes = new Byte[byteBuffer.size ()];
        byteBuffer.toArray ( finalBytes);
        return new String(ArrayUtils.toPrimitive (finalBytes), StandardCharsets.UTF_8);
    }



    /**
     * function receiving an integer value over the serial port of this device
     * Note: This function blocks until an integer value is received and returned
     * @return the received integer value
     * @throws IOException the integer value could not be received beacuse an
     * IO error occurred
     */
    private int receiveInt() throws IOException
    {
        while(true)
        {
            if(bytesAvailable () >= 4)
            {
                byte[] rxBytes = new byte[4];
                readBytes (rxBytes, 4);
                return Convert.BytesToInt(rxBytes);
            }
        }
    }



    /**
     * function receiving one unsigned byte over the serial connection
     * Note: This function blocks until a byte is received and returned
     *
     * @return the byte received over the serial connection represented as a
     * short, ranging from 0 - 255
     * Note: a return type of short has to be used as java does not support
     * unsigned type variables.
     * By using a short, it's possible for this function to return an unsigned
     * byte value anyways
     * @throws IOException the unsigned byte could not be received because
     * an IO error occurred
     */
    private short receiveUnsignedByte() throws IOException
    {
        while (true)
        {
            if(bytesAvailable () > 0)
            {
                byte[] rxByte = new byte[1];
                readBytes (rxByte, 1);
                //convert the signed byte to its unsigned value and return it as a short
                return (short) (rxByte[0] & 0xff);

            }
        }
    }
    //endregion

    //region waiter functions

    /**
     * function waiting, until the specified byte was received via the serial
     * connection or the timeout limit was reached
     * <br/>
     * Note: This function blocks until the specified byte is received or the
     * specified timeout  was reached
     * <br/>
     * @param b the byte which should be received
     * @param timeOut the time limit for this function to receive the specified
     *               byte in milliseconds; has to be > 0
     * @return true, if the specified byte was received, false if the timeout
     * was exceeded or the function was interrupted
     * @throws IOException the byte could not be received because an
     * IO error occurred
     */
    private boolean waitForByte(Byte b, int timeOut) throws IOException
    {
        return waitForOneOf (timeOut, b) == 0;
    }


    /**
     * function waiting, until one of the specified bytes was received via the
     * serial connection or the timeout limit was reached
     * <br/>
     * Note: This function blocks until one of the specified byte is received or
     * the specified timeout  was reached
     * <br/>
     * @param timeOut the time limit for this function to receive the specified
     *                byte in milliseconds; has to be > 0
     * @param bytes the bytes to wait for
     * @return the index of the received byte, or -1 if the timeOut was exceeded
     * or the function was interrupted
     * @throws IOException the byte could not be received because an
     * IO error occurred
     */
    private  int waitForOneOf( int timeOut, byte... bytes) throws IOException
    {
        //try to read the specified byte every 1ms
        for(int i = 0; i < timeOut; i++)
        {
            //check if there are bytes available to read
            if(bytesAvailable () > 0)
            {
                //read a byte from the buffer
                byte[] rxBytes = new byte[1];
                readBytes (rxBytes, 1);

                //check if the received byte equals one of the specified bytes
                for(int j = 0; j < bytes.length; j++)
                {
                    if(rxBytes[0] == bytes[j])
                    {
                        //the received byte equals the specified byte
                        return j;
                    }
                }
            }

            try
            {
                //sleep for 1 millisecond
                Thread.sleep (1);
            }
            catch (InterruptedException e)
            {
                //the sleep got interrupted unexpectedly
                e.printStackTrace ( );
                return -1;
            }
        }
        //the specified byte could not be received within the timeOut limit
        return -1;
    }

    /**
     * function waiting for a connection request, returning as soon as a
     * connection request was received
     * Note: This function blocks until a byte is received or a timeout of 10
     * seconds was reached
     * @return true, if the byte was received, else false
     * @throws IOException the byte could not be received because an
     * IO error occurred
     */
    private boolean waitForConnectionRequest ( ) throws IOException
    {
        //try for 10s to receive a connection request
        return waitForByte (Constants.CONNECTION_REQUEST_BYTE, RECEIVER_TIMEOUT);
    }

    //endregion



    //region clear
    /**
     * function setting all LEDs of the device to 0
     * @throws TimeoutException  no frame acknowledgement received within
     * the timeout limit
     * @throws IOException the data could not be sent because an IO error
     * occurred
     */
    public void clear() throws TimeoutException, IOException
    {
        //create a new empty frame
        frame = new Frame ( );
        //set the clear command
        frame.setProtocolCommand (Constants.COMMAND_CLEAR);
        //send it to the device
        send();
    }

    /**
     * function setting all LEDs of the device to 0
     * In comparison to Clear(), this function catches TimeoutExceptions
     * and IOExceptions by printing the StackTrace to the terminal.
     */
    public void simpleClear()
    {
        try
        {
            clear();
        }
        catch (TimeoutException | IOException e)
        {
            e.printStackTrace ( );
        }
    }


    /**
     * function setting the command of the current frame to clear; when
     * used with a non-empty frame, this will set all unchanged LEDs to black
     * @param state true, if the clear command should be set, else false
     */
    public void setClear(boolean state)
    {
        if(state)
        {
            frame.setProtocolCommand (Constants.COMMAND_CLEAR);
        }
        else
        {
            frame.setProtocolCommand (Constants.COMMAND_NONE);
        }
    }

    //endregion



    //region getter

    /**
     * function returning the currently for this device stored LEDs or, if no
     * LEDs are stored, a new LED array with all possible LEDs set to 0
     * @return an array of LEDs; guaranteed to be non-null
     */
    public LED[] getLeds()
    {
        //check if the current frame is null
        if(frame == null)
        {
            //the current frame is null; create a new one
            frame = new Frame ();
        }

        //check if the currently stored LED array is null or empty
        if(frame.getLeds () == null || frame.getLeds ().length == 0)
        {
            //the currently stored LED array is null or empty; initialize a new array
            // with the maximum size
            LED[] leds = new LED[configuration.getNumOfLeds ()];
            for(int i = 0; i < leds.length; i++)
            {
                leds[i] = new LED ( 0,0,0);
            }
            frame.setLeds (leds);
        }

        return frame.getLeds ();
    }


    /**
     * getter for the ping
     * @return the ping to the device from the last frame sent until its answer
     * in nanoseconds
     */
    public long getPing()
    {
        return ping;
    }



    /**
     * getter for the ping in milliseconds
     * @return the ping to the device from the last frame sent until its answer
     * in milliseconds
     */
    public long getPingMS()
    {
        return ping / 1000000;
    }

    public int getOffset()
    {
        return frame.getHeader ().getOffset ();
    }


    /**
     * function indicating if this device is fully connected and ready for data
     * transmission
     * @return true, if the device is fully connected, else false
     */
    public boolean isConnected()
    {
        return connectionState == CONNECTION_STATE.CONNECTED;
    }


    /**
     * function returning the connection state of this device
     * @return the connection state of this device; one of CONNECTION_STATE
     */
    public CONNECTION_STATE getConnectionState()
    {
        return connectionState;
    }

    /**
     * function returning the configuration of this device
     * @return the device configuration or null if no configuration was received yet
     */
    public DeviceConfiguration getConfiguration()
    {
        return configuration;
    }

//endregion



    //region setter
    /**
     * function setting the subcommand for a subprogram which should be
     * executed on the receiving device
     * @param id the ID of the subprogram to execute; has to be within a range of 0-247
     * @throws IllegalArgumentException the given ID is not within a range of 0-247
     */
    public void setSubcommand(int id)
    {
        if(frame == null)
        {
            //create a new Frame with an empty LED array and default headers
            frame = new Frame ( );
        }
        //set the subcommand of the given ID
        frame.setSubcommand (id);
    }



    /**
     * function setting the led values of this frame; automatically cuts the
     * array to the size of the led strip if it is too large
     * @param leds the led values to be set
     */
    public void setLeds(LED[] leds)
    {
        if(frame == null)
        {
            //create a new frame if there is no frame
            frame = new Frame ( );
        }
        frame.setLeds (leds);
    }



    /**
     * function setting the offset for the current frame
     * @param offset the value to set the body offset to; has to be a positive
     *              value; the offset + leds.length has to be smaller than the number
     *              of leds connected to the device
     *  @throws OutOfRangeException the offset value of the frame which
     *  should be sent was invalid
     */
    public void setOffset(int offset)
    {
        //create a new frame if there is no frame
        if(frame == null)
        {
            frame = new Frame ( );
        }

        frame.setOffset (offset);
    }
    //endregion
}
