package com.tonic.lib;


/**
 * ArrayLib provides a collection of static utility methods for creating and manipulating arrays.
 * <p>
 * It supports arrays of type int, boolean, and String (and multidimensional arrays thereof).
 * The provided methods include creation (1D, 2D, 3D) and common operations such as:
 * <ul>
 *   <li>Getting the length (for 1D arrays)</li>
 *   <li>Reversing the array</li>
 *   <li>Concatenating two arrays</li>
 *   <li>Extracting a subarray (slice)</li>
 *   <li>Sorting the array (using bubble sort)</li>
 * </ul>
 * <p>
 * For int arrays, additional operations like sum, min, and max are also provided.
 * </p>
 */
public class ArrayLib {

    // ---------------------------
    // Array Creation Methods
    // ---------------------------

    // --- int arrays ---
    /**
     * Creates a new 1-dimensional integer array of the given size.
     *
     * @param size the desired size of the array.
     * @return a new int array of length {@code size}.
     */
    public static int[] int1d(int size) {
        return new int[size];
    }

    /**
     * Creates a new 2-dimensional integer array with the specified dimensions.
     *
     * @param size1 the number of rows.
     * @param size2 the number of columns.
     * @return a new 2D int array with dimensions [size1][size2].
     */
    public static int[][] int2d(int size1, int size2) {
        return new int[size1][size2];
    }

    /**
     * Creates a new 3-dimensional integer array with the specified dimensions.
     *
     * @param size1 the size of the first dimension.
     * @param size2 the size of the second dimension.
     * @param size3 the size of the third dimension.
     * @return a new 3D int array with dimensions [size1][size2][size3].
     */
    public static int[][][] int3d(int size1, int size2, int size3) {
        return new int[size1][size2][size3];
    }

    // --- boolean arrays ---
    /**
     * Creates a new 1-dimensional boolean array of the given size.
     *
     * @param size the desired size of the array.
     * @return a new boolean array of length {@code size}.
     */
    public static boolean[] bool1d(int size) {
        return new boolean[size];
    }

    /**
     * Creates a new 2-dimensional boolean array with the specified dimensions.
     *
     * @param size1 the number of rows.
     * @param size2 the number of columns.
     * @return a new 2D boolean array with dimensions [size1][size2].
     */
    public static boolean[][] bool2d(int size1, int size2) {
        return new boolean[size1][size2];
    }

    /**
     * Creates a new 3-dimensional boolean array with the specified dimensions.
     *
     * @param size1 the size of the first dimension.
     * @param size2 the size of the second dimension.
     * @param size3 the size of the third dimension.
     * @return a new 3D boolean array with dimensions [size1][size2][size3].
     */
    public static boolean[][][] bool3d(int size1, int size2, int size3) {
        return new boolean[size1][size2][size3];
    }

    // --- String arrays ---
    /**
     * Creates a new 1-dimensional String array of the given size.
     *
     * @param size the desired size of the array.
     * @return a new String array of length {@code size}.
     */
    public static String[] string1d(int size) {
        return new String[size];
    }

    /**
     * Creates a new 2-dimensional String array with the specified dimensions.
     *
     * @param size1 the number of rows.
     * @param size2 the number of columns.
     * @return a new 2D String array with dimensions [size1][size2].
     */
    public static String[][] string2d(int size1, int size2) {
        return new String[size1][size2];
    }

    /**
     * Creates a new 3-dimensional String array with the specified dimensions.
     *
     * @param size1 the size of the first dimension.
     * @param size2 the size of the second dimension.
     * @param size3 the size of the third dimension.
     * @return a new 3D String array with dimensions [size1][size2][size3].
     */
    public static String[][][] string3d(int size1, int size2, int size3) {
        return new String[size1][size2][size3];
    }

    // ---------------------------
    // Common Operations for 1D Arrays
    // ---------------------------

    // --- Length ---
    /**
     * Returns the length of a 1D int array.
     *
     * @param arr the array.
     * @return the number of elements in the array.
     */
    public static int length(int[] arr) {
        return arr.length;
    }

    /**
     * Returns the length of a 1D boolean array.
     *
     * @param arr the array.
     * @return the number of elements in the array.
     */
    public static int length(boolean[] arr) {
        return arr.length;
    }

    /**
     * Returns the length of a 1D String array.
     *
     * @param arr the array.
     * @return the number of elements in the array.
     */
    public static int length(String[] arr) {
        return arr.length;
    }

    // --- Reverse ---
    /**
     * Returns a new int array with the elements of the given array in reverse order.
     *
     * @param arr the original array.
     * @return a new int array with reversed order.
     */
    public static int[] reverse(int[] arr) {
        int n = arr.length;
        int[] result = new int[n];
        for (int i = 0; i < n; i = i + 1) {
            result[i] = arr[n - 1 - i];
        }
        return result;
    }

    /**
     * Returns a new boolean array with the elements of the given array in reverse order.
     *
     * @param arr the original array.
     * @return a new boolean array with reversed order.
     */
    public static boolean[] reverse(boolean[] arr) {
        int n = arr.length;
        boolean[] result = new boolean[n];
        for (int i = 0; i < n; i = i + 1) {
            result[i] = arr[n - 1 - i];
        }
        return result;
    }

    /**
     * Returns a new String array with the elements of the given array in reverse order.
     *
     * @param arr the original array.
     * @return a new String array with reversed order.
     */
    public static String[] reverse(String[] arr) {
        int n = arr.length;
        String[] result = new String[n];
        for (int i = 0; i < n; i = i + 1) {
            result[i] = arr[n - 1 - i];
        }
        return result;
    }

    // --- Concatenation ---
    /**
     * Concatenates two int arrays into a new array.
     *
     * @param a the first array.
     * @param b the second array.
     * @return a new int array containing all elements of {@code a} followed by all elements of {@code b}.
     */
    public static int[] concat(int[] a, int[] b) {
        int[] result = new int[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    /**
     * Concatenates two boolean arrays into a new array.
     *
     * @param a the first array.
     * @param b the second array.
     * @return a new boolean array containing all elements of {@code a} followed by all elements of {@code b}.
     */
    public static boolean[] concat(boolean[] a, boolean[] b) {
        boolean[] result = new boolean[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    /**
     * Concatenates two String arrays into a new array.
     *
     * @param a the first array.
     * @param b the second array.
     * @return a new String array containing all elements of {@code a} followed by all elements of {@code b}.
     */
    public static String[] concat(String[] a, String[] b) {
        String[] result = new String[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    // --- Subarray Extraction ---
    /**
     * Returns a subarray of the given int array from index {@code start} (inclusive) to {@code end} (exclusive).
     *
     * @param arr   the original array.
     * @param start the starting index (inclusive). Values less than 0 are treated as 0.
     * @param end   the ending index (exclusive). Values greater than the array length are clamped.
     * @return a new int array representing the subarray, or an empty array if {@code start} >= {@code end}.
     */
    public static int[] subArray(int[] arr, int start, int end) {
        if (start < 0) {
            start = 0;
        }
        if (end > arr.length) {
            end = arr.length;
        }
        if (start >= end) {
            return new int[0];
        }
        int size = end - start;
        int[] result = new int[size];
        System.arraycopy(arr, start, result, 0, size);
        return result;
    }

    /**
     * Returns a subarray of the given boolean array from index {@code start} (inclusive) to {@code end} (exclusive).
     *
     * @param arr   the original array.
     * @param start the starting index (inclusive).
     * @param end   the ending index (exclusive).
     * @return a new boolean array representing the subarray, or an empty array if {@code start} >= {@code end}.
     */
    public static boolean[] subArray(boolean[] arr, int start, int end) {
        if (start < 0) {
            start = 0;
        }
        if (end > arr.length) {
            end = arr.length;
        }
        if (start >= end) {
            return new boolean[0];
        }
        int size = end - start;
        boolean[] result = new boolean[size];
        System.arraycopy(arr, start, result, 0, size);
        return result;
    }

    /**
     * Returns a subarray of the given String array from index {@code start} (inclusive) to {@code end} (exclusive).
     *
     * @param arr   the original array.
     * @param start the starting index (inclusive).
     * @param end   the ending index (exclusive).
     * @return a new String array representing the subarray, or an empty array if {@code start} >= {@code end}.
     */
    public static String[] subArray(String[] arr, int start, int end) {
        if (start < 0) {
            start = 0;
        }
        if (end > arr.length) {
            end = arr.length;
        }
        if (start >= end) {
            return new String[0];
        }
        int size = end - start;
        String[] result = new String[size];
        System.arraycopy(arr, start, result, 0, size);
        return result;
    }

    // --- Sorting (Bubble Sort) ---
    /**
     * Returns a sorted copy of the given int array in ascending order.
     * Uses the bubble sort algorithm.
     *
     * @param arr the array to sort.
     * @return a new int array sorted in ascending order.
     */
    public static int[] sort(int[] arr) {
        int[] result = new int[arr.length];
        System.arraycopy(arr, 0, result, 0, arr.length);
        for (int i = 0; i < result.length - 1; i = i + 1) {
            for (int j = 0; j < result.length - 1 - i; j = j + 1) {
                if (result[j] > result[j + 1]) {
                    int temp = result[j];
                    result[j] = result[j + 1];
                    result[j + 1] = temp;
                }
            }
        }
        return result;
    }

    /**
     * Returns a sorted copy of the given boolean array.
     * For booleans, false is considered less than true.
     *
     * @param arr the boolean array to sort.
     * @return a new boolean array with false values preceding true values.
     */
    public static boolean[] sort(boolean[] arr) {
        boolean[] result = new boolean[arr.length];
        System.arraycopy(arr, 0, result, 0, arr.length);
        for (int i = 0; i < result.length - 1; i = i + 1) {
            for (int j = 0; j < result.length - 1 - i; j = j + 1) {
                // Swap if current is true and next is false.
                if (result[j] && !result[j + 1]) {
                    boolean temp = result[j];
                    result[j] = result[j + 1];
                    result[j + 1] = temp;
                }
            }
        }
        return result;
    }

    /**
     * Returns a sorted copy of the given String array in lexicographical order.
     * Uses the bubble sort algorithm.
     *
     * @param arr the String array to sort.
     * @return a new String array sorted in ascending (lexicographical) order.
     */
    public static String[] sort(String[] arr) {
        String[] result = new String[arr.length];
        System.arraycopy(arr, 0, result, 0, arr.length);
        for (int i = 0; i < result.length - 1; i = i + 1) {
            for (int j = 0; j < result.length - 1 - i; j = j + 1) {
                // Assume non-null strings.
                if (result[j].compareTo(result[j + 1]) > 0) {
                    String temp = result[j];
                    result[j] = result[j + 1];
                    result[j + 1] = temp;
                }
            }
        }
        return result;
    }

    // --- Additional int-only Operations ---
    /**
     * Returns the sum of all elements in the given int array.
     *
     * @param arr the array of integers.
     * @return the sum of the array's elements.
     */
    public static int sum(int[] arr) {
        int total = 0;
        for (int j : arr) {
            total = total + j;
        }
        return total;
    }

    /**
     * Returns the maximum element in the given int array.
     * If the array is empty, returns 0.
     *
     * @param arr the array of integers.
     * @return the maximum value in the array, or 0 if the array is empty.
     */
    public static int max(int[] arr) {
        if (arr.length == 0) return 0;
        int m = arr[0];
        for (int i = 1; i < arr.length; i = i + 1) {
            if (arr[i] > m) {
                m = arr[i];
            }
        }
        return m;
    }

    /**
     * Returns the minimum element in the given int array.
     * If the array is empty, returns 0.
     *
     * @param arr the array of integers.
     * @return the minimum value in the array, or 0 if the array is empty.
     */
    public static int min(int[] arr) {
        if (arr.length == 0) return 0;
        int m = arr[0];
        for (int i = 1; i < arr.length; i = i + 1) {
            if (arr[i] < m) {
                m = arr[i];
            }
        }
        return m;
    }

    /**
     * Returns an array containing all integers from start to end (inclusive).
     * If start is greater than end, returns an empty array.
     *
     * @param start the starting integer.
     * @param end   the ending integer.
     * @return an array of integers from start to end.
     */
    public static int[] range(int start, int end) {
        if (start > end) {
            return new int[0];
        }
        int size = end - start + 1;
        int[] result = new int[size];
        for (int i = 0; i < size; i = i + 1) {
            result[i] = start + i;
        }
        return result;
    }
}