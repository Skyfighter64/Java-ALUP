# Subprograms


## General

The ALUP supports the usage of `Subprograms`, which enable a user to execute pre-defined code on the slave device from the master device by sending a `Subcommand`,
 which contains the ID of the `Subprogram`, from the master device to the slave device.
 
 Note:
 - A `Subprogram` is the actual code on the slave device, which gets executed. Each `Subprogram` has it's own `SubprogramID`.
 
  - The `SubProgramID` is the identifier used for a specific `Subprogram`.
  
 - A `Subcommand` is a command which gets sent from the master device to the slave device, telling the slave device to __execute__ a specific `Subprogram`. 
        It contains the `SubCommandID` for the `Subprogram` which should be executed. 

<br/>

For more information on `Subprograms`, see the [Documentation of the ALUP](https://github.com/Skyfighter64/ALUP/blob/master/Documentation/Documentation_en-us.md#subprograms_link)

## Adding  subprograms

How to add `subprograms` depends on the slave device used.

If you want to add your own `subprograms`, follow the guide of your used slave device.

<br/>

## Calling subprograms

:information_source: Connect to the device using `myDevice.Connect()` or `myDevice.SimpleConnect()` if not already done.

1. Set the Subcommand by using `myDevice.SetSubCommand( [subprogram id] )` and replace `[subprogram id]` with the ID of the subprogram you want to call.

2. Send the frame by using `myDevice.Send()` or `myDevice.SimpleSend()`

<br />

:information_source: The Subcommand is sent with the header of a Frame, which means, that you can send LED data alongside the subcommand (e.g. by using `myDevice.SetLeds(LED[] leds)` before sending).

:warning: This function overrides any previously set subcommands and protocol commands, specifically `CLEAR`, which is set by default. Therefore, when sending LED data alongside the subcommand, you will experience the same effect as when calling `myDevice.setClear(false)` before sending. For more information, see [setClear()] (TODO: add link to docs)

:warning: Some functions will override the ID of `myDevice.SetSubcommand(int id)`. See [`Device.SetSubCommand()`] (TODO: add link to docs) for more info.

