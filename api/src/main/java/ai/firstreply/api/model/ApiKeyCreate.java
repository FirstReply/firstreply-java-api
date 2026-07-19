package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiKeyCreate {
    private String name;

    /** Days until the key expires; 0 or negative means it never expires. */
    private int expirationDays;
}
