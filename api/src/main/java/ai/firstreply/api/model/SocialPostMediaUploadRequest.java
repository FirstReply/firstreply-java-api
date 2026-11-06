package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Registers a file for upload: JPEG, PNG, WebP or GIF images, MP4 or MOV videos, up to 1 GB.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocialPostMediaUploadRequest {
    private String fileName;
    private String contentType;
    private Long size;
    /** Video length */
    private Long durationMs;
}
