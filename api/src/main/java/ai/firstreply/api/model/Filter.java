package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * The criteria of a saved conversation filter, the same as the matching fields of
 * {@link ConversationListParams}.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Filter {
    private List<String> inboxIds;
    private List<String> classificationIds;
    private List<String> memberIds;
    private String search;
    private String status;
    private String contactId;
    private int priority = -1;
    private Long createdFrom;
    private Long createdTo;
    private Long lastUpdateFrom;
    private Long lastUpdateTo;
}
