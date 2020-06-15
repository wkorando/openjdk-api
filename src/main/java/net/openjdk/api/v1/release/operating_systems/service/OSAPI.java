package net.openjdk.api.v1.release.operating_systems.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import net.openjdk.api.datasource.DataSourceInterface;
import net.openjdk.api.util.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class OSAPI {

    @Autowired
    DataSourceInterface dataSource;

    private final String key = "operating_systems";

    public OSAPI() {}

    public ObjectNode getJSON() {
        return JSONUtil.listOfObjectsToJson(
                key, dataSource.getListOfSupportedOperationSystems()
        );
    }

}
