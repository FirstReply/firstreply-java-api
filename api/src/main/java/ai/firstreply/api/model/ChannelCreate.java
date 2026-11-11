package ai.firstreply.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A new channel of a type that is set up with settings or credentials: EMAIL, WEBCHAT, BLUESKY,
 * TELEGRAM, APPSTORE or PLAYSTORE. Only the fields of that type are used. Accounts that are
 * connected by signing in on the platform start with {@link ChannelConnectRequest} instead.
 */
@Data
@NoArgsConstructor
public class ChannelCreate {
    /** EMAIL, WEBCHAT, BLUESKY, TELEGRAM, APPSTORE or PLAYSTORE */
    private String type;
    /** Name of the channel as the team sees it */
    private String name;
    /** The account's handle, e.g. example.bsky.social */
    private String blueskyHandle;
    /** Social accounts: posts can target this channel; counts against the plan's publishing limit */
    private Boolean publishingEnabled;
    /** Social accounts: which comments on posts open conversations: ALL, QUESTIONS or NONE */
    private String commentMode;
    /** Social accounts: the AI may answer comments publicly on its own instead of drafting */
    private Boolean commentAutoReply;
    private String emailHost;
    /** SMTP port */
    private Integer emailPort;
    private Integer emailPortImap;
    /** The mailbox address, which is also the login */
    private String emailUsername;
    /** Stored encrypted, never returned */
    private String emailPassword;
    private Boolean emailSsl;
    private String webchatTitle;
    private String webchatWelcomeMessage;
    /** Accent color as hex, e.g. #2563eb */
    private String webchatColor;
    /** auto, light or dark */
    private String webchatTheme;
    /** right or left */
    private String webchatPosition;
    private String webchatLauncherLabel;
    private Boolean webchatRequireContact;
    /** Comma-separated domains the widget may be embedded on; empty allows every site */
    private String webchatAllowedDomains;
    /** An app password, never the account password. Stored encrypted, never returned */
    private String blueskyAppPassword;
    /** The token of the bot from @BotFather. Stored encrypted, never returned */
    private String telegramBotToken;
    /**
     * The Telegram channel the bot posts to ({@code @name}, a t.me link or the id); the bot has
     * to be an administrator of it. Optional: without it the channel only receives messages.
     */
    private String telegramChat;
    /** The app's Apple ID (the number in its App Store link) */
    private String appstoreAppId;
    private String appstoreAppName;
    private String appstoreIssuerId;
    private String appstoreKeyId;
    /** Content of the .p8 private key file. Stored encrypted, never returned */
    private String appstorePrivateKey;
    /** The organization's service account has to be invited in the Play Console first */
    private String playstorePackageName;
    private String playstoreApplicationName;
}
