package net.openjdk.api.v1.release.operating_systems.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import net.openjdk.api.datasource.DataSourceInterface;
import net.openjdk.api.util.JSONUtil;
import net.openjdk.api.v1.release.binary.model.OpenAPI_BinarySchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OSAPI {

	DataSourceInterface dataSource;

	public OSAPI(DataSourceInterface dataSource) {
		this.dataSource = dataSource;
	}

	public ObjectNode getJSON() {
		return JSONUtil.listOfObjectsToJson(OpenAPI_BinarySchema.key, dataSource.getListOfSupportedOperationSystems());
	}

}
