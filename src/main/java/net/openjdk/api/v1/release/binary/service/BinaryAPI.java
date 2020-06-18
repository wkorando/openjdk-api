package net.openjdk.api.v1.release.binary.service;

import com.fasterxml.jackson.databind.node.ObjectNode;

import net.openjdk.api.datasource.DataSourceInterface;
import net.openjdk.api.util.JSONUtil;

import net.openjdk.api.v1.release.binary.model.OpenAPI_BinarySchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class BinaryAPI {

    @Autowired
    private DataSourceInterface dataSource;

    public String getBinaryURL(String version, String os_family, String os_arch) {
        var bin = dataSource.getBinaryURL(version, os_family, os_arch);
        return bin.getBinaryLink();
    }

    public ObjectNode getBinariesFilteredBy(
            String version,
            String os_family,
            String os_arch
    ) {
        return JSONUtil.listOfObjectsToJson(
                OpenAPI_BinarySchema.key, dataSource.getBinaryBy(version, os_family, os_arch)
        );
    }

}
