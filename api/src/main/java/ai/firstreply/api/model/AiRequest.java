package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiRequest {
    private String conversationId;
    private String promptId;
    private String prompt;
    private boolean removeKnowledge;
}
