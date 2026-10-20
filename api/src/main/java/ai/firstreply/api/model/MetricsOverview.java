package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricsOverview {
    private int total;
    private int closed;
    private double closingRate;
    private Double firstReplyP50Ms;
    private Double firstReplyP90Ms;
    private Double firstReplyAvgMs;
    private int firstReplySamples;
    private int aiReplies;
    private double aiReplyShare;
    private int inboundMessages;
    private int positiveMessages;
    private double sentimentPositiveShare;
}
