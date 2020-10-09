package test;

import org.junit.jupiter.api.Test;

import ALUP.LED;

import static org.junit.jupiter.api.Assertions.*;

class LEDTest
{

    @Test
    void serializeArray ( )
    {
        LED[] leds = new LED[0];
        assertArrayEquals(new short[]{} ,LED.serializeArray (leds));

        leds = new LED[1];
        leds[0] = new LED ( 0,0,0);
        assertArrayEquals(new short[]{0,0,0},  LED.serializeArray (leds));

        leds[0] = new LED ( 1,2,3);
        assertArrayEquals(new short[]{1,2,3},  LED.serializeArray (leds));

        leds[0] = new LED(255, 255, 255);
        assertArrayEquals(new short[]{255, 255, 255},  LED.serializeArray (leds));

        leds = new LED[2];
        leds[0] = new LED ( 125,125,125);
        assertArrayEquals(new short[]{125, 125, 125, 0, 0, 0},  LED.serializeArray (leds));

        leds = new LED[2];
        leds[0] = new LED ( 125,125,125);
        leds[1] = new LED ( 125,125,125);
        assertArrayEquals(new short[]{125, 125, 125, 125, 125, 125},  LED.serializeArray (leds));

        assertThrows (IllegalArgumentException.class, () -> {new LED(256, 256, 256);});
        assertThrows (IllegalArgumentException.class, () -> {new LED(-1, -1, -1);});
        assertThrows (IllegalArgumentException.class, () -> {new LED(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);});

        assertThrows (IllegalArgumentException.class, () -> {new LED((short) 256, (short) 256, (short) 256);});
        assertThrows (IllegalArgumentException.class, () -> {new LED((short) -1, (short) -1, (short) -1);});
        assertThrows (IllegalArgumentException.class, () -> {new LED(Short.MAX_VALUE, Short.MAX_VALUE, Short.MAX_VALUE);});




    }
}