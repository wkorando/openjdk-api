package net.openjdk.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.assertj.core.util.Lists;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

public class TestJDKBinaryInfo {

	public record JDKBinary(ReleaseInfo release_info, String binary_url) {
	}

	public record ReleaseInfo(OperatingSystem operating_system, VersionInfo version_info) {
	}

	public record OperatingSystem(String os_family, String os_arch, String alias) {
	}

	public record VersionInfo(String build_number, String major, String minor, String type, String security,
			String version) {
	}

	private static RestTemplate restTemplate = new RestTemplate();
	private static ObjectMapper mapper = new ObjectMapper();

	private static Stream<Arguments> loadJDKBinaries() throws Exception {
		URI apiPath = new URI(Paths.get(".").toUri().toString() + "/api");
		List<File> jsonFiles = new ArrayList<>();
		List<JDKBinary> jdkBinaries = new ArrayList<>();

		try (Stream<Path> paths = Files.walk(Paths.get(apiPath))) {
			paths.filter(path -> path.toString().endsWith(".json")).map(p -> p.toFile()).forEach(jsonFiles::add);
		}
		for (File jsonFile : jsonFiles) {
			try {
				jdkBinaries.addAll(Lists.newArrayList(mapper.readValue(jsonFile, JDKBinary[].class)));
			} catch (MismatchedInputException e) {
				try {
					jdkBinaries.add(mapper.readValue(jsonFile, JDKBinary.class));
				} catch (Exception innerE) {
					throw innerE;
				}
			}
		}

		return jdkBinaries.stream().map(j -> Arguments.of(j, String.format("validating%s_%s_%s_%s_%s_%s_%s", //
				j.release_info.operating_system.os_family, //
				j.release_info.operating_system.os_arch, //
				j.release_info.version_info.major, //
				j.release_info.version_info.minor, //
				j.release_info.version_info.security, //
				j.release_info.version_info.type, //
				j.release_info.version_info.version)));//
	}

	@ParameterizedTest(name = "{index} {1}")
	@MethodSource("loadJDKBinaries")
	public void validateDownloadBinaries(JDKBinary jdkBinary, String testName) throws Exception {
		ResponseEntity<Object> response = restTemplate.exchange(jdkBinary.binary_url, HttpMethod.HEAD, null,
				Object.class);
		assertThat(response.getStatusCode()).isEqualTo(200);
	}

}
