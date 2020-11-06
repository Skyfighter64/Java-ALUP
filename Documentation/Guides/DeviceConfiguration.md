# Device Configuration
The 'device configuration' contains the configuration data transmitted when connecting to a device.

 <br/>
 <br/>

## Obtaining the Configuration
The configuration of a specific device (any subclass of 'Device') can be obtained using:
 ```java
 DeviceConfiguration myConfiguration = myDevice.getConfiguration()
```

- If the device is connected, the `DeviceConfiguration` object will be returned.
- If the device is not connected, `null` will be returned.

<br/>

:information_source: Useful guides:
 - [Check if a device is connected ](/Documentation/Guides/Devices.md#checking-connection-state)
 - [How to connect to a device.](/Documentation/Guides/Devices.md#connecting-a-device "Device Documentation")
 
 <br/>
 <br/>
 
 ## Configuration Values
 
 A `DeviceConfiguration` objects contains the following configuration values:
 
 Name | Range | Description
 --- |:---:| ---
 numOfLeds | \>= 0 | The number of LEDs of the addressable LED strip connected to the slave device.
 dataPin | \>= 0 | The data pin at which the addressable LED strip is connected to the slave device.
 clockPin | \>= 0 |  The clock pin at which the addressable LED strip is connected to the slave device. <br/> <br/> This value may be unused depending on the LED strip. In this case, it can be any number >=0.
protocolVersion | Any String value | The protocol version in String representation.
deviceName | Any String value | The name of the Device. This name my not be unique.
extraValues | Any String value | Any extra configuration data. Can be ignored if not stated otherwise.

<br/>
<br/>

#### Obtaining Configuration Values

Each [configuration value](#configuration-values) has it's own  'getter function' to obtain it's value:

```java
//get the number of LEDs
int myNumberOfLeds = myConfiguration.getNumOfLeds();

//get the data pin
int myDataPin = myConfiguration.getDataPin();

//get the clock pin
int myClockPin = myConfiguration.getClockPin();

//get the protocol version
String myProtocolVersion = myConfiguration.getProtocolVersion();

//get the device name
String myDeviceName = myConfiguration.getDeviceName();

//get the extra values
String myExtraValues = myConfiguration.getExtraValues();
```

<br/>
<br/>

#### Changing Configuration Values

Configuration values can't be changed using this library, because they are defined as constant inside the code of the slave device.

You can change the configuration values by editing the code of your slave device implementation.
See it's documentation for more info on how to do it. 