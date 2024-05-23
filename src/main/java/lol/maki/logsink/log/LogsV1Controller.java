package lol.maki.logsink.log;

import java.util.HexFormat;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import io.opentelemetry.proto.logs.v1.LogsData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LogsV1Controller {

	private final Logger log = LoggerFactory.getLogger(LogsV1Controller.class);

	@PostMapping(path = "/v1/logs", consumes = MediaType.APPLICATION_PROTOBUF_VALUE)
	public void logs(@RequestBody LogsData logs) throws InvalidProtocolBufferException {
		System.out.println(JsonFormat.printer().print(logs));
		String traceId = HexFormat.of()
			.formatHex(logs.getResourceLogsList()
				.getFirst()
				.getScopeLogsList()
				.getFirst()
				.getLogRecordsList()
				.getFirst()
				.getTraceId()
				.toByteArray());
		log.info("Received traceId={}", traceId);
	}

}
