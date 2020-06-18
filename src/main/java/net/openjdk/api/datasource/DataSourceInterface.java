package net.openjdk.api.datasource;

import net.openjdk.api.v1.release.binary.model.BinarySchema;
import net.openjdk.api.v1.release.information.models.InfoSchema;
import net.openjdk.api.v1.release.operating_systems.models.OSSchema;
import net.openjdk.api.v1.release.versions.models.VersionSchema;

import java.util.stream.Stream;


public interface DataSourceInterface {

    Stream<VersionSchema> getListOfAvailableVersions();

    Stream<OSSchema> getListOfSupportedOperationSystems();

    Stream<InfoSchema> getListOfReleasesBy(String version, String os_family, String os_arch);

    Stream<BinarySchema> getBinariesBy(String version, String os_family, String os_arch);

    BinarySchema getBinaryURL(String version, String os_family, String os_arch);

}
