package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionTestResult {
    private boolean success;
    private String message;
    private String platform;
    private long testTime;
    private String errorCode;
    private String errorDetails;
    private Map<String,Object> testResults;
    private long responseTime;
}
