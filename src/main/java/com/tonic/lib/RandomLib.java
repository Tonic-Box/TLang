package com.tonic.lib;

import java.util.Random;

/**
 * A utility library for generating random values.
 * <p>
 * All methods in this class are static and use only <code>int</code>,
 * <code>boolean</code>, <code>String</code> (or arrays of these) as parameters
 * and return values. Internally, a {@link java.util.Random} instance is used for
 * random number generation.
 * </p>
 */
public class RandomLib {

    /**
     * Returns a random integer in the range [0, n).
     *
     * @param n the exclusive upper bound (must be positive)
     * @return a random integer between 0 (inclusive) and n (exclusive)
     * @throws IllegalArgumentException if n is not positive
     */
    public static int nextInt(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n must be positive");
        }
        return new Random().nextInt(n);
    }

    /**
     * Returns a random integer in the range [min, max] (inclusive).
     *
     * @param min the lower bound
     * @param max the upper bound (must be >= min)
     * @return a random integer between min and max, inclusive
     * @throws IllegalArgumentException if max is less than min
     */
    public static int nextInt(int min, int max) {
        if (max < min) {
            throw new IllegalArgumentException("max must be >= min");
        }
        return min + new Random().nextInt(max - min + 1);
    }

    /**
     * Returns a random boolean value.
     *
     * @return a random boolean, either true or false
     */
    public static boolean nextBool() {
        return new Random().nextBoolean();
    }

    /**
     * Returns a random letter as a String of length 1.
     * The letter is chosen from the set of uppercase and lowercase English letters.
     *
     * @return a random letter (as a one-character String)
     */
    public static String nextChar() {
        int totalLetters = 52; // 26 uppercase + 26 lowercase
        int r = new Random().nextInt(totalLetters);
        char c;
        if (r < 26) {
            c = (char) ('A' + r);
        } else {
            c = (char) ('a' + (r - 26));
        }
        return String.valueOf(c);
    }

    /**
     * Returns a random string of the specified length.
     * The string consists solely of random letters (both uppercase and lowercase).
     *
     * @param length the desired length of the string (must be non-negative)
     * @return a random string of the given length
     * @throws IllegalArgumentException if length is negative
     */
    public static String nextString(int length) {
        if (length < 0) {
            throw new IllegalArgumentException("length must be non-negative");
        }
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(nextChar());
        }
        return sb.toString();
    }

    /**
     * Returns an array of random integers.
     *
     * @param length the length of the array (must be non-negative)
     * @param min    the minimum value for each element
     * @param max    the maximum value for each element (must be >= min)
     * @return an array of random integers of the specified length
     * @throws IllegalArgumentException if length is negative or max is less than min
     */
    public static int[] nextIntArray(int length, int min, int max) {
        if (length < 0) {
            throw new IllegalArgumentException("length must be non-negative");
        }
        int[] arr = new int[length];
        for (int i = 0; i < length; i++) {
            arr[i] = nextInt(min, max);
        }
        return arr;
    }

    /**
     * Returns an array of random booleans.
     *
     * @param length the length of the array (must be non-negative)
     * @return an array of random booleans of the specified length
     * @throws IllegalArgumentException if length is negative
     */
    public static boolean[] nextBoolArray(int length) {
        if (length < 0) {
            throw new IllegalArgumentException("length must be non-negative");
        }
        boolean[] arr = new boolean[length];
        for (int i = 0; i < length; i++) {
            arr[i] = nextBool();
        }
        return arr;
    }

    /**
     * Returns an array of random strings.
     *
     * @param arrayLength  the length of the string array (must be non-negative)
     * @param stringLength the length of each random string (must be non-negative)
     * @return an array of random strings of the specified dimensions
     * @throws IllegalArgumentException if arrayLength or stringLength is negative
     */
    public static String[] nextStringArray(int arrayLength, int stringLength) {
        if (arrayLength < 0 || stringLength < 0) {
            throw new IllegalArgumentException("arrayLength and stringLength must be non-negative");
        }
        String[] arr = new String[arrayLength];
        for (int i = 0; i < arrayLength; i++) {
            arr[i] = nextString(stringLength);
        }
        return arr;
    }
}
