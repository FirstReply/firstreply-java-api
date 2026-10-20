package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationUsage {
    private String plan;
    private boolean trial;
    private boolean active;
    private boolean cancelled;
    private int numInboxes;
    private int maxInboxes;
    private int numMembers;
    private int maxMembers;
    private boolean inboxesWarning;
    private boolean membersWarning;
    private boolean inboxesLimitReached;
    private boolean membersLimitReached;
}
