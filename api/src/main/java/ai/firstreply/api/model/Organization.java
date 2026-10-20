package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Organization {
    private String id;
    private Long lastUpdate;
    private Long created;
    private String name;
    private String language;
    private String description;
    private int color;
    private String publicId;
    private int numInboxes;
    private int numMembers;
    private String plan;
    private int maxInboxes;
    private int maxMembers;
    private int autoCloseInactivityDays;
    private boolean emailDailySummary;
    private boolean emailWeeklySummary;
    private boolean emailMonthlySummary;
    private String aiPromptEgo;
    private String aiPromptConfig;
    private String aiPromptClassification;
    private long expires;
    private int permission;
    private boolean trial;
    private boolean active;
    private boolean cancelled;
    private boolean expired;
}
