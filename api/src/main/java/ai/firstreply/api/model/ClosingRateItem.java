package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClosingRateItem {
    private String date;
    private int created;
    private int closed;
    private double closingRate;
}
