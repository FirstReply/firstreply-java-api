package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationListParams {
    private String search;
    private String sort;
    private String order;
    private int page;
    private int limit;
}
