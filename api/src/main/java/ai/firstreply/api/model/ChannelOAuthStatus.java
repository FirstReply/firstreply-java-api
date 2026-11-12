package ai.firstreply.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The state of the login of a channel that is connected on its platform.
 */
@Data
@NoArgsConstructor
public class ChannelOAuthStatus {
    private boolean connected;
    /** CONNECTED, EXPIRED, ERROR or DISCONNECTED */
    private String status;
    private String platform;
    /** Epoch millis */
    private Long tokenExpiryDate;
    private boolean requiresReauth;
    private String lastError;
    private String accountName;
    private String accountId;
}
