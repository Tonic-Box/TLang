package com.tonic.lib;

/**
 * SystemLib provides methods to interact with the underlying system.
 * It supports only int, bool, string, and arrays of these.
 */
public class SystemLib {

    /**
     * Exits the program with the given exit code.
     *
     * @param code the exit status code.
     */
    public static void exit(int code) {
        System.exit(code);
    }

    /**
     * Retrieves the value of the specified environment variable.
     * If the variable is not set, returns an empty string.
     *
     * @param name the name of the environment variable.
     * @return the value of the environment variable or "" if not found.
     */
    public static String getenv(String name) {
        String value = System.getenv(name);
        return (value != null) ? value : "";
    }

    /**
     * Returns the current system time in seconds since the Unix epoch.
     *
     * @return the current time in seconds.
     */
    public static int currentTimeSeconds() {
        return (int)(System.currentTimeMillis() / 1000);
    }
}
