package com.tonic.lib;

import com.tonic.annotations.DefaultArg;

/**
 * A utility library for common string operations.
 * <p>
 * All methods in this class are static and accept/return only int, boolean,
 * String, or arrays of these types. This class can serve as a builtin
 * library for TLang.
 * </p>
 */
public class StringLib {

    /**
     * Returns the length of the given string.
     *
     * @param s the string to measure
     * @return the number of characters in s, or 0 if s is null
     */
    public static int length(String s) {
        return (s == null) ? 0 : s.length();
    }

    /**
     * Returns a substring of the given string from beginIndex (inclusive) to endIndex (exclusive).
     *
     * @param s          the original string
     * @param beginIndex the beginning index, inclusive
     * @param endIndex   the ending index, exclusive
     * @return the substring of s, or an empty string if s is null
     * @throws IndexOutOfBoundsException if beginIndex or endIndex are invalid
     */
    public static String substring(String s, int beginIndex, int endIndex) {
        if (s == null) {
            return "";
        }
        return s.substring(beginIndex, endIndex);
    }

    /**
     * Returns the character at the specified index as a string of length 1.
     *
     * @param s     the original string
     * @param index the index of the character to retrieve
     * @return a string containing the character at index, or an empty string if s is null or index is invalid
     */
    public static String charAt(String s, int index) {
        if (s == null || index < 0 || index >= s.length()) {
            return "";
        }
        return s.substring(index, index + 1);
    }

    /**
     * Returns the index within s of the first occurrence of the specified substring.
     *
     * @param s       the string to search
     * @param pattern the substring to search for
     * @return the index of the first occurrence of pattern in s, or -1 if not found
     */
    public static int indexOf(String s, String pattern) {
        if (s == null || pattern == null) {
            return -1;
        }
        return s.indexOf(pattern);
    }

    /**
     * Compares two strings for equality.
     *
     * @param s the first string
     * @param t the second string
     * @return true if s and t are equal (or both null), false otherwise
     */
    public static boolean equals(String s, String t) {
        if (s == null) {
            return t == null;
        }
        return s.equals(t);
    }

    /**
     * Checks if the given string contains the specified substring.
     *
     * @param s       the string to search
     * @param pattern the substring to find
     * @return true if s contains pattern, false otherwise
     */
    public static boolean contains(String s, String pattern) {
        if (s == null || pattern == null) {
            return false;
        }
        return s.contains(pattern);
    }

    /**
     * Checks if the given string starts with the specified prefix.
     *
     * @param s      the string to check
     * @param prefix the prefix to look for
     * @return true if s starts with prefix, false otherwise
     */
    public static boolean startsWith(String s, String prefix) {
        if (s == null || prefix == null) {
            return false;
        }
        return s.startsWith(prefix);
    }

    /**
     * Checks if the given string ends with the specified suffix.
     *
     * @param s      the string to check
     * @param suffix the suffix to look for
     * @return true if s ends with suffix, false otherwise
     */
    public static boolean endsWith(String s, String suffix) {
        if (s == null || suffix == null) {
            return false;
        }
        return s.endsWith(suffix);
    }

    /**
     * Concatenates two strings.
     *
     * @param s the first string
     * @param t the second string
     * @return the concatenation of s and t. If either s or t is null, it is treated as an empty string.
     */
    public static String concat(String s, String t) {
        if (s == null) {
            s = "";
        }
        if (t == null) {
            t = "";
        }
        return s + t;
    }

    /**
     * Converts all characters in the given string to upper case.
     *
     * @param s the string to convert
     * @return the upper-case version of s, or an empty string if s is null
     */
    public static String toUpperCase(String s) {
        if (s == null) {
            return "";
        }
        return s.toUpperCase();
    }

    /**
     * Converts all characters in the given string to lower case.
     *
     * @param s the string to convert
     * @return the lower-case version of s, or an empty string if s is null
     */
    public static String toLowerCase(String s) {
        if (s == null) {
            return "";
        }
        return s.toLowerCase();
    }

    /**
     * Removes leading and trailing whitespace from the given string.
     *
     * @param s the string to trim
     * @return the trimmed string, or an empty string if s is null
     */
    public static String trim(String s) {
        if (s == null) {
            return "";
        }
        return s.trim();
    }

    /**
     * Splits the given string around matches of the given delimiter.
     *
     * @param s         the string to split
     * @param delimiter the delimiter string (regular expression)
     * @return an array of substrings computed by splitting s; returns an empty array if s or delimiter is null
     */
    public static String[] split(String s, String delimiter) {
        if (s == null || delimiter == null) {
            return new String[0];
        }
        return s.split(delimiter);
    }

    /**
     * Replaces all occurrences of the target substring in s with the replacement string.
     *
     * @param s           the original string
     * @param target      the substring to be replaced
     * @param replacement the replacement substring
     * @return the resulting string after replacements, or s if any argument is null
     */
    public static String replace(String s, String target, String replacement) {
        if (s == null || target == null || replacement == null) {
            return s;
        }
        return s.replace(target, replacement);
    }

    /**
     * Checks whether the given string is empty.
     *
     * @param s the string to check
     * @return true if s is null or has length 0, false otherwise
     */
    public static boolean isEmpty(String s, @DefaultArg("true") boolean ignoreWhiteSpace) {
        return s == null || (ignoreWhiteSpace ? s.isBlank() : s.isEmpty());
    }

    /**
     * Returns a new string that is the reverse of the input string.
     *
     * @param s the string to reverse
     * @return the reversed string, or an empty string if s is null
     */
    public static String reverse(String s) {
        if (s == null) {
            return "";
        }
        return new StringBuilder(s).reverse().toString();
    }
}
