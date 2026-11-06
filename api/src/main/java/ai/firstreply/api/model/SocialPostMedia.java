package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A file of a social post. {@code url} is a short-lived signed link for previews; it is set
 * on a single post, not in lists.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocialPostMedia {
    private String id;
    private String postId;
    private String fileName;
    private String contentType;
    private Long size;
    private Integer width;
    private Integer height;
    private Long durationMs;
    private int position;
    private String altText;
    /** UPLOADING or READY */
    private String status;
    private String url;
}
