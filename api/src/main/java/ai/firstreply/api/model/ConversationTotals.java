package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationTotals {
    private int total;
    private int open;
    private int closed;
    private int unclassified;
    private int unassigned;
}
