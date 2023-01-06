package ca.fxco.moreculling.utils;

public class BitUtils {

    public static final byte ALL_DIRECTIONS = 63;

    //
    // Byte Methods (8 bits)
    //

    public static byte set(byte bits, int index) {
        bits |= (1 << index);
        return bits;
    }

    public static byte unset(byte bits, int index) {
        bits &= ~(1 << index);
        return bits;
    }

    public static boolean get(byte bits, int index) {
        return (bits & (1 << index)) != 0;
    }

    public static byte fromStringByte(String str) {
        byte bits = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '1') {
                bits = set(bits, i);
            } else if (str.charAt(i) != '0') {
                throw new IllegalArgumentException("Invalid character: " + str.charAt(i));
            }
        }
        return bits;
    }

    //
    // Short Methods (16 bits)
    //

    public static short set(short bits, int index) {
        bits |= (1 << index);
        return bits;
    }

    public static short unset(short bits, int index) {
        bits &= ~(1 << index);
        return bits;
    }

    public static boolean get(short bits, int index) {
        return (bits & (1 << index)) != 0;
    }

    public static short fromStringShort(String str) {
        short bits = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '1') {
                bits = set(bits, i);
            } else if (str.charAt(i) != '0') {
                throw new IllegalArgumentException("Invalid character: " + str.charAt(i));
            }
        }
        return bits;
    }

    //
    // Integer Methods (32 bits)
    //

    public static int set(int bits, int index) {
        return bits | (1 << index);
    }

    public static int unset(int bits, int index) {
        return bits & ~(1 << index);
    }

    public static boolean get(int bits, int index) {
        return (bits & (1 << index)) != 0;
    }

    public static int fromStringInt(String str) {
        int bits = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '1') {
                bits = set(bits, i);
            } else if (str.charAt(i) != '0') {
                throw new IllegalArgumentException("Invalid character: " + str.charAt(i));
            }
        }
        return bits;
    }

    //
    // Long Methods (64 bits)
    //

    public static long set(long bits, int index) {
        return bits | (1L << index);
    }

    public static long unset(long bits, int index) {
        return bits & ~(1L << index);
    }

    public static boolean get(long bits, int index) {
        return (bits & (1L << index)) != 0;
    }

    public static long fromStringLong(String str) {
        long bits = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '1') {
                bits = set(bits, i);
            } else if (str.charAt(i) != '0') {
                throw new IllegalArgumentException("Invalid character: " + str.charAt(i));
            }
        }
        return bits;
    }
}
