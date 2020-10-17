package test;

import com.fazecast.jSerialComm.SerialPort;

import org.apache.commons.lang3.ObjectUtils;
import org.junit.jupiter.api.Test;

import java.util.Scanner;
import java.util.concurrent.TimeoutException;

import ALUP.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * class used for testing this protocol implementation
 *
 * In order to run this test, you have to have an Arduino connected to a USB port with an implementation of the ALUP v. 0.1 installed
 * Make sure to match the baud rate and select the right serial port
 */
public class ArduinoTests
{
    static final int baudRate = 115200;


    @Test
    void ConnectionTest()
    {
        SerialDevice device = new SerialDevice (SelectPort());

        //test before connecting the device
        assertEquals (Device.CONNECTION_STATE.DISCONNECTED, device.getConnectionState ());
        assertNull (device.getConfiguration ());
        assertEquals (0, device.getPing ());
        assertEquals (0, device.getPingMS ());

        //connect the device
        try
        {
            device.connect (baudRate);
        }
        catch (TimeoutException e)
        {
            System.out.println ( "Device timed out; No testing possible");
            e.printStackTrace ( );
            return;
        }
        catch (IncompatibleVersionException e)
        {
            System.out.println ( "Device Version incompatible; No testing possible");
            e.printStackTrace ( );
            return;
        }

        //test after successful connection
        assertEquals (Device.CONNECTION_STATE.CONNECTED, device.getConnectionState ());
        assertNotNull (device.getConfiguration ());

        assertThrows (NullPointerException.class, () -> device.setLeds (null));

        //test subcommands
        assertThrows (IllegalArgumentException.class, () -> device.setSubcommand ( - 1));
        assertThrows (IllegalArgumentException.class, () -> device.setSubcommand ( 248));
        assertThrows (IllegalArgumentException.class, () -> device.setSubcommand ( Integer.MAX_VALUE));

        //test sending frames
        assertDoesNotThrow( () -> SendLegalFrame (device));
        //make sure that illegal frames are properly corrected and no frame error gets thrown
        assertDoesNotThrow ( () -> SendIllegalBodySizeFrame(device));
        assertDoesNotThrow( () -> SendLegalFrame (device));

        //make sure that illegal frames are properly corrected and no frame error gets thrown
        assertDoesNotThrow(() -> SetIllegalBodyOffsetFrame(device));
        assertDoesNotThrow( () -> SendLegalFrame (device));


        device.disconnect ();

        assertEquals (Device.CONNECTION_STATE.DISCONNECTED, device.getConnectionState ());
        assertNull (device.getConfiguration ());
    }

    void SetIllegalBodyOffsetFrame(Device device) throws TimeoutException, OutOfRangeException
    {
        device.setOffset ( device.getConfiguration ().getNumOfLeds () +1);
    }

    void SendIllegalBodySizeFrame(Device device) throws TimeoutException, FrameErrorException
    {
        device.send (new LED[device.getConfiguration ().getNumOfLeds () + 1]);
    }

    void SendLegalFrame(Device device) throws TimeoutException
    {
        device.send ( new LED[] {});
    }


    @Test
    void ImplementationTests()
    {
        /*
    TODO: add arduino implementation tests

         *  test list:
         *  too big body size
         *  body size not multiple of 3
         *  negative body size
         * invalid frame offsets
         *  commands (subcommands, protocolCommands)


     */
    }

    /**
     * function letting the user select a serial port via stdin
     * @return the selected Serial Port, or null if no Port was selected
     */
    private static SerialPort SelectPort()
    {
        //get all serial devices connected to the system
        System.out.println("Searching for serial devices...");
        SerialPort[] ports = SerialPort.getCommPorts();
        if(ports.length == 0)
        {
            //no devices found
            System.out.println("No Serial devices found.");
            return null;
        }
        else if (ports.length == 1)
        {
            System.out.println("Only one device found: Auto selecting it...");
            //autoselect the only possible port
            return ports[0];
        }

        //list the connected serial devices to the user
        for(int i = 0; i < ports.length; i++)
        {
            System.out.println ( "Port " + (i +1) + " : " + ports[i].getDescriptivePortName () );
        }

        //let the user choose one device
        System.out.println ( "\nEnter the Number of the Port you want to choose");
        Scanner scanner = new Scanner (System.in);
        int portIndex = scanner.nextInt () - 1;
        if(portIndex < 0 || portIndex >= ports.length)
        {
            //the given input was invalid
            System.out.println ( "Invalid Port selected");
            return null;
        }

        //confirm choice to the user and return the chosen port
        System.out.println ("Port " + (portIndex + 1) + " chosen: " + ports[portIndex].getDescriptivePortName ());
        return ports[portIndex];

    }

}
