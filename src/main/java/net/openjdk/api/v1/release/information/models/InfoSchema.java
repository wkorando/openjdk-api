package net.openjdk.api.v1.release.information.models;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import net.openjdk.api.v1.release.operating_systems.models.OSSchema;
import net.openjdk.api.v1.release.versions.models.VersionSchema;

public class InfoSchema {

	@JsonProperty("operating_system")
	private final OSSchema os;
	@JsonProperty("version_info")
	private final VersionSchema version;

	public InfoSchema(@JsonProperty("operating_system") @NotNull OSSchema os,
			@JsonProperty("version_info") @NotNull VersionSchema version) {
		this.os = os;
		this.version = version;
	}

	@JsonIgnore
	public VersionSchema getVersionSchema() {
		return version;
	}

	@JsonIgnore
	public OSSchema getOSSchema() {
		return os;
	}

	public String toString() {
		return String.format("%s for %s", version.toString(), os.toString());
	}

}
