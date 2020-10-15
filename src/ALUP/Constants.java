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
 * a class containing relevant constants of the ALUP protocol version 0.1
 */
public class Constants
{
    public static final String VERSION = "0.1 (internal)";

    final static byte CONNECTION_REQUEST_BYTE = (byte) 255;
    final static byte CONNECTION_ACKNOWLEDGEMENT_BYTE = (byte) 254;
    final static byte CONFIGURATION_START_BYTE = (byte) 253;
    final static byte CONFIGURATION_ACKNOWLEDGEMENT_BYTE = (byte) 252;
    final static byte CONFIGURATION_ERROR_BYTE = (byte) 251;

    final static byte FRAME_ACKNOWLEDGEMENT_BYTE = (byte) 250 ;
    final static byte FRAME_ERROR_BYTE = (byte) 249;


    //command constants
    final static byte COMMAND_NONE = (byte) 0;
    final static byte COMMAND_DISCONNECT = (byte) 2;
    final static byte COMMAND_CLEAR = (byte) 1;
    final static byte SUBCOMMAND_OFFSET = (byte) 8;

}
