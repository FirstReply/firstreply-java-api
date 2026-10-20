package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationMember {
    private String id;
    private String organizationId;
    private int permission;
    private Profile profile;
    private String inviteEmail;
    private boolean owner;
    private boolean admin;
    private boolean manager;
    private boolean member;
    private boolean managerOrAdmin;
    private boolean deleted;
    private boolean activated;
    private boolean emailDailySummary;
    private boolean emailWeeklySummary;
    private boolean emailMonthlySummary;
}
