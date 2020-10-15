package ALUP;

import java.nio.ByteBuffer;

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
 * class hosting static methods used for conversion
 */
public  class Convert
{
    /**
     * function converting an array of bytes to an integer number
     * @param bytes an array containing 4 bytes which get converted to a number
     * @return the number to which the given bytes were converted
     */
    public static int BytesToInt(byte[] bytes)
    {
        ByteBuffer buffer = ByteBuffer.wrap (bytes);

        return  buffer.getInt ();
    }

    /**
     * function converting the given integer value to an array of bytes
     * @param i the integer value to convert
     * @return the converted array of bytes; has a length of 4
     */
    public static byte[] IntToBytes(int i)
    {
        //convert the given integer to an array of bytes
        return ByteBuffer.allocate (4).putInt (i).array ();
    }

}
