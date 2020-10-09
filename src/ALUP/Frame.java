package ALUP;

import java.util.Arrays;

/**
 * class representing a data frame according to the ALUP v. 0.1 (internal)
 * @version 0.1 (internal)
 */
public class Frame
{
    // frame header; guaranteed to be non-null
    Header header;

    //led values of the frame body
    LED[] leds;

    public Header getHeader ( )
    {
        return header;
    }

    /**
     * setter for the header variable
     * @param header the header instance to set to; has to be non-null
     * @throws NullPointerException the given header is null
     */
    public void setHeader (Header header)
    {
        if(header == null)
        {
            throw new NullPointerException ( "the given Header is null");
        }
        this.header = header;
    }

    public LED[] getLeds ( )
    {
        return leds;
    }

    /**
     * setter for the LEDs array; automatically setting the bodySize for the header
     * @param leds the array of LEDs to set to; has to be non-null
     * @throws NullPointerException the given LED array is null
     */
    public void setLeds (LED[] leds)
    {
        if(leds == null)
        {
            throw new NullPointerException ( "the given LED array is null");
        }

        //check each individual led if it is null
        for(int i = 0; i < leds.length; i++)
        {
            //check if the current led is null
            if(leds[i] == null)
            {
                //the current LED is null, initialize it with zero values
                leds[i] = new LED ( 0,0,0);
            }
        }

        this.leds = leds;
        // set the body size to the length of the array multiplied by 3
        header.setBodySize (leds.length * 3);
    }

    /**
     * default constructor
     */
    public Frame()
    {
        //set the header to an instance of the default header
        this.header = new Header ( );
        //set the LED array to an array with a length of 0
        this.leds = new LED[]{};
    }


    /**
     * specialized constructor initializing the Frame using the given LED array and a default header
     * @param leds the LEDs to initialize this Frame with; has to be non-null
     * @throws NullPointerException the given LED array is null
     */
    public Frame(LED[] leds)
    {
        //set the header to an instance of the default header
        this.header = new Header ( );

        //set the leds, automatically adjusting the body size inside the header
        setLeds (leds);
    }


    /**
     * specialized constructor initializing the Frame using the given LED array and a default header
     * @param leds the LEDs to initialize this Frame with; has to be non-null
     * @param subProgramID the id of the subprogram to be executed on the receiver when it gets received; has to be within a range of 0-247
     * @throws NullPointerException the given LED array is null
     * @throws IllegalArgumentException the given subCommandID is not within a range of 0-247
     */
    public Frame(LED[] leds, int subProgramID)
    {
        //set the header to an instance of the default header
        this.header = new Header ();
        //set the subCommand to the header
        setSubcommand (subProgramID);

        //set the leds, automatically adjusting the body size inside the header
        setLeds (leds);
    }

    /**
     * specialized constructor initializing the Frame using the given LED array and a default header
     * @param subProgramID the id of the subprogram to be executed on the receiver when it gets received; has to be within a range of 0-247
     * @throws IllegalArgumentException the given subCommandID is not within a range of 0-247
     */
    public Frame(int subProgramID)
    {
        //set the header to an instance of the default header
        this.header = new Header ();
        //set the subCommand to the header
        setSubcommand (subProgramID);

        //set the LED array to an array with a length of 0
        this.leds = new LED[]{};
    }


    /**
     * function setting the offset of the led data of this frame
     * @param offset the number of LEDs of which this frame should be offset; has to be positive and, combined with the frame length, smaller than the number of LEDs connected to the device
     * @throws IllegalArgumentException the given offset is < 0
     *
     * NOTE: the upper boundary of the offset will be validated when sending the data to a device.
     */
    public void setOffset(int offset)
    {
        //set the offset into the header of this frame
        header.setOffset ( offset);
    }

    /**
     * function setting the protocol command of this frame, overriding any other command
     * @param id the id of the protocol command to be executed; has to be within the range of 0-7
     *           Note: use the Protocol commands defined in Constants.java for easier use
     */
    public void setProtocolCommand(int id)
    {
        //check if the given ID is within the valid range
        if(id < 0 || id >= 8)
        {
            throw new IllegalArgumentException("The given Subcommand ID of " + id +" is invalid");
        }

        //set the command byte to the byte of the corresponding subcommands with the needed offset
        header.setCommandByte ( (short) (id));
    }

    /**
     * function setting the subcommand which should be executed when this frame gets received on the receiver, overriding any other command
     * @param id the id of the subprogram to be executed; has to be within the range of 0-247
     * @throws IllegalArgumentException the given id is not within a range of 0-247
     */
    public void setSubcommand(int id)
    {
        //check if the given ID is within the valid range
        if(id < 0 || id > 255 - Constants.SUBCOMMAND_OFFSET)
        {
            throw new IllegalArgumentException("The given Subcommand ID of " + id +" is invalid");
        }

        //set the command byte to the byte of the corresponding subcommands with the needed offset
        header.setCommandByte ( (short) (id + Constants.SUBCOMMAND_OFFSET));
    }

    @Override
    public String toString ( )
    {
        return "Frame{" +
                "header=" + header.toString () +
                ", leds=" + Arrays.toString (leds) +
                '}';
    }
}
