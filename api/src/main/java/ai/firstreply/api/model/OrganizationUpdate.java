package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationUpdate {
    private String name;
    private String language;
    private String description;
    private Integer color;
    private Integer autoCloseInactivityDays;
    private Boolean emailDailySummary;
    private Boolean emailWeeklySummary;
    private Boolean emailMonthlySummary;
    private String aiPromptEgo;
    private String aiPromptConfig;
    private String aiPromptClassification;
}
