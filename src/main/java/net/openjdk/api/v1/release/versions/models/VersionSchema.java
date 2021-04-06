package net.openjdk.api.v1.release.versions.models;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;


public class VersionSchema {

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("build_number")
    private final String buildNumber;

    @JsonProperty("major")
    private final String major;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("minor")
    private final String minor;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("security")
    private final String security;

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
        if (versionType.equalsIgnoreCase(VersionTypeSchema.EA()) ||
                versionType.equalsIgnoreCase(VersionTypeSchema.ProjectEA())) {
            return String.format("%s+%s", major, buildNumber);
        }

        // what if build number is not present?
        // what if minor and security is not present?
        var fmt = "%s%s%s";
        if (!buildNumber.isEmpty()) {
            fmt += "+%s";
        }
        return String.format(fmt, major,
                minor.isEmpty() ? "" : "." + minor,
                security.isEmpty() ? "" : "." + security,
                buildNumber);
    }

    @JsonIgnore
    public String getVersionType() {
        return versionType;
    }

    @JsonIgnore
    public String getMajor() {
        return major;
    }

    @JsonIgnore
    public String getMinor() {
        return minor;
    }

    public Boolean isMatch(String version) {
        return getVersion().startsWith(version);
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

    @JsonIgnore
    public String getVersionID() {
        var part = "";
        var versionType = getVersionType();
        if (versionType.startsWith(VersionTypeSchema.ProjectEA())) {
            part = String.format("jdk-%s", getMinor());
        }
        if (versionType.startsWith(VersionTypeSchema.EA())) {
            part = VersionTypeSchema.EA();
        }
        if (versionType.startsWith(VersionTypeSchema.GA())) {
            part = VersionTypeSchema.GA();
        }
        return part;
    }

}
