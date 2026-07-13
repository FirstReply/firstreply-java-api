package ai.firstreply.api;

/**
 * Thrown when the FirstReply API returns a non-2xx response or a network error occurs.
 */
public class FirstReplyException extends RuntimeException {

    private final int statusCode;

    public FirstReplyException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    /** HTTP status code returned by the server, or 0 for network/IO errors. */
    public int getStatusCode() {
        return statusCode;
    }
}
