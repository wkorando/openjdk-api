package net.openjdk.api.v1.release.versions.controller;


import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.openjdk.api.v1.release.operating_systems.models.OpenAPI_OSSchema;
import net.openjdk.api.v1.release.versions.models.VersionSchema;
import net.openjdk.api.v1.release.versions.service.VersionsAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/v1/release")
@Tag(name = "OpenJDK version API")
public class VersionsController {

    @Autowired
    private VersionsAPI versioAPI;

    @Operation(summary = "Get a list of all available OpenJDK versions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found OpenJDK versions",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OpenAPI_OSSchema.class)) })
    })
    @RequestMapping(
            value = "/versions",
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.GET
    )
    @ResponseStatus(HttpStatus.OK)
    public ObjectNode versions() {
        return versioAPI.toJSON();
    }

}
