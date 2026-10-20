package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberConversationStatisticItem {
    private String memberId;
    private String userId;
    private String memberName;
    private int total;
    private int open;
    private int closed;
}
