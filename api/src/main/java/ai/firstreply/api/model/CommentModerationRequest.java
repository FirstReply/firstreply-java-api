package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A moderation action on a comment of a comment thread.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentModerationRequest {
    /** HIDE, UNHIDE, DELETE or LIKE (Facebook only) */
    private String action;
}
