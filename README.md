# OpenJDK API "Getting Started"

## Reasons for

Provide convenient way to work with Oracle OpenJDK releases/builds that can be consumed in a manner of ReST API.

## OpenAPI v3.0 support

When application launched, the OpenAPI v3.0 UI is available her:
```text
http://localhost:8080/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config
```

Swagger spec is available here:
```text
http://localhost:8080/api-docs/
```

## Development

```bash
mvn clean compile
mvn clean test
mvn spring-boot:run
```

Supplying custom `application.properties`:

```bash
java -jar target/api-1.0.jar --spring.config.location=file:application_config/application.properties
```

## jdk.java.net CLI with index

```shell
mvn clean compile assembly:single

java -jar target/api-1.0.jar
```