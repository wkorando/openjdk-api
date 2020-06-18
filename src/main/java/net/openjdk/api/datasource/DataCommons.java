package net.openjdk.api.datasource;

import net.openjdk.api.v1.release.binary.model.BinarySchema;
import net.openjdk.api.v1.release.information.models.InfoSchema;
import net.openjdk.api.v1.release.operating_systems.models.OSSchema;
import net.openjdk.api.v1.release.versions.models.VersionSchema;

import java.util.stream.Collectors;
import java.util.stream.Stream;

class DataCommons {

    protected static boolean filteringRule(VersionSchema v, OSSchema os, String version, String os_family, String os_arch) {
        var filterByVersion = !version.isEmpty() ? v.isMatch(version): true;
        var filterByOSFamily = !os_family.isEmpty() ? os.isMatchByOSFamily(os_family): true;
        var filterByOSArch = !os_family.isEmpty() ? os.isMatchByOSArch(os_arch): true;

        return filterByVersion && (filterByOSFamily || filterByOSArch);
    }

    public static Stream<InfoSchema> getListOfReleasesBy(String version, String os_family, String os_arch, Stream<InfoSchema> schemas) {
        return schemas.filter(
                x-> filteringRule(x.getVersionSchema(), x.getOSSchema(), version, os_family, os_arch)
        );
    }

    public static Stream<BinarySchema> getBinariesBy(String version, String os_family, String os_arch, Stream<BinarySchema> binaries) {
        return binaries.filter(
                x->{
                    var rel = x.getReleaseInfo();
                    var osSchema = rel.getOSSchema();
                    return filteringRule(rel.getVersionSchema(), osSchema, version, os_family, os_arch);
                }
        );
    }

    public static BinarySchema getBinaryURL(String version, String os_family, String os_arch, Stream<BinarySchema> binaries) {
        BinarySchema fin = null;
        var res = getBinariesBy(version, os_family, os_arch, binaries).collect(Collectors.toList());
        if (res.size() > 0) {
            fin = res.get(0);
        }
        return fin;
    }

}
