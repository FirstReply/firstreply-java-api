package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageListParams {
    private String search;
    private String sort;
    private String order;
    private int count;
    private int page;
    private int limit;
    private String conversationId;
}
