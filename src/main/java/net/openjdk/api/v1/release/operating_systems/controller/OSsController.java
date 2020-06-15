package net.openjdk.api.v1.release.operating_systems.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.openjdk.api.v1.release.operating_systems.models.OSSchema;
import net.openjdk.api.v1.release.operating_systems.service.OSAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/v1/release")
@Tag(name = "OpenJDK supported operating systems API")
public class OSsController {

    @Autowired
    private OSAPI osAPI;

    @Operation(summary = "Get a list of all supported operating systems")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found operating systems",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OSSchema.class)) })
    })
    @RequestMapping(
            value = "/operating_systems",
            produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.GET
    )
    @ResponseStatus(HttpStatus.OK)
    public ObjectNode arch() {
        return osAPI.getJSON();
    }

}
