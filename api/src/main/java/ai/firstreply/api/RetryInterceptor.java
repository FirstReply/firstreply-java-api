package ai.firstreply.api;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * OkHttp interceptor that retries failed requests with exponential backoff and jitter.
 *
 * <p>Retries are applied only to idempotent HTTP methods (GET, HEAD, OPTIONS, PUT, DELETE) to
 * avoid double-submitting non-idempotent operations like POST. The interceptor retries on:
 * <ul>
 *   <li>Network IO errors ({@link IOException})</li>
 *   <li>HTTP 408 Request Timeout</li>
 *   <li>HTTP 429 Too Many Requests &mdash; honors {@code Retry-After} header</li>
 *   <li>HTTP 5xx server errors &mdash; honors {@code Retry-After} header when present</li>
 * </ul>
 *
 * <p>Backoff is exponential ({@code base * 2^attempt}) with up to ±25% jitter, clamped at
 * {@code maxBackoff}. The thread is interrupted-aware: an interrupt during sleep aborts the retry
 * loop and rethrows.
 */
public class RetryInterceptor implements Interceptor {

    private static final Set<String> RETRYABLE_METHODS = Set.of("GET", "HEAD", "OPTIONS", "PUT", "DELETE");

    private final int maxRetries;
    private final Duration initialBackoff;
    private final Duration maxBackoff;

    public RetryInterceptor() {
        this(3, Duration.ofMillis(500), Duration.ofSeconds(30));
    }

    public RetryInterceptor(int maxRetries, Duration initialBackoff, Duration maxBackoff) {
        if (maxRetries < 0) {
            throw new IllegalArgumentException("maxRetries must be >= 0");
        }
        this.maxRetries = maxRetries;
        this.initialBackoff = initialBackoff;
        this.maxBackoff = maxBackoff;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        boolean retryable = RETRYABLE_METHODS.contains(request.method());

        IOException lastIoe = null;
        Response response = null;

        for (int attempt = 0; attempt <= maxRetries; attempt++) {
            if (response != null) {
                response.close();
                response = null;
            }
            try {
                response = chain.proceed(request);
            } catch (IOException ioe) {
                lastIoe = ioe;
                if (!retryable || attempt == maxRetries) {
                    throw ioe;
                }
                sleepBackoff(attempt, null);
                continue;
            }

            if (!retryable || !isRetryableStatus(response.code()) || attempt == maxRetries) {
                return response;
            }

            Duration retryAfter = parseRetryAfter(response.header("Retry-After"));
            sleepBackoff(attempt, retryAfter);
        }

        if (response != null) {
            return response;
        }
        throw lastIoe != null ? lastIoe : new IOException("retry loop exited without a response");
    }

    private static boolean isRetryableStatus(int code) {
        return code == 408 || code == 429 || (code >= 500 && code <= 599);
    }

    private void sleepBackoff(int attempt, Duration retryAfter) throws IOException {
        long delayMs;
        if (retryAfter != null) {
            delayMs = Math.min(retryAfter.toMillis(), maxBackoff.toMillis());
        } else {
            long base = initialBackoff.toMillis() * (1L << attempt);
            long capped = Math.min(base, maxBackoff.toMillis());
            long jitter = ThreadLocalRandom.current().nextLong(-capped / 4, capped / 4 + 1);
            delayMs = Math.max(0L, capped + jitter);
        }
        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            InterruptedIOException iioe = new InterruptedIOException("retry sleep interrupted");
            iioe.initCause(e);
            throw iioe;
        }
    }

    static Duration parseRetryAfter(String header) {
        if (header == null || header.isEmpty()) {
            return null;
        }
        try {
            long seconds = Long.parseLong(header.trim());
            return seconds >= 0 ? Duration.ofSeconds(seconds) : null;
        } catch (NumberFormatException ignored) {
            // fall through to HTTP-date parsing
        }
        try {
            Instant target = Instant.from(DateTimeFormatter.RFC_1123_DATE_TIME.parse(header.trim()));
            Duration delta = Duration.between(Instant.now(), target);
            return delta.isNegative() ? Duration.ZERO : delta;
        } catch (DateTimeParseException ignored) {
            return null;
        }
    }
}
