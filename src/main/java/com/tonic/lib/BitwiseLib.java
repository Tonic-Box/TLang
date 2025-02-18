package com.tonic.lib;

/**
 * BitwiseLib provides helper methods for performing common bitwise operations on integers.
 */
public class BitwiseLib {

    /**
     * Returns the bitwise AND of two integers.
     *
     * @param a the first integer.
     * @param b the second integer.
     * @return a & b.
     */
    public static int and(int a, int b) {
        return a & b;
    }

    /**
     * Returns the bitwise OR of two integers.
     *
     * @param a the first integer.
     * @param b the second integer.
     * @return a | b.
     */
    public static int or(int a, int b) {
        return a | b;
    }

    /**
     * Returns the bitwise XOR of two integers.
     *
     * @param a the first integer.
     * @param b the second integer.
     * @return a ^ b.
     */
    public static int xor(int a, int b) {
        return a ^ b;
    }

    /**
     * Returns the bitwise complement (NOT) of an integer.
     *
     * @param a the integer.
     * @return ~a.
     */
    public static int not(int a) {
        return ~a;
    }

    /**
     * Returns the result of left-shifting the given integer by n bits.
     *
     * @param a the integer to shift.
     * @param n the number of positions to shift.
     * @return a << n.
     */
    public static int leftShift(int a, int n) {
        return a << n;
    }

    /**
     * Returns the result of right-shifting the given integer by n bits (arithmetic shift).
     *
     * @param a the integer to shift.
     * @param n the number of positions to shift.
     * @return a >> n.
     */
    public static int rightShift(int a, int n) {
        return a >> n;
    }

    /**
     * Returns the number of one-bits in the two's complement binary representation of the integer.
     *
     * @param a the integer.
     * @return the count of one-bits.
     */
    public static int bitCount(int a) {
        // You can either use Java's built-in or write a loop.
        return Integer.bitCount(a);
    }

    public static int bitPack(boolean[] array)
    {
        int packed = 0;
        int length = Math.min(array.length, 32); // Limit to 32 bits

        for (int i = 0; i < length; i++) {
            if (array[i]) {
                packed |= (1 << (31 - i)); // Shift and set the bit
            }
        }

        return packed;
    }
}
