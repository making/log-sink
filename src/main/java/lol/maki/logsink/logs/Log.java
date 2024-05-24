package lol.maki.logsink.logs;

import java.time.Instant;
import java.util.Map;

import org.jilt.Builder;

@Builder
public record Log(Instant timestamp, String severity, String serviceName, String scope, String body, String traceId,
		String spanId, Map<String, Object> attributes, Map<String, Object> resourceAttributes) {
}
