package net.openjdk.api.datasource;

import net.openjdk.api.v1.release.binary.model.BinarySchema;
import net.openjdk.api.v1.release.information.models.InfoSchema;

import java.util.stream.Stream;

class DataCommons {

    public static Stream<InfoSchema> getListOfReleasesBy(String version, String os_family, String os_arch, Stream<InfoSchema> schemas) {
        return schemas.filter(x -> !version.isEmpty() ? x.getVersionSchema().isMatch(version): true)
                .filter(x->{
                    var osSchema = x.getOSSchema();
                    return !os_family.isEmpty() ? osSchema.isMatchByOSFamily(os_family): true;})
                .filter(x->{
                    var osSchema = x.getOSSchema();
                    return !os_arch.isEmpty() ? osSchema.isMatchByOSArch(os_arch): true;
                });
    }

    public static Stream<BinarySchema> getBinariesBy(String version, String os_family, String os_arch, Stream<BinarySchema> binaries) {
        return binaries.filter(v->{
            // filter by version
            var rel = v.getReleaseInfo();
            return !version.isEmpty() ? rel.getVersionSchema().isMatch(version): true;
        }).filter(o->{
            var rel = o.getReleaseInfo();
            var osSchema = rel.getOSSchema();
            return !os_family.isEmpty() ? osSchema.isMatchByOSFamily(os_family): true;
        }).filter(a->{
            var rel = a.getReleaseInfo();
            var osSchema = rel.getOSSchema();
            return !os_arch.isEmpty() ? osSchema.isMatchByOSArch(os_arch): true;
        });
    }

    public static BinarySchema getBinaryURL(String version, String os_family, String os_arch, Stream<BinarySchema> binaries) {
        return getBinariesBy(version, os_family, os_arch, binaries).findFirst().orElse(null);
    }

}
