package com.tonic.lib;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A utility library for basic web operations.
 * <p>
 * All public methods in this class use only <code>int</code>, <code>boolean</code>,
 * <code>String</code>, or arrays of these types as parameters and return values.
 * In addition to HTTP GET support, this version also provides methods for HTTP POST
 * requests and for building URLs with query parameters.
 * </p>
 */
public class WebLib {

    /**
     * Sends an HTTP GET request to the given URL and returns the response body as a String.
     * <p>
     * If any error occurs during the request, an empty string is returned.
     * </p>
     *
     * @param url the URL to fetch, as a String.
     * @return the HTTP response body, or an empty string on error.
     */
    public static String httpGet(String url) {
        try {
            URL u = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            InputStream in;
            int status = conn.getResponseCode();
            if (status >= 300) {
                in = conn.getErrorStream();
            } else {
                in = conn.getInputStream();
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            reader.close();
            conn.disconnect();
            return content.toString();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Sends an HTTP GET request to the given URL and returns the HTTP status code.
     * <p>
     * If an error occurs, -1 is returned.
     * </p>
     *
     * @param url the URL to fetch, as a String.
     * @return the HTTP status code, or -1 on error.
     */
    public static int httpGetStatus(String url) {
        try {
            URL u = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            int status = conn.getResponseCode();
            conn.disconnect();
            return status;
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Checks whether the given URL is reachable by sending an HTTP HEAD request.
     * <p>
     * Returns true if the HTTP status code is between 200 and 299; false otherwise.
     * </p>
     *
     * @param url the URL to check, as a String.
     * @return true if reachable, false otherwise.
     */
    public static boolean isUrlReachable(String url) {
        try {
            URL u = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            conn.setRequestMethod("HEAD");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            int status = conn.getResponseCode();
            conn.disconnect();
            return status >= 200 && status < 300;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Retrieves the HTTP response headers from a GET request to the given URL.
     * <p>
     * The headers are returned as an array of Strings in the format "Key: value".
     * If an error occurs, an empty array is returned.
     * </p>
     *
     * @param url the URL to fetch, as a String.
     * @return an array of Strings representing the HTTP response headers.
     */
    public static String[] httpGetHeaders(String url) {
        try {
            URL u = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            Map<String, List<String>> headersMap = conn.getHeaderFields();
            List<String> headers = new ArrayList<>();
            for (Map.Entry<String, List<String>> entry : headersMap.entrySet()) {
                String key = entry.getKey();
                List<String> values = entry.getValue();
                StringBuilder sb = new StringBuilder();
                if (key != null) {
                    sb.append(key).append(": ");
                }
                boolean first = true;
                for (String val : values) {
                    if (!first) {
                        sb.append(", ");
                    }
                    sb.append(val);
                    first = false;
                }
                headers.add(sb.toString());
            }
            conn.disconnect();
            return headers.toArray(new String[0]);
        } catch (Exception e) {
            return new String[0];
        }
    }

    /**
     * Sends an HTTP POST request with the specified body data to the given URL and returns the response body.
     * <p>
     * The request's content type is set to "application/x-www-form-urlencoded". The request body should be a
     * URL-encoded string (e.g. "key1=value1&key2=value2"). If an error occurs, an empty string is returned.
     * </p>
     *
     * @param url  the URL to send the POST request to.
     * @param data the request body data, as a String.
     * @return the HTTP response body as a String, or an empty string on error.
     */
    public static String httpPost(String url, String data) {
        try {
            URL u = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            OutputStream os = conn.getOutputStream();
            os.write(data.getBytes("UTF-8"));
            os.flush();
            os.close();

            InputStream in;
            int status = conn.getResponseCode();
            if (status >= 300) {
                in = conn.getErrorStream();
            } else {
                in = conn.getInputStream();
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            reader.close();
            conn.disconnect();
            return content.toString();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Sends an HTTP POST request with the specified body data to the given URL and returns the HTTP status code.
     * <p>
     * If an error occurs, returns -1.
     * </p>
     *
     * @param url  the URL to send the POST request to.
     * @param data the request body data, as a String.
     * @return the HTTP status code as an int, or -1 on error.
     */
    public static int httpPostStatus(String url, String data) {
        try {
            URL u = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            OutputStream os = conn.getOutputStream();
            os.write(data.getBytes("UTF-8"));
            os.flush();
            os.close();

            int status = conn.getResponseCode();
            conn.disconnect();
            return status;
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Sends an HTTP POST request with the specified body data to the given URL and returns the response headers.
     * <p>
     * Each header is returned as a String in the format "Key: value". If an error occurs, an empty array is returned.
     * </p>
     *
     * @param url  the URL to send the POST request to.
     * @param data the request body data, as a String.
     * @return an array of Strings representing the HTTP response headers.
     */
    public static String[] httpPostHeaders(String url, String data) {
        try {
            URL u = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            OutputStream os = conn.getOutputStream();
            os.write(data.getBytes("UTF-8"));
            os.flush();
            os.close();

            Map<String, List<String>> headersMap = conn.getHeaderFields();
            List<String> headers = new ArrayList<>();
            for (Map.Entry<String, List<String>> entry : headersMap.entrySet()) {
                String key = entry.getKey();
                List<String> values = entry.getValue();
                StringBuilder sb = new StringBuilder();
                if (key != null) {
                    sb.append(key).append(": ");
                }
                boolean first = true;
                for (String val : values) {
                    if (!first) {
                        sb.append(", ");
                    }
                    sb.append(val);
                    first = false;
                }
                headers.add(sb.toString());
            }
            conn.disconnect();
            return headers.toArray(new String[0]);
        } catch (Exception e) {
            return new String[0];
        }
    }

    /**
     * Constructs a URL by appending the provided query parameters to the base URL.
     * <p>
     * The parameters are provided as two arrays of Strings: one for keys and one for values.
     * Both arrays must have the same length. The query parameters are URL-encoded using UTF-8.
     * </p>
     *
     * @param baseUrl the base URL as a String.
     * @param keys    an array of parameter keys.
     * @param values  an array of parameter values.
     * @return the full URL with query parameters appended.
     */
    public static String buildUrlWithQuery(String baseUrl, String[] keys, String[] values) {
        if (keys.length != values.length) {
            return baseUrl;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(baseUrl);
        if (!baseUrl.contains("?")) {
            sb.append("?");
        } else if (!baseUrl.endsWith("?") && !baseUrl.endsWith("&")) {
            sb.append("&");
        }
        for (int i = 0; i < keys.length; i++) {
            sb.append(urlEncode(keys[i])).append("=").append(urlEncode(values[i]));
            if (i < keys.length - 1) {
                sb.append("&");
            }
        }
        return sb.toString();
    }

    /**
     * URL-encodes a string using UTF-8 encoding.
     * <p>
     * If encoding fails, the original string is returned.
     * </p>
     *
     * @param s the string to encode.
     * @return the URL-encoded string.
     */
    private static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (Exception e) {
            return s;
        }
    }
}
