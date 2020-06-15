package net.openjdk.api.datasource;

import net.openjdk.api.v1.release.binary.model.BinarySchema;
import net.openjdk.api.v1.release.information.models.InfoSchema;
import net.openjdk.api.v1.release.operating_systems.models.OSSchema;
import net.openjdk.api.v1.release.versions.models.VersionSchema;
import net.openjdk.api.v1.release.versions.models.VersionTypeSchema;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class DataStub implements DataSourceInterface{

    public final static VersionSchema v14 = new VersionSchema("14", "0", "1",
            "0", VersionTypeSchema.GA());

    public final static OSSchema lin = new OSSchema("x64", "linux");
    public final static OSSchema macOs = new OSSchema("x64", "macos");
    public final static OSSchema win = new OSSchema("x64", "windows");

    public final static InfoSchema linX64 = new InfoSchema(lin, v14);
    public final static InfoSchema macOsX64 = new InfoSchema(macOs, v14);
    public final static InfoSchema winX64 = new InfoSchema(win, v14);

    public final static List<BinarySchema> binaries = Arrays.asList(
            new BinarySchema(DataStub.linX64,
                    "https://download.java.net/java/GA/jdk14.0.1/664493ef4a6946b186ff29eb326336a2/7/GPL/openjdk-14.0.1_linux-x64_bin.tar.gz"
    ),
            new BinarySchema(DataStub.macOsX64,
                "https://download.java.net/java/GA/jdk14.0.1/664493ef4a6946b186ff29eb326336a2/7/GPL/openjdk-14.0.1_osx-x64_bin.tar.gz"
    ),
            new BinarySchema(DataStub.winX64,
                "https://download.java.net/java/GA/jdk14.0.1/664493ef4a6946b186ff29eb326336a2/7/GPL/openjdk-14.0.1_windows-x64_bin.zip"
    )
        );

    @Override
    public List<VersionSchema> getListOfAvailableVersions() {
        return Collections.singletonList(v14);
    }

    @Override
    public List<OSSchema> getListOfSupportedOperationSystems() {
        return Arrays.asList(win, lin, macOs);
    }

    @Override
    public List<InfoSchema> getListOfReleases() {
        return Arrays.asList(linX64, macOsX64, winX64);
    }

    @Override
    public List<BinarySchema> getListOfBinaries() {
        return binaries;
    }

    @Override
    public Stream<BinarySchema> getBinariesPerVersion(String version) {
        return binaries.parallelStream().filter(
                x-> x.getReleaseInfo().getVersionSchema().isMatch(version)
        );
    }

    @Override
    public Stream<BinarySchema> getBinariesPerVersionAndOS(String version, String os_family) {
        return binaries.parallelStream().filter(
                x->{
                    var rel = x.getReleaseInfo();
                    return rel.getOSSchema().isMatch(os_family) &&
                            rel.getVersionSchema().isMatch(version);
                }
        );
    }

    @Override
    public BinarySchema getBinary(String version, String os_family, String os_arch) {
        var res = binaries.parallelStream().filter(
                x->{
                    var rel = x.getReleaseInfo();
                    return rel.getOSSchema().isMatch(os_family) &&
                            rel.getVersionSchema().isMatch(version);
                }
        ).collect(Collectors.toList());
        if (res.size() > 0) {
            return res.get(0);
        }
        return null;
    }

}
