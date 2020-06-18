package net.openjdk.api.v1.release.information.controller;


import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.openjdk.api.v1.release.information.models.OpenAPI_InfoSchema;
import net.openjdk.api.v1.release.information.service.InfoAPI;
import net.openjdk.api.v1.release.information.models.InfoSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/release")
@Tag(name = "OpenJDK Release Info API", description = "represents OpenJDK release information API")
public class InfoController {

    @Autowired
    private InfoAPI serviceAPI;

    @Operation(summary = "Get a list of all available OpenJDK releases")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found OpenJDK releases",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OpenAPI_InfoSchema.class)) })
    })
    @RequestMapping(
            value = "/information",
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.GET
    )
    @ResponseStatus(HttpStatus.OK)
    public ObjectNode info() {
        return serviceAPI.toJSON();
    }

}
