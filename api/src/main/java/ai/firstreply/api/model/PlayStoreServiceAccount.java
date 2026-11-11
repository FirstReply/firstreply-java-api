package ai.firstreply.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The organization's own Google service account for Google Play reviews. Its email is invited in
 * the Play Console (Users and permissions, with the permission to reply to reviews) before a
 * PLAYSTORE channel can be created.
 */
@Data
@NoArgsConstructor
public class PlayStoreServiceAccount {
    private String email;
}
