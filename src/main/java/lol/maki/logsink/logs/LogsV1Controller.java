package lol.maki.logsink.logs;

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

	@PostMapping(path = "/v1/logs",
			consumes = { MediaType.APPLICATION_PROTOBUF_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public void logs(@RequestBody LogsData logs) throws InvalidProtocolBufferException {
		log.info("Received {}", JsonFormat.printer().omittingInsignificantWhitespace().print(logs));
	}

}
