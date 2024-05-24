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

import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LogsV1Controller {

	@PostMapping(path = "/v1/logs",
			consumes = { MediaType.APPLICATION_PROTOBUF_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public void logs(@RequestBody LogsData logs) throws InvalidProtocolBufferException {
		StringBuilder message = new StringBuilder();
		for (int i = 0; i < logs.getResourceLogsCount(); i++) {
			ResourceLogs resourceLogs = logs.getResourceLogs(i);
			message.append("ResourceLogs #").append(i).append(System.lineSeparator());
			Resource resource = resourceLogs.getResource();
			message.append("Attributes:").append(System.lineSeparator());
			resource.getAttributesList()
				.forEach(attribute -> message.append("\t-> ")
					.append(attribute.getKey())
					.append("\t: ")
					.append(any(attribute.getValue()))
					.append(System.lineSeparator()));
			for (int j = 0; j < resourceLogs.getScopeLogsCount(); j++) {
				ScopeLogs scopeLogs = resourceLogs.getScopeLogs(j);
				message.append("\tSpanLogs #").append(j).append(System.lineSeparator());
				InstrumentationScope scope = scopeLogs.getScope();
				message.append("\tScope: ").append(scope.getName());
				if (StringUtils.hasText(scope.getVersion())) {
					message.append("\t").append(scope.getVersion());
				}
				message.append(System.lineSeparator());
				message.append("\tAttributes:").append(System.lineSeparator());
				scope.getAttributesList()
					.forEach(attribute -> message.append("\t\t-> ")
						.append(attribute.getKey())
						.append("\t: ")
						.append(any(attribute.getValue()))
						.append(System.lineSeparator()));
				for (int k = 0; k < scopeLogs.getLogRecordsCount(); k++) {
					LogRecord logRecord = scopeLogs.getLogRecords(k);
					message.append("\t\tLog #").append(k).append(System.lineSeparator());
					message.append("\t\t\tTimestamp\t: ")
						.append(Instant.EPOCH.plusNanos(logRecord.getTimeUnixNano()))
						.append(System.lineSeparator());
					message.append("\t\t\tSeverity\t: ")
						.append(logRecord.getSeverityText())
						.append(System.lineSeparator());
					message.append("\t\t\tBody    \t: ")
						.append(any(logRecord.getBody()))
						.append(System.lineSeparator());
					message.append("\t\t\tTrace ID\t: ")
						.append(HexFormat.of().formatHex(logRecord.getTraceId().toByteArray()))
						.append(System.lineSeparator());
					message.append("\t\t\tSpan ID \t: ")
						.append(HexFormat.of().formatHex(logRecord.getSpanId().toByteArray()))
						.append(System.lineSeparator());
					message.append("\t\t\tFlags   \t: ").append(logRecord.getFlags()).append(System.lineSeparator());
					;
					message.append("\t\t\tAttributes\t: ").append(System.lineSeparator());
					logRecord.getAttributesList()
						.forEach(attribute -> message.append("\t\t\t\t-> ")
							.append(attribute.getKey())
							.append("\t: ")
							.append(any(attribute.getValue()))
							.append(System.lineSeparator()));
				}
			}
			System.out.println(message.toString().trim());
		}
	}

	static String any(AnyValue value) {
		if (value.hasStringValue()) {
			return "\"%s\"".formatted(value.getStringValue());
		}
		if (value.hasBoolValue()) {
			return "%s".formatted(value.getBoolValue());
		}
		if (value.hasBytesValue()) {
			return "%s".formatted(value.getBytesValue());
		}
		if (value.hasIntValue()) {
			return "%d".formatted(value.getIntValue());
		}
		if (value.hasDoubleValue()) {
			return "%s".formatted(value.getDoubleValue());
		}
		if (value.hasArrayValue()) {
			return "%s".formatted(value.getArrayValue().getValuesList().stream().map(LogsV1Controller::any).toList());
		}
		if (value.hasKvlistValue()) {
			return "%s".formatted(Map.ofEntries(value.getKvlistValue()
				.getValuesList()
				.stream()
				.map(kv -> Map.entry(kv.getKey(), any(kv.getValue())))
				.toArray(Map.Entry[]::new)));
		}
		return "";
	}

}
