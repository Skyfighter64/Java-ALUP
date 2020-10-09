package ALUP;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeviceTest
{

    @Test
    void simpleSetOffset ( )
    {
    }

    @Test
    void fitArrayLength ( )
    {
        assertEquals (0, Device.FitArrayLength (10, 3, -4));
        assertEquals (0, Device.FitArrayLength (10, 3, -3));
        assertEquals (1, Device.FitArrayLength (10, 3, -2));
        assertEquals (2, Device.FitArrayLength (10, 3, -1));
        assertEquals (3, Device.FitArrayLength (10, 3, 0));
        assertEquals (3, Device.FitArrayLength (10, 3, 7));
        assertEquals (2, Device.FitArrayLength (10, 3,  8));
        assertEquals (1, Device.FitArrayLength (10, 3, 9));
        assertEquals (0, Device.FitArrayLength (10, 3, 10));
        assertEquals (0, Device.FitArrayLength (10, 3, 11));
    }

    @Test
    void clampOffset ( )
    {
        assertEquals (0, Device.ClampOffset (-2, 10));
        assertEquals (0, Device.ClampOffset (-1, 10));
        assertEquals (0, Device.ClampOffset (0, 10));
        assertEquals (1, Device.ClampOffset (1, 10));
        assertEquals (2, Device.ClampOffset (2, 10));
        assertEquals (8, Device.ClampOffset (8, 10));
        assertEquals (9, Device.ClampOffset (9, 10));
        assertEquals (10, Device.ClampOffset (10, 10));
        assertEquals (10, Device.ClampOffset (11, 10));
    }
}