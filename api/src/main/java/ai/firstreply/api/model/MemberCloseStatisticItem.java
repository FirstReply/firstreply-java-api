package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberCloseStatisticItem {
    private String memberId;
    private String userId;
    private String memberName;
    private int manual;
    private int inactivity;
    private int ai;
    private int unknown;
    private int totalClosed;
}
