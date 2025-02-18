package com.tonic.lib;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 * A utility library for basic file operations.
 * <p>
 * All methods in this class are static and only use <code>int</code>,
 * <code>boolean</code>, <code>String</code> (and arrays of these) as parameters
 * and return values. This library provides methods to read, write, append,
 * query, list, and delete files.
 * </p>
 */
public class FileLib {

    /**
     * Reads the file at the given path and returns its contents as an array of lines.
     *
     * @param path the file path as a String
     * @return an array of Strings where each element is one line of the file;
     *         returns an empty array if the file does not exist or an error occurs.
     */
    public static String[] readFile(String path) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(path));
            return lines.toArray(new String[0]);
        } catch (IOException e) {
            // On error (e.g. file not found), return an empty array.
            return new String[0];
        }
    }

    /**
     * Reads the entire file at the given path and returns its content as a single String.
     *
     * @param path the file path as a String
     * @return the file contents as a String; returns an empty string if the file
     *         does not exist or an error occurs.
     */
    public static String readFileAsString(String path) {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(path));
            return new String(bytes);
        } catch (IOException e) {
            return "";
        }
    }

    /**
     * Writes the given content to the file at the specified path.
     * If the file already exists, its content is overwritten.
     *
     * @param path    the file path as a String
     * @param content the content to write to the file as a String
     * @return true if the file was written successfully, false otherwise.
     */
    public static boolean writeFile(String path, String content) {
        try {
            Files.write(Paths.get(path), content.getBytes());
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Appends the given content to the file at the specified path.
     *
     * @param path    the file path as a String
     * @param content the content to append as a String
     * @return the new file size in bytes after appending, or -1 if an error occurs.
     */
    public static int appendToFile(String path, String content) {
        try {
            Files.write(Paths.get(path), content.getBytes(), StandardOpenOption.APPEND);
            return (int) Files.size(Paths.get(path));
        } catch (IOException e) {
            return -1;
        }
    }

    /**
     * Checks whether a file exists at the given path.
     *
     * @param path the file path as a String
     * @return true if the file exists, false otherwise.
     */
    public static boolean fileExists(String path) {
        return Files.exists(Paths.get(path));
    }

    /**
     * Returns the size of the file at the given path in bytes.
     *
     * @param path the file path as a String
     * @return the file size in bytes as an int, or -1 if the file does not exist
     *         or an error occurs.
     */
    public static int fileSize(String path) {
        try {
            if (!fileExists(path)) {
                return -1;
            }
            return (int) Files.size(Paths.get(path));
        } catch (IOException e) {
            return -1;
        }
    }

    /**
     * Lists the names of files and directories in the directory at the specified path.
     *
     * @param path the directory path as a String
     * @return an array of Strings containing the names of files and subdirectories;
     *         returns an empty array if the path is not a directory or an error occurs.
     */
    public static String[] listDirectory(String path) {
        File dir = new File(path);
        if (dir.exists() && dir.isDirectory()) {
            String[] list = dir.list();
            if (list != null) {
                return list;
            }
        }
        return new String[0];
    }

    /**
     * Deletes the file at the specified path.
     *
     * @param path the file path as a String
     * @return true if the file was deleted successfully, false otherwise.
     */
    public static boolean deleteFile(String path) {
        try {
            return Files.deleteIfExists(Paths.get(path));
        } catch (IOException e) {
            return false;
        }
    }
}
