package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactFieldValue {
    private int fieldType;
    private String name;
    private String key;
    private String mask;
    private String value;
}
