package lol.maki.logsink.logs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPOutputStream;

import com.google.protobuf.util.JsonFormat;
import io.opentelemetry.proto.logs.v1.LogsData;
import lol.maki.logsink.IntegrationTestBase;
import org.junit.jupiter.api.Test;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;

import static org.assertj.core.api.Assertions.assertThat;

class LogsV1ControllerTest extends IntegrationTestBase {

	@Test
	void ingestProtobuf() throws Exception {
		String json = StreamUtils.copyToString(new ClassPathResource("logs.json").getInputStream(),
				StandardCharsets.UTF_8);
		LogsData.Builder builder = LogsData.newBuilder();
		JsonFormat.parser().merge(json, builder);
		ResponseEntity<Void> response = this.restClient.post()
			.uri("/v1/logs")
			.contentType(MediaType.APPLICATION_PROTOBUF)
			.body(builder.build())
			.retrieve()
			.toBodilessEntity();
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	void ingestProtobufGzip() throws Exception {
		String json = StreamUtils.copyToString(new ClassPathResource("logs.json").getInputStream(),
				StandardCharsets.UTF_8);
		LogsData.Builder builder = LogsData.newBuilder();
		JsonFormat.parser().merge(json, builder);
		ResponseEntity<Void> response = this.restClient.post()
			.uri("/v1/logs")
			.contentType(MediaType.APPLICATION_PROTOBUF)
			.header(HttpHeaders.CONTENT_ENCODING, "gzip")
			.body(compress(builder.build().toByteArray()))
			.retrieve()
			.toBodilessEntity();
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	void ingestJson() throws Exception {
		String json = StreamUtils.copyToString(new ClassPathResource("logs.json").getInputStream(),
				StandardCharsets.UTF_8);
		ResponseEntity<Void> response = this.restClient.post()
			.uri("/v1/logs")
			.contentType(MediaType.APPLICATION_JSON)
			.body(json)
			.retrieve()
			.toBodilessEntity();
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	static byte[] compress(byte[] body) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(baos)) {
			gzipOutputStream.write(body);
		}
		return baos.toByteArray();
	}

}