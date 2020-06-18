package net.openjdk.api.v1.release.information.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class OpenAPI_InfoSchema {

    public static final String key = "info";

    @JsonProperty(key)
    private List<InfoSchema> infos;
}
