package ALUP;


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


import java.util.Random;


/*
TODO: Ideas for more effects:
    - Gradient
    - waves
    - Sine
    - fade in/out/...
 */

/**
 * class providing various LED effects for use with the ALUP v. 0.1
 */
public class Effects
{

    /**
     * function providing a rainbow effect for the given amount of leds
     * @param speed the speed of the effect; has to be positive or 0
     * @param multiplier factor to stretch the effect; has to be positive
     * @param frame the frame for which to generate the effect for; increment to get an animated effect
     * @param leds the number of leds to generate the effect for; a positive value
     * @return an array of LED values containing the generated effect
     */
    public static LED[] Rainbow(int speed, int multiplier, int frame, int leds)
    {
        LED[] ledArray = new LED[leds];

        //loop trough all LEDs
        for(int i = 0; i < ledArray.length; i++)
        {
            //calculate the color of the color spectrum for the current LED
            ledArray[i] = Spectrum ((frame*speed + i * multiplier));
        }
        return ledArray;
    }



    /**
     * function generating a color depending on the given value from the color spectrum
     *
     * @param i the value to generate the color spectrum for; any number; loops at 768
     * @return the generated color of the spectrum for the given number
     */
    private static LED Spectrum(int i)
    {
        LED led = new LED (0 ,0,0);

        //limit the given value from 0 - 768
        i = i % 768;

        //generate red value
        if(i >= 0 && i < 256)
        {
            led.setRed (-i + 255);
        }
        else if (i > 512 && i < 768)
        {
            led.setRed ( - Math.abs ( i - 768) + 255);
        }


        //generate green value
        if(i > 0 && i < 512)
        {
            led.setGreen ( - Math.abs ( i - 256) + 255);
        }

        //generate blue value
        if( i > 256 && i < 768)
        {
            led.setBlue ( - Math.abs(i - 512) + 255);
        }

        return led;
    }

    /**
     * function returning an led Array containing the given number of LEDs with the given RGB color
     * @param r the red value for the color
     * @param g the green value for the color
     * @param b the blue value for the color
     * @param numOfLeds the number of LEDs
     * @return an array with the given number of LEDs set to the given RGB color
     */
    public static LED[] Color(int r, int g, int b, int numOfLeds)
    {
        //clamp the RGB values between 0 and 255
        r = Math.max(Math.min (r, 255), 0);
        g = Math.max(Math.min (g, 255), 0);
        b = Math.max(Math.min (b, 255), 0);

        //create the array and set all LEDs to the color
        LED[] leds = new LED[numOfLeds];
        for(int i = 0; i < numOfLeds; i++)
        {
            leds[i] = new LED (r, g, b);
        }
        return leds;
    }

    public static void Flash(int r, int g, int b, int numOfLeds, int offset, Device device)
    {
        Flash (new LED ( r,g,b), numOfLeds, offset, device);
    }

    public static void Flash(LED led, int numOfLeds, int offset, Device device)
    {
        device.simpleSend ( Color(led.getRed (),led.getGreen (),led.getBlue (), numOfLeds), offset);
        device.simpleClear ();
    }

    public static void Flash(LED led, long delay, int numOfLeds, int offset, Device device)
    {
        device.simpleSend ( Color(led.getRed (),led.getGreen (),led.getBlue (), numOfLeds), offset);

        //subtract the time needed for sending and make sure the delay is always positive
        delay = Math.max(0, delay - device.getRttMS ());

        try
        {
            //delay the end of the flash
            Thread.sleep (delay);
        }
        catch (InterruptedException e)
        {
            //sleep was interrupted
            e.printStackTrace ( );
        }
        device.simpleClear ();
    }

    /**
     * function creating a random lightning effect within the given boundaries
     * @param minLeds the minimum amount of LEDs used for a flash. Has
     *               to be positive and smaller or equal to the number of LEDs
     *                connected to the device.
     * @param maxLeds the maximum amount of LEDs used for a flash. Has
     *                to be greater than minLeds and 0, and smaller or equal to the number
     *                of LEDs connected to the device.
     * @param minOffset the minimum offset of the LEDs used for a flash. Has
     *                to be positive and smaller or equal to the number of LEDs
     *                connected to the device.
     * @param maxOffset the maximum offset of the LEDs used for a flash. Has
     *                 to be greater than minOffset and 0, and smaller or equal to the number
     *                 of LEDs connected to the device.
     * @param minFlashes the minimum number of flashes in this lightning.
     *                   Has to be positive or 0.
     * @param maxFlashes the maximum number of flashes in this lightning.
     *                   Has to be greater than minFlashes
     * @param minDelay the minimum delay in ms between the flashes.
     *                 Has to be positive or 0.
     * @param maxDelay the maximum delay in ms between the flashes.
     *                 Has to be greater than minDelay and 0.
     * @param minFlashLength The minimum length of a flash in ms. Has to be positive
     * @param maxFlashLength The maximum length of a flash in ms. Has to be
     *                       greater than minFlashLength.
     * @param color The color of the flashes.
     * @param device the device on which the lightning should be performed
     */
    public static void Lightning(int minLeds, int maxLeds,int minOffset, int maxOffset, int minFlashes, int maxFlashes, int minDelay, int maxDelay, int minFlashLength, int maxFlashLength, LED color, Device device)
    {

        if(!device.isConnected ( ))
        {
            //device not connected
            return;
        }
        if(color == null)
        {
            //no color set, use a default color
            color = new LED (125,135,255);
        }

        //clamp the given values
        minDelay = Math.max (0, minDelay);
        maxDelay = Math.max (0, maxDelay);

        minLeds = Clamp (minLeds, 0, device.getConfiguration ().getNumOfLeds ());
        maxLeds = Clamp (maxLeds, minLeds, device.getConfiguration ().getNumOfLeds ());

        Random rnd = new Random ( );
        //generate a random number for the amount of LEDs
        int numOfLeds = rnd.nextInt (maxLeds - minLeds + 1) + minLeds;

        minOffset = Clamp (minOffset, 0, device.getConfiguration ().getNumOfLeds () - numOfLeds);
        maxOffset = Clamp (maxOffset, minOffset, device.getConfiguration ().getNumOfLeds () - numOfLeds);

        int offset = rnd.nextInt (maxOffset - minOffset + 1) + minOffset;

        minFlashes = Math.max (0, minFlashes);
        maxFlashes = Math.max (minFlashes, maxFlashes);

        int flashes = rnd.nextInt (maxFlashes - minFlashes + 1) + minFlashes;

        minDelay = Math.max (0, minDelay);
        maxDelay = Math.max (minDelay, maxDelay);



        minFlashLength = Math.max (0, minFlashLength);
        maxFlashLength = Math.max (minFlashLength, maxFlashLength);

        //execute the flashes
        for(int i = 0; i < flashes; i++)
        {
            int flashDelay = rnd.nextInt (maxFlashLength - minFlashLength + 1) + minFlashLength;
            long delay = rnd.nextInt (maxDelay - minDelay + 1) + minDelay;
            Flash (color,flashDelay, numOfLeds, offset, device);

            //delay the next flash
            //check if this is the last flash
            if(i != flashes - 1)
            {
                //subtract the time needed for sending from the delay and make sure it stays positive
                delay = Math.max (0, delay - device.getRttMS ());
                //this is not the last flash;
                try
                {
                    //delay the next flash
                    Thread.sleep (delay);
                }
                catch (InterruptedException e)
                {
                    //sleep was interrupted
                    e.printStackTrace ( );
                }
            }

        }

        System.out.println ( "Flashed: " + flashes +" times (LEDs: " + numOfLeds + " offset: " + offset +  ")");

    }

    public static LED[] Waves(int i )
    {
        int startPos = 10;
        int radius = 5;
        LED color = new LED ( 255,0,0);
        int length = 10;

        color = DimColor (color, 255- i);

        return Combine (Shine ( startPos + i, radius, color, length), Shine ( startPos - i, radius, color, length));
    }

    public static LED[] LinearShine(int position, int radius, LED color, int numOfLeds)
    {
        LED[] effect = new LED[numOfLeds];


        /*
         *  linear brightness falloff
         */
        for(int i = 0; i < radius; i++)
        {
            effect[position + i] = DimColor (color, ((radius - i) * 255)/ radius);
            effect[position  - i] = DimColor (color, ((radius - i) * 255)/ radius);
        }

        return effect;
    }

    /**
     * function creating a visual shining effect for an LED at the given position
     * @param position the position of the shining LED in the strip
     * @param radius the radius of the effect
     * @param color the color of the effect
     * @param length the length of the resulting array
     * @return an array of LEDs containing the effect
     */
    public static LED[] Shine(int position, int radius, LED color, int length)
    {
        if(radius == 0)
        {
            return null;
        }

        LED[] effect = new LED[length];

        /*
         *  quadratic brightness falloff
         */
        for(int i = 0; i < length; i++)
        {
            int offset = i - position;
            int brightness =  -(255/(radius * radius)) * offset * offset + 255;
            brightness = Clamp (brightness, 0, 255);
            effect[i] =  DimColor (color, brightness);
        }

        return effect;
    }

    /**
     * function combining the given led arrays by mixing their colors
     * @param leds the arrays of LEDs to combine
     * @return the combined LED array
     */
    public static LED[] Combine(LED[]... leds )
    {
        // get the size of the largest array
        int size = 0;
        for(int i = 0; i < leds.length; i++)
        {
            if(leds[i] != null)
            {
                size = Math.max (size, leds[i].length);
            }
        }

        //create an array with the maximum required size
        LED[] result = new LED[size];

        //combine all arrays
        for(int i = 0; i < leds.length; i++)
        {
            if(leds[i] == null)
            {
                continue;
            }
            for(int j = 0; j < leds[i].length; j++)
            {
                result[j] = Combine (result[j], leds[i][j]);
            }
        }

        return result;
    }

    /**
     * function combining the given LEDs by adding their colors
     * @param led1 the first LED to mix
     * @param led2 the second LED to mix
     * @return the resulting LED
     */
    public static LED Combine(LED led1, LED led2)
    {
        if(led1 == null)
        {
            led1 = new LED (0,0,0 );
        }
        if(led2 == null)
        {
            led2 = new LED (0,0,0 );
        }

        int newRed = Math.min((led1.getRed () + led2.getRed ()), 255);
        int newGreen = Math.min((led1.getGreen () + led2.getGreen ()), 255);
        int newBlue = Math.min((led1.getBlue () + led2.getBlue ()), 255);

        return new LED ( newRed, newGreen, newBlue);
    }


    /**
     * function clamping the given number in between the given min and max value
     * @param number the number to be clamped
     * @param min the min value which should not be exceeded
     * @param max the max value which should not be exceeded
     * @return the clamped value, in between min and max
     */
    private static int Clamp(int number, int min, int max)
    {
            return Math.max( Math.min (number,max), min);
    }

    /**
     * function dimming the given color to the given brightness
     * @param color the color which should be dimmed
     * @param brightness the brightness to which the color should be dimmed to.
     *                   Can range from 0 to 255
     * @return
     */
    public static LED DimColor(LED color, int brightness)
    {
        /*
        TODO: maybe make DimColor a function of the LED class
         */
        brightness = Clamp (brightness, 0, 255);
        return new LED(Math.round ((float) color.getRed () / 255f * brightness), Math.round ((float) color.getGreen () / 255f * brightness), Math.round ((float) color.getBlue () / 255f * brightness));
    }


}
