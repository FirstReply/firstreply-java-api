package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeListParams {
    private String search;
    private String sort;
    private String order;
    private int count;
    private int page;
    private int limit;
    private List<String> classificationIds;
    private long created;
    private String sourceId;
}
