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
 * class representing a data frame header according to the ALUP v. 0.1 (internal)
 *@version 0.1 (internal)
 */
public class Header
{
    //the size of the frame body in bytes; has to be a positive value
    private int bodySize;

    //the offset as a number of LEDs of the frame body
    private int bodyOffset;

    //the command Byte stored as a short because java does not support unsigned values
    //range: 0-255
    private short commandByte;



    /**
     * default constructor initializing the header with the following default values
     * bodySize: 0
     * bodyOffset: 0
     * commandByte: COMMAND_NONE
     */
    public Header()
    {
        bodySize = 0;
        bodyOffset = 0;

        //TODO: add this to the implementation docs
        //set the command byte to "clear" so that all unchanged LEDs get set to black
        // if you don't want to set the unmodified LEDs to black, call setClear(false);

        commandByte = Constants.COMMAND_CLEAR;
    }

    /**
     * specialized constructor initializing the header with the given values
     * @param bodySize the value used to initialize the body size; has to be a positive number
     * @param offset the value used to initialize the body offset; has to be a positive number
     * @param commandByte the value used to initialize the command byte; has to be within a range of 0-255
     * @throws IllegalArgumentException the given commandByte value is not within a range of 0-255
     *                                                                          the given offset is < 0
     *                                                                          the given bodySize is < 0
     *                                                                          the given body size is not a multiple of 3
     */
    public Header(int bodySize, int offset, short commandByte)
    {
        //set the values given
        setBodySize (bodySize);
        setCommandByte (commandByte);
        setOffset(offset);
    }


    public int getOffset ( )
    {
        return bodyOffset;
    }

    public int getBodySize ( )
    {
        return bodySize;
    }

    public short getCommandByte ( )
    {
        return commandByte;
    }


    /**
     * function serializing this header by converting it to an array of unsigned bytes according to the ALUP v. 0.1 (internal) header format
     * @return the serialized header
     * Note: an array type of short is needed because Java does not support unsigned values.
     */
    public short[] serialize()
    {
        short[] result = new short[9];
        //convert and insert the body size into the bytes 0-3
        byte[] bodySizeBytes = Convert.IntToBytes (getBodySize ());
        byte[] bodyOffsetBytes = Convert.IntToBytes (getOffset ());

        //convert the bytes into unsigned short values and insert them into the short array
        result[0] = (short) Byte.toUnsignedInt ( bodySizeBytes[0]);
        result[1] = (short) Byte.toUnsignedInt ( bodySizeBytes[1]);
        result[2] = (short) Byte.toUnsignedInt ( bodySizeBytes[2]);
        result[3] = (short) Byte.toUnsignedInt ( bodySizeBytes[3]);

        result[4] = (short) Byte.toUnsignedInt ( bodyOffsetBytes[0]);
        result[5] = (short) Byte.toUnsignedInt ( bodyOffsetBytes[1]);
        result[6] = (short) Byte.toUnsignedInt ( bodyOffsetBytes[2]);
        result[7] = (short) Byte.toUnsignedInt ( bodyOffsetBytes[3]);
        //set the last byte to the command byte
        result[8] = getCommandByte ();

        return  result;
    }


    /**
     * setter for the body offset
     * @param bodyOffset the value to set the body offset to; has to be a positive value
     * @throws IllegalArgumentException the given bodySize is < 0
     */
    public void setOffset (int bodyOffset)
    {
        /*
        this check can be removed as the offset gets clamped when sending
        if(bodyOffset < 0)
        {
            throw new IllegalArgumentException ( "The given bodyOffset with a value of " + bodyOffset + " is < 0");
        }*/
        this.bodyOffset = bodyOffset;
    }

    /**
     * setter for the body size
     * @param bodySize the value to set the body size to; has to be a positive value and a multiple of 3
     * @throws IllegalArgumentException the given bodySize is < 0 or the given body size is not a multiple of 3
     */
    public void setBodySize (int bodySize)
    {
        if(bodySize < 0)
        {
            throw new IllegalArgumentException ( "The given bodySize with a value of " + bodySize + " is < 0");
        }

        if(bodySize % 3 != 0)
        {
            throw new IllegalArgumentException ( "The given bodySize with a value of " + bodySize + " is not a multiple of 3");
        }
        this.bodySize = bodySize;
    }

    /**
     * setter for the command byte
     * @param commandByte the value to set the commandByte to; has to be within a range of 0-255
     * @throws IllegalArgumentException the given value is not within a range of 0-255
     */
    public void setCommandByte (short commandByte)
    {
        //check if the given value is within the size of a byte
        if(commandByte < 0 || commandByte > 255)
        {
            throw new IllegalArgumentException ( "The given commandByte of " + commandByte + " is not within the boundaries of  0-255");
        }
        this.commandByte = commandByte;
    }

    /**
     * setter for the command byte, overloading seCommandByte(short)
     * @param commandByte the value to set the commandByte to; has to be within a range of 0-255
     * @throws IllegalArgumentException the given value is not within a range of 0-255
     */
    public void setCommandByte (int commandByte)
    {
        //check if the given value is within the size of a byte
        //Note: this check has to be done here too, because java casting just cuts off the upper bytes of an integer
        //therefore a value higher than Short.MAX_VALUE could cause issues if not checked
        if(commandByte < 0 || commandByte > 255)
        {
            throw new IllegalArgumentException ( "The given commandByte of " + commandByte + " is not within the boundaries of  0-255");
        }
        setCommandByte ((short) commandByte);
    }
    @Override
    public String toString ( )
    {
        return "Header{" +
                "bodySize=" + bodySize +
                ", commandByte=" + commandByte +
                '}';
    }

}
