package net.openjdk.api.v1;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Collections;


@SpringBootApplication(scanBasePackages = {
		"net.openjdk.api",
		"net.openjdk.api.v1",
		"net.openjdk.api.v1.release",
		"net.openjdk.api.datasource"
})
public class RestApiServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestApiServiceApplication.class, args);
	}

	@Bean
	public OpenAPI customOpenAPI(@Value("${application-description}") String appDesciption,
								 @Value("${application-version}") String appVersion) {
		var oa = new OpenAPI();
		oa = new OpenAPI().info(new Info()
				.title("OpenJDK API (OpenAPI V3)")
				.version(appVersion)
				.description(appDesciption)
				.termsOfService("https://openjdk.java.net/legal/tou/")
				.license(new License().name("GNU General Public License, version 2,\n" +
						"with the Classpath Exception").url("https://openjdk.java.net/"))
		);
		oa.setServers(Collections.singletonList(new Server().url("http://localhost:8080")));

		return oa;
	}
}
