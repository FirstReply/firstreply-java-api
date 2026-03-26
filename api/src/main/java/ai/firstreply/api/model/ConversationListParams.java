package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationListParams {
    private String search;
    private String sort;
    private String order;
    private int page;
    private int limit;

    private List<String> classificationIds;
    private String language;
    private String status;
    private String contactId;
    private List<String> memberIds;
    private List<String> inboxIds;
    private int priority = -1;
    private Boolean spam;
    private Boolean custom;
    private int count;
}
