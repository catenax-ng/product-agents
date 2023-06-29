package org.eclipse.tractusx.agents.remoting;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(basePackages = "org.eclipse.tractusx.agents.remoting")
public class TestConfiguration {

    @Bean
    ServletWebServerFactory getServletWebServerFactory() {
        return new TomcatServletWebServerFactory();
    }

}
