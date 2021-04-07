package net.openjdk.api.v1.release.versions.service;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.node.ObjectNode;

import net.openjdk.api.datasource.DataSourceInterface;
import net.openjdk.api.util.JSONUtil;
import net.openjdk.api.v1.release.operating_systems.models.OpenAPI_OSSchema;

@Service
public class VersionsAPI {

	DataSourceInterface dataSource;

	public VersionsAPI(DataSourceInterface dataSource) {
		this.dataSource = dataSource;
	}

	public ObjectNode toJSON() {
		return JSONUtil.listOfObjectsToJson(OpenAPI_OSSchema.key, dataSource.getListOfAvailableVersions());
	}

}
