package net.openjdk.api.v1.release.operating_systems.models;

import com.fasterxml.jackson.annotation.JsonProperty;


public class OSSchema {

    @JsonProperty("os_family")
    protected final String os_family;
    @JsonProperty("os_arch")
    protected final String os_arch;

    public OSSchema(String os_arch, String os_family) {
        this.os_arch = os_arch;
        this.os_family = os_family;
    }

    @JsonProperty("alias")
    public String getAlias() {
        return String.format("%s/%s", os_family, os_arch);
    }

    public String getUnderScoreAlias() {
        return String.format("%s_%s", os_family, os_arch).replaceAll(" ", "_");
    }

    public Boolean isMatchByOSFamily(String os_family) {
        return this.os_family.equalsIgnoreCase(os_family);
    }

    public Boolean isMatchByOSArch(String os_arch) {
        return this.os_arch.equalsIgnoreCase(os_arch);
    }

    public String toString() {
        return getAlias();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OSSchema osSchema = (OSSchema) o;

        return toString().equalsIgnoreCase(osSchema.toString());
    }

    @Override
    public int hashCode() {
        int result = os_family != null ? os_family.hashCode() : 0;
        result = 31 * result + (os_arch != null ? os_arch.hashCode() : 0);
        return result;
    }

}
