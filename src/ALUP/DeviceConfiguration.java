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
 * class containing configuration values for a device
 * @version 0.1 (internal)
 */
public class DeviceConfiguration
{
    //the protocol version used by the connected device
    private final String protocolVersion;

    //the name of the connected device
    private final String deviceName;

    //the number of Leds connected to this device; -1 if the device is not connected
    private final int numOfLeds;

    //the arduino digital pin on which the data signal gets outputted
    //a positive number within the range of arduino digital pins
    private final int dataPin;

    //the arduino digital pin on which the clock signal gets outputted if used by the LED strip
    //a positive number within the range of arduino digital pins. This value may be unused depending on the LED strip.
    private final int clockPin;

    //a string of extra values which can be used by any developer to configure his software
    private final String extraValues;


    public int getNumOfLeds ( )
    {
        return numOfLeds;
    }

    public int getDataPin ( )
    {
        return dataPin;
    }

    public int getClockPin ( )
    {
        return clockPin;
    }

    public String getProtocolVersion ( )
    {
        return protocolVersion;
    }

    public String getDeviceName ( )
    {
        return deviceName;
    }

    private String getExtraValues ( )
    {
        return extraValues;
    }

    /**
     * default constructor of this class
     * @param protocolVersion   the protocol version to initialize this class with; has to be a non-empty string
     * @param deviceName the device name  to initialize this class with
     * @param numOfLeds the number of leds  to initialize this class with; has to be > 0
     * @param dataPin the data pin  to initialize this class with; has to be >= 0
     * @param clockPin the clock pin to initialize this class with; has to be >= 0
     * @param extraValues the extra configuration values to initialize this class with
     * @throws IllegalArgumentException one of the given arguments was invalid according to the ALUP Configuration Values
     */
    public DeviceConfiguration(String protocolVersion, String deviceName, int numOfLeds, int dataPin, int clockPin, String extraValues) throws IllegalArgumentException
    {
        //check if the given configuration values are valid
        if (protocolVersion.length ( ) == 0)
        {
            throw new IllegalArgumentException ("Configuration could not be applied: Illegal Argument given: Protocol version: \'" + protocolVersion + "\'");
        }
        if (numOfLeds <= 0)
        {
            throw new IllegalArgumentException ("Configuration could not be applied: Illegal Argument given: numOfLeds: \'" + numOfLeds + "\'");
        }
        else if (dataPin < 0)
        {
            throw new IllegalArgumentException ("Configuration could not be applied: Illegal Argument given: dataPin: \'" + dataPin + "\'" );
        }
        else if (clockPin < 0)
        {
            throw new IllegalArgumentException ("Configuration could not be applied: Illegal Argument given: clockPin: \'" + clockPin + "\'");
        }

        this.protocolVersion = protocolVersion;
        this.deviceName = deviceName;
        this.numOfLeds = numOfLeds;
        this.dataPin = dataPin;
        this.clockPin = clockPin;
        this.extraValues = extraValues;
    }

    @Override
    public String toString ( )
    {
        return "DeviceConfiguration{" +
                "protocolVersion='" + protocolVersion + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", numOfLeds=" + numOfLeds +
                ", dataPin=" + dataPin +
                ", clockPin=" + clockPin +
                ", extraValues='" + extraValues + '\'' +
                '}';
    }
}
