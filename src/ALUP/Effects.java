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



}
