package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * An uploaded media file a post takes, with its alt text.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocialPostMediaInput {
    private String id;
    private String altText;
}
