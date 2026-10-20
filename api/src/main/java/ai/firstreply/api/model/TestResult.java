package ai.firstreply.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestResult {
    private String message;
    private String prompt;
    private String reply;
    private boolean spam;
    @JsonProperty("classificationDto")
    private Classification classification;
}
