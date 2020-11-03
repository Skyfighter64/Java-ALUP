package test;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import ALUP.Device;
import ALUP.FrameErrorException;
import ALUP.IncompatibleVersionException;
import ALUP.LED;
import ALUP.OutOfRangeException;
import ALUP.SerialDevice;
import ALUP.WifiDevice;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class WifiTest
{
    static final String ip = "192.168.178.100";
    static final int port = 1201;

    @Test
    void ConnectionTest()
    {
        WifiDevice device = new WifiDevice (ip,port);

        //test before connecting the device
        assertEquals (Device.CONNECTION_STATE.DISCONNECTED, device.getConnectionState ());
        assertNull (device.getConfiguration ());
        assertEquals (0, device.getRtt ());
        assertEquals (0, device.getRttMS ());

        //connect the device
        try
        {
            device.connect ();
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
        catch (IOException e)
        {
            System.out.println ( "IO error occurred; No testing possible");
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

    void SendIllegalBodySizeFrame(Device device) throws TimeoutException, FrameErrorException, IOException
    {
        device.send (new LED[device.getConfiguration ().getNumOfLeds () + 1]);
    }

    void SendLegalFrame(Device device) throws TimeoutException, IOException
    {
        device.send ( new LED[] {});
    }
}
