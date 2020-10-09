package main;

import com.fazecast.jSerialComm.*;

import java.util.Scanner;
import java.util.concurrent.TimeoutException;

import ALUP.Device;
import ALUP.Effects;
import ALUP.LED;

public class Main
{

    public static void main(String[] args)
    {
	    // write your code here
        SerialPort port = SelectPort();

        if(port == null)
        {
            System.out.println ( "No port selected");
            return;
        }

        Device device = new Device (port);
        device.SimpleConnect (115200);

        System.out.println ( "Connected");
        System.out.println ( device.getConfiguration ().toString ());

        LED[] leds = new LED[] {new LED ( 255, 0, 0)};
        device.setOffset (0);
        device.SimpleSend (leds);


        for (int i = 0; i < device.getConfiguration ().getNumOfLeds (); i++)
        {
            leds = Effects.Rainbow (1,10, i, 1);
            device.setOffset (i);
            device.SimpleSend (leds);
            try
            {
                device.Clear ();
            }
            catch (TimeoutException e)
            {
                e.printStackTrace ( );
            }
            //device.SimpleSend(new LED[]{});
            System.out.println ( "Ping: " + device.getPingMS () + "ms (" +  device.getPing () +"ns)");

        }


        try
        {
            device.Clear ();
            System.out.println ( "cleared");
        }
        catch (TimeoutException e)
        {
            e.printStackTrace ( );
        }
        //device.SimpleSend(new LED[]{});
        System.out.println ( "Ping: " + device.getPingMS () + "ms (" +  device.getPing () +"ns)");
        //Delay(1);
        //Delay(1);
        device.Disconnect ();
        //TODO: clear and disconnect are only working with a delay...




 /*       for(int i = 0; i < 1000; i ++)
        {
            //LED[] leds = new LED[] {new LED ( 255,0,0), new LED ( 0,255,0), new LED ( 0,0,255)};
            LED[] leds = Effects.Rainbow (5,20, i, 10);
            device.setLeds (leds);
            device.SimpleSend ();

            System.out.println ( "Ping: " + device.getPingMS () + "ms (" +  device.getPing () +"ns)");
            try
            {
                Thread.sleep (100);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace ( );
            }
        }*/

        //device.Clear();
        /*try
        {
            Thread.sleep (10);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace ( );
        }*/


       // device.Disconnect ();


        /*
        LED[] leds = new LED[device.getConfiguration ().getNumOfLeds ()];

        for(int i = 0; i < leds.length; i++)
        {
            leds[i] = new LED ( 255,0,0);
        }

        device.setLeds (leds);
        try
        {
            device.Send ();
        }
        catch (TimeoutException | FrameErrorException e)
        {
            System.out.println ( "Error while sending data");
            e.printStackTrace ( );
        }
*/
        /*

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

        // -------------------------------------------TESTS -------------------------------------------
        //TODO: convert to unit tests
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


    private static void SetLed(Device device)
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
            device.Send ();
        }
        catch (TimeoutException e)
        {
            System.out.println ( "Device timed out");
            e.printStackTrace ( );
        }

        //output frame ping
        System.out.println ( "Ping: " + device.getPingMS () + "ms (" +  device.getPing () +"ns)");
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
