package ai.firstreply.api;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HttpsEnforcementTest {

    @Test
    void httpsBaseUrlIsAccepted() {
        assertEquals("https://api.firstreply.ai/", FirstReplyService.requireSecureBaseUrl("https://api.firstreply.ai/"));
    }

    @Test
    void plainHttpToCustomHostIsRejected() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> FirstReplyService.requireSecureBaseUrl("http://api.example.com/"));
        assertEquals(true, ex.getMessage().contains("HTTPS"));
    }

    @Test
    void httpToLocalhostIsAllowedForLocalDev() {
        assertDoesNotThrow(() -> FirstReplyService.requireSecureBaseUrl("http://localhost:8080/"));
        assertDoesNotThrow(() -> FirstReplyService.requireSecureBaseUrl("http://127.0.0.1:8080/"));
    }

    @Test
    void emptyOrNullBaseUrlRejected() {
        assertThrows(IllegalArgumentException.class, () -> FirstReplyService.requireSecureBaseUrl(null));
        assertThrows(IllegalArgumentException.class, () -> FirstReplyService.requireSecureBaseUrl(""));
    }

    @Test
    void schemelessBaseUrlRejected() {
        assertThrows(IllegalArgumentException.class, () -> FirstReplyService.requireSecureBaseUrl("api.example.com/"));
    }

    @Test
    void constructorRejectsPlainHttp() {
        assertThrows(IllegalArgumentException.class,
                () -> new FirstReplyService("token", "org-id", "http://api.example.com/", Duration.ofSeconds(5)));
    }
}
