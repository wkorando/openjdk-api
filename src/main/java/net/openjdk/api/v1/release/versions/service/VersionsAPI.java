package net.openjdk.api.v1.release.versions.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import net.openjdk.api.datasource.DataSourceInterface;
import net.openjdk.api.util.JSONUtil;
import net.openjdk.api.v1.release.operating_systems.models.OpenAPI_OSSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class VersionsAPI {

    @Autowired
    DataSourceInterface dataSource;

    public ObjectNode toJSON() {
        return JSONUtil.listOfObjectsToJson(
                OpenAPI_OSSchema.key, dataSource.getListOfAvailableVersions()
        );
    }

}
