package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI help with a post text: WRITE a post from the instruction (with the knowledge base), or
 * SHORTEN or REWRITE the given text (the instruction says how).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocialPostWriteRequest {
    /** WRITE, SHORTEN or REWRITE */
    private String action;
    /** The current text (SHORTEN, REWRITE) or a starting point (WRITE) */
    private String text;
    /** What the post is about, or how to change the text */
    private String instruction;
    /** Channel type the text is for (e.g. MESSENGER, INSTAGRAM, LINKEDIN, BLUESKY), null for any platform */
    private String platform;
    /** Character limit to stay within */
    private Integer maxLength;
    /** Two-letter code; default the organization's language */
    private String language;
}
