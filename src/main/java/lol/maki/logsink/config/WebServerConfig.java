package lol.maki.logsink.config;

import org.eclipse.jetty.server.handler.gzip.GzipHandler;

import org.springframework.boot.web.embedded.jetty.JettyServerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class WebServerConfig {

	@Bean
	public JettyServerCustomizer jettyServerCustomizer() {
		return server -> {
			GzipHandler gzipHandler = new GzipHandler();
			gzipHandler.setInflateBufferSize(1);
			gzipHandler.setHandler(server.getHandler());
			server.setHandler(gzipHandler);
		};
	}

}
