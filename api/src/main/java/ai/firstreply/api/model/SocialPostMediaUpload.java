package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * A started media upload: send the file to {@code uploadUrl} with {@code uploadMethod} and
 * {@code uploadHeaders}, then confirm it with {@code completePostMediaUpload}.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocialPostMediaUpload {
    private SocialPostMedia media;
    private String uploadUrl;
    private String uploadMethod;
    private Map<String, String> uploadHeaders;
}
