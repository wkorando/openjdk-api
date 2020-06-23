package net.openjdk.api.datasource;

import net.openjdk.api.v1.release.versions.models.VersionTypeSchema;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.util.Objects;


public class JDKNetTest {

    private static JDKNet api;

    @BeforeAll
    static void initAll() throws Exception {
        api = new JDKNet();

    }

    @Test
    public void testValidBinaryURL() throws Exception {
        var binary = api.getBinaryURL("14.0.1", "linux", "x64");
        new URL(binary.getBinaryLink()).toURI();
    }

    @Test
    public void testFilterBinariesByArch() {
        var os_family = "";
        var os_arch = "x64";
        var version = "";
        var binaries = api.getBinariesBy(version, os_family, os_arch);
        binaries.forEach(x -> {
            var os = x.getReleaseInfo().getOSSchema();
            assert !os.isMatchByOSFamily(os_family);
            assert os.isMatchByOSArch(os_arch);
            assert !x.getReleaseInfo().getVersionSchema().getVersion().equalsIgnoreCase(version);
        });
    }

    @Test
    public void testFilterBinariesByOSandArch() {
        var os_family = "linux";
        var os_arch = "x64";
        var version = "";
        var binaries = api.getBinariesBy(version, os_family, os_arch);
        binaries.forEach(x -> {
            var os = x.getReleaseInfo().getOSSchema();
            assert os.isMatchByOSFamily(os_family);
            assert os.isMatchByOSArch(os_arch);
            assert !x.getReleaseInfo().getVersionSchema().getVersion().equalsIgnoreCase(version);
        });
    }

    @Test
    public void testFilterBinariesByFullMatch() {
        var os_family = "linux";
        var os_arch = "x64";
        var version = "14";
        var binaries = api.getBinariesBy(version, os_family, os_arch);
        binaries.forEach(x -> {
            var os = x.getReleaseInfo().getOSSchema();
            assert os.isMatchByOSFamily(os_family);
            assert os.isMatchByOSArch(os_arch);
            assert x.getReleaseInfo().getVersionSchema().isMatch(version);
        });
    }

    @Test
    public void testFilterBinariesByVersionOnly() {
        var os_family = "";
        var os_arch = "";
        var version = "14";
        var binaries = api.getBinariesBy(version, os_family, os_arch);
        binaries.forEach(x -> {
            var os = x.getReleaseInfo().getOSSchema();
            assert !os.isMatchByOSFamily(os_family);
            assert !os.isMatchByOSArch(os_arch);
            assert x.getReleaseInfo().getVersionSchema().isMatch(version);
        });
    }

    @Test
    public void testGARelease() {
        var gaVersion = api.getListOfAvailableVersions().filter(
                x -> x.getVersionType().equalsIgnoreCase(VersionTypeSchema.GA())
        ).findFirst().orElse(null);
        assert Objects.nonNull(gaVersion);
    }

}
