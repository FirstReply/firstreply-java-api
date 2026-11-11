package ai.firstreply.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Starts connecting an account that is connected by signing in on the platform: MESSENGER (a
 * Facebook Page), INSTAGRAM, LINKEDIN, WHATSAPP, THREADS, X, TIKTOK, YOUTUBE, GOOGLE_BUSINESS or
 * MASTODON.
 */
@Data
@NoArgsConstructor
public class ChannelConnectRequest {
    private String platform;
    /** Name of the new channel as the team sees it */
    private String channelName;
    /**
     * MESSAGING (default), PUBLISHING, COMMENTS, INSIGHTS: what the login asks for on Facebook and
     * Instagram. Facebook Pages grant one capability per login; other platforms have one login
     * for everything.
     */
    private List<String> capabilities;
    /** Mastodon: the server the account is on, e.g. mastodon.social */
    private String server;
}
