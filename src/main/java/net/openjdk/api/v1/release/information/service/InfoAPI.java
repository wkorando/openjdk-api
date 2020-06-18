package net.openjdk.api.v1.release.information.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import net.openjdk.api.datasource.DataSourceInterface;
import net.openjdk.api.util.JSONUtil;
import net.openjdk.api.v1.release.binary.model.OpenAPI_BinarySchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;


@Service
public class InfoAPI {

    @Autowired
    private DataSourceInterface dataSource;

    public ObjectNode toJSON() {
        System.out.println(Objects.isNull(dataSource));
        var r = dataSource.getListOfReleases();
        return JSONUtil.listOfObjectsToJson(OpenAPI_BinarySchema.key, r);
    }

}
