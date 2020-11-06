# Devices
This file gives an overview on how to use 'Devices' in this library.

A device represents a physical slave device, such as a microcontroller, with addressable LEDs connected to it.
Because this library supports multiple kinds of devices, there are different classes for each:

- [Device.java](/ALUP/Device.java "Device.java File")
- [SerialDevice.java](/ALUP/SerialDevice.java "SerialDevice.java File")
- [WifiDevice.java](/ALUP/WifiDevice.java "WifiDevice.java File")
    
The 'Device' class acts as a base for all devices. It contains functions and properties which all devices should have in common. This makes it easy to implement new Device types.
 'SerialDevice' and 'WifiDevice' each represent special types of devices.

<br/>



###WifiDevice
The 'WifDevice' class represents a device which will connect over a Network connection using TCP. 

Special properties of this device type are:

-  IP Address: The address of the physical slave device
-  TCP Port: The Port at which the slave device is listening for ALUP connections

<br/>



### SerialDevice
The 'SerialDevice' class represents a device which will connect over a Serial connection like USB. 

Special properties of this device type are:

-  Serial Port: The COM port at which the device is connected
-  Baud rate: The communication speed of the device

<br/>
<br/>




## Creating a device

To Create a device, use the constructor of the special device type you want to use.

This is the first step to set up a device.





##### Examples:

SerialDevice:

```java
//Creating a SerialDevice:

String comPort = "COM5";
int baudRate = 115200;

Device myDevice = new SerialDevice(comPort, baudRate);
```

<br/>

WifiDevice:
```java
//Creating a WifiDevice:

String ipAddress = "10.16.1.123";
int port = 1201;

Device myDevice = new WifiDevice(ipAddress, port);
```

:information_source: Note that the type of `myDevice` in both cases is `Device` and not `WifiDevice` or `SerialDevice`. This makes code using the `myDevice` variable usable for all 
types of devices.



<br/>
<br/>



## Connecting a device
This section is talking about connecting the software-side of things. You have to connect the hardware for this step.


There are two ways to connect to a Device: 

- `myDevice.connect();`
- `myDevice.simpleConnect()`

Both of them establish the connection to the device, if possible, and getting the device configuration.
After this step, you are able to read the configuration from the device by using
 `myDevice.getConfiguration()`, send data to the device, and disconnect from it.
 
 <br/>
 
 
##### Examples:

SimpleConnect:
```java
/* here is the code where you create 'myDevice'*/

//Connecting using simpleConnect()
myDevice.simpleConnect();
```

<br/> 

Connect:
```java
/* here is the code where you create 'myDevice'*/

//Connecting using connect()
try
{
    myDevice.connect();
}
catch(TimeoutException e)
{
    //the device timed out while trying to connect
    //Do stuff here
}
catch(IOException e)
{
    //Could not connect to the device
    //Do stuff here
}
```




<br/> 
<br/> 





## Checking Connection State
Sometimes it is useful to know, if a device is connected. To check for the connection state, use:

```java
CONNECTION_STATE state = myDevice.getConnectionState();
```
This will return a `CONNECTION_STATE`, which can be one of:

Name | Description
--- | ---
 `CONNECTION_STATE.CONNECTED` | Indicates that the device is fully connected.
` CONNECTION_STATE.DISCONNECTED` | Indicates that the device is not connected.


<br/> 
<br/> 

Alternatively, you can use:

```java
bool connected = myDevice.isConnected();
```
This will return `true`, if the device is connected and otherwise `false`.




<br/> 
<br/> 




## Device Configuration
When connecting to a device, configuration data gets transmitted. This data contains useful values and
 can be obtained using:

```java
DeviceConfiguration myConfiguration = myDevice.getConfiguration();
``` 

For a full guide on the DeviceConfiguration, see [here](/Documentation/Guides/DeviceConfiguration.md)




<br/>
<br/> 




## Sending data
There are multiple ways to send data over to the device:

- `myDevice.send();`
- `myDevice.send(LED[] leds)`
- `myDevice.send(LED[] leds, int offset)`

<br/>

Just like with `connect()`, there are also 'simple' versions of each function, which again, catch `TimeoutExceptions` and `IOExceptions` by printing them out to the console.

- `myDevice.simpleSend();`
- `myDevice.simpleSend(LED[] leds)`
- `myDevice.simpleSend(LED[] leds, int offset)`

<br/>

When using `myDevice.send()`, the following values currently set to `myDevice` get sent over to the slave device:

 - The current LED array, or nothing if not set
   - Can be set by using `myDevice.setLeds(...);`
    
- The current offset, or 0 if not set
    - Can be set by using `myDevice.setOFfset(...);`

- The current subcommand, or 1 if not set
    - Can be set by using `myDevice.setSubCommand(...);`    
    
   <br/>
    
When using `myDevice.send(LED[] leds)`, the same values get sent, __except__ that the currently set LED array gets overridden
by the LED array given as `leds` argument.     

<br/>

When using `myDevice.send(LED[] leds, int offset)`, the same values get sent, __except__ that the currently set LED array gets overridden
by the LED array given as `leds` argument __and__ the currently set offset gets overridden by the given `offset` argument.     
    
    
    TODO: add argument description, add what happens when they are too big/ too small, etc.
 ##### Examples
 
 Send:
 ```java
 /* here is the code where you create 'myDevice' and connect to it*/

//create an LED array which contains one LED with the color 'white'
LED[] leds = new LED[]{new LED(255,255,255)};

//set it to myDevice
myDevice.setLeds(leds);

//send it to myDevice
try
{
    myDevice.send();
}
catch(TimeoutException | IOException e)
{
    //an exception occurred
    //do stuff here
}
 ```

 <br/>

SimpleSend:
```java
 /* here is the code where you create 'myDevice' and connect to it*/

//create an LED array which contains one LED with te color 'white'
LED[] leds = new LED[]{new LED(255,255,255)};

//set it to myDevice
myDevice.setLeds(leds);

//send it to myDevice
myDevice.simpleSend();

 ```

<br/>

SimpleSend(LED[] leds):
```java
 /* here is the code where you create 'myDevice' and connect to it*/

//create an LED array which contains one LED with te color 'white'
LED[] leds = new LED[]{new LED(255,255,255)};

//set it and send it to myDevice
myDevice.simpleSend(leds);

 ```



<br/>
<br/>




## 'Simple' functions
 Some functions, like `myDevice.send()` have a  related __'simple'__ function, like `myDevice.simpleSend()`.
 
 Both functions do the same, except that __'simple'__ functions already catch `TimeoutExceptions` and `IOExceptions` by printing them using `System.out.println();`.
 
 This makes them easier to use with simple console applications. When writing more complex or non-console applications, it is 
 recommended to use `myDevice.connect()`.
 
 
 
 <br/>
 <br/>



## Subcommands

This section is not finished yet. For now, see [Subprograms](/Documentation/Guides/Subprograms.md "Subprograms Guide")


<br/>
<br/>


## Disconnecting

When you finished sending data to the slave device, you have to disconnect from the device.
This is done by using `myDevice.disconnect()`.

##### Example
```java
myDevice.disconnect();
```

 
 <br/>
 <br/>

 


## Example summary

A complete program could look like this:

<br/>

With using 'Simple' functions:
```java
public static void main(String[] args)
{
   String comPort = "COM5";
  int baudRate = 115200;

  //Creating a SerialDevice:
  Device myDevice = new SerialDevice(comPort, baudRate);

 //Connecting using simpleConnect()
 myDevice.simpleConnect();

  //create an array for the leds which has the same size as the LED strip
  LED[] leds = new LED[myDevice.getConfiguration().getNumOfLeds()];

  for(int i = 0; i < 5000; i++)
  {

      //generate rainbow colors using the Effects library
      leds = Effects.Rainbow(1,30, i, leds.length);

      //send the led array to the device
    //send it to myDevice
    myDevice.simpleSend(leds);
  }
  
  //finished with sending data
  //disconnect from the device
  myDevice.disconnect ();
}
```


<br/>
<br/>


Without using 'Simple' functions:

```java
public static void main(String[] args)
{
   String comPort = "COM5";
  int baudRate = 115200;

  //Creating a SerialDevice:
  Device myDevice = new SerialDevice(comPort, baudRate);

  //Connecting using connect()
  try
  {
      myDevice.connect();
  }
  catch(TimeoutException e)
  {
      //the device timed out while trying to connect
      System.out.println("Oh no! Could not connect to the device because it timed out.");
      return;
  }
  catch(IOException e)
  {
      //Could not connect to the device
      //the device timed out while trying to connect
      System.out.println("Could not connect to the device because there is a connection problem");
      return;
  }

  //create an array for the leds which has the same size as the LED strip
  LED[] leds = new LED[myDevice.getConfiguration().getNumOfLeds()];

  for(int i = 0; i < 5000; i++)
  {

      //generate rainbow colors using the Effects library
      leds = Effects.Rainbow(1,30, i, leds.length);

      //send the led array to the device
      try
      {
          myDevice.send ( leds);
      }
      catch (TimeoutException e)
      {
          //the device timed out while trying to send the data
          System.out.println("Oh no! Could not send the data  because the device timed out.");
      }
      catch (IOException e)
      {
          //Could not connect to the device
          System.out.println("Could send the data because there is a connection problem");
      }
  }
  
  //finished with sending data
  //disconnect from the device
  myDevice.disconnect ();
}
```