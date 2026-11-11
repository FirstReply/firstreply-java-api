package ai.firstreply.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * A new login for a connected channel, after an expired or revoked one or to grant more
 * capabilities. Only the account the channel is connected to is accepted.
 */
@Data
@NoArgsConstructor
public class ChannelReconnectRequest {
    /**
     * MESSAGING (default), PUBLISHING, COMMENTS, INSIGHTS: what the login asks for on Facebook and
     * Instagram. On a TikTok channel, COMMENTS adds the second login that reads comments.
     */
    private List<String> capabilities;
}
