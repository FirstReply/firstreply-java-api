package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TranslateResult {
    private String source;
    private String translation;
    private String sourceLanguage;
    private boolean translated;
}
