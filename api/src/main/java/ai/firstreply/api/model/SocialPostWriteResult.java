package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The text the AI wrote; nothing is saved.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocialPostWriteResult {
    private String text;
    /** The text still exceeds the requested maxLength */
    private boolean tooLong;
}
