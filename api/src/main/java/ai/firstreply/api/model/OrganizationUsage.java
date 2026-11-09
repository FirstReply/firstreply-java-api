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
    private int numChannels;
    private int maxChannels;
    private int numMembers;
    private int maxMembers;
    private boolean channelsWarning;
    private boolean membersWarning;
    private boolean channelsLimitReached;
    private boolean membersLimitReached;
    /** Channels with Publishing on, counted against their own limit */
    private int numPublishingChannels;
    private int maxPublishingChannels;
    private boolean publishingLimitReached;
}
