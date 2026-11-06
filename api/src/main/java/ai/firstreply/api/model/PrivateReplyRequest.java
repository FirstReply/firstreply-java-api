package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A private message answering a comment (Facebook and Instagram, once per comment, within 7
 * days of it).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrivateReplyRequest {
    private String text;
    /** The comment's message; null answers the newest comment of the conversation */
    private String messageId;
}
