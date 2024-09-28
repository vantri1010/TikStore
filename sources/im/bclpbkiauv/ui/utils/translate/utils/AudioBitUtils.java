package im.bclpbkiauv.ui.utils.translate.utils;

import kotlin.UByte;

public class AudioBitUtils {
    public static boolean isEmpty(CharSequence text) {
        if (text == null || text.length() == 0) {
            return true;
        }
        return false;
    }

    public static byte[] GetBytes(short shortValue, boolean bigEnding) {
        byte[] byteArray = new byte[2];
        if (bigEnding) {
            byteArray[1] = (byte) (shortValue & 255);
            byteArray[0] = (byte) (((short) (shortValue >> 8)) & 255);
        } else {
            byteArray[0] = (byte) (shortValue & 255);
            byteArray[1] = (byte) (((short) (shortValue >> 8)) & 255);
        }
        return byteArray;
    }

    public static short GetShort(byte firstByte, byte secondByte, boolean bigEnding) {
        if (bigEnding) {
            return (short) ((secondByte & UByte.MAX_VALUE) | ((short) (((short) ((firstByte & UByte.MAX_VALUE) | 0)) << 8)));
        }
        return (short) ((firstByte & UByte.MAX_VALUE) | ((short) (((short) ((secondByte & UByte.MAX_VALUE) | 0)) << 8)));
    }

    public static short GetInt(byte firstByte, byte secondByte, byte thirdByte, byte fourthByte, boolean bigEnding) {
        if (bigEnding) {
            byte b = (byte) (firstByte << 24);
            byte firstByte2 = b;
            short shortValue = (short) (b | 0);
            byte b2 = (byte) (secondByte << 16);
            byte secondByte2 = b2;
            short shortValue2 = (short) (b2 | shortValue);
            byte b3 = (byte) (thirdByte << 8);
            byte thirdByte2 = b3;
            short shortValue3 = (short) (b3 | shortValue2);
            byte b4 = (byte) (fourthByte << 0);
            byte fourthByte2 = b4;
            return (short) (b4 | shortValue3);
        }
        byte b5 = (byte) (firstByte << 0);
        byte firstByte3 = b5;
        short shortValue4 = (short) (b5 | 0);
        byte b6 = (byte) (secondByte << 8);
        byte secondByte3 = b6;
        short shortValue5 = (short) (b6 | shortValue4);
        byte b7 = (byte) (thirdByte << 16);
        byte thirdByte3 = b7;
        short shortValue6 = (short) (b7 | shortValue5);
        byte b8 = (byte) (fourthByte << 24);
        byte fourthByte3 = b8;
        return (short) (b8 | shortValue6);
    }

    public static byte[] AverageShortByteArray(byte firstShortHighByte, byte firstShortLowByte, byte secondShortHighByte, byte secondShortLowByte, boolean bigEnding) {
        return GetBytes((short) ((GetShort(firstShortHighByte, firstShortLowByte, bigEnding) / 2) + (GetShort(secondShortHighByte, secondShortLowByte, bigEnding) / 2)), bigEnding);
    }
}
