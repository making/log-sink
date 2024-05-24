package lol.maki.logsink.config;

import java.util.function.Predicate;

import am.ik.accesslogger.AccessLogger;
import am.ik.accesslogger.AccessLoggerBuilder;
import io.micrometer.core.instrument.config.MeterFilter;

import org.springframework.boot.actuate.web.exchanges.HttpExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;

@Configuration(proxyBeanMethods = false)
public class AppConfig {

	@Bean
	public ProtobufHttpMessageConverter protobufHttpMessageConverter() {
		return new ProtobufHttpMessageConverter();
	}

	@Bean
	public AccessLogger accessLogger() {
		Predicate<HttpExchange> excludeActuator = httpExchange -> {
			String uri = httpExchange.getRequest().getUri().getPath();
			return uri != null && !(uri.equals("/readyz") || uri.equals("/livez") || uri.startsWith("/actuator")
					|| uri.startsWith("/cloudfoundryapplication"));
		};
		return AccessLoggerBuilder.accessLogger().filter(excludeActuator).addKeyValues(true).build();
	}

	@Bean
	public MeterFilter customMeterFilter() {
		return MeterFilter.deny(id -> {
			String uri = id.getTag("uri");
			return uri != null && (uri.startsWith("/readyz") || uri.startsWith("/livez") || uri.startsWith("/actuator")
					|| uri.startsWith("/cloudfoundryapplication"));
		});
	}

}
