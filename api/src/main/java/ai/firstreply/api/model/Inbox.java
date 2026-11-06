package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inbox {
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
    /** Posts can target this inbox (social channels only) */
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
    private Contact contact;
    private long lastMessage;
    /** The connection was granted the permissions to publish; null for inbox types that do not publish */
    private Boolean publishingAuthorized;
}
