package ALUP;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;

/**
 * class representing a LED with a pair of 3 bytes for each color channel
 *@version 0.1 (internal)
 */
public class LED
{
    /**
     * the RGB values get stored as signed 16bit integer values internally, even though they represent unsigned byte values
     *when sent, they get converted to unsigned byte values and therefore have to have a value within 0-255
     */

    private short red;
    private short green;
    private  short blue;


    public short getRed ( )
    {
        return  red ;
    }

    public short getGreen ( )
    {
        return green;
    }

    public short getBlue ( )
    {
        return blue;
    }

    /**
     * setter for the red value of this LED
     * @param red the red value to set; has to be within a range of 0-255
     * @throws IllegalArgumentException the given value is not within a range of 0-255
     */
    public void setRed (short red)
    {
        //check if the given value is within the size of a byte
        if(red < 0 || red > 255)
        {
            throw new IllegalArgumentException ( "The given red value of " + red + " is not within the boundaries of  0-255");
        }
        //convert the integer value to a byte value
        this.red = red;
    }

    /**
     * setter for the green value of this LED
     * @param green the green value to set; has to be within a range of 0-255
     * @throws IllegalArgumentException the given value is not within a range of 0-255
     */
    public void setGreen (short  green)
    {
        //check if the given value is within the size of a byte
        if(green < 0 || green > 255)
        {
            throw new IllegalArgumentException ( "The given green value of " + green + " is not within the boundaries of  0-255");
        }
        //convert the integer value to a byte value
        this.green =  green;
    }


    /**
     * setter for the blue value of this LED
     * @param blue the blue value to set; has to be within a range of 0-255
     * @throws IllegalArgumentException the given value is not within a range of 0-255
     */
    public void setBlue (short blue)
    {
        //check if the given value is within the size of a byte
        if(blue < 0 || blue > 255)
        {
            throw new IllegalArgumentException ( "The given blue value of " + blue + " is not within the boundaries of  0-255");
        }
        this.blue = blue;
    }


    /**
     * setter for the red value of this LED
     * @param red the red value to set; has to be within a range of 0-255
     * @throws IllegalArgumentException the given value is not within a range of 0-255
     */
    public void setRed (int red)
    {
        //check if the given value is within the size of a byte
        if(red < 0 || red > 255)
        {
            throw new IllegalArgumentException ( "The given red value of " + red + " is not within the boundaries of  0-255");
        }
        //convert the integer value to a byte value
        this.red = (short) red;
    }

    /**
     * setter for the green value of this LED
     * @param green the green value to set; has to be within a range of 0-255
     * @throws IllegalArgumentException the given value is not within a range of 0-255
     */
    public void setGreen (int  green)
    {
        //check if the given value is within the size of a byte
        if(green < 0 || green > 255)
        {
            throw new IllegalArgumentException ( "The given green value of " + green + " is not within the boundaries of  0-255");
        }
        //convert the integer value to a byte value
        this.green =  (short)  green;
    }


    /**
     * setter for the blue value of this LED
     * @param blue the blue value to set; has to be within a range of 0-255
     * @throws IllegalArgumentException the given value is not within a range of 0-255
     */
    public void setBlue (int blue)
    {
        //check if the given value is within the size of a byte
        if(blue < 0 || blue > 255)
        {
            throw new IllegalArgumentException ( "The given blue value of " + blue + " is not within the boundaries of  0-255");
        }
        this.blue = (short)  blue;
    }



    /**
     * default constructor initializing this LED object wit values of 0
     */
    public LED()
    {
        //set the RGB values to 0
        setRGB ((short) 0, (short) 0, (short) 0);
    }


    /**
     * specialized constructor initializing this object with the given RGB values
     * @param r the initial red value of this LED; has to be within a range of 0-255
     * @param g the initial green value of this LED; has to be within a range of 0-255
     * @param b the initial blue value of this LED; has to be within a range of 0-255
     * @throws IllegalArgumentException one or more of the given values was not within a range of 0-255
     */
    public LED(short r, short g, short b)
    {
        setRGB (r,g,b);
    }

    /**
     * specialized constructor initializing this object with the given RGB values
     * @param r the initial red value of this LED; has to be within a range of 0-255
     * @param g the initial green value of this LED; has to be within a range of 0-255
     * @param b the initial blue value of this LED; has to be within a range of 0-255
     * @throws IllegalArgumentException one or more of the given values was not within a range of 0-255
     */
    public LED(int r, int g, int b)
    {
        setRGB (r,g,b);
    }



    @Override
    public String toString ( )
    {
        return "LED{" +
                "red=" + red +
                ", green=" + green +
                ", blue=" + blue +
                '}';
    }

    /**
     * function setting the RGB values of this LED
     * @param r the red value to be set to this LED; has to be within a range of 0-255
     * @param g the green value to be set to this LED; has to be within a range of 0-255
     * @param b the blue value to be set to this LED; has to be within a range of 0-255
     * @throws IllegalArgumentException one or more of the given values was not within a range of 0-255
     */
    public void setRGB(short r, short g, short b)
    {
        setRed (r);
        setGreen (g);
        setBlue (b);
    }



    /**
     * function setting the RGB values of this LED, overloading setRGB(short, short, short)
     * @param r the red value to be set to this LED; has to be within a range of 0-255
     * @param g the green value to be set to this LED; has to be within a range of 0-255
     * @param b the blue value to be set to this LED; has to be within a range of 0-255
     * @throws IllegalArgumentException one or more of the given values was not within a range of 0-255
     */
    public void setRGB(int r, int g, int b)
    {
        setRed (r);
        setGreen (g);
        setBlue (b);
    }


    /**
     * function converting the content of this object to an array of unsigned bytes according to the ALUP v. 0.1 (internal)
     * @return an array containing the contents of this object in a unsigned byte representation
     * Note: a data type of short needs to be used to represent unsigend byte values because Java does not support unsigned values
     */
    public short[] toUnsignedByte()
    {
        return new short[] {red, green, blue};
    }



    /**
     * function serializing the given array by converting the given LED array to an array of unsigned bytes according to the ALUP v. 0.1 (internal) Frame body format
     * @param leds the led array to serialize; has to be non-null
     * @return the serialized led array
     * Note: an array type of short is needed because Java does not support unsigned values.
     */
    public static short[] serializeArray(LED[] leds)
    {
        //create an array with a short value for each RGB value
        ArrayList<Short> unsignedBytes = new ArrayList<> ( );

        for(int i = 0; i < leds.length; i++)
        {
            //check if the current LED is null
            if(leds[i] == null)
            {
                //initialize it with 0 values
                leds[i] = new LED ( 0,0,0);
            }

            short[] currentLed = leds[i].toUnsignedByte ();
            unsignedBytes.add (currentLed[0]);
            unsignedBytes.add (currentLed[1]);
            unsignedBytes.add (currentLed[2]);
        }

        //convert the arrayList to a short array and return it
        Short[] result = new Short[unsignedBytes.size ()];
        unsignedBytes.toArray ( result);
        return  ArrayUtils.toPrimitive ( result);
    }
}
