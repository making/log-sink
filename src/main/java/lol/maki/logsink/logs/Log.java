package lol.maki.logsink.logs;

import java.time.Instant;
import java.util.Map;

import org.jilt.Builder;

@Builder
public record Log(Instant timestamp, String traceId, String spanId, int traceFlags, String severity, String body,
		Map<String, Object> attributes, String scope, Map<String, Object> resourceAttributes) {
}
