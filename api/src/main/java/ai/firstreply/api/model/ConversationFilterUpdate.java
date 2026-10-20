package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationFilterUpdate {
    private String name;
    private Filter filter;
    private int color;
    private int arrangement;
}
