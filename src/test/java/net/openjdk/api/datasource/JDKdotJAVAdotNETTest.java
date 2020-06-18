package net.openjdk.api.datasource;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URL;


public class JDKdotJAVAdotNETTest {

    private static JDKdotJAVAdotNET api;

    @BeforeAll
    static void initAll() throws Exception {
        api = new JDKdotJAVAdotNET();
        var rawHTML = api.readHTML();
        api.parseXML(rawHTML);
    }

    @Test
    public void testParser() {
        // JDK 9 (GA, 9.0.1) releases were available only for 1 operating system
        // which means the release included only 1 binary instead of 3
        assert (api.getListOfAvailableVersions().count()*3)-4 == api.getBinariesBy(
                "", "", "").count();
    }

    @Test
    public void testOSSchemas() {
        var oss = api.getListOfSupportedOperationSystems();
        assert 3 == oss.count();
    }

    @Test
    public void testValidBinaryURL() throws Exception {
        var binary = api.getBinaryURL("9.0.0+181", "linux", "x64");
        new URL(binary.getBinaryLink()).toURI();
    }

}
