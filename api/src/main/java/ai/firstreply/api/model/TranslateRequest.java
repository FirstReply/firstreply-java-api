package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TranslateRequest {
    private String language;
    private String text;
    /** Most characters the translation may have (the reply limit of the channel), null for no limit */
    private Integer maxLength;

    public TranslateRequest(String language, String text) {
        this(language, text, null);
    }
}
