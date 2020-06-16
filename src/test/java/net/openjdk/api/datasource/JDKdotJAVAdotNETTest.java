package net.openjdk.api.datasource;

import org.junit.jupiter.api.Test;


public class JDKdotJAVAdotNETTest {

    @Test
    public void testHTML() throws Exception {
        var jdkXML = new JDKdotJAVAdotNET();
        var rawHTML = jdkXML.readHTML();
        jdkXML.parseXML(rawHTML);

        // JDK 9 (GA, 9.0.1) releases were available only for 1 operating system
        // which means the release included only 1 binary instead of 3
        assert (jdkXML.getListOfAvailableVersions().size()*3)-4 == jdkXML.getListOfBinaries().size();
    }

}
