package net.openjdk.api.datasource;

import net.openjdk.api.v1.release.binary.model.BinarySchema;
import net.openjdk.api.v1.release.information.models.InfoSchema;
import net.openjdk.api.v1.release.operating_systems.models.OSSchema;
import net.openjdk.api.v1.release.versions.models.VersionSchema;
import net.openjdk.api.v1.release.versions.models.VersionTypeSchema;

import java.util.*;
import java.util.stream.Stream;


public class DataStub extends DataCommons implements DataSourceInterface {

    final static VersionSchema v14 = new VersionSchema("14", "0", "1",
            "0", VersionTypeSchema.GA());
    final static List<VersionSchema> versions = Collections.singletonList(v14);

    final static OSSchema lin = new OSSchema("x64", "linux");
    final static OSSchema macOs = new OSSchema("x64", "macos");
    final static OSSchema win = new OSSchema("x64", "windows");

    final static InfoSchema linX64 = new InfoSchema(lin, v14);
    final static InfoSchema macOsX64 = new InfoSchema(macOs, v14);
    final static InfoSchema winX64 = new InfoSchema(win, v14);

    protected List<InfoSchema> infos = Arrays.asList(winX64, macOsX64, linX64);
    protected Set<OSSchema> schemas = new HashSet<>(Arrays.asList(win, lin, macOs));

    final static List<BinarySchema> binaries = Arrays.asList(
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
    public Stream<VersionSchema> getListOfAvailableVersions() {
        return versions.stream();
    }

    @Override
    public Stream<VersionSchema> getListOfAvailableVersionsFilteredByMajor(String openJDKmajorVersion) {
        return versions.stream().filter(x-> openJDKmajorVersion.equalsIgnoreCase(x.getMajor()));
    }

    @Override
    public Stream<OSSchema> getListOfSupportedOperationSystems() {
        return schemas.stream();
    }

    @Override
    public Stream<InfoSchema> getListOfReleases() {
        return infos.stream();
    }

    @Override
    public Stream<BinarySchema> getListOfBinaries() {
        return binaries.stream();
    }

    @Override
    public Stream<BinarySchema> getBinaryBy(String version, String os_family, String os_arch) {
        return DataCommons.getBinaryBy(version, os_family, os_arch, binaries.stream());
    }

    @Override
    public BinarySchema getBinaryURL(String version, String os_family, String os_arch) {
        return DataCommons.getBinaryURL(version, os_family, os_arch, binaries.stream());
    }

}
