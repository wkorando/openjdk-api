package net.openjdk.api.v1.release.information.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import net.openjdk.api.datasource.DataSourceInterface;
import net.openjdk.api.util.JSONUtil;
import net.openjdk.api.v1.release.binary.model.OpenAPI_BinarySchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InfoAPI {

	private DataSourceInterface dataSource;

	public InfoAPI(DataSourceInterface dataSource) {
		this.dataSource = dataSource;
	}

	public ObjectNode getListOfReleasesBy(String version, String os_family, String os_arch) {
		var r = dataSource.getListOfReleasesBy(version, os_family, os_arch);
		return JSONUtil.listOfObjectsToJson(OpenAPI_BinarySchema.key, r);
	}

}
