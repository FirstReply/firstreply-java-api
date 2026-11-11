package ai.firstreply.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The link that continues a connect. The user opens {@code authUrl} in a browser where they are
 * signed in to FirstReply, signs in on the platform, and FirstReply creates or updates the channel
 * (and asks which account when the login offers several).
 */
@Data
@NoArgsConstructor
public class ChannelConnectResult {
    private String authUrl;
    private String state;
    private String provider;
}
