package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationMemberUpdate {
    private Integer permission;
    private Boolean deleted;
    private Boolean activated;
    private Boolean emailDailySummary;
    private Boolean emailWeeklySummary;
    private Boolean emailMonthlySummary;
}
