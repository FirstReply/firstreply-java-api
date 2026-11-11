package ai.firstreply.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * What a TikTok channel may post right now, read fresh from TikTok. A TikTok target of a post
 * needs a privacy level out of {@code privacyLevelOptions}; there is no default.
 */
@Data
@NoArgsConstructor
public class TikTokCreatorInfo {
    private String nickname;
    private String username;
    private String avatarUrl;
    private List<String> privacyLevelOptions;
    private boolean commentDisabled;
    private boolean duetDisabled;
    private boolean stitchDisabled;
    private Integer maxVideoPostDurationSec;
    /** false: TikTok does not let the creator post right now, {@code cannotPostMessage} says why */
    private boolean canPost;
    private String cannotPostMessage;
    /** false: FirstReply's app has not passed TikTok's audit yet, so only SELF_ONLY is offered */
    private boolean audited;
}
