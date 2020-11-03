package test;

import org.junit.jupiter.api.Test;

import ALUP.*;

import static org.junit.jupiter.api.Assertions.*;

class EffectsTest
{

    @Test
    void combine ( )
    {
        assertEquals (new LED ( 255, 255, 0), Effects.Combine (new LED ( 255,0,0), new LED ( 0,255,0)));
        assertEquals (new LED ( 255, 255, 255), Effects.Combine (new LED ( 255,0,0), new LED ( 0,255,255)));
        assertEquals (new LED ( 0, 0, 0), Effects.Combine (new LED ( 0,0,0), new LED ( 0,0,0)));

        assertEquals (new LED ( 150, 150, 150), Effects.Combine (new LED ( 25,25,25), new LED ( 125,125,125)));
        assertEquals (new LED ( 150, 150, 150), Effects.Combine (new LED ( 125,125,125), new LED ( 25,25,25)));
        assertEquals (new LED ( 57, 66, 88), Effects.Combine (new LED ( 12,54,24), new LED ( 45,12,64)));
    }

}