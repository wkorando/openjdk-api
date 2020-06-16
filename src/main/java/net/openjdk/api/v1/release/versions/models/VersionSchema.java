package net.openjdk.api.v1.release.versions.models;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;

public class VersionSchema {

    @NotBlank
    @JsonProperty("build_number")
    private final String buildNumber;

    @NotBlank
    @JsonProperty("major")
    private final String major;

    @NotBlank
    @JsonProperty("minor")
    private final String minor;

    @NotBlank
    @JsonProperty("security")
    private final String security;

    @NotBlank
    @JsonProperty("type")
    private final String versionType;

    public VersionSchema(String major, String minor, String security, String buildNumber, String versionType) {
        this.major = major;
        this.minor = minor;
        this.security = security;
        this.buildNumber = buildNumber;
        this.versionType = versionType;
    }

    @JsonGetter("version")
    public String getVersion() {
        return String.format("%s.%s.%s+%s", major, minor, security, buildNumber);
    }

    public Boolean isMatch(String version) {
        return getVersion().equalsIgnoreCase(version);
    }

    public String toString() {
        return getVersion();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VersionSchema that = (VersionSchema) o;

        return toString().equalsIgnoreCase(that.toString());
    }

}
