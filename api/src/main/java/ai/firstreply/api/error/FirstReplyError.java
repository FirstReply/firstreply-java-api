package ai.firstreply.api.error;

public class FirstReplyError extends Exception {

    private final int errorCode;

    public FirstReplyError(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
