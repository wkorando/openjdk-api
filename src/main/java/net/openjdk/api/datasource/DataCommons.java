package net.openjdk.api.datasource;

import net.openjdk.api.v1.release.binary.model.BinarySchema;

import java.util.stream.Collectors;
import java.util.stream.Stream;

class DataCommons {

    public static Stream<BinarySchema> getBinaryBy(String version, String os_family, String os_arch, Stream<BinarySchema> binaries) {
        return binaries.filter(
                x->{
                    var rel = x.getReleaseInfo();
                    var osSchema = rel.getOSSchema();
                    var filterByVersion = !version.isEmpty() ? rel.getVersionSchema().isMatch(version): true;
                    var filterByOSFamily = !os_family.isEmpty() ? osSchema.isMatchByOSFamily(os_family): true;
                    var filterByOSArch = !os_family.isEmpty() ? osSchema.isMatchByOSArch(os_arch): true;

                    return filterByVersion && (filterByOSFamily || filterByOSArch);
                }
        );
    }

    public static BinarySchema getBinaryURL(String version, String os_family, String os_arch, Stream<BinarySchema> binaries) {
        BinarySchema fin = null;
        var res = getBinaryBy(version, os_family, os_arch, binaries).collect(Collectors.toList());
        if (res.size() > 0) {
            fin = res.get(0);
        }
        return fin;
    }

}
