package net.openjdk.api.v1.release.information.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import net.openjdk.api.datasource.DataSourceInterface;
import net.openjdk.api.util.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import java.util.Objects;


@Service
public class InfoAPI {

    @Autowired
    private DataSourceInterface dataSource;

    protected final String key = "info";

    public ObjectNode toJSON() {
        System.out.println(Objects.isNull(dataSource));
        var r = dataSource.getListOfReleases();
        return JSONUtil.listOfObjectsToJson(key, r);
    }

}
