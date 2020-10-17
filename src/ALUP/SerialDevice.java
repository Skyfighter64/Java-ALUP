package ALUP;

import com.fazecast.jSerialComm.SerialPort;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.ArrayUtils;


/*
Copyright 2020 Skyfighter64

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */


/**
 * Class representing a Device connected to this Computer over a Serial Connection
 * Note: "this device" refers to an instance of this class, not this Computer
 *
 *
 * Simple functions vs. regular functions:
 * The simple functions like SimpleSend() are designed to be easier to use
 * than the regular counterparts.
 *
 *
 * Keep in mind that the simple functions are limiting your ability to catch
 * some events, like a device timeOut in SimpleSend()
 *
 *@version 0.1 (internal)
 *
 *
 */
public class SerialDevice extends Device
{
    //the serial connection object for this device used for serial communication
    public SerialPort serialPort;

    /**
     * constructor creating a new Device using the given serialPort and baud rate
     * @param serialPort a serial port, does not have to be open
     * @param baud  the baud rate used for serial communication between
     *             the devices; has to be a valid baud rate supported by both
     *              devices and the same as set on the slave device
     */
    public SerialDevice(SerialPort serialPort, int baud)
    {
        super();
        this.serialPort = serialPort;
        this.serialPort.setBaudRate (baud);
    }


    /**
     * function connecting this device so it can be used for led data
     * transmission
     * according to the ALUP protocol v. 0.1
     * @throws TimeoutException the connection could not be established
     * because the serial device did not send a connection request within 10
     * seconds
     * @throws  IncompatibleVersionException The protocol Version of the
     * SerialDevice and the version at Constants.VERSION
     * do not match and are therefore incompatible
     * @throws IllegalArgumentException The configuration received from
     * the device or parts of it were invalid. Therefore the
     *                           connection attempt was stopped.
     */
    public void openConnection()
    {
        //establish the serial connection
        serialPort.openPort ();
    }




    /**
     * function receiving the given number of bytes over the serial
     * connection and storing them into the given buffer
     * @param buffer the buffer to store the received data in; has to have a
     *               size of bytesToRead
     * @param bytesToRead the number of bytes to read
     */
    @Override
    protected void readBytes (byte[] buffer, int bytesToRead)
    {
        serialPort.readBytes (buffer, bytesToRead);
    }


    /**
     * function sending the given bytes over the serial connection
     * @param buffer an array containing the data which should be sent.
     */
    @Override
    protected void writeBytes (byte[] buffer)
    {
        serialPort.writeBytes (buffer, buffer.length);
    }


    /**
     * function returning the number of bytes which are currently available
     * to be read form the serial connection
     * @return  the number of bytes which is currently available to be read
     * from the serial connection
     */
    @Override
    protected int bytesAvailable ( )
    {
        return serialPort.bytesAvailable ();
    }

    /**
     *  function closing the serial connection
     */
    @Override
    public void closeConnection ( )
    {
            serialPort.closePort ();
    }

    @Override
    public String toString ( )
    {
        return "SerialDevice{" +
                "serialPort=" + serialPort +
                ", configuration=" + configuration +
                ", ping=" + ping +
                ", frame=" + frame +
                ", connectionState=" + connectionState +
                '}';
    }
}
