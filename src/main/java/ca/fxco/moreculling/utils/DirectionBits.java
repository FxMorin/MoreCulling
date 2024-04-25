package ca.fxco.moreculling.utils;

import net.minecraft.util.math.Direction;

// A faster replacement for Set<Direction>
// TODO: Add static methods to work with containers not using DirectionBits
public class DirectionBits {

    public static final byte ALL_DIRECTIONS = 63;

    private byte bits;

    public DirectionBits() {
        this.bits = 0;
    }

    public DirectionBits(byte bits) {
        this.bits = bits;
    }

    public void add(int index) {
        bits |= (1 << index);
    }

    public void add(Direction direction) {
        bits |= (1 << direction.ordinal());
    }

    public void remove(int index) {
        bits &= ~(1 << index);
    }

    public void remove(Direction direction) {
        bits &= ~(1 << direction.ordinal());
    }

    public void clear() {
        bits = 0;
    }

    public void fill() {
        bits = ALL_DIRECTIONS;
    }

    public boolean contains(int index) {
        return (bits & (1 << index)) != 0;
    }

    public boolean contains(Direction direction) {
        return (bits & (1 << direction.ordinal())) != 0;
    }

    public boolean isEmpty() {
        return bits == 0;
    }

    public boolean containsAny() {
        return bits != 0;
    }

    public boolean isFull() {
        return bits == ALL_DIRECTIONS;
    }

    public boolean notFull() {
        return bits != ALL_DIRECTIONS;
    }

    public static DirectionBits of(byte bits) {
        return new DirectionBits(bits);
    }

    public static DirectionBits of(short bits) {
        return new DirectionBits((byte) bits);
    }

    public static DirectionBits of(int bits) {
        return new DirectionBits((byte) bits);
    }

    public static DirectionBits fromStringByte(String str) {
        DirectionBits bits = new DirectionBits();
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '1') {
                bits.add(i);
            } else if (str.charAt(i) != '0') {
                throw new IllegalArgumentException("Invalid character: " + str.charAt(i));
            }
        }
        return bits;
    }
}
