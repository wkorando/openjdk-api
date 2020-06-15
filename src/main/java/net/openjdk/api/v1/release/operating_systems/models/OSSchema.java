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

    public Boolean isMatch(String os_family, String os_arch) {
        return this.os_family.equalsIgnoreCase(os_family) &&
                this.os_arch.equalsIgnoreCase(os_arch);
    }

    public Boolean isMatch(String os_family) {
        return this.os_family.equalsIgnoreCase(os_family);
    }

}
