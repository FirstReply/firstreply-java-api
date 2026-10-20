package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationFilter {
    private String id;
    private long lastUpdate;
    private long created;
    private String organizationId;
    private String userId;
    private String name;
    private Filter filter;
    private int color;
    private int arrangement;
    private ConversationFilterStatistic statistics;
}
