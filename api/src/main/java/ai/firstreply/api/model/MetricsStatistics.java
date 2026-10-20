package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricsStatistics {
    private MetricsOverview overview;
    private List<ClosingRateItem> closingRateSeries;
    private List<CloseReasonStatisticItem> closeReasons;
    private List<MemberCloseStatisticItem> memberCloses;
}
