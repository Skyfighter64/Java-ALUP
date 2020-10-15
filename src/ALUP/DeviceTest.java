package ALUP;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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