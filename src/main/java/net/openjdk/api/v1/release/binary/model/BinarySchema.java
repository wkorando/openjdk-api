package net.openjdk.api.v1.release.binary.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.openjdk.api.v1.release.information.models.InfoSchema;

public class BinarySchema {

    @JsonProperty("release_info")
    protected final InfoSchema releaseInfo;
    @JsonProperty("binary_url")
    protected final String binaryLink;

    public BinarySchema(InfoSchema releaseInfo, String binaryLink) {
        this.releaseInfo = releaseInfo;
        this.binaryLink = binaryLink;
    }

    @JsonIgnore
    public InfoSchema getReleaseInfo() {
        return releaseInfo;
    }

    @JsonIgnore
    public String getBinaryLink() {
        return binaryLink;
    }

}
