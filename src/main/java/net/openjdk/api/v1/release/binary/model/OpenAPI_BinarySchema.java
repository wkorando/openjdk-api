package net.openjdk.api.v1.release.binary.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class OpenAPI_BinarySchema {

    public static final String key = "binaries";

    @JsonProperty(key)
    private List<BinarySchema> binaries;
}
