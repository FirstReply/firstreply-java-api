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
    /** HIDE, UNHIDE, DELETE or LIKE; which of them a platform offers: {@code FirstReplyService#moderateComment} */
    private String action;
}
