package net.openjdk.api.v1.release.versions.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import net.openjdk.api.datasource.DataSourceInterface;
import net.openjdk.api.util.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class VersionsAPI {

    @Autowired
    DataSourceInterface dataSource;

    protected final String key = "versions";

    public ObjectNode toJSON() {
        return JSONUtil.listOfObjectsToJson(
                key, dataSource.getListOfAvailableVersions()
        );
    }

}
