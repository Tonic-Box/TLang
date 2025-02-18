package com.tonic.lib;

/**
 * MathLib provides a set of static utility methods for common mathematical operations.
 * <p>
 * All methods operate on primitive integers (int) and return values as int or boolean.
 * Negative values for functions such as power or factorial are handled gracefully.
 * Each method is self-contained and does not call any other helper methods.
 * </p>
 */
public class MathLib {

    /**
     * Returns the result of raising a base to an exponent.
     * If the exponent is negative, the method returns 0.
     *
     * @param base     the base integer.
     * @param exponent the exponent (must be non-negative).
     * @return base raised to the exponent, or 0 if exponent is negative.
     */
    public static int power(int base, int exponent) {
        if (exponent < 0) {
            return 0;
        }
        int result = 1;
        for (int i = 0; i < exponent; i = i + 1) {
            result = result * base;
        }
        return result;
    }

    /**
     * Returns the factorial of a non-negative integer.
     * If n is negative, the method returns 0.
     *
     * @param n the integer.
     * @return n! (the factorial of n) or 0 if n is negative.
     */
    public static int factorial(int n) {
        if (n < 0) {
            return 0;
        }
        int result = 1;
        for (int i = 1; i <= n; i = i + 1) {
            result = result * i;
        }
        return result;
    }

    /**
     * Returns the greatest common divisor (GCD) of two integers using the Euclidean algorithm.
     *
     * @param a the first integer.
     * @param b the second integer.
     * @return the greatest common divisor of a and b.
     */
    public static int gcd(int a, int b) {
        // Inline absolute value: if a or b is negative, make it positive.
        if (a < 0) {
            a = -a;
        }
        if (b < 0) {
            b = -b;
        }
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

    /**
     * Returns the least common multiple (LCM) of two integers.
     * If either integer is zero, the method returns 0.
     *
     * @param a the first integer.
     * @param b the second integer.
     * @return the least common multiple of a and b.
     */
    public static int lcm(int a, int b) {
        if (a == 0 || b == 0) {
            return 0;
        }
        // Inline absolute value for a and b product.
        int prod = a * b;
        if (prod < 0) {
            prod = -prod;
        }
        // Inline GCD computation:
        int A = a < 0 ? -a : a;
        int B = b < 0 ? -b : b;
        while (B != 0) {
            int temp = B;
            B = A % B;
            A = temp;
        }
        int gcd = A;
        return prod / gcd;
    }

    /**
     * Returns the absolute value of an integer.
     *
     * @param a the integer.
     * @return the absolute value of a.
     */
    public static int abs(int a) {
        return (a < 0) ? -a : a;
    }

    /**
     * Returns the minimum of two integers.
     *
     * @param a the first integer.
     * @param b the second integer.
     * @return the smaller of a and b.
     */
    public static int min(int a, int b) {
        return (a < b) ? a : b;
    }

    /**
     * Returns the maximum of two integers.
     *
     * @param a the first integer.
     * @param b the second integer.
     * @return the larger of a and b.
     */
    public static int max(int a, int b) {
        return (a > b) ? a : b;
    }

    /**
     * Checks whether the given integer is a prime number.
     * Numbers less than 2 are not considered prime.
     *
     * This method inlines the integer square root computation.
     *
     * @param n the integer.
     * @return true if n is prime, false otherwise.
     */
    public static boolean isPrime(int n) {
        if (n < 2) {
            return false;
        }
        // Inline integer square root computation.
        int i = 2;
        // Find integer square root of n without calling a helper method.
        while (i * i <= n) {
            if (n % i == 0) {
                return false;
            }
            i = i + 1;
        }
        return true;
    }

    /**
     * Returns the integer square root of n, i.e., the largest integer whose square
     * is less than or equal to n. If n is negative, returns 0.
     *
     * @param n the integer.
     * @return the integer square root of n.
     */
    public static int sqrt(int n) {
        if (n < 0) {
            return 0;
        }
        int i = 0;
        while (i * i <= n) {
            i = i + 1;
        }
        return i - 1;
    }

    /**
     * Returns the sum of the elements in the given integer array.
     *
     * @param arr the array of integers.
     * @return the sum of the array's elements.
     */
    public static int sum(int[] arr) {
        int total = 0;
        for (int i = 0; i < arr.length; i = i + 1) {
            total = total + arr[i];
        }
        return total;
    }
}
