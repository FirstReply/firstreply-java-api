# FirstReply Java API Client

Official Java client library for the [FirstReply](https://firstreply.ai)
customer support API. It wraps the REST API in a typed, blocking service with
built-in HTTPS enforcement, request retries, and automatic token refresh.

- **Conversations** — create, search, read, update, close, delete
- **Messages** — read, reply, update, search
- **Contacts** — read and update customer contacts
- **Organizations** — list, create, update, search
- **API keys** — list, create (plaintext returned once), delete
- **Authentication** — login, signup, token refresh, password reset

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
    implementation 'ai.firstreply:firstreply-api:0.1.1'
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
    <version>0.1.1</version>
</dependency>
```

### From source

```bash
./gradlew publishToMavenLocal
```

Then depend on `ai.firstreply:firstreply-api:0.1.1` with `mavenLocal()` in
your repositories.

## Authentication

Create an **organization API key** in FirstReply under
Organization → Development → API Keys. Keys are named, support optional
expiry, and are shown exactly once on creation. Requests authenticate with
the permissions of the organization owner — treat the key as a secret.

Alternatively, authenticate a user with `login()` and use the returned JWT
with a `TokenStore` for automatic refresh on 401.

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
[publish workflow](.github/workflows/publish.yml) when a GitHub release is
created.

## License

[MIT](LICENSE)
