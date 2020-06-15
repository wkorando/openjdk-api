package net.openjdk.api.datasource;

import net.openjdk.api.v1.release.binary.model.BinarySchema;
import net.openjdk.api.v1.release.information.models.InfoSchema;
import net.openjdk.api.v1.release.operating_systems.models.OSSchema;
import net.openjdk.api.v1.release.versions.models.VersionSchema;

import java.util.List;
import java.util.stream.Stream;


public interface DataSourceInterface {

    List<VersionSchema> getListOfAvailableVersions();

    List<OSSchema> getListOfSupportedOperationSystems();

    List<InfoSchema> getListOfReleases();

    List<BinarySchema> getListOfBinaries();

    Stream<BinarySchema> getBinariesPerVersion(String version);
    Stream<BinarySchema> getBinariesPerVersionAndOS(String version, String os_family);
    BinarySchema getBinary(String version, String os_family, String os_arch);

}