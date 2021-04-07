package net.openjdk.api.v1.release.binary.controller;

import java.util.Objects;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.node.ObjectNode;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.openjdk.api.v1.release.binary.model.OpenAPI_BinarySchema;
import net.openjdk.api.v1.release.binary.service.BinaryAPI;

@RestController
@RequestMapping("/v1/release")
@Tag(name = "OpenJDK Release Binaries API")
public class BinaryController {

	private BinaryAPI binaryAPI;

	public BinaryController(BinaryAPI binaryAPI) {
		this.binaryAPI = binaryAPI;
	}

	@Operation(summary = "Get a list of all available OpenJDK release binaries")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Found OpenJDK release binaries", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = OpenAPI_BinarySchema.class)) }) })
	@RequestMapping(value = "/binaries", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public ObjectNode binaries(@RequestParam(name = "version", required = false, defaultValue = "") String version,
			@RequestParam(name = "os_family", required = false, defaultValue = "") String os_family,
			@RequestParam(name = "os_arch", required = false, defaultValue = "") String os_arch) {
		return binaryAPI.getBinariesFilteredBy(version, os_family, os_arch);
	}

	@Operation(summary = "Get the particular binary URL via HTTP 307")
	@ApiResponses(value = { @ApiResponse(responseCode = "307", description = "Found OpenJDK release binary URL"),
			@ApiResponse(responseCode = "404", description = "Not Found") })
	@RequestMapping(value = "/binaries/{version}/{os_family}/{os_arch}", produces = MediaType.TEXT_PLAIN_VALUE, method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.TEMPORARY_REDIRECT)
	public ResponseEntity<String> getBinary(
			@Parameter(description = "OpenJDK release version", example = "14.0.1+0") @PathVariable String version,
			@Parameter(description = "Operating system family", example = "macOs") @PathVariable String os_family,
			@Parameter(description = "Operating system architecture", example = "x64") @PathVariable String os_arch) {
		var url = binaryAPI.getBinaryURL(version, os_family, os_arch);
		if (Objects.nonNull(url)) {
			var headers = new HttpHeaders();
			headers.add("Location", url);
			return new ResponseEntity<>(headers, HttpStatus.TEMPORARY_REDIRECT);
		}

		return new ResponseEntity<>(
				String.format("OpenJDK binary version: '%s' not found for %s/%s", version, os_arch, os_arch),
				HttpStatus.NOT_FOUND);
	}

}
