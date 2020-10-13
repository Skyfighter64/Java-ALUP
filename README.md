# Java-ALUP
A Java implementation of the ALUP

## Table of Contents

This README includes the following points:

* Specifications
* Requirements
* Installation
* Configuration
* Usage
* Contributing
* Credits
* License


## Specifications

### Compatible Protocol Versions
  * ALUP v.0.1 

### Compatible Connection Types
  * USB (using Serial)


### Supported Operating Systems:


OS | Confirmed working | Comments
--------------- |:-----------------:| --------------------
Windows     | :heavy_check_mark: |  
Debian (Linux) | :heavy_check_mark: | Confirmed using Raspbian Buster
MacOS | :x: |
Android | :x: |


:information_source: In theory, all operating systems working with the [jSerialComm] library should work, but I am unable to test all of them. If you can confirm that one of the operating systems not listed as confirmed above is working, we'll add it to the list. 



### Supported LED strips

 * All individually addressable RGB LED strips supported by the used slave device.


### Protocol specific

#### Timeouts

Description| timeout in ms
--- | ---
Waiting for Connection Request | 10000
Waiting for Configuration Start | 10000
Waiting for Configuration Acknowledgement | 10000
Waiting for Frame Acknowledgement | 10000
Waiting for Frame Error | 10000


## Requirements
### Software:
* Java 9 or higher

### Hardware:
* USB Port, USB cable


## Installation

### DISCLAIMER

[add installation disclaimer here]


1. Download the latest library version from [here] (TODO: add link to releases)
2. Include the downloaded library in your project

## Configuration

This implementation does not need to be configured before being used.
[TODO: add configuration values such as baud, com port, etc]



### Subprograms


#### Implementation specific subprograms

A list of subprograms which need to be added to the slave device for this implementation to fully work:

* This implementation does not need any additional subprograms being added to the slave device in order to work


#### Adding your own subprograms

If you want to add your own subprograms, please follow the guide of your used slave device.

#### Calling your own subprograms

:information_source: Connect to the device using `yourDevice.Connect()` or `yourDevice.SimpleConnect()` if not already done.

1. Set the Subcommand by using `yourDevice.SetSubCommand( [subprogram id] )` and replace `[subprogram id]` with the ID of the subprogram you want to call.

2. Send the frame by using `yourDevice.Send()` or `yourDevice.SimpleSend()`

<br />

:information_source: The Subcommand is sent with the header of a Frame, which means, that you can send LED data alongside the subcommand (e.g. by using `yourDevice.SetLeds(LED[] leds)` before sending).

:warning: This function overrides any previously set subcommands and protocol commands, specifically `CLEAR`, which is set by default. Therefore, when sending LED data alongside the subcommand, you will experience the same effect as when calling `yourDevice.setClear(false)` before sending. For more information, see [setClear()] (TODO: add link to docs)

:warning: Some functions will override the ID of `yourDevice.SetSubcommand(int id)`. See [`Device.SetSubCommand()`] (TODO: add link to docs) for more info.



## Usage

You can use this library inside your java programs. To do so, see the [documentation] (TODO: add link to wiki).

### Examples:
 
 ```java
   public static void main(String[] args)
   {
      //set the serial port at which the slave device is connected
      String serialPortName = "COM5"; 
      SerialPort port = SerialPort.getCommPort (serialPortName);
      
      //the baud rate for serial communication
      //set the baud rate to the same value as set on the slave device
      int baud = 115200;
      
      //create a new device
      Device myDevice = new Device(port);
      
      //try to connect to the device
      myDevice.SimpleConnect(baud);
      
      //create LED data which should be sent
      LED[] leds = new LED[] { new LED(255, 0, 0), new LED(0, 255, 0), new LED(0, 0, 255)};
      
      //Send LED data
      myDevice.SimpleSend(leds);
      
      //disconnect the device
      myDevice.Disconnect();
         
   }
 
 ```


## Contributing

If you want to contribute to this project, please see CONTRIBUTING.md (TODO: add link)


## Credits

Libraries used:

* [jSerialComm]

* [Apache Commons Lang](https://commons.apache.org/proper/commons-lang/ "Apache Webpage")


## License

This project is licensed under [add license here]. For more information, see LICENSE (TODO: add link)

[jSerialComm]: https://github.com/Fazecast/jSerialComm "jSerialComm GitHub Page"
