package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeEntryCreate {
    private String classificationId;
    private boolean positive;
    private String prompt;
    private String completion;
}
