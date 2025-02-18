package com.tonic.lib;

/**
 * ConsoleLib provides basic methods for console input and output.
 */
public class ConsoleLib {
    /**
     * Prints the given message to standard output followed by a newline.
     *
     * @param message the string to print.
     */
    public static void println(String message) {
        System.out.println(message);
    }

    /**
     * Reads a full line of text from standard input.
     *
     * @return the line read from the console.
     */
    public static String readLine() {
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        return scanner.nextLine();
    }

    /**
     * Reads an integer value from standard input.
     *
     * @return the integer read.
     */
    public static int readInt() {
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        return scanner.nextInt();
    }

    /**
     * Reads a boolean value from standard input.
     * The string "true" (ignoring case) returns true; any other input returns false.
     *
     * @return the boolean value read.
     */
    public static boolean readBool() {
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        String input = scanner.nextLine();
        return Boolean.parseBoolean(input);
    }
}
