package ALUP;


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
