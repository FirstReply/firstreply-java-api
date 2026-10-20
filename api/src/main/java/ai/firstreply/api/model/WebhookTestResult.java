package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebhookTestResult {
    private boolean success;
    private Integer statusCode;
    private long durationMs;
    private String error;
}
