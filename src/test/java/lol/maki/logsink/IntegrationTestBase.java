package lol.maki.logsink;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.BasicJsonTester;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestClient;
import org.zalando.logbook.spring.LogbookClientHttpRequestInterceptor;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
		properties = { "logging.structured.format.console=" })
public abstract class IntegrationTestBase {

	@Autowired
	protected RestClient.Builder restClientBuilder;

	@Autowired
	LogbookClientHttpRequestInterceptor logbookClientHttpRequestInterceptor;

	protected RestClient restClient;

	@LocalServerPort
	protected int port;

	protected BasicJsonTester json = new BasicJsonTester(getClass());

	@BeforeEach
	public void init() {
		if (this.restClient == null) {
			this.restClient = this.restClientBuilder.baseUrl("http://localhost:" + port)
				.defaultStatusHandler(__ -> true, (req, res) -> {
				})
				.requestInterceptor(this.logbookClientHttpRequestInterceptor)
				.build();
		}
	}

}
