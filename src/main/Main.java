package main;

import com.fazecast.jSerialComm.*;

import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

import ALUP.Device;
import ALUP.Effects;
import ALUP.LED;
import ALUP.SerialDevice;
import ALUP.WifiDevice;
public class Main
{

    /**
     *  Command line arguments:
     *  -a  |  --autoselect     automatically select the port if only one is avaliable
     *
     */
    public static void main(String[] args)
    {

        WifiTest ();
       // SerialTest(args);






        /*
        // -------------------------------------------TESTS -------------------------------------------
        //TODO: create separate project/ package for testing slave device implementations
        //
          Frame frame = new Frame ();
      System.out.println (frame.toString ());

      frame.setLeds (new LED[]{ new LED(255 ,255,255)});
      frame.setSubcommand (36);
      System.out.println ( frame.toString ());


        port.setBaudRate (115200);
        port.openPort ();
        Device device = new Device ();
        device.serialPort = port;

        SendByte (device);
        //System.out.println (  device.ReceiveUnsignedByte ());


       // PrintSerialRead (port);


        System.out.println ( "receiving int...");
         System.out.println ("Int received: \"" +  device.ReceiveInt () + "\"");
        System.out.println ( "receiving int...");
        System.out.println ("Int received: \"" +  device.ReceiveInt () + "\"");
        System.out.println ( "receiving int...");
        System.out.println ("Int received: \"" +  device.ReceiveInt () + "\"");
        System.out.println ( "receiving int...");
        System.out.println ("Int received: \"" +  device.ReceiveInt () + "\"");
*/
      //  System.out.println ( "receiving String...");
       // System.out.println ("String received: \"" +  device.ReceiveString () + "\"");
    }

    private static void WifiTest()
    {
        //set the IP and port of the slave device
        String ip = "192.168.178.100";
        int port = 1201;

        WifiDevice myDevice = new WifiDevice (ip, port);

        //try to connect to the device
        myDevice.simpleConnect();

        System.out.println ( myDevice.getConfiguration ().toString ());

        //create LED data which should be sent
        LED[] leds = new LED[] { new LED(255, 0, 0), new LED(0, 255, 0), new LED(0, 0, 255)};

        //Send LED data
        myDevice.simpleSend(leds);
        System.out.println ( "RTT : " + myDevice.getRttMS ());

        long averageRtt = 0;
        long maxRtt = 0;
        for(int i = 0; i < 10000; i ++)
        {
            //LED[] leds = new LED[] {new LED ( 255,0,0), new LED ( 0,255,0), new LED ( 0,0,255)};
             leds = Effects.Rainbow (30,20, i, 10);
            myDevice.setLeds (leds);
            myDevice.simpleSend ();
            System.out.println ( "RTT: " + myDevice.getRttMS () + "ms");
            averageRtt += myDevice.getRttMS ();
            maxRtt = Math.max (maxRtt, myDevice.getRttMS ( ));
        }

        System.out.println ( "Average RTT: " + averageRtt/10000 + "ms ::  max RTT: " + maxRtt + "\n");
        //disconnect the device
        myDevice.disconnect();
        System.out.println("Disconnected");
    }



    private static void SerialTest(String[] args)
    {
       // SerialPort port = SelectPort((ArrayUtils.contains (args , "-a") || ArrayUtils.contains (args, "--autoselect")));
        SerialPort port = SelectPort(false);

        if(port == null)
        {
            System.out.println ( "No port selected");
            return;
        }

        Device device = new SerialDevice (port, 115200);
        device.simpleConnect ();

        System.out.println ( "Connected");
        System.out.println ( device.getConfiguration ().toString ());

        LED[] leds = new LED[] {new LED ( 255, 0, 0)};
        device.setOffset (0);
        device.simpleSend (leds);


        for (int i = 0; i < 1000; i++)
        {
            leds = Effects.Rainbow (1,10, i, device.getConfiguration ().getNumOfLeds ());
            //device.setOffset (i);
            device.simpleSend (leds);

            System.out.println ( "RTT: " + device.getRttMS () + "ms (" +  device.getRttMS () +"ns)");
        }


        device.simpleClear ();
        System.out.println ( "RTT: " + device.getRttMS () + "ms (" +  device.getRttMS () +"ns)");
        device.disconnect ();
    }


    private  void SetLed(Device device)
    {
        Scanner scanner = new Scanner (System.in);

        System.out.println ( "Enter led index: ");
        int i = scanner.nextInt ();

        System.out.print ( "Red: ");
        int r = scanner.nextInt ();

        System.out.print ( "Green: ");
        int g = scanner.nextInt ();

        System.out.print ( "Blue: ");
        int b = scanner.nextInt ();

        LED[] leds = device.getLeds();
        leds[i] = new LED (r, g, b);

        device.setLeds (leds);


        try
        {
            device.send ();
        }
        catch (TimeoutException e)
        {
            System.out.println ( "Device timed out");
            e.printStackTrace ( );
        }
        catch (IOException e)
        {
            System.out.println ( "IO error occured");
            e.printStackTrace ( );
        }

        //output frame rtt
        System.out.println ( "RTT: " + device.getRtt () + "ms (" +  device.getRtt () +"ns)");
    }


    /**
     * function letting the user select a serial port via stdin
     * @param autoSelect true, if the device should be austomatically selected if only one is found, else false
     * @return the selected Serial Port, or null if no Port was selected
     */
    private static SerialPort SelectPort(boolean autoSelect)
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
        else if (ports.length == 1 && autoSelect)
        {
            System.out.println("Only one device found: Auto selecting it...");
            //auto-select the only possible port
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


    private static void Delay(int ms)
    {
        try
        {
            Thread.sleep (ms);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace ( );
        }
    }



    private static void PrintSerialRead(SerialPort port)
    {
        while(true)
        {
            int bytesAvailable = port.bytesAvailable ();
            if( bytesAvailable > 0)
            {
                byte[] rxBytes = new byte[bytesAvailable];
                port.readBytes (  rxBytes, bytesAvailable);

                for(int i = 0; i < bytesAvailable; i++)
                {
                    System.out.print ( rxBytes[i] + "  ");
                }
                System.out.println ( );
            }
        }
    }

    private static void SendByte(SerialPort serialPort)
    {
        while (true)
        {
            System.out.println ("Enter a byte value:");
            //read a byte value from the user
            Scanner scanner = new Scanner (System.in);
            int inNumber = scanner.nextInt ();

            //get the first byte of the integer
            byte lowByte = (byte) (inNumber & 0xff);

            //write the byte to the serial device
            serialPort.writeBytes (new byte[]{lowByte  }, 1);
            System.out.println ( "Byte sent");
        }

    }
/*
    private static  void SendByte(Device device)
    {
        while (true)
        {
            System.out.println ("Enter a byte value:");
            //read a byte value from the user
            Scanner scanner = new Scanner (System.in);
            short inNumber = scanner.nextShort ();

            device.SendUnsignedByte (inNumber);
            System.out.println ( "Byte sent");
        }
    }
*/


}
