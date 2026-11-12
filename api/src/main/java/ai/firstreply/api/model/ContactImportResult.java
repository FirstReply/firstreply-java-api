package ai.firstreply.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Summary of a CSV contact import.
 */
@Data
@NoArgsConstructor
public class ContactImportResult {
    private int created;
    private int updated;
    private int skipped;
    private int errorCount;
    private List<RowError> errors;

    @Data
    @NoArgsConstructor
    public static class RowError {
        private int line;
        private String message;
    }
}
