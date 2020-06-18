package net.openjdk.api.v1.release.versions.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class OpenAPI_VersionSchema {

    public static final String key = "versions";

    @JsonProperty(key)
    private List<VersionSchema> versions;
}
