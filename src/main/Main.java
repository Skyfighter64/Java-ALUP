package main;

import com.fazecast.jSerialComm.*;

import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.util.Random;
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
        String comPort = "COM5";
        int baudRate = 115200;

        //Creating a SerialDevice:
        Device myDevice = new SerialDevice(comPort, baudRate);

        //Connecting using connect()
        try
        {
            myDevice.connect();
        }
        catch(TimeoutException e)
        {
            //the device timed out while trying to connect
            System.out.println("Oh no! Could not connect to the device because it timed out.");
            return;
        }
        catch(IOException e)
        {
            //Could not connect to the device
            //the device timed out while trying to connect
            System.out.println("Could not connect to the device because there is a connection problem");
            return;
        }

        //create an array for the leds which has the same size as the LED strip
        LED[] leds = new LED[myDevice.getConfiguration().getNumOfLeds()];

        for(int i = 0; i < 5000; i++)
        {

            //generate rainbow colors using the Effects library
            leds = Effects.Rainbow(1,30, i, leds.length);

            //send the led array to the device
            try
            {
                myDevice.send ( leds);
            }
            catch (TimeoutException e)
            {
                //the device timed out while trying to send the data
                System.out.println("Oh no! Could not send the data  because the device timed out.");
            }
            catch (IOException e)
            {
                //Could not connect to the device
                System.out.println("Could send the data because there is a connection problem");
            }
        }

        myDevice.disconnect ();

 /*      // WifiTest ();
        //SerialTest(args);

        Device device = new SerialDevice (SelectPort (true), 115200);
        //Device device = new WifiDevice ("192.168.178.100", 1201);

        device.simpleConnect ();

        AnimatedShineTest (device);
        //RainbowTest (device);

       // device.simpleSend ( Effects.Shine (24, 15, new LED ( 255, 75, 0), device.getConfiguration ().getNumOfLeds ()));

        System.out.println ( "RTT: " + device.getRttMS () + "ms (" +  device.getRtt () +"ns)");
        //device.simpleClear ();
        device.disconnect ();
    */}

    private static void WifiTest()
    {
        //set the IP and port of the slave device
        String ip = "192.168.178.100";
        int port = 1201;

        WifiDevice myDevice = new WifiDevice (ip, port);

        //try to connect to the device
        myDevice.simpleConnect();
        myDevice.simpleClear ();

        System.out.println ( myDevice.getConfiguration ().toString ());

        Random rnd = new Random ( );

       /* LED[] leds = Effects.Color(255 , 77 , 0, myDevice.getConfiguration ().getNumOfLeds ());

        for(int i = 0; i < leds.length; i++)
        {
            int brightness = rnd.nextInt ( 255);
            leds[i].setRGB (Math.round ((float) leds[i].getRed () / 255f) * brightness, Math.round ((float) leds[i].getGreen () / 255f * brightness), Math.round ((float) leds[i].getBlue () / 255f  * brightness));
        }*/

       // LED[] leds = Effects.Shine (7, 7, new LED ( 255,255,255), myDevice.getConfiguration ().getNumOfLeds ());
        //myDevice.simpleSend ( leds);
          for(int i = 0; i < 10; i++)
        {
            Effects.Lightning (5,48,0,48,1,3,0,100,0,50, new LED ( 125,135,255), myDevice);
            myDevice.simpleSend ( Effects.Shine (24, 15, new LED ( 0, 186, 120), myDevice.getConfiguration ().getNumOfLeds ()));
           Delay(10000);
        }


     /*   long averageRtt = 0;
        long maxRtt = 0;
        for(int i = 0; i < 10000; i ++)
        {
            //LED[] leds = new LED[] {new LED ( 255,0,0), new LED ( 0,255,0), new LED ( 0,0,255)};
             leds = Effects.Rainbow (10,20, i, 48);
            myDevice.setLeds (leds);
            myDevice.setClear (false);
            myDevice.simpleSend ();
            System.out.println ( "RTT: " + myDevice.getRttMS () + "ms");
            averageRtt += myDevice.getRttMS ();
            maxRtt = Math.max (maxRtt, myDevice.getRttMS ( ));
        }

        System.out.println ( "Average RTT: " + averageRtt/10000 + "ms ::  max RTT: " + maxRtt + "\n");*/
        //disconnect the device
        myDevice.disconnect();
        System.out.println("Disconnected");
    }

    private static void ShineTest(Device device)
    {
            if(!device.isConnected ())
            {
                System.out.println ( "Device not connected!");
                return;
            }

            LED[] leds = Effects.Combine(
                    Effects.Shine (3, 2, new LED ( 0,125, 0), 10),
                    Effects.Shine (6, 2, new LED ( 0,125,125), 10));
            device.simpleSend (leds );
    }

    private static void AnimatedShineTest(Device device)
    {
        if(!device.isConnected ())
        {
            System.out.println ( "Device not connected!");
            return;
        }
        for(int j = 0; j < 10; j++)
        {
            for(int i = 0; i < device.getConfiguration ().getNumOfLeds () + 10; i++)
            {
                LED[] leds = Effects.Waves (i);
                device.simpleSend ( leds);

                Delay (100);
            }
        }

    }

    private static void RainbowTest(Device device)
    {
        if(!device.isConnected ())
        {
            System.out.println ( "Device not connected!");
            return;
        }
        LED[] leds;
        for (int i = 0; i < 1000; i++)
        {
            leds = Effects.Rainbow (1,10, i, device.getConfiguration ().getNumOfLeds ());
            device.simpleSend (leds);
        }
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




        device.simpleClear ();
        System.out.println ( "RTT: " + device.getRttMS () + "ms (" +  device.getRtt () +"ns)");
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
