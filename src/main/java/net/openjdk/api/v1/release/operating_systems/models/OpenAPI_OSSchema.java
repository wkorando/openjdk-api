package net.openjdk.api.v1.release.operating_systems.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class OpenAPI_OSSchema {

    public static final String key = "operating_systems";

    @JsonProperty(key)
    private List<OSSchema> os;
}
