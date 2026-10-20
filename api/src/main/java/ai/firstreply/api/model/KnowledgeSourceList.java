package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeSourceList {
    private List<KnowledgeSource> data;
    private int page;
    private int limit;
    private int total;
}
