package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Channel {
    private String id;
    private long lastUpdate;
    private long created;
    private String organizationId;
    private String contactId;
    private boolean activated;
    private boolean healthy;
    private String type;
    private String name;
    private String emailHost;
    private Integer emailPort;
    private Integer emailPortImap;
    private String emailUsername;
    private Boolean emailSsl;
    private String emailAuthentication;
    private String emailSignature;
    private String whatsappBusinessAccountId;
    private String whatsappPhoneNumberId;
    private String whatsappPhoneNumber;
    private String whatsappBusinessName;
    private String linkedinOrganizationId;
    private String linkedinPageId;
    private String linkedinCompanyName;
    private String linkedinPageUrl;
    private String linkedinScope;
    private Long linkedinTokenExpiry;
    private String messengerPageId;
    private String messengerPageName;
    private String messengerPageUrl;
    private String messengerScope;
    private Long messengerTokenExpiry;
    private String instagramBusinessAccountId;
    private String instagramUserId;
    private String instagramUsername;
    private String instagramProfileUrl;
    private String instagramScope;
    private Long instagramTokenExpiry;
    private String playstorePackageName;
    private String playstoreApplicationName;
    private String playstoreScope;
    private Long playstoreTokenExpiry;
    private String appstoreAppId;
    private String appstoreAppName;
    private String appstoreIssuerId;
    private String appstoreKeyId;
    private String webchatKey;
    private String webchatTitle;
    private String webchatWelcomeMessage;
    private String webchatColor;
    private String webchatTheme;
    private String webchatPosition;
    private String webchatLauncherLabel;
    private Boolean webchatRequireContact;
    private String webchatAllowedDomains;
    /** Posts can target this channel (social channels only) */
    private Boolean publishingEnabled;
    /** Which comments on posts open conversations: ALL, QUESTIONS or NONE */
    private String commentMode;
    /** The AI may answer comments publicly on its own */
    private Boolean commentAutoReply;
    private String threadsUserId;
    private String threadsUsername;
    private String threadsProfileUrl;
    private String threadsScope;
    private Long threadsTokenExpiry;
    /** X (Twitter) account */
    private String twitterUserId;
    private String twitterUsername;
    private String twitterProfileUrl;
    private String twitterScope;
    private Long twitterTokenExpiry;
    private String tiktokOpenId;
    private String tiktokUsername;
    private String tiktokDisplayName;
    private String tiktokScope;
    private Long tiktokTokenExpiry;
    private Long tiktokRefreshExpiry;
    /** The optional second TikTok login (TikTok API for Business) for comments */
    private String tiktokBusinessOpenId;
    private String tiktokBusinessScope;
    private Long tiktokBusinessTokenExpiry;
    private String youtubeChannelId;
    private String youtubeChannelTitle;
    private String youtubeHandle;
    private String youtubeScope;
    private Long youtubeTokenExpiry;
    /** Google Business Profile: the account ({@code accounts/1}) and the location ({@code locations/2}) */
    private String googleBusinessAccountId;
    private String googleBusinessLocationId;
    private String googleBusinessLocationTitle;
    private String googleBusinessAddress;
    private String googleBusinessMapsUri;
    private String googleBusinessReviewUri;
    private String googleBusinessLanguageCode;
    private String googleBusinessScope;
    private Long googleBusinessTokenExpiry;
    private String blueskyDid;
    private String blueskyHandle;
    private String blueskyPdsUrl;
    /** The session scope; direct messages need an app password that allows them */
    private String blueskyScope;
    private Long blueskyTokenExpiry;
    private String mastodonServer;
    private String mastodonAccountId;
    /** The handle {@code name@domain} */
    private String mastodonAccount;
    private String mastodonScope;
    private String telegramBotId;
    private String telegramBotUsername;
    /** The Telegram channel the bot posts to; null when the bot only receives messages */
    private String telegramChatId;
    private String telegramChatUsername;
    private String telegramChatTitle;
    private String telegramDiscussionChatId;
    private Contact contact;
    private long lastMessage;
    /** The connection was granted the permissions to publish; null for channel types that do not publish */
    private Boolean publishingAuthorized;
}
