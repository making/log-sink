package lol.maki.logsink.logs;

import java.time.Instant;
import java.util.HexFormat;
import java.util.Map;

import com.google.protobuf.InvalidProtocolBufferException;
import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.InstrumentationScope;
import io.opentelemetry.proto.logs.v1.LogRecord;
import io.opentelemetry.proto.logs.v1.LogsData;
import io.opentelemetry.proto.logs.v1.ResourceLogs;
import io.opentelemetry.proto.logs.v1.ScopeLogs;
import io.opentelemetry.proto.resource.v1.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LogsV1Controller {

	private final Logger log = LoggerFactory.getLogger(LogsV1Controller.class);

	@PostMapping(path = "/v1/logs",
			consumes = { MediaType.APPLICATION_PROTOBUF_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public void logs(@RequestBody LogsData logs) throws InvalidProtocolBufferException {
		// log.info("Received {}",
		// JsonFormat.printer().omittingInsignificantWhitespace().print(logs));
		StringBuilder message = new StringBuilder();
		for (int i = 0; i < logs.getResourceLogsCount(); i++) {
			ResourceLogs resourceLogs = logs.getResourceLogs(i);
			message.append("ResourceLogs #").append(i).append(System.lineSeparator());
			Resource resource = resourceLogs.getResource();
			message.append("Resource attributes:").append(System.lineSeparator());
			resource.getAttributesList()
				.forEach(attribute -> message.append("  -> ")
					.append(attribute.getKey())
					.append(" : ")
					.append(any(attribute.getValue()))
					.append(System.lineSeparator()));
			for (int j = 0; j < resourceLogs.getScopeLogsCount(); j++) {
				ScopeLogs scopeLogs = resourceLogs.getScopeLogs(j);
				message.append("  SpanLogs #").append(j).append(System.lineSeparator());
				InstrumentationScope scope = scopeLogs.getScope();
				message.append("  Scope: ")
					.append(scope.getName())
					.append(" ")
					.append(scope.getVersion())
					.append(System.lineSeparator());
				message.append("  Scope attributes:").append(System.lineSeparator());
				scope.getAttributesList()
					.forEach(attribute -> message.append("    -> ")
						.append(attribute.getKey())
						.append(" : ")
						.append(any(attribute.getValue()))
						.append(System.lineSeparator()));
				for (int k = 0; k < scopeLogs.getLogRecordsCount(); k++) {
					LogRecord logRecord = scopeLogs.getLogRecords(k);
					message.append("    Log #").append(k).append(System.lineSeparator());
					message.append("      Timestamp: ")
						.append(Instant.ofEpochMilli(logRecord.getTimeUnixNano() / 1_000_1000))
						.append(System.lineSeparator());
					message.append("      Severity: ")
						.append(logRecord.getSeverityText())
						.append(System.lineSeparator());
					message.append("      Body:  ").append(any(logRecord.getBody())).append(System.lineSeparator());
					message.append("      Trace ID: ")
						.append(HexFormat.of().formatHex(logRecord.getTraceId().toByteArray()))
						.append(System.lineSeparator());
					message.append("      Span ID:  ")
						.append(HexFormat.of().formatHex(logRecord.getSpanId().toByteArray()))
						.append(System.lineSeparator());
					message.append("      Attributes:").append(System.lineSeparator());
					logRecord.getAttributesList()
						.forEach(attribute -> message.append("      -> ")
							.append(attribute.getKey())
							.append(" : ")
							.append(any(attribute.getValue()))
							.append(System.lineSeparator()));
				}
			}
			System.out.println(message.toString().trim());
		}
	}

	static String any(AnyValue value) {
		if (value.hasStringValue()) {
			return "%s".formatted(value.getStringValue());
		}
		if (value.hasBoolValue()) {
			return "Bool(%s)".formatted(value.getBoolValue());
		}
		if (value.hasBytesValue()) {
			return "Bytes(%s)".formatted(value.getBytesValue());
		}
		if (value.hasIntValue()) {
			return "Int(%d)".formatted(value.getIntValue());
		}
		if (value.hasDoubleValue()) {
			return "Double(%s)".formatted(value.getDoubleValue());
		}
		if (value.hasArrayValue()) {
			return "Array(%s)"
				.formatted(value.getArrayValue().getValuesList().stream().map(LogsV1Controller::any).toList());
		}
		if (value.hasKvlistValue()) {
			return "KvList(%s)".formatted(Map.ofEntries(value.getKvlistValue()
				.getValuesList()
				.stream()
				.map(kv -> Map.entry(kv.getKey(), any(kv.getValue())))
				.toArray(Map.Entry[]::new)));
		}
		return "";
	}

}
