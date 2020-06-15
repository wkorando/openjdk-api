package net.openjdk.api.v1.release.binary.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import net.openjdk.api.datasource.DataSourceInterface;
import net.openjdk.api.util.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class BinaryAPI {

    @Autowired
    private DataSourceInterface dataSource;

    protected final String key = "binaries";

    public ObjectNode toJSON() {
        return JSONUtil.listOfObjectsToJson(key, dataSource.getListOfBinaries());
    }

    public String getBinaryURL(String version, String os_family, String os_arch) {
        var bin = dataSource.getBinary(version, os_family, os_arch);
        return bin.getBinaryLink();
    }

}
