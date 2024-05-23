package lol.maki.logsink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration(proxyBeanMethods = false)
public class TestLogSinkAppApplication {

	public static void main(String[] args) {
		SpringApplication.from(LogSinkAppApplication::main).with(TestLogSinkAppApplication.class).run(args);
	}

}
