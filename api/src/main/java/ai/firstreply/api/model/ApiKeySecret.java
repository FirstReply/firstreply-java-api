package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Returned once when an API key is created. The plaintext {@code apiKey}
 * is exposed only on this response and cannot be retrieved later.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiKeySecret {
    private String id;
    private String name;
    private String prefix;
    private String apiKey;
    private Long expiresAt;
    private Long created;
}
