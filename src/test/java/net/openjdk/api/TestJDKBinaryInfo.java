package net.openjdk.api;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

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

	@Test
	public void validateDownloadBinaries() throws IOException, URISyntaxException {
		URI projectRootPath = Paths.get(".").toUri();
		URI apiPath = new URI(projectRootPath.toString() + "/api");
		ObjectMapper mapper = new ObjectMapper();
		List<File> jsonFiles = new ArrayList<>();

		try (Stream<Path> paths = Files.walk(Paths.get(apiPath))) {
			paths.filter(path -> path.toString().endsWith(".json")).map(p -> p.toFile()).forEach(jsonFiles::add);
		}
		for (File jsonFile : jsonFiles) {
			System.out.println(jsonFile.getName());
			try {
				JDKBinary[] jdkBinaryArray = (mapper.readValue(jsonFile, JDKBinary[].class));
				for (JDKBinary jdkBinary : jdkBinaryArray) {
					System.out.println(jdkBinary.binary_url());
				}
			} catch (MismatchedInputException e) {
				try {
					JDKBinary jdkBinary = (mapper.readValue(jsonFile, JDKBinary.class));
					System.out.println(jdkBinary.binary_url());
				} catch (Exception innerE) {
					throw innerE;
				}

			}

		}

	}
}
