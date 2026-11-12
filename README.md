# FirstReply Java API Client

Official Java client library for the [FirstReply](https://firstreply.ai)
customer support API. It wraps the REST API in a typed, blocking service with
built-in HTTPS enforcement, request retries, and automatic token refresh.

- **Conversations**: create, search, read with the full thread, update,
  close, mark as read or spam, delete; totals, daily statistics, statistics
  per member and support metrics; hide, unhide, delete or like a comment of a
  comment thread and answer it privately
- **Messages**: read, reply (sent, as a draft or as an internal note), edit
  drafts, send drafts, search, export as CSV, delete; upload attachments
- **Contacts**: search, create, read, update, delete, import from CSV;
  contact fields
- **Organizations**: list, read, update, usage; members (list, invite, update,
  remove)
- **Channels**: list, read, statistics, fetch new messages now, test the
  connection; create a channel from settings or credentials (email, website
  chat, Bluesky, Telegram, App Store, Google Play), start or renew the login
  of an account that is connected on its platform (Facebook Page, Instagram,
  LinkedIn, WhatsApp, Threads, X, TikTok, YouTube, Google Business Profile,
  Mastodon), read the state of that login or disconnect it, change a
  channel's settings, delete a channel, the Google Play service account and
  what a TikTok account may post
- **Classifications**: list, create, read, update, delete with reassignment,
  impact, statistics
- **Knowledge base**: search, create, read, update, delete; knowledge sources
  (list, create, read, update, delete, sync, sync status)
- **AI**: draft a reply for a conversation, free prompt, translate, test a
  message against the classifications
- **Search** across conversations, contacts, knowledge and messages
- **Social posts**: list, draft, schedule, edit, publish now, approve, retry a
  failed channel, delete (optionally on the platforms too); write, shorten
  or rewrite a text with AI; media uploads, the comment threads of a post,
  metric snapshots per post, post counts per state and publishing analytics.
  Webhooks receive
  `post.create`, `post.update` (status changes, including published and
  failed) and `post.delete`
- **Templates, prompts, webhooks and saved filters**: list, create, read,
  update, delete; test webhooks and rotate their signing secret
- **API keys**: list, create (plaintext returned once), delete
- **Authentication**: login, signup, token refresh, password reset

## Requirements

- Java 11+

## Installation

The library is published as `ai.firstreply:firstreply-api` to
[GitHub Packages](https://github.com/FirstReply/firstreply-java-api/packages).
GitHub requires authentication to download packages, even public ones — use a
personal access token with the `read:packages` scope.

### Gradle

```gradle
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/FirstReply/firstreply-java-api")
        credentials {
            username = providers.gradleProperty("gpr.user").orElse(System.getenv("GITHUB_ACTOR")).getOrElse("")
            password = providers.gradleProperty("gpr.key").orElse(System.getenv("GITHUB_TOKEN")).getOrElse("")
        }
    }
}

dependencies {
    implementation 'ai.firstreply:firstreply-api:0.3.0'
}
```

### Maven

```xml
<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/FirstReply/firstreply-java-api</url>
    </repository>
</repositories>

<dependency>
    <groupId>ai.firstreply</groupId>
    <artifactId>firstreply-api</artifactId>
    <version>0.3.0</version>
</dependency>
```

### From source

```bash
./gradlew publishToMavenLocal
```

Then depend on `ai.firstreply:firstreply-api:0.3.0` with `mavenLocal()` in
your repositories.

## Authentication

Create an **organization API key** in FirstReply under
Organization → Development → API Keys. Keys are named, support optional
expiry, and are shown exactly once on creation. Requests authenticate with
the permissions of the organization owner, so treat the key as a secret.

A key only reaches its own organization: `listOrganizations` returns just
that organization, and calls for other organizations, `createOrganization`
and `searchOrganizations` are refused with 403. Those need a user token.

Alternatively, authenticate a user with `login()` and use the returned JWT
with a `TokenStore` for automatic refresh on 401. Refresh tokens are rotated:
`refresh()` revokes the token it was given, so keep the `refreshToken` of its
response for the next refresh.

## Quick start

```java
import ai.firstreply.api.FirstReplyService;
import ai.firstreply.api.model.*;

FirstReplyService service = new FirstReplyService("fr_...", "<organizationId>");

// Search open conversations
ConversationListParams params = new ConversationListParams();
params.setStatus("open");
params.setPage(1);
params.setLimit(20);
ConversationList conversations = service.searchConversations(params);

// Read a conversation
Conversation conversation = service.getConversation("<conversationId>");

// Reply to a message
MessageCreate reply = new MessageCreate();
reply.setMessagePlain("Thanks for reaching out — this is fixed now.");
Message sent = service.replyMessage("<messageId>", reply);

// Close the conversation
service.closeConversation("<conversationId>");
```

### Drafts, search and metrics

```java
// Let the AI draft a reply, store it as a draft and send it after review
AiResult suggestion = service.draftReply("<conversationId>");
MessageCreate draft = new MessageCreate();
draft.setMessagePlain(suggestion.getResult());
draft.setDraft(true);
Message stored = service.replyMessage("<messageId>", draft);
service.sendMessage(stored.getId());

// Search conversations, contacts, knowledge and messages at once
SearchResult hits = service.search("from:anna@example.com refund", 10);

// Support metrics for September
MetricsStatistics metrics = service.getMetricsStatistics(
        new ConversationStatisticsParams("2026-09-01", "2026-09-30"));
Double medianFirstReplyMs = metrics.getOverview().getFirstReplyP50Ms(); // null without replies
```

### Social posts

```java
// Schedule a post on a channel (a channel with publishingEnabled)
SocialPostCreate post = new SocialPostCreate();
post.setContent("Our new feature is live.");
post.setStatus("SCHEDULED");
post.setScheduledAt(Instant.parse("2026-11-10T09:00:00Z").toEpochMilli());
post.setTimezone("Europe/Vienna");
post.setTargets(List.of(new SocialPostTargetInput("<channelId>", null, null)));
SocialPost scheduled = service.createPost(post); // PENDING_APPROVAL when a manager has to approve it

// How did the last 30 days do?
SocialAnalytics analytics = service.getPostAnalytics(null, null, null);
Long views = analytics.getViews(); // null when no channel offers views

// Let the AI write a post from the knowledge base; nothing is saved
SocialPostWriteResult text = service.writePost(
        new SocialPostWriteRequest("WRITE", null, "Announce the new reporting page", "LINKEDIN", 3000, null));

// The comment threads on a post, as conversations
ConversationList comments = service.listPostConversations(scheduled.getId(), 1, 50);
```

### Channels

```java
// A Bluesky account is created from its handle and an app password
ChannelCreate bluesky = new ChannelCreate();
bluesky.setType("BLUESKY");
bluesky.setName("Bluesky");
bluesky.setBlueskyHandle("example.bsky.social");
bluesky.setBlueskyAppPassword("<app password>");
Channel channel = service.createChannel(bluesky);

// Accounts that sign in on their platform start with a link the user opens
// in a browser where they are signed in to FirstReply (valid for 10 minutes)
ChannelConnectRequest connect = new ChannelConnectRequest();
connect.setPlatform("MASTODON");
connect.setChannelName("Mastodon");
connect.setServer("mastodon.social");
String link = service.connectChannel(connect).getAuthUrl();

// Let posts target the channel; fields left null keep their value
ChannelUpdate update = new ChannelUpdate();
update.setPublishingEnabled(true);
service.updateChannel(channel.getId(), update);
```

### Upgrading

- 0.3.0: `Channel` gained `publishingAuthorized` and the Threads
  (`threads*`), X (`twitter*`), TikTok (`tiktok*`), YouTube (`youtube*`),
  Google Business Profile (`googleBusiness*`), Bluesky (`bluesky*`),
  Mastodon (`mastodon*`) and Telegram (`telegram*`) fields, `Conversation`
  the comment thread fields (`kind`, `externalThreadId`, `externalPostId`,
  `socialPostId`, `socialPost`), `ConversationListParams` the filters `kind`
  and `socialPostId`, `SocialPost` `authorName` and `SocialPostTarget`
  `externalImageUrl`, so their Lombok all-args constructors take more
  parameters, in a new order. Code calling those constructors no longer
  compiles: use the no-args constructor and the setters. `TranslateRequest`
  gained `maxLength` and keeps its two-argument constructor.

### Managing API keys

```java
// List keys (metadata only — the key value is never returned after creation)
ApiKeyList keys = service.listApiKeys(1, 25);

// Create a key; the plaintext is available exactly once on this response
ApiKeyCreate request = new ApiKeyCreate("CI pipeline", 90); // expires in 90 days, 0 = never
ApiKeySecret created = service.createApiKey(request);
String plaintext = created.getApiKey(); // store it now — not retrievable later

// Delete (revoke) a key
service.deleteApiKey(created.getId());
```

## Error handling

All calls throw `FirstReplyException` (a `RuntimeException`) on non-2xx
responses or network errors. It carries the HTTP status code and the error
body returned by the API.

```java
try {
    service.getConversation("<conversationId>");
} catch (FirstReplyException e) {
    System.err.println(e.getStatusCode() + ": " + e.getMessage());
}
```

## Configuration

| Setting | Description |
|---------|-------------|
| `FIRSTREPLY_API_URL` (env) | API base URL, default `https://api.firstreply.ai/` |
| Constructor `timeout` | HTTP read timeout, default 10s (`Duration.ZERO` disables it) |
| Constructor `baseUrl` | Explicit base URL; must be HTTPS (plain HTTP only for loopback) |

The default HTTP client retries idempotent requests up to 3 times with
exponential backoff. Advanced setups can supply a custom `TokenStore` or a
pre-built `FirstReplyApi` (Retrofit) instance.

## Development

```bash
./gradlew build   # compile and run tests
./gradlew test
```

Releases are published to GitHub Packages by the
[publish workflow](.github/workflows/publish.yml) when a `vX.Y.Z` tag is
pushed. This repository is a read-only mirror of `packages/firstreply-java-api`
in the FirstReply monorepo, where releases are tagged.

## License

[MIT](LICENSE)
