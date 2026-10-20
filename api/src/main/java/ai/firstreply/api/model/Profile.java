package ai.firstreply.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Profile {
    private String id;
    private Long lastUpdate;
    private Long created;
    private String email;
    private String firstname;
    private String lastname;
    private String imageUrl;
    private String ipAddress;
    private String language;
    private String referrer;
    private boolean newsletter;
    private boolean admin;
    private String oauthProvider;
    private boolean hasPassword;
    private boolean emailVerified;
    private boolean mfaEnabled;
    private Long lastLogin;
    private int numOrganizations;
}
