package test;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Test;

import ALUP.Header;

import static org.junit.jupiter.api.Assertions.*;

class HeaderTest
{

    @Test
    void serialize ( )
    {
        Header header = new Header ();

        //test the body size value
        assertArrayEquals ( new short[]{0, 0, 0, 0, 0, 0, 0, 0, 1}, header.serialize ());
        header.setBodySize (3);
        assertArrayEquals ( new short[]{0, 0, 0, 3, 0, 0, 0, 0, 1}, header.serialize ());
        header.setBodySize (67305993);
        assertArrayEquals ( new short[]{4, 3, 2, 9, 0, 0, 0, 0, 1}, header.serialize ());
        assertThrows (IllegalArgumentException.class, () -> header.setBodySize (1));


        //test the command value
        header.setBodySize (0);
        header.setCommandByte ((short) 1);
        assertArrayEquals ( new short[]{0, 0, 0, 0, 0, 0, 0, 0, 1}, header.serialize ());
        header.setCommandByte ((short) 255);
        assertArrayEquals ( new short[]{0, 0, 0, 0, 0, 0, 0, 0, 255}, header.serialize ());

        //test the overloaded function
        header.setBodySize (0);
        header.setCommandByte (1);
        assertArrayEquals ( new short[]{0, 0, 0, 0, 0, 0, 0, 0, 1}, header.serialize ());
        header.setCommandByte ( 255);
        assertArrayEquals ( new short[]{0, 0, 0, 0, 0, 0, 0, 0, 255}, header.serialize ());


        //test the offset value
        header.setBodySize (0);
        header.setCommandByte (0);
        header.setOffset (1);
        assertArrayEquals ( new short[]{0, 0, 0, 0, 0, 0, 0, 1, 0}, header.serialize ());
        header.setOffset (Integer.MAX_VALUE);
        assertArrayEquals ( new short[]{0, 0, 0, 0, 127, 255, 255, 255, 0}, header.serialize ());


    }

    @Test
    void setCommandByte()
    {
        Header header = new Header ();

        assertThrows (IllegalArgumentException.class, () -> {header.setCommandByte ((short) 256);});
        assertThrows (IllegalArgumentException.class, () -> {header.setCommandByte ((short) -1);});
        assertThrows (IllegalArgumentException.class, () -> {header.setCommandByte (256);});
        assertThrows (IllegalArgumentException.class, () -> {header.setCommandByte (-1);});
        assertThrows (IllegalArgumentException.class, () -> {header.setCommandByte (Short.MAX_VALUE + 1);});
    }
}