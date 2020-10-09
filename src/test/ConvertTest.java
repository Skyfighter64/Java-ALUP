package test;

import org.junit.jupiter.api.Test;

import java.nio.BufferUnderflowException;

import ALUP.Constants;
import ALUP.Convert;

import static org.junit.jupiter.api.Assertions.*;

class ConvertTest
{

    @Test
    void bytesToInt ( )
    {
        assertEquals (0, Convert.BytesToInt (new byte[]{(byte) 0, (byte) 0, (byte) 0, (byte) 0}));
        assertEquals (1, Convert.BytesToInt (new byte[]{(byte) 0, (byte) 0, (byte) 0, (byte) 1}));
        assertEquals (-1, Convert.BytesToInt (new byte[]{(byte) 255, (byte) 255, (byte) 255, (byte) 255}));

        assertThrows (NullPointerException.class, () -> {Convert.BytesToInt (null);});
        assertThrows (BufferUnderflowException.class, () -> {Convert.BytesToInt (new byte[]{(byte) 0, (byte) 0, (byte) 0});});
    }

    @Test
    void intToBytes ( )
    {
        assertArrayEquals (new byte[]{(byte) 0, (byte) 0, (byte) 0, (byte) 0} , Convert.IntToBytes (0));
        assertArrayEquals (new byte[]{(byte) 0, (byte) 0, (byte) 0, (byte) 1} , Convert.IntToBytes (1));
        assertArrayEquals (new byte[]{(byte) 255, (byte) 255, (byte) 255, (byte) 255} , Convert.IntToBytes (-1));
    }
}